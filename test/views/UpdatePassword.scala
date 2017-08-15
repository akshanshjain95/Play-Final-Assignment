package views

import controllers.AllForms
import models.Assignment
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.mvc.Flash

class UpdatePassword extends PlaySpec with MockitoSugar {

  val allFormsObj = new AllForms
  val updatePasswordForm = allFormsObj.updatePasswordForm

  "Update Password template" should {

    "render update password page" in {

      val mockMessages = mock[Messages]
      val updatePasswordPage = views.html.updatePassword.render(updatePasswordForm, mockMessages, Flash(Map()))

      updatePasswordPage.toString.contains("Update password!") mustEqual true
    }
  }
}
