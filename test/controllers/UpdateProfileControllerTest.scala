package controllers

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import models._
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
import org.mockito.Matchers._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UpdateProfileControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val hobbies: List[Hobby] = List(Hobby(1, "Programming"), Hobby(2, "Sports"))
  val mockUserRepository: UserRepository = mock[UserRepository]
  val mockAllForms: AllForms = mock[AllForms]
  val allFormsObj = new AllForms
  val updateUserForm: Form[UpdateUserForm] = allFormsObj.updateUserForm
  val updatePasswordForm: Form[UpdatePassword] = allFormsObj.updatePasswordForm
  val mockHobbyRepository: HobbyRepository = mock[HobbyRepository]
  val mockUserHobbyRepository: UserHobbyRepository = mock[UserHobbyRepository]
  val config: Configuration = Configuration(ConfigFactory.load("application.conf"))
  val defaultMessages: DefaultMessagesApi = new DefaultMessagesApi(Environment.simple(), config, new DefaultLangs(config))
  val updateProfileController = new UpdateProfileController(mockUserRepository, mockHobbyRepository, mockUserHobbyRepository, mockAllForms, defaultMessages)
  val user = User(100, "Akshansh", None, "Jain", 9999819877L, "akshansh@knoldus.com", "akshansh123", "male", 21, false, true)

  "UpdateProfileController" should {

    "Redirect to Login page when user ID is not found" in {

      val result = call(updateProfileController.showProfile, FakeRequest(GET, "/"))

      redirectLocation(result) mustBe Some("/login")
    }

    "Redirect to Login page when user is not found in Database" in {

      when(mockUserRepository.getUserByID(1)).thenReturn(Future(Nil))

      val result = call(updateProfileController.showProfile, FakeRequest(GET, "/").withSession("userID" -> "1", "isAdmin" -> "false"))

      redirectLocation(result) mustBe Some("/login")
    }

    "Redirect to Login page if no hobbies are received" in {

      when(mockUserRepository.getUserByID(1)).thenReturn(Future(List(user)))

      when(mockUserHobbyRepository.getUserHobby(1)).thenReturn(Future(Nil))

      val result = call(updateProfileController.showProfile, FakeRequest(GET, "/").withSession("userID" -> "1", "isAdmin" -> "false"))

      redirectLocation(result) mustBe Some("/login")
    }

    "Render profile page of the user" in {

      when(mockUserRepository.getUserByID(1)).thenReturn(Future(List(user)))

      when(mockUserHobbyRepository.getUserHobby(1)).thenReturn(Future(List(1,3)))

      when(mockHobbyRepository.getHobbies).thenReturn(Future(hobbies))

      when(mockAllForms.updateUserForm).thenReturn(updateUserForm)

      val result = call(updateProfileController.showProfile, FakeRequest(GET, "/").withSession("userID" -> "1", "isAdmin" -> "false"))

      status(result) mustEqual OK
    }

    "Should show errors when wrong form is submitted" in {

      when(mockAllForms.updateUserForm).thenReturn(updateUserForm)

      when(mockHobbyRepository.getHobbies).thenReturn(Future(hobbies))

      val result = call(updateProfileController.showProfilePost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" ->"Jain",
        "mobileNo" -> "999981987", "gender" -> "male", "age" -> "21", "hobbies[0]" -> "1",
        "hobbies[1]" -> "3"
      ))

      status(result) mustEqual BAD_REQUEST
    }

    "Redirect to login page if user ID is not found" in {

      when(mockAllForms.updateUserForm).thenReturn(updateUserForm)

      when(mockHobbyRepository.getHobbies).thenReturn(Future(hobbies))

      val result = call(updateProfileController.showProfilePost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" ->"Jain",
        "mobileNo" -> "9999819877", "gender" -> "male", "age" -> "21", "hobbies[0]" -> "1",
        "hobbies[1]" -> "3"
      ))

      redirectLocation(result) mustBe Some("/login")
    }

    "Redirect to show profile page if user updation fails" in {

      when(mockAllForms.updateUserForm).thenReturn(updateUserForm)

      when(mockHobbyRepository.getHobbies).thenReturn(Future(hobbies))

      when(mockUserRepository.updateUser(any(classOf[UpdateUserForm]), any[Int])).thenReturn(Future(false))

      val result = call(updateProfileController.showProfilePost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" ->"Jain",
        "mobileNo" -> "9999819877", "gender" -> "male", "age" -> "21", "hobbies[0]" -> "1",
        "hobbies[1]" -> "3"
      ).withSession("userID" -> "1"))

      redirectLocation(result) mustBe Some("/showprofile")
    }

    "Redirect to show profile page if user hobby deletion fails" in {

      when(mockAllForms.updateUserForm).thenReturn(updateUserForm)

      when(mockHobbyRepository.getHobbies).thenReturn(Future(hobbies))

      when(mockUserRepository.updateUser(any(classOf[UpdateUserForm]), any[Int])).thenReturn(Future(false))

      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(false))

      val result = call(updateProfileController.showProfilePost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" ->"Jain",
        "mobileNo" -> "9999819877", "gender" -> "male", "age" -> "21", "hobbies[0]" -> "1",
        "hobbies[1]" -> "3"
      ).withSession("userID" -> "1"))

      redirectLocation(result) mustBe Some("/showprofile")
    }

    "Redirect to show profile page if user profile updation is successful" in {

      when(mockAllForms.updateUserForm).thenReturn(updateUserForm)

      when(mockHobbyRepository.getHobbies).thenReturn(Future(hobbies))

      when(mockUserRepository.updateUser(any(classOf[UpdateUserForm]), any[Int])).thenReturn(Future(false))

      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(true))

      when(mockUserHobbyRepository.addUserHobby(1, List(1,3))).thenReturn(Future(true))

      val result = call(updateProfileController.showProfilePost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" ->"Jain",
        "mobileNo" -> "9999819877", "gender" -> "male", "age" -> "21", "hobbies[0]" -> "1",
        "hobbies[1]" -> "3"
      ).withSession("userID" -> "1"))

      redirectLocation(result) mustBe Some("/showprofile")
    }

    "Redirect to show profile page if user profile updation is unsuccessful" in {

      when(mockAllForms.updateUserForm).thenReturn(updateUserForm)

      when(mockHobbyRepository.getHobbies).thenReturn(Future(hobbies))

      when(mockUserRepository.updateUser(any(classOf[UpdateUserForm]), any[Int])).thenReturn(Future(false))

      when(mockUserHobbyRepository.deleteUserHobby(1)).thenReturn(Future(true))

      when(mockUserHobbyRepository.addUserHobby(1, List(1,3))).thenReturn(Future(false))

      val result = call(updateProfileController.showProfilePost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "name.firstName" -> "Akshansh", "name.middleName" -> "", "name.lastName" ->"Jain",
        "mobileNo" -> "9999819877", "gender" -> "male", "age" -> "21", "hobbies[0]" -> "1",
        "hobbies[1]" -> "3"
      ).withSession("userID" -> "1"))

      redirectLocation(result) mustBe Some("/showprofile")
    }

    "Render the update Password page" in {

      when(mockAllForms.updatePasswordForm).thenReturn(updatePasswordForm)

      val result = call(updateProfileController.updatePassword, FakeRequest(GET, "/"))

      status(result) mustEqual OK
    }

    "Handle wrong form for updatePasswordPost method" in {

      when(mockAllForms.updatePasswordForm).thenReturn(updatePasswordForm)

      val result = call(updateProfileController.updatePasswordPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "email" -> "akshansh", "password" -> "akshansh123", "repassword" -> "akshansh123"
      ))

      status(result) mustEqual BAD_REQUEST
    }

    "Not update password for invalid email" in {

      when(mockAllForms.updatePasswordForm).thenReturn(updatePasswordForm)

      when(mockUserRepository.updateUserByEmail(any(classOf[UpdatePassword]))).thenReturn(Future(false))

      val result = call(updateProfileController.updatePasswordPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123", "repassword" -> "akshansh123"
      ))

      redirectLocation(result) mustBe Some("/updatepassword")
    }

    "Update password for valid email" in {

      when(mockAllForms.updatePasswordForm).thenReturn(updatePasswordForm)

      when(mockUserRepository.updateUserByEmail(any(classOf[UpdatePassword]))).thenReturn(Future(true))

      val result = call(updateProfileController.updatePasswordPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "email" -> "akshansh@knoldus.com", "password" -> "akshansh123", "repassword" -> "akshansh123"
      ))

      redirectLocation(result) mustBe Some("/login")
    }

  }
}
