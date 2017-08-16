package views

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class EnableDisableTest extends PlaySpec with MockitoSugar {

  "EnableDisable template" should {

    "render enableDisable page" in {

      val mockMessage = mock[Messages]
      val enableDisablePage = views.html.enableDisable.render(Map(), mockMessage, Flash(Map("success" -> "Success")))

      enableDisablePage.toString.contains("Showing All user emails") mustEqual true
    }
  }
}
