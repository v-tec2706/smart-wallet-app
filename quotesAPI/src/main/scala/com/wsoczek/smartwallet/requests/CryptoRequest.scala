package com.wsoczek.smartwallet.requests

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.wsoczek.smartwallet.{Asset, Crypto}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CryptoRequest(val symbol: String) extends Request {
  override def perform(): Future[Asset] = {
    val request = getRequest(prepareParams())
    val response = sendRequest(request)
    response.flatMap(x => Unmarshal(x).to[Crypto])
  }

  private def prepareParams(): Map[String, String] = {
    val resolution = 60 // 60min candle resolution
    val currentTimestamp = System.currentTimeMillis() // current stock listing
    val prevHourTimestamp = currentTimestamp - 3600000
    Map("resolution" -> resolution.toString,
      "from" -> prevHourTimestamp.toString,
      "to" -> currentTimestamp.toString)
  }
}
