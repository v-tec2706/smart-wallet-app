package com.wsoczek.smartwallet

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri.{Path, Query}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

case class Quote(c: Int, h: Int, l: Int, o: Int, pc: Int, t: Int)

object SampleRequest extends App {

  implicit val quoteJsonFormat: RootJsonFormat[Quote] = jsonFormat6(Quote)

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val token = "bt8095f48v6rsr8q8br0"

  val entity = FormData(Map("symbol" -> "AAPL", "token" -> token)).toEntity

  val url = Uri("https://finnhub.io")
    .withPath(Path("/api/v1/quote"))
    .withQuery(Query(Map("symbol" -> "AAPL", "token" -> token)))
  val request = HttpRequest(
    uri = url
  )
  val res = sendRequest()
  //  res.foreach(x => println("iii" + x))
  val mappedRes = res.map(x => Unmarshal(x).to[Quote])

  def sendRequest(): Future[HttpEntity] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    val entityFuture: Future[HttpEntity.Strict] = responseFuture.flatMap(response => response.entity.toStrict(2.seconds))
    entityFuture.map(entity => entity)
  }

  mappedRes.foreach(x => println("x:" + x))
  //  mappedRes.foreach(x => println("yyy" + x))
}
