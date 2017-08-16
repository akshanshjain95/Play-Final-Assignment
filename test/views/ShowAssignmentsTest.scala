package views

import models.Assignment
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages

class ShowAssignmentsTest extends PlaySpec with MockitoSugar {

  val assignment = List(Assignment(1, "title", "description"))

  "Show Assignments template" should {

    "render all the assignments in Database" in {

      val mockMessages = mock[Messages]
      val showAssignmentsPage = views.html.showAssignments.render(assignment, Some("true"), mockMessages)

      showAssignmentsPage.toString.contains("Assignments") mustEqual true
    }
  }
}
