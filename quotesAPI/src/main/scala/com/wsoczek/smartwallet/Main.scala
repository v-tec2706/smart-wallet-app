package com.wsoczek.smartwallet

import com.wsoczek.smartwallet.requests.{CryptoRequest, Request, StockRequest}

import scala.concurrent.ExecutionContext.Implicits.global


object Main extends App {
  val stockRequest: StockRequest = new StockRequest("AAPL")
  val cryptoRequest: CryptoRequest = new CryptoRequest("BINANCE:BTCUSDT")
  val requests: List[Request] = List(stockRequest, cryptoRequest)
  stockRequest.perform().foreach(println)
  cryptoRequest.perform().foreach(println)
}
