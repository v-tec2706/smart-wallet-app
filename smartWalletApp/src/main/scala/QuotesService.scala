import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import model.Asset
import model.Assets._
import utils.Configuration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

sealed trait QuotesRequest

trait QuotesService extends Configuration {
  def getQuotes(request: QuotesRequest): Future[Asset] = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val host: String = appProperties.getConfig("quotes").getString("host")
    val port: Int = appProperties.getConfig("quotes").getInt("port")

    val url = Uri(host.concat(s":$port").concat(resolvePath(request)))
      .withQuery(Query(Map("symbol" -> (request match {
        case StockQuotesRequest(symbol, _) => symbol
        case CryptoQuotesRequest(symbol, _) => symbol
      }))))

    val httpRequest = HttpRequest(uri = url)
    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    val response =
      responseFuture.flatMap(response => response.entity.toStrict(2.seconds))
    //    response.map(x => x.data.utf8String)
    response.flatMap(x => Unmarshal(x).to[Asset])

  }

  private def resolvePath(request: QuotesRequest): String = {
    val path: String = appProperties.getConfig("quotes").getString("path")
    request match {
      case StockQuotesRequest(_, _) => path.concat("/stock")
      case CryptoQuotesRequest(_, _) => path.concat("/crypto")
    }
  }
}

case class StockQuotesRequest(symbol: String, currency: String = "PLN")
  extends QuotesRequest

case class CryptoQuotesRequest(symbol: String, currency: String = "PLN")
  extends QuotesRequest
