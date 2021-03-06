package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class HomeController extends Controller {

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

}
