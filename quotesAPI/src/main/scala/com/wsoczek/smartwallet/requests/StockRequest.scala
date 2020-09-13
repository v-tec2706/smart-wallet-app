package com.wsoczek.smartwallet.requests

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.wsoczek.smartwallet.{Asset, Stock}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StockRequest(val symbol: String) extends Request {
  override def perform(): Future[Asset] = {
    val request = getRequest()
    val response = sendRequest(request)
    response.flatMap(x => Unmarshal(x).to[Stock])
  }
}
