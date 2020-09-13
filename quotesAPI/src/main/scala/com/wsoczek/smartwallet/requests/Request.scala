package com.wsoczek.smartwallet.requests

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.{Path, Query}
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import com.wsoczek.smartwallet.Asset
import com.wsoczek.smartwallet.utils.Configuration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

trait Request extends Configuration {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val token: String = appProperties.getConfig("api").getString("token")
  val uri: String = appProperties.getConfig("api").getString("uri")
  val path: String = appProperties.getConfig("api").getString("path")

  def perform(): Future[Asset]

  def symbol: String

  def getRequest(queryParams: Map[String, String] = Map.empty) =
    HttpRequest(uri = getUrl(queryParams))

  def getUrl(queryParams: Map[String, String]): Uri =
    Uri(uri)
      .withPath(Path(path))
      .withQuery(
        Query(Map("symbol" -> symbol, "token" -> token) ++ queryParams))

  def sendRequest(httpRequest: HttpRequest): Future[HttpEntity] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    responseFuture.flatMap(response => response.entity.toStrict(2.seconds))
  }
}
