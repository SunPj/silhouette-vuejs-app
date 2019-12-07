package utils

import models.daos.DAOSlick
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.libs.json._
import slick.ast.Ordering
import slick.ast.Ordering.{Asc, Desc, Direction}
import slick.lifted.{ColumnOrdered, Ordered, Query, Rep}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NoStackTrace

case class GridRequest(draw: Option[Int], page: Int, pageSize: Int, sort: Seq[(String, Direction)], filter: Option[String]) {
  val start: Int = (page - 1) * pageSize
}

object GridRequest {

  implicit object ColumnSortDetailFormatter extends Formatter[(String, Direction)] {
    override val format = Some(("format.column.sort", Nil))

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], (String, Direction)] = {
      data(key).split(",").toList match {
        case column :: "desc" :: Nil =>
          Right((column, Desc))
        case column :: "asc" :: Nil =>
          Right((column, Asc))
        case _ =>
          Left(Seq(FormError(key, "Incorrect sort column format. Correct format is 'columnName,(ask|desc)'")))
      }
    }

    override def unbind(key: String, value: (String, Direction)) = Map(key -> s"${value._1},${value._2.toString}")
  }

  val gridMapping = mapping(
    "draw" -> optional(number),
    "page" -> number,
    "pageSize" -> number,
    "order" -> seq(of[(String, Direction)]),
    "search" -> optional(text)
  )(GridRequest.apply)(GridRequest.unapply)

  val form = Form(gridMapping)
}

case class GridResponse[T](draw: Option[Int], recordsTotal: Int, recordsFiltered: Int, data: Seq[T])

object GridResponse {
  implicit def writes[T](implicit fmt: Writes[T]) = new Writes[GridResponse[T]] {
    override def writes(o: GridResponse[T]): JsValue = Json.obj(
      "draw" -> o.draw,
      "recordsTotal" -> o.recordsTotal,
      "recordsFiltered" -> o.recordsFiltered,
      "data" -> o.data.map(fmt.writes)
    )
  }
}

object DynamicGridQuerySupport {

  /**
    * Adds "toGridQuery" method to slick queries
    *
    * @param query slick query
    * @tparam E query type
    * @tparam U query result type
    */
  implicit class GridQuerySupportImplicits[E, U](query: Query[E, U, Seq]) {
    def toGridQuery = GridQuery(query)
  }

}

case class GridQuery[E, U](q: Query[E, U, Seq],
                           sortableColumns: PartialFunction[String, E => Rep[_]] = PartialFunction.empty) {
  def withSortableColumns(sortableColumns: PartialFunction[String, E => Rep[_]]) = GridQuery(q, sortableColumns)
}

trait SlickGridQuerySupport {
  this: DAOSlick =>

  case class InvalidSort(invalidField: String) extends Exception with NoStackTrace

  private case class GridQueryBuilderResult[E, U](countQuery: Rep[Int], dataQuery: Query[E, U, Seq], drawCounter: Option[Int])

  /** Sorts the query by multiple columns in order. Invalid sort names are skipped */
  protected def dynamicSortBy[E, U](query: Query[E, U, Seq], sortableColumns: PartialFunction[String, E => Rep[_]], sortBy: Seq[(String, Direction)]): Either[InvalidSort, Query[E, U, Seq]] = {
    val zero: Either[InvalidSort, Query[E, U, Seq]] = Right(query)
    sortBy.foldRight(zero) {
      // foldRight specifically because stacking sortBy invocations on a query operates in reverse order,
      // so the reversed order of a right fold means the final result is in the desired order.
      case (_, err@Left(_)) =>
        err
      case ((sortColumn, sortOrder), Right(queryToSort)) =>
        val sortOrderRep: Rep[_] => Ordered = ColumnOrdered(_, Ordering(sortOrder))
        val sortColumnRep: E => Rep[_] = { t =>
          if (sortableColumns.isDefinedAt(sortColumn))
            sortableColumns(sortColumn)(t)
          else
            throw InvalidSort(sortColumn)
        }
        try {
          Right(queryToSort.sortBy(sortColumnRep)(sortOrderRep))
        } catch {
          case ex: InvalidSort => Left(ex)
        }
    }
  }

  protected def dynamicSortBy[E, U](gridQuery: GridQuery[E, U], sortBy: Seq[(String, Direction)]): Either[InvalidSort, Query[E, U, Seq]] = {
    dynamicSortBy(gridQuery.q, gridQuery.sortableColumns, sortBy)
  }

  private def buildQuery[E, U](gridQuery: GridQuery[E, U], gridRequest: GridRequest): Either[InvalidSort, GridQueryBuilderResult[E, U]] = {
    dynamicSortBy(gridQuery, gridRequest.sort) map { sortedQuery =>
      val dataQuery = sortedQuery.drop(gridRequest.start).take(gridRequest.pageSize)
      val countQuery = sortedQuery.size
      GridQueryBuilderResult(countQuery, dataQuery, gridRequest.draw)
    }
  }

  def runGridQuery[E, U](gridQuery: GridQuery[E, U], gridRequest: GridRequest)(implicit ec: ExecutionContext): Future[GridResponse[U]] = {
    import profile.api._

    buildQuery(gridQuery, gridRequest) match {
      case Right(GridQueryBuilderResult(countQuery, dataQuery, drawCounter)) =>
        val resultQuery = for {
          count <- countQuery.result
          data <- dataQuery.result
        } yield (count, data)

        db.run(resultQuery).map {
          case (count, data) =>
            GridResponse(drawCounter.map(_ + 1), count, data.size, data)
        }

      case Left(InvalidSort(field)) =>
        Future.failed(new RuntimeException(s"Table can't be sorted by column $field"))
    }
  }
}
