package com.wsoczek.smartwallet

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.wsoczek.smartwallet.Assets._
import com.wsoczek.smartwallet.requests.{CryptoRequest, StockRequest}
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class QuotesServer {
  lazy val apiRoutes: Route = pathPrefix("api") {
    pathPrefix("v0") {
      pathPrefix("quotes") {
        path("stock") {
          (get &
            parameters("symbol")) { symbol =>
            onComplete(new StockRequest(symbol).perform()) {
              case Success(value) =>
                complete(
                  HttpEntity(ContentTypes.`application/json`,
                    Assets
                      .stockToResponse(symbol,
                        value.asInstanceOf[Stock])
                      .toJson
                      .toString()))
              case Failure(_) => complete("Internal server error")
            }
          }
        } ~
          path("crypto") {
            (get &
              parameters("symbol")) { symbol =>
              onComplete(new CryptoRequest(symbol).perform()) {
                case Success(value) =>
                  complete(value.asInstanceOf[Crypto].toJson.toString())
                case Failure(_) => complete("Internal server error")
              }
            }
          }
      }
    }
  }
}

object Server {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("AkkaQuotesApi")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val quotesServer = new QuotesServer()

    Http().bindAndHandle(quotesServer.apiRoutes, "localhost", 8081)
    Await.result(actorSystem.whenTerminated, Duration.Inf)
  }
}
