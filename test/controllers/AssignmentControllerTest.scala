package controllers

import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import models.{Assignment, AssignmentRepository}
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Configuration, Environment}
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import org.mockito.Mockito._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.mockito.Matchers._

class AssignmentControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite {

  implicit lazy val materializer: Materializer = app.materializer
  val config: Configuration = Configuration(ConfigFactory.load("application.conf"))
  val defaultMessages: DefaultMessagesApi = new DefaultMessagesApi(Environment.simple(), config, new DefaultLangs(config))
  val mockAssignmentRepository = mock[AssignmentRepository]
  val mockAllForms = mock[AllForms]
  val allFormsObj = new AllForms
  val addAssignmentForm = allFormsObj.addAssignmentForm
  val assignmentController = new AssignmentController(mockAssignmentRepository, mockAllForms, defaultMessages)

  "AssignmentController" should {

    "render the view assignment page" in {

      when(mockAssignmentRepository.getAllAssignments).thenReturn(Future(List(Assignment(1, "title", "description"))))

      val result = call(assignmentController.showAssignments, FakeRequest(GET, "/").withSession("isAdmin" -> "true"))

      status(result) mustEqual OK
    }

    "render the add assignment page" in {

      when(mockAssignmentRepository.getAllAssignments).thenReturn(Future(List(Assignment(1, "title", "description"))))

      when(mockAllForms.addAssignmentForm).thenReturn(addAssignmentForm)

      val result = call(assignmentController.addAssignment, FakeRequest(GET, "/").withSession("isAdmin" -> "true"))

      status(result) mustEqual OK
    }

    "render the not authorized page" in {

      when(mockAssignmentRepository.getAllAssignments).thenReturn(Future(List(Assignment(1, "title", "description"))))

      val result = call(assignmentController.addAssignment, FakeRequest(GET, "/").withSession("isAdmin" -> "false"))

      status(result) mustEqual NOT_FOUND
    }

    "Redirect to login page when value of isAdmin is not found" in {

      when(mockAssignmentRepository.getAllAssignments).thenReturn(Future(List(Assignment(1, "title", "description"))))

      val result = call(assignmentController.addAssignment, FakeRequest(GET, "/"))

      redirectLocation(result) mustBe Some("/login")
    }

    "Not delete the assignment" in {

      when(mockAssignmentRepository.deleteAssignment(1)).thenReturn(Future(false))

      val result = call(assignmentController.deleteAssignment(1), FakeRequest(POST, "/"))

      redirectLocation(result) mustBe Some("/addassignment")
    }

    "Delete the assignment" in {

      when(mockAssignmentRepository.deleteAssignment(1)).thenReturn(Future(true))

      val result = call(assignmentController.deleteAssignment(1), FakeRequest(POST, "/"))

      redirectLocation(result) mustBe Some("/addassignment")
    }

    "be able to handle form with errors" in {

      when(mockAssignmentRepository.getAllAssignments).thenReturn(Future(List(Assignment(1, "title", "description"))))

      when(mockAllForms.addAssignmentForm).thenReturn(addAssignmentForm)

      val result = call(assignmentController.addAssignmentPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "title" -> "", "description" -> ""
      ))

      status(result) mustEqual BAD_REQUEST
    }

    "not be able to add assignment" in {

      when(mockAssignmentRepository.getAllAssignments).thenReturn(Future(List(Assignment(1, "title", "description"))))

      when(mockAllForms.addAssignmentForm).thenReturn(addAssignmentForm)

      when(mockAssignmentRepository.addAssignment(any(classOf[Assignment]))).thenReturn(Future(false))

      val result = call(assignmentController.addAssignmentPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "title" -> "title", "description" -> "description"
      ))

      redirectLocation(result) mustBe Some("/addassignment")
    }

    "be able to add assignment" in {

      when(mockAssignmentRepository.getAllAssignments).thenReturn(Future(List(Assignment(1, "title", "description"))))

      when(mockAllForms.addAssignmentForm).thenReturn(addAssignmentForm)

      when(mockAssignmentRepository.addAssignment(any(classOf[Assignment]))).thenReturn(Future(true))

      val result = call(assignmentController.addAssignmentPost, FakeRequest(POST, "/").withFormUrlEncodedBody(
        "title" -> "title", "description" -> "description"
      ))

      redirectLocation(result) mustBe Some("/addassignment")
    }

  }

}
