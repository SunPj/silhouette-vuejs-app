package models.services

import javax.inject.Inject
import play.api.mvc.RequestHeader
import play.filters.csrf.CSRF
import play.filters.csrf.CSRF.Token

import scala.io.Source

class IndexRenderService @Inject() () {

  def render(title: Option[String] = None, meta: Seq[(String, String)] = Seq.empty)(implicit request: RequestHeader): String = {
    val Token(name, value) = CSRF.getToken.get

    val metaTags = title.map(t => s"<title>$t</title>").getOrElse("") +
      meta.map { case (n, c) => s"""<meta name="$n" content="$c">""" }.mkString("")

    Source.fromFile("public/ui/index.html").mkString
      .replace("csrf-token-value=\"\"", s"csrf-token-value='$value'")
      .replace("</head>", s"$metaTags</head>")
  }
}
