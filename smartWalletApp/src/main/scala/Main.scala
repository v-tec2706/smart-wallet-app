import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, get, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("AkkaHTTPExampleServices")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    lazy val apiRoutes: Route = pathPrefix("api") {
      get {
        complete {
          "Hello world"
        }
      }
    }

    Http().bindAndHandle(apiRoutes, "localhost", 8080)
    Await.result(actorSystem.whenTerminated, Duration.Inf)
  }
}
