package TypeDefs

case class Recip(id: Int, recipType: Int)

case class Quote(
  dealer: String,
  securityId: String,
  recips: List[Recip],
  side: String,
  price: Option[Double],
  spread: Option[Int])

object Quote {

  def map2Quote(m: Map[String, Any]) = Quote(
    m("dlr").asInstanceOf[String],
    m("security").asInstanceOf[String],
    m("recips").asInstanceOf[List[Recip]],
    m("side").asInstanceOf[String],
    Option(m.getOrElse("price", None).asInstanceOf[Double]),
    Option(m.getOrElse("spread", None).asInstanceOf[Double].toInt))
}
