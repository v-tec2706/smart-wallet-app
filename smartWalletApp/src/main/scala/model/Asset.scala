package model

import spray.json.DefaultJsonProtocol.{jsonFormat, _}
import spray.json.RootJsonFormat

case class Asset(name: String, symbol: String, value: Float, currency: String)

object Assets {
  implicit val assetJsonFormat: RootJsonFormat[Asset] =
    jsonFormat(Asset, "name", "symbol", "value", "currency")
}
