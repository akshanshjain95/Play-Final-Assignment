package views

import controllers.AllForms
import models.Hobby
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages

class SignUpTest extends PlaySpec with MockitoSugar {

  val hobbies: List[Hobby] = List(Hobby(1, "Programming"), Hobby(2, "Sports"))
  val allFormsObj = new AllForms
  val signUpForm = allFormsObj.signUpForm

  "signUp template" should {

    "render sign up page" in {

      val mockMessage = mock[Messages]
      val signUpPage = views.html.signUp.render(signUpForm, hobbies, mockMessage)

      signUpPage.toString.contains("Signup Form!") mustEqual true
    }
  }
}
