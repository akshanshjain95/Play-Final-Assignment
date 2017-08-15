package models

import com.google.inject.Inject
import controllers.{UpdatePassword, UpdateUserForm}
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import play.api.Logger

import scala.concurrent.Future

case class User(id: Int, firstName: String, middleName: Option[String], lastName: String,
                mobileNo: Long, email: String, password: String,
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

    def password: Rep[String] = column[String]("password")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def isAdmin: Rep[Boolean] = column[Boolean]("isadmin")

    def isEnabled: Rep[Boolean] = column[Boolean]("isenabled")

    def * : ProvenShape[User] = (id, firstName, middleName, lastName, mobileNo, email, password, gender, age, isAdmin, isEnabled) <> (User.tupled, User.unapply)

  }

}

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepositoryTable {

  import driver.api._

  def addUser(user: User): Future[Boolean] = {
    Logger.info("Adding user to database")
    db.run(userQuery += user) map (_ > 0)
  }

  def checkEmail(email: String): Future[Boolean] = {
    Logger.info("Checking if email exists in Database")
    val emailList = db.run(userQuery.filter(_.email === email).to[List].result)
    emailList.map { email =>
      if (email.isEmpty) true else false
    }
  }

  def checkIfUserExists(email: String, password: String): Future[Boolean] = {
    Logger.info("Checking if user exists in Database")
    val userList = db.run(userQuery.filter(_.email === email).to[List].result)
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

  def getUserByID(userID: Int): Future[List[User]] = {
    Logger.info("Retrieving user from database from ID stored in session")
    db.run(userQuery.filter(_.id === userID).to[List].result)
  }

  def getUserID(email: String): Future[List[Int]] = {
    Logger.info("Getting user ID based on user E-mail")
    db.run(userQuery.filter(_.email === email).map(_.id).to[List].result)
  }

  def updateUser(updateUser: UpdateUserForm, id: Int): Future[Boolean] = {
    Logger.info("Updating user for given user ID")
    db.run(userQuery.filter(_.id === id).map(user => (user.firstName, user.middleName, user.lastName,
    user.mobileNo, user.gender, user.age)).update((updateUser.name.firstName, updateUser.name.middleName,
      updateUser.name.lastName, updateUser.mobileNo, updateUser.gender, updateUser.age)))
      .map(_ > 0)
  }

  def updateUserByEmail(updatePassword: UpdatePassword): Future[Boolean] = {
    Logger.info("Updating password for given user")
    db.run(userQuery.filter(_.email === updatePassword.email).map(_.password).update(updatePassword.password)) map (_ > 0)
  }

  def getAllUsersWithStatus(id: Int): Future[Map[String, Boolean]] = {
    Logger.info("Getting all users with enable/disable status")
    db.run(userQuery.filter(_.id =!= id).map(user => (user.email, user.isEnabled)).sorted.to[List].result).map(_.toMap)
  }

  def enableUser(email: String, status: Boolean): Future[Boolean] = {
    Logger.info("Enabling/Disabling the given user")
    db.run(userQuery.filter(_.email === email).map(_.isEnabled).update(status)) map (_ > 0)
  }

  def getUserInfoForSession(email: String): Future[List[(Int, Boolean, Boolean)]] = {
    Logger.info("Getting user ID and isAdmin for given user email if he's enabled")
    db.run(userQuery.filter(_.email === email).map(user => (user.id,user.isAdmin, user.isEnabled)).to[List].result)
  }

}
