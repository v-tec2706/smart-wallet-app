import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class QuotesServer extends QuotesService {
  lazy val apiRoutes: Route = pathPrefix("api") {
    pathPrefix("v0") {
      pathPrefix("quotes") {
        path("stock") {
          (get &
            parameters("symbol")) { symbol =>
            onComplete(getQuotes(StockQuotesRequest(symbol))) {
              case Success(value) => complete(s"${value}")
              case Failure(ex) => complete(s"Internal server error, $ex")
            }
          }
        } ~
          path("crypto") {
            (get &
              parameters("symbol")) { symbol =>
              onComplete(getQuotes(CryptoQuotesRequest(symbol))) {
                case Success(value) => complete(s"stock: $value")
                case Failure(ex) => complete("Internal server error")
              }
            }
          }
      }
    }
  }
}

object Server {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("AkkaHTTPExampleServices")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val quotesServer = new QuotesServer()

    Http().bindAndHandle(quotesServer.apiRoutes, "localhost", 8080)
    Await.result(actorSystem.whenTerminated, Duration.Inf)
  }
}
