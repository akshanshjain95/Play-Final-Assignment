package controllers

import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class HomeController extends Controller {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

}
