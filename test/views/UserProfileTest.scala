package views

import controllers.AllForms
import models.{Assignment, Hobby}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class UserProfileTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new AllForms
  val updateUserForm = allFormsObj.updateUserForm
  val hobbies: List[Hobby] = List(Hobby(1, "Programming"), Hobby(2, "Sports"))

  "User profile template" should {

    "render user profile page" in {

      val mockMessages = mock[Messages]
      val userProfilePage = views.html.userProfile.render(updateUserForm, hobbies, Some("true"), mockMessages, Flash(Map()))

      userProfilePage.toString.contains("SignUp information") mustEqual true
    }
  }
}
