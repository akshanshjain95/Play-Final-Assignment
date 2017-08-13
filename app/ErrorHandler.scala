import javax.inject.Singleton
import play.api.http.HttpErrorHandler
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import scala.concurrent.Future

@Singleton
class ErrorHandler extends HttpErrorHandler {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {

    statusCode match {

      case 404 => Future.successful(Status(statusCode)("Page not found!"))
      case 400 => Future.successful(Status(statusCode)("Bad Request1"))
      case 403 => Future.successful(Status(statusCode)("Forbidden area!"))
      case 407 => Future.successful(Status(statusCode)("Proxy Authentication required!"))
      case _ => Future.successful(Status(statusCode)("Something went wrong!"))
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {

    Future.successful(
      InternalServerError("An internal server error occured " + exception.getMessage)
    )
  }

}
