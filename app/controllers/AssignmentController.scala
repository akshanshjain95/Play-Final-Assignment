package controllers

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import models._
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

class AssignmentController @Inject()(userRepository: UserRepository, hobbyRepository: HobbyRepository,
                                     assignmentRepository: AssignmentRepository, userHobbyRepository: UserHobbyRepository,
                                     allForms: AllForms, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def showAssignments: Action[AnyContent] = Action.async { implicit request =>

    val isAdmin: Option[String] = request.session.get("isAdmin")
    assignmentRepository.getAllAssignments.map {
      assignments => Ok(views.html.showAssignments(assignments, isAdmin))
    }
  }

  def addAssignment: Action[AnyContent] = Action.async { implicit request =>

    val isAdmin: Option[String] = request.session.get("isAdmin")
    assignmentRepository.getAllAssignments.map {
      assignments => Ok(views.html.addAssignment(allForms.addAssignmentForm, assignments, isAdmin))
    }
  }

  def deleteAssignment(assignmentID: Int): Action[AnyContent] = Action.async { implicit request =>
    val isAdmin = request.session.get("isAdmin")
    assignmentRepository.deleteAssignment(assignmentID).map {
      case true => Redirect(routes.AssignmentController.addAssignment).flashing("success" -> "Assignment was deleted successfully")
      case false => Redirect(routes.AssignmentController.addAssignment).flashing("error" -> "Something went wrong. Assignment was not deleted.")
    }
  }

  def addAssignmentPost: Action[AnyContent] = Action.async { implicit request =>
    val isAdmin: Option[String] = request.session.get("isAdmin")
    assignmentRepository.getAllAssignments.flatMap {
      assignments =>
        allForms.addAssignmentForm.bindFromRequest.fold(
          formWithErrors => {
            Logger.error("Form was submitted with errors " + formWithErrors)
            Future.successful(BadRequest(views.html.addAssignment(formWithErrors, assignments, isAdmin)))
          },
          addAssignmentData => {
            Logger.info("Form was submitted successfully")
            val assignment = Assignment(0, addAssignmentData.title, addAssignmentData.description)
            assignmentRepository.addAssignment(assignment).map {
              case true => Redirect(routes.AssignmentController.addAssignment).flashing("success" -> "Assignment was added successfully!")
              case false => Redirect(routes.AssignmentController.addAssignment).flashing("error" -> "Something went wrong. Please add the assignment again.")
            }
          }
        )
    }
  }

}
