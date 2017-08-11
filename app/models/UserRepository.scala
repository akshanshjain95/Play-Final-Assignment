package models

import com.google.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import play.api.Logger

import scala.concurrent.Future

case class User(id: Int, firstName: String, middleName: Option[String], lastName: String,
                mobileNo: Long, username: String, password: String,
                gender: String, age: Int)

trait UserRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userQuery: TableQuery[UserTable] = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[User](tag, "usertable") {

    def id: Rep[Int] = column[Int]("id", O.AutoInc)

    def firstName: Rep[String] = column[String]("firstname")

    def middleName: Rep[Option[String]] = column[Option[String]]("middlename")

    def lastName: Rep[String] = column[String]("lastname")

    def mobileNo: Rep[Long] = column[Long]("mobileno")

    def username: Rep[String] = column[String]("username", O.PrimaryKey)

    def password: Rep[String] = column[String]("password")

    def gender: Rep[String] = column[String]("gender")

    def age: Rep[Int] = column[Int]("age")

    def * : ProvenShape[User] = (id, firstName, middleName, lastName, mobileNo, username, password, gender, age) <> (User.tupled, User.unapply)

  }

}

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepositoryTable {

  import driver.api._

  def addUser(user: User): Future[Boolean] = {
    Logger.info("Adding user to database")
    db.run(userQuery += user) map (_>0)
  }

  def checkUsername(username: String): Future[Boolean] = {
    Logger.info("Checking if username exists in Database")
    val userList = db.run(userQuery.filter(_.username === username).to[List].result)
    userList.map { user =>
      if(user.isEmpty) true else false
    }
  }

}
