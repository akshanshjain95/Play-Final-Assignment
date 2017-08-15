package models

import org.scalatestplus.play.PlaySpec

class AssignmentRepositoryTest extends PlaySpec {

  val assignmentRepo = new ModelsTest[AssignmentRepository]

  val assignment = Assignment(1, "title", "description")

  "Assignment Repository" should {

    "add assignment in database" in {

      val result = assignmentRepo.result(assignmentRepo.repository.addAssignment(assignment))

      result mustEqual true
    }

    "return all assignments in database" in {

      val result = assignmentRepo.result(assignmentRepo.repository.getAllAssignments)

      result mustEqual List(assignment)
    }

    "delete assignment in database" in {

      val result = assignmentRepo.result(assignmentRepo.repository.deleteAssignment(1))

      result mustEqual true
    }
  }
}
