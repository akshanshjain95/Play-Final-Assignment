package controllers

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import models._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Configuration, Environment}
import play.api.data.Form
import play.api.i18n.{DefaultLangs, DefaultMessagesApi, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SignUpControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val mockUserRepository: UserRepository = mock[UserRepository]
  val allFormsObj: AllForms = new AllForms
  val name: Name = Name("Akshansh", None, "Jain")
  val hobbies: List[Hobby] = List(Hobby(1, "Programming"), Hobby(2, "Sports"))
  val user: SignUp = SignUp(name, 9999819877L, "akshansh@knoldus.com", "akshansh123", "akshansh123", "male", 21, hobbies)
  val userForm: Form[SignUp] = allFormsObj.signUpForm
  val mockAllForms: AllForms = mock[AllForms]
  val allForms: AllForms = new AllForms
  val messages: MessagesApi = mock[MessagesApi]
  val mockHobbyRepository: HobbyRepository = mock[HobbyRepository]
  val mockUserHobbyRepository: UserHobbyRepository = mock[UserHobbyRepository]
  val hobbyList: Future[List[String]] = Future.successful(List("Programming", "Reading", "Sports", "Writing", "Swimming"))
  val config: Configuration = Configuration(ConfigFactory.load("application.conf"))
  val defaultMessages: DefaultMessagesApi = new DefaultMessagesApi(Environment.simple(), config, new DefaultLangs(config))
  val signUpController: SignUpController = new SignUpController(mockUserRepository, mockHobbyRepository, mockUserHobbyRepository, mockAllForms, defaultMessages)

  "SignUpController" should{

    "show the sign up page" in {

      when(mockHobbyRepository.getHobbies).thenReturn(hobbyList)

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUp, FakeRequest(GET, "/signup"))

      status(result) mustEqual OK
    }

    "not be able to retrieve user ID from database" in {

      when(mockUserRepository.checkEmail("akshansh@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.addUser(ArgumentMatchers.any(classOf[User]))).thenReturn(Future(true))

      when(mockHobbyRepository.getHobbyIDs(hobbies)).thenReturn(Future(List(List(1,3))))

      when(mockUserRepository.getUserID("akshansh@knoldus.com")).thenReturn(Future(Nil))

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUpPost,FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming", "hobbies[1]" -> "Sports")
      )

      redirectLocation(result) mustBe Some("/signup")
    }

    "not be able to add user hobby in database" in {

      when(mockUserRepository.checkEmail("akshansh@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.addUser(ArgumentMatchers.any(classOf[User]))).thenReturn(Future(true))

      when(mockHobbyRepository.getHobbyIDs(hobbies)).thenReturn(Future(List(List(1,3))))

      when(mockUserRepository.getUserID("akshansh@knoldus.com")).thenReturn(Future(List(1)))

      when(mockUserHobbyRepository.addUserHobby(1, List(1,3))).thenReturn(Future(false))

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUpPost,FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming", "hobbies[1]" -> "Sports")
      )

      redirectLocation(result) mustBe Some("/signup")
    }

    "be able to add user hobby in database" in {

      when(mockUserRepository.checkEmail("akshansh@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.addUser(ArgumentMatchers.any(classOf[User]))).thenReturn(Future(true))

      when(mockHobbyRepository.getHobbyIDs(hobbies)).thenReturn(Future(List(List(1,3))))

      when(mockUserRepository.getUserID("akshansh@knoldus.com")).thenReturn(Future(List(1)))

      when(mockUserHobbyRepository.addUserHobby(1, List(1,3))).thenReturn(Future(true))

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUpPost,FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming", "hobbies[1]" -> "Sports")
      )

      redirectLocation(result) mustBe Some("/showprofile")
    }

    "not be able to add the user in database" in {

      when(mockUserRepository.checkEmail("akshansh@knoldus.com")).thenReturn(Future(true))

      when(mockUserRepository.addUser(ArgumentMatchers.any(classOf[User]))).thenReturn(Future(false))

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUpPost,FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming")
      )

      redirectLocation(result) mustBe Some("/signup")
    }

    "not be able to find the email in database" in {

      when(mockUserRepository.checkEmail("akshansh@knoldus.com")).thenReturn(Future(false))

      when(mockAllForms.signUpForm).thenReturn(userForm)

      val result = call(signUpController.signUpPost, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming")
      )

      redirectLocation(result) mustBe Some("/signup")
    }

    "receive form with errors" in {

      when(mockAllForms.signUpForm).thenReturn(userForm)

      when(mockHobbyRepository.getHobbies).thenReturn(hobbyList)

      val result = call(signUpController.signUpPost, FakeRequest(POST, "/signuppost").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" -> "Jain", "mobileNo" -> "9999819877",
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh12", "repassword" -> "akshansh123",
        "gender" -> "male", "age" -> "21", "hobbies[0]" -> "Programming", "hobbies[1]" -> "Sports")
      )

      status(result) mustEqual BAD_REQUEST
    }
  }
}
