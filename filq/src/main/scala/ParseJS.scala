
import scala.util.parsing.combinator.JavaTokenParsers
import TypeDefs._;

class ParseJS extends JavaTokenParsers {

  def arrobj: Parser[List[Map[String, Any]]] =
    "[" ~> repsep(obj, ",") <~ "]" 

  def obj: Parser[Map[String, Any]] =
    "{" ~> repsep(member, ",") <~ "}" ^^ (Map() ++ _)

  def arr: Parser[List[Any]] =
    "[" ~> repsep(value, ",") <~ "]"

  def arrRecip: Parser[List[Recip]] =
    "[" ~> repsep(recip, ",") <~ "]"

  def recip: Parser[Recip] =
    decimalNumber ~ ":" ~ decimalNumber ^^
      { case i ~ ":" ~ t => Recip(i.toInt, t.toInt) }

  def member: Parser[(String, Any)] =
    dequoted ~ ":" ~ value ^^
      { case name ~ ":" ~ value => (name, value) }

  def dequoted: Parser[String] = stringLiteral ^^ { str => str.substring(1, str.length - 1) }

  def value: Parser[Any] = (
    arrRecip
    | arr
    | dequoted
    | decimalNumber ^^ (_.toDouble)
    | stringLiteral
    | "null" ^^ (x => null)
    | "true" ^^ (x => true)
    | "false" ^^ (x => false))

  def parseMap(s: String): Map[String, Any] = {
    val parseResult = parseAll(obj, s)
    parseResult.get
  }

  def parseList(s: String): Option[List[Map[String, Any]]] = parseAll(arrobj, s) match {
    case Success(result, _) => Some(result)
    case failure : NoSuccess => None
  }

  def parseQuote(s: String): Option[Quote] = parseAll(obj, s) match {
    case Success(result, _) => Some(TypeDefs.Quote.map2Quote(result))
    case failure : NoSuccess => None
  }

  def parseQuotes(s: String): List[Quote] = parseAll(arrobj, s) match {
    case Success(result, _) => result.map(m => TypeDefs.Quote.map2Quote(m))
    case failure : NoSuccess => List()
  }
}

object ParseJS extends App {

  val quote = new ParseJS().parseQuote("""{
        |"dlr": "AAA",
        |"security": "CIBM1U5",
        |"recips": [
            |4169985:1,
            |6064:2
          |],
        |"side" : "A",
        |"price" : 100.99,
        |"spread" : 250
        |}""".stripMargin)

/*
[{"dlr":"AAA","security":"CIBM1U5","recips":[4169985:1,6064:2],"side":"A","price":100.99,"spread":250},{"dlr":"AAA","security":"CIBM1U7","recips":[4169985:1,6064:2],"side":"A","price":100.99,"spread":250}]
 */
  val quotes = new ParseJS().parseQuotes("""[{
        |"dlr": "AAA",
        |"security": "CIBM1U5",
        |"recips": [
            |4169985:1,
            |6064:2
          |],
        |"side" : "A",
        |"price" : 100.99,
        |"spread" : 250
        |},{
        |"dlr": "AAA",
        |"security": "CIBM1U5",
        |"recips": [
            |4169985:1,
            |6064:2
          |],
        |"side" : "A",
        |"price" : 100.99,
        |"spread" : 250
        |}]""".stripMargin)

  println(quote)
  println(quotes)

}