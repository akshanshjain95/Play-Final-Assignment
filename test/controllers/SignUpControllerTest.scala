package controllers

import models.UserRepository
import org.scalatest.{AsyncFunSuite, FunSuite}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatestplus.play.PlaySpec
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.mvc.Result._
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/*firstName: String, middleName: Option[String], lastName: String,
mobileNo: Long, username: String, password: String, repassword: String,
gender: String, age: Int*/

class SignUpControllerTest extends PlaySpec with MockitoSugar {

  val mockUserRepository: UserRepository = mock[UserRepository]
  val allFormsObj = new AllForms
  val user = SignUp("Akshansh", None, "Jain", 9999819877L, "Akshansh95", "akshansh123", "akshansh123", "male", 21)
  val userForm: Form[SignUp] = allFormsObj.signUpForm.fill(user)
  val mockAllForms: AllForms = mock[AllForms]
  val allForms = new AllForms
  val messages: MessagesApi = mock[MessagesApi]
  val signUpController = new SignUpController(mockUserRepository, mockAllForms, messages)


  "SignUpController" should{

    "be able to register the user account" in {

      when(mockUserRepository.checkUsername("Akshansh95")).thenReturn(Future(false))

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = signUpController.signUpPost.apply(FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "firstName" -> "Akshansh", "middleName" -> "None", "lastName" -> "Jain", "mobileNo" -> "9999819877",
        "username" -> "Akshansh95", "password" -> "akshansh123", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21")
      )

      redirectLocation(result) mustBe Some("/signup")
    }
  }
}
