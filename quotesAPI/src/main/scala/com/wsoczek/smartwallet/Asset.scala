package com.wsoczek.smartwallet

import spray.json.DefaultJsonProtocol.{jsonFormat, _}
import spray.json.RootJsonFormat

sealed trait Asset

case class Stock(currentPrice: Float,
                 highestPriceOfDay: Float,
                 lowestPriceOfDay: Float,
                 openPrice: Float,
                 previousClosePrice: Float)
  extends Asset

case class Crypto(openPrice: Float,
                  highestPriceOfDay: Float,
                  lowestPriceOfDay: Float,
                  closePrice: Float)
  extends Asset

case class StockResponse(name: String,
                         symbol: String,
                         value: Float,
                         currency: String)

object Asset {
  implicit val stockJsonFormat: RootJsonFormat[Stock] =
    jsonFormat(Stock, "c", "h", "l", "o", "pc")
  implicit val cryptoJsonFormat: RootJsonFormat[Crypto] =
    jsonFormat(Crypto, "c", "h", "l", "o")
}

object Assets {
  implicit val stockJsonFormat: RootJsonFormat[Stock] =
    jsonFormat(Stock, "c", "h", "l", "o", "pc")
  implicit val stockResponseJsonFormat: RootJsonFormat[StockResponse] =
    jsonFormat(StockResponse, "name", "symbol", "value", "currency")
  implicit val cryptoJsonFormat: RootJsonFormat[Crypto] =
    jsonFormat(Crypto, "c", "h", "l", "o")

  def stockToResponse(symbol: String, stock: Stock): StockResponse = {
    StockResponse(symbol, symbol, stock.currentPrice, "USD")
  }
}
