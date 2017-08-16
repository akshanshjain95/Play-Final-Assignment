package views

import controllers.AllForms
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class LoginTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new AllForms
  val loginForm = allFormsObj.loginForm

  "Login template" should {

    "render the login page" in {

      val mockMessages = mock[Messages]
      val loginPage = views.html.login.render(loginForm, mockMessages, Flash(Map()))

      loginPage.toString.contains("Login Form!") mustEqual true
    }
  }
}
