package com.gatling.tests

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.filter._

class ProdTest extends Simulation {
  val uri = "https://ticket.rakuten.co.jp"
  //val qps=Integer.getInteger("qps",1)

  val httpProtocol = http.baseUrl(uri)

  val headers_ui = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
    "pragma" -> "no-cache",
    "sec-ch-ua" -> """Google Chrome";v="113", "Chromium";v="113", "Not-A.Brand";v="24""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "macOS",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "none",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")


  val headers_api = Map(
    "accept" -> "application/json, text/javascript, */*; q=0.01",
    "pragma" -> "no-cache",
    "sec-ch-ua" -> """Google Chrome";v="113", "Chromium";v="113", "Not-A.Brand";v="24""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "macOS",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-requested-with" -> "XMLHttpRequest")

  val stg_ui = exec(
    http("cart-ui")
      .get(uri + "/music/jpop/")
      .headers(headers_ui).check(status.is(200)))

  val cart_ui = exec(
    http("cart-ui")
      .get(uri + "/cart/performances/61393")
      .headers(headers_ui).check(status.is(503)))

  val seatapi = exec(
    http("seat-api")
      .get(uri + "/cart/performances/61393/sales_segment/179343/seat_types")
      .headers(headers_api).check(status.is(503)))

  val infoapi = exec(
    http("seat-info-api")
      .get(uri + "/cart/performances/61393/sales_segment/179343/info")
      .headers(headers_api).check(status.is(503)))

  val orderreview = exec(
    http("order")
      .get(uri + "/orderreview")
      .headers(headers_api).check(status.is(503)))
    .exec {
      session =>
        println("orderreview api--->" + session("ResponseBody").as[String]);
        session
    }


  val ui = scenario("cartTestStage").exec(stg_ui)

  val cart = scenario("cartTestStage").exec(cart_ui,seatapi,infoapi)

  val or = scenario("cartTestStage01").exec(orderreview)


  // concurrent users in the system is constant
 // print("qps" + qps)
  setUp(
    ui.inject(constantUsersPerSec(1) during(5 seconds)).protocols(httpProtocol)
//
//    or.inject(constantConcurrentUsers(1) during(5)).protocols(httpProtocol.inferHtmlResources())

  )





  before {
    println("Simulation is about to start!")
  }

  after {
    println("Simulation is finished!")
  }

}
