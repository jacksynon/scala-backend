package example

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import spray.json.enrichAny
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends App with JsonSupport {
  implicit val system: ActorSystem = ActorSystem("scala-backend")
  import system.dispatcher

  val testRequestActor: ActorRef = system.actorOf(TestRequestActor.props, "testRequestActor")

  implicit val timeout: Timeout = 5.seconds

  val route = concat(
    path("health") {
      get {
        complete("Service is running")
      }
    },
    path("test") {
      get {
        val response = (testRequestActor ? "handleRequest").mapTo[List[PostWithComments]]
        complete(response)
      }
    }
  )

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

  println(s"Server online at http://localhost:8080/")
  sys.addShutdownHook {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}