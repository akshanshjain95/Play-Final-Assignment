package controllers

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import models.UserRepository
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Mockito
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Configuration, Environment}
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.Mockito._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnableDisableControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val mockUserRepository: UserRepository = mock[UserRepository]
  val config: Configuration = Configuration(ConfigFactory.load("application.conf"))
  val defaultMessages: DefaultMessagesApi = new DefaultMessagesApi(Environment.simple(), config, new DefaultLangs(config))
  val enableDisableController = new EnableDisableController(mockUserRepository, defaultMessages)

  "EnableDisableController" should {

    "Redirect to login page if value of isAdmin is not found" in {

      val result = call(enableDisableController.showUserList, FakeRequest(GET, "/"))

      redirectLocation(result) mustBe Some("/login")
    }

    "Show not authorised page when user is not an admin" in {

      val result = call(enableDisableController.showUserList, FakeRequest(GET, "/").withSession("isAdmin" -> "false"))

      status(result) mustBe NOT_FOUND
    }

    "Redirect to login page if user id is not found" in {

      val result = call(enableDisableController.showUserList, FakeRequest(GET, "/").withSession("isAdmin" -> "true"))

      redirectLocation(result) mustBe Some("/login")
    }

    "Render the view users page" in {

      when(mockUserRepository.getAllUsersWithStatus(1)).thenReturn(Future(Map(
        "akshansh@knoldus.com" -> true
      )))

      val result = call(enableDisableController.showUserList, FakeRequest(GET, "/").withSession("isAdmin" -> "true", "userID" -> "1"))

      status(result) mustBe OK
    }

    "Successfully enable the user" in {

      when(mockUserRepository.enableUser("akshansh@knoldus.com", true)).thenReturn(Future(true))

      val result = call(enableDisableController
        .enableDisablePost("akshansh@knoldus.com"), FakeRequest(POST, "/")
        .withFormUrlEncodedBody("ableButton" -> "enable"))

      redirectLocation(result) mustBe Some("/enabledisable")
    }

    "not be able to enable the user" in {

      when(mockUserRepository.enableUser("akshansh@knoldus.com", true)).thenReturn(Future(false))

      val result = call(enableDisableController
        .enableDisablePost("akshansh@knoldus.com"), FakeRequest(POST, "/")
        .withFormUrlEncodedBody("ableButton" -> "enable"))

      redirectLocation(result) mustBe Some("/enabledisable")
    }

    "Successfully disable the user" in {

      when(mockUserRepository.enableUser("akshansh@knoldus.com", false)).thenReturn(Future(true))

      val result = call(enableDisableController
        .enableDisablePost("akshansh@knoldus.com"), FakeRequest(POST, "/")
        .withFormUrlEncodedBody("ableButton" -> "disable"))

      redirectLocation(result) mustBe Some("/enabledisable")
    }

    "not be able to disable the user" in {

      when(mockUserRepository.enableUser("akshansh@knoldus.com", false)).thenReturn(Future(false))

      val result = call(enableDisableController
        .enableDisablePost("akshansh@knoldus.com"), FakeRequest(POST, "/")
        .withFormUrlEncodedBody("ableButton" -> "disable"))

      redirectLocation(result) mustBe Some("/enabledisable")
    }

  }
}
