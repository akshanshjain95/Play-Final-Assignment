import javax.inject.Singleton
import play.api.http.HttpErrorHandler
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import scala.concurrent.Future

@Singleton
class ErrorHandler extends HttpErrorHandler {

  val NOT_FOUND = 404
  val BAD_REQUEST = 400
  val FORBIDDEN = 403
  val PROXY_AUTHENTICATION_REQUIRED = 407

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {

    statusCode match {

      case NOT_FOUND => Future.successful(Status(statusCode)("Page not found!"))
      case BAD_REQUEST => Future.successful(Status(statusCode)("Bad Request!"))
      case FORBIDDEN => Future.successful(Status(statusCode)("Forbidden area!"))
      case PROXY_AUTHENTICATION_REQUIRED => Future.successful(Status(statusCode)("Proxy Authentication required!"))
      case _ => Future.successful(Status(statusCode)("Something went wrong!"))
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {

    Future.successful(
      InternalServerError("An internal server error occured " + exception.getMessage)
    )
  }

}
