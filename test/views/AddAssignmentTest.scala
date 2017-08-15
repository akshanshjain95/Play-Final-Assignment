package views

import controllers.AllForms
import models.Assignment
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages

class AddAssignmentTest extends PlaySpec with MockitoSugar {

  val allFormsObj = new AllForms
  val addAssignmentForm = allFormsObj.addAssignmentForm
  val assignment = List(Assignment(1, "title", "description"))

  "addAssignment template" should {

    "render addAssignment page" in {

      val mockMesssages = mock[Messages]
      val addAssignmentPage = views.html.addAssignment.render(addAssignmentForm, assignment, Some("true"), mockMesssages)

      addAssignmentPage.toString.contains("Add Assignments") mustEqual true
    }
  }
}
