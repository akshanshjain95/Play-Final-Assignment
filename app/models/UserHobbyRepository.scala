package models

import com.google.inject.Inject
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.{MappedProjection, ProvenShape, QueryBase}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UserToHobby(id: Int, userID: Int, hobbyID: Int)

trait UserHobbyRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val userHobbyQuery: TableQuery[UserHobbyTable] = TableQuery[UserHobbyTable]

  class UserHobbyTable(tag: Tag) extends Table[UserToHobby](tag, "usertohobbyid") {

    def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

    def userID: Rep[Int] = column[Int]("userid")

    def hobbyID: Rep[Int] = column[Int]("hobby_id")

    def * : ProvenShape[UserToHobby] = (id, userID, hobbyID) <> (UserToHobby.tupled, UserToHobby.unapply)

  }

}

class UserHobbyRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserHobbyRepositoryTable with HobbyRepositoryTable {

  import driver.api._

  def addUserHobby(userID: Int, hobbies: List[List[Int]]): Future[Boolean] = {
    Logger.info("Adding hobbies for given user")
    val listOfValidHobbies = hobbies.filter(_ != Nil)
    val listOfResult: List[Future[Boolean]] = listOfValidHobbies.map (
      hobbyID => db.run(userHobbyQuery += UserToHobby(0, userID, hobbyID.head)).map(_ > 0)
      )
    Future.sequence(listOfResult).map {
      result =>
        if (result.contains(false)) false else true
    }
  }

  def getUserHobby(userID: Int): Future[Seq[String]] = {
    Logger.info("Retrieving user hobbies using user email")
    val emailHobbyJoin: QueryBase[Seq[(Int, String)]] = for{
      (user,hobbyName) <- userHobbyQuery join hobbyQuery on (_.hobbyID === _.id)
    } yield (user.userID, hobbyName.hobby)

    val emailHobbySeq: Future[Seq[(Int, String)]] = db.run(emailHobbyJoin.result)

    emailHobbySeq.map(emailHobby => emailHobby.filter(_._1 == userID).map(_._2).toList)
  }

  def deleteUserHobby(userID: Int): Future[Boolean] = {
    Logger.info("Deleting user hobbies for given user ID")
    db.run(userHobbyQuery.filter(_.userID === userID).delete) map (_>0)
  }

}
