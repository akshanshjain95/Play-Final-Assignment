package views

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.twirl.api.Html

class MainTest extends PlaySpec with MockitoSugar {

  "main template" should {

    "render main page" in {

      val mainPage = views.html.main.render("title", Html.apply("<h1>content</h1>"))

      mainPage.toString.contains("content") mustEqual true
    }
  }
}
