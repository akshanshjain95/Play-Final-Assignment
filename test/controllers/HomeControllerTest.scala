package controllers

import akka.stream.Materializer
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._

class HomeControllerTest extends PlaySpec with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val homeController = new HomeController

  "HomeController" should {

    "render the Welcome page" in {

      val result = call(homeController.index(), FakeRequest(GET, "/"))

      status(result) mustBe OK
    }
  }
}
