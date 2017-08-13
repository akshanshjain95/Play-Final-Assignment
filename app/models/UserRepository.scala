package models

import com.google.inject.Inject
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import play.api.Logger
import scala.concurrent.Future

case class User(id: Int, firstName: String, middleName: Option[String], lastName: String,
                mobileNo: Long, email: String, username: String, password: String,
                gender: String, age: Int, isAdmin: Boolean, isEnabled: Boolean)

trait UserRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userQuery: TableQuery[UserTable] = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[User](tag, "usertable") {

    def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

    def firstName: Rep[String] = column[String]("firstname")

    def middleName: Rep[Option[String]] = column[Option[String]]("middlename")

    def lastName: Rep[String] = column[String]("lastname")

    def mobileNo: Rep[Long] = column[Long]("mobileno")

    def email: Rep[String] = column[String]("email")

    def username: Rep[String] = column[String]("username")

    def password: Rep[String] = column[String]("password")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def isAdmin: Rep[Boolean] = column[Boolean]("isadmin")

    def isEnabled: Rep[Boolean] = column[Boolean]("isenabled")

    def * : ProvenShape[User] = (id, firstName, middleName, lastName, mobileNo, email, username, password, gender, age, isAdmin, isEnabled) <> (User.tupled, User.unapply)

  }

}

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepositoryTable {

  import driver.api._

  def addUser(user: User): Future[Boolean] = {
    Logger.info("Adding user to database")
    db.run(userQuery += user) map (_ > 0)
  }

  def checkUsername(username: String): Future[Boolean] = {
    Logger.info("Checking if username exists in Database")
    val userList = db.run(userQuery.filter(_.username === username).to[List].result)
    userList.map { user =>
      if (user.isEmpty) true else false
    }
  }

  def checkEmail(email: String): Future[Boolean] = {
    Logger.info("Checking if email exists in Database")
    val emailList = db.run(userQuery.filter(_.email === email).to[List].result)
    emailList.map { email =>
      if (email.isEmpty) true else false
    }
  }

  def checkIfUserExists(username: String, password: String): Future[Boolean] = {
    Logger.info("Checking if user exists in Database")
    val userList = db.run(userQuery.filter(_.username === username).to[List].result)
    userList.map { user =>
      if (user.isEmpty) {
        false
      }
      else if (!BCrypt.checkpw(password, user.head.password)) {
        false
      }
      else {
        true
      }
    }
  }

  def getEmail(username: String): Future[String] = {
    Logger.info("Sending data for maintaining session for user")
    val userList: Future[List[User]] = db.run(userQuery.filter(_.username === username).to[List].result)
    userList.map(user => user.head.email)
  }

  def getUser(email: String): Future[List[User]] ={
    Logger.info("Retrieving user from database from email stored in session")
    db.run(userQuery.filter(_.email === email).to[List].result)
  }

}
