package controllers

import com.google.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, Controller, _}

class LoginController @Inject()(implicit val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val allForms: AllForms = new AllForms
  val loginForm: Form[Login] = allForms.loginForm

  def login: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(loginForm))
  }

  def loginPost: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      studentData => {
        Ok(views.html.showDataOfLogin(studentData))
      })
  }

}
