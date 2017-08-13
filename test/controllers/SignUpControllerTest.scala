package controllers

import akka.stream.Materializer
import models.{HobbyRepository, User, UserRepository}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SignUpControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val mockUserRepository: UserRepository = mock[UserRepository]
  val allFormsObj = new AllForms
  val name = Name("Akshansh", None, "Jain")
  val hobbies = List("Programming", "Sports")
  val user = SignUp(name, 9999819877L, "akshansh@knoldus.com", "Akshansh95", "akshansh123", "akshansh123", "male", 21, hobbies)
  val userForm: Form[SignUp] = allFormsObj.signUpForm
  val mockAllForms: AllForms = mock[AllForms]
  val allForms = new AllForms
  val messages: MessagesApi = mock[MessagesApi]
  val mockHobbyRepository: HobbyRepository = mock[HobbyRepository]
  val hobbyList: Future[List[String]] = Future.successful(List("Programming", "Reading", "Sports", "Writing", "Swimming"))
  val signUpController = new SignUpController(mockUserRepository, mockHobbyRepository, mockAllForms, messages)


  "SignUpController" should{

    "show the sign up page" in {

      when(mockHobbyRepository.getHobbies).thenReturn(hobbyList)

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUp, FakeRequest(GET, "/signup"))

      status(result) mustEqual OK
    }

    "be able to add the user in database" in {

      when(mockUserRepository.checkEmail("akshansh@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.checkUsername("Akshansh95")).thenReturn(Future(true))

      when(mockUserRepository.addUser(ArgumentMatchers.any(classOf[User]))).thenReturn(Future(true))

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUpPost,FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "username" -> "Akshansh95", "password" -> "akshansh123", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming")
      )

      status(result) mustBe OK
    }

    "not be able to add the user in database" in {

      when(mockUserRepository.checkEmail("akshansh@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.checkUsername("Akshansh95")).thenReturn(Future(true))

      when(mockUserRepository.addUser(ArgumentMatchers.any(classOf[User]))).thenReturn(Future(false))

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUpPost,FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "username" -> "Akshansh95", "password" -> "akshansh123", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming")
      )

      redirectLocation(result) mustBe Some("/signup")
    }

    "not be able to find the email in database" in {

      when(mockUserRepository.checkEmail("akshansh@knoldus.com")).thenReturn(Future(false))

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUpPost, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "username" -> "Akshansh95", "password" -> "akshansh123", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming")
      )

      redirectLocation(result) mustBe Some("/signup")
    }

    "not be able to find the username in database" in {

      when(mockUserRepository.checkEmail("akshansh@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.checkUsername("Akshansh95")).thenReturn(Future(false))

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUpPost, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "username" -> "Akshansh95", "password" -> "akshansh123", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming")
      )

      redirectLocation(result) mustBe Some("/signup")
    }

    "receive form with errors" in {

      when(mockAllForms.signUpForm).thenReturn(userForm)

      when(mockHobbyRepository.getHobbies).thenReturn(hobbyList)

      val result = call(signUpController.signUpPost, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "username" -> "Akshansh95", "password" -> "akshansh12", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming", "hobbies[1]" -> "Sports")
      )

      status(result) mustEqual BAD_REQUEST
    }
  }
}
