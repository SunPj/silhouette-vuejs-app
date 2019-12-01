package models.services

import javax.inject.Inject
import play.api.mvc.RequestHeader
import play.filters.csrf.CSRF
import play.filters.csrf.CSRF.Token

import scala.io.Source

class IndexRenderService @Inject() () {

  def render(title: Option[String] = None, meta: Seq[(String, String)] = Seq.empty)(implicit request: RequestHeader): String = {
    val metaTags = title.map(t => s"<title>$t</title>").getOrElse("") +
      meta.map { case (n, c) => s"""<meta name="$n" content="$c">""" }.mkString("")

    val html = Source.fromFile("public/ui/index.html").mkString
    setCsrfToken(html).replace("</head>", s"$metaTags</head>")
  }

  def setCsrfToken(html: String)(implicit request: RequestHeader): String = {
    val Token(_, value) = CSRF.getToken.get

    html.replace("csrf-token-value=\"\"", s"csrf-token-value='$value'")
  }
}
