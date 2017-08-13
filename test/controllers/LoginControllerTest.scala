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
import play.api.mvc.{Action, EssentialAction, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val mockUserRepository = mock[UserRepository]
  val mockAllForms = mock[AllForms]
  val messages: MessagesApi = mock[MessagesApi]
  val loginController = new LoginController(mockUserRepository, mockAllForms, messages)
  val allFormsObj = new AllForms
  val login = Login("Akshansh95", "akshansh123")
  val loginForm: Form[Login] = allFormsObj.loginForm.fill(login)

  "LoginController" should {

    "be able to render the login page" in {

      when(mockAllForms.loginForm).thenReturn(loginForm)

      val result = loginController.login.apply(FakeRequest(GET, "/"))

      status(result) mustEqual OK
    }

    "not be able to find the user in database" in {

      when(mockAllForms.loginForm).thenReturn(loginForm)

      when(mockUserRepository.checkIfUserExists("Akshansh95", "akshansh123")).thenReturn(Future(false))

      val result = loginController.loginPost.apply(FakeRequest(POST, "/").withFormUrlEncodedBody(
        "username" -> "Akshansh95", "password" -> "akshansh123"
      ))

      redirectLocation(result) mustBe Some("/login")
    }

    "be able to login the user" in {

      when(mockAllForms.loginForm).thenReturn(loginForm)

      when(mockUserRepository.checkIfUserExists("Akshansh95", "akshansh123")).thenReturn(Future(true))

      val result = call(loginController.loginPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "username" -> "Akshansh95", "password" -> "akshansh123"
      ))

      status(result) mustEqual OK
    }

  }
}
