package models

import com.google.inject.Inject
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class Hobby(id: Int, hobby: String)

trait HobbyRepositoryTable extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val hobbyQuery: TableQuery[HobbyTable] = TableQuery[HobbyTable]

  class HobbyTable(tag: Tag) extends Table[Hobby](tag, "hobbytable") {

    def id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)

    def hobby: Rep[String] = column[String]("hobby")

    def * : ProvenShape[Hobby] = (id, hobby) <> (Hobby.tupled, Hobby.unapply)

  }

}

class HobbyRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HobbyRepositoryTable {

  import driver.api._

  def getHobbies: Future[List[Hobby]] = {
    Logger.info("Getting the list of all the hobbies in the Database")
    db.run(hobbyQuery.to[List].result)
  }

  def getHobbyIDs(hobbies: List[String]): Future[List[List[Int]]] = {
    Logger.info("Getting the list of IDs of the given hobbies")
    Future.sequence(hobbies.map(hobby => db.run(hobbyQuery.filter(_.hobby === hobby).map(_.id).to[List].result)))
  }

}
