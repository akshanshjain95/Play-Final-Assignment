package views

import org.scalatestplus.play.PlaySpec

class IndexTest extends PlaySpec {

  "Index template" should {

    "Render welcome page" in {

      val indexPage = views.html.index.render()

      indexPage.toString.contains("Welcome to Knoldus!") mustEqual true
    }
  }
}
