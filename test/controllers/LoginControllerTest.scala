package controllers

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import models.{HobbyRepository, User, UserRepository}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Configuration, Environment}
import play.api.data.Form
import play.api.i18n.{DefaultLangs, DefaultMessagesApi, MessagesApi}
import play.api.mvc.{Action, EssentialAction, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val mockUserRepository: UserRepository = mock[UserRepository]
  val mockAllForms: AllForms = mock[AllForms]
  val messages: MessagesApi = mock[MessagesApi]
  val allFormsObj: AllForms = new AllForms
  val login: Login = Login("akshansh@knoldus.com", "akshansh123")
  val loginForm: Form[Login] = allFormsObj.loginForm.fill(login)
  val config: Configuration = Configuration(ConfigFactory.load("application.conf"))
  val defaultMessages: DefaultMessagesApi = new DefaultMessagesApi(Environment.simple(), config, new DefaultLangs(config))
  val loginController: LoginController = new LoginController(mockUserRepository, mockAllForms, defaultMessages)

  "LoginController" should {

    "be able to render the login page" in {

      when(mockAllForms.loginForm).thenReturn(loginForm)

      val result = loginController.login.apply(FakeRequest(GET, "/"))

      status(result) mustEqual OK
    }

    "not be able to find the user in database" in {

      when(mockAllForms.loginForm).thenReturn(loginForm)

      when(mockUserRepository.checkIfUserExists("akshansh@knoldus.com", "akshansh123098")).thenReturn(Future(false))

      val result = call(loginController.loginPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123098"
      ))

      redirectLocation(result) mustBe Some("/login")
    }

    "not be able to get details fo user" in {

      when(mockAllForms.loginForm).thenReturn(loginForm)

      when(mockUserRepository.checkIfUserExists("akshansh@knoldus.com", "akshansh123")).thenReturn(Future(true))

      when(mockUserRepository.getUserInfoForSession("akshansh@knoldus.com")).thenReturn(Future(Nil))

      val result = call(loginController.loginPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123"
      ))

      redirectLocation(result) mustBe Some("/login")
    }

    "not be able to login disabled user" in {

      when(mockAllForms.loginForm).thenReturn(loginForm)

      when(mockUserRepository.checkIfUserExists("akshansh@knoldus.com", "akshansh123")).thenReturn(Future(true))

      when(mockUserRepository.getUserInfoForSession("akshansh@knoldus.com")).thenReturn(Future(List((0,true,false))))

      val result = call(loginController.loginPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123"
      ))

      redirectLocation(result) mustBe Some("/login")
    }

    "be able to login user as normal user" in {

      when(mockAllForms.loginForm).thenReturn(loginForm)

      when(mockUserRepository.checkIfUserExists("akshansh@knoldus.com", "akshansh123")).thenReturn(Future(true))

      when(mockUserRepository.getUserInfoForSession("akshansh@knoldus.com")).thenReturn(Future(List((0,false,true))))

      val result = call(loginController.loginPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123"
      ))

      redirectLocation(result) mustBe Some("/showprofile")
    }

    "be able to login user as an admin" in {

      when(mockAllForms.loginForm).thenReturn(loginForm)

      when(mockUserRepository.checkIfUserExists("akshansh@knoldus.com", "akshansh123")).thenReturn(Future(true))

      when(mockUserRepository.getUserInfoForSession("akshansh@knoldus.com")).thenReturn(Future(List((0,true,true))))

      val result = call(loginController.loginPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123"
      ))

      redirectLocation(result) mustBe Some("/showprofile")
    }

    "be able to go in formWithErrors" in {

      when(mockAllForms.loginForm).thenReturn(loginForm)

      //when(mockUserRepository.checkIfUserExists("akshansh@knoldus.com", "akshansh123")).thenReturn(Future(true))

      val result = call(loginController.loginPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "email" -> "akshansh@knoldus.com", "password" -> ""
      ))

      status(result) mustEqual BAD_REQUEST
    }

    "be able to logout the user" in {

      val result = call(loginController.logout, FakeRequest(GET, "/"))

      redirectLocation(result) mustBe Some("/")
    }

  }
}
