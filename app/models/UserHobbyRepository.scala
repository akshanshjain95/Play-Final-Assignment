package models

import com.google.inject.Inject
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.{MappedProjection, ProvenShape, QueryBase}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UserToHobby(id: Int, email: String, hobbyID: Int)

trait UserHobbyRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userHobbyQuery: TableQuery[UserHobbyTable] = TableQuery[UserHobbyTable]

  class UserHobbyTable(tag: Tag) extends Table[UserToHobby](tag, "usertohobbyid") {

    def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

    def email: Rep[String] = column[String]("email")

    def hobbyID: Rep[Int] = column[Int]("hobby_id")

    def * : ProvenShape[UserToHobby] = (id, email, hobbyID) <> (UserToHobby.tupled, UserToHobby.unapply)

  }

}

class UserHobbyRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserHobbyRepositoryTable with HobbyRepositoryTable {

  import driver.api._

  def addUserHobby(email: String, hobbies: List[List[Int]]): Future[Boolean] = {
    Logger.info("Adding hobbies for given user")
    val listOfValidHobbies = hobbies.filter(_ != Nil)
    val listOfResult: List[Future[Boolean]] = listOfValidHobbies.map (
      hobbyID => db.run(userHobbyQuery += UserToHobby(0, email, hobbyID.head)).map(_ > 0)
      )
    Future.sequence(listOfResult).map {
      result =>
        if (result.contains(false)) false else true
    }
  }

  def getUserHobby(email: String) = {
    val innerJoin: QueryBase[Seq[(String, String)]] = for{
      (user,hobbyName) <- userHobbyQuery join hobbyQuery
    } yield (user.email, hobbyName.hobby)
  }

}
