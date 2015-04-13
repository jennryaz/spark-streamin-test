import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import TypeDefs._

@RunWith(classOf[JUnitRunner])
class ParseTest extends FunSuite with BeforeAndAfter {
   
  test("ParseJSon->quote") {
    val qop = new ParseJS().parseQuote("""{
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
    
        
    println(qop)
    qop match {
      case Some(q) => 
        {
          assert(q.dealer.equals("AAA"))
          assert(q.securityId.equals("CIBM1U5"))
          assert(q.price.get.equals(100.99))
        }
      case _ => fail()
    }
  }

  test("ParseJSon->error") {
    val ql = new ParseJS().parseQuotes("\n")
    assert(ql == None)
  }
  
  test("ParseJSon->quoteList") {
    val ql = new ParseJS().parseQuotes("""[{
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
        |"dlr": "ABB",
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
        |"security": "CIBM1u7",
        |"recips": [
            |4169985:1,
            |6064:2
          |],
        |"side" : "A",
        |"price" : 100.99,
        |"spread" : 250
        |}]""".stripMargin)
    
    println(ql)

    ql match {
      case head :: list => {
        val q = head
        println(q)
        assert(q.dealer.equals("AAA"))
        assert(q.securityId.equals("CIBM1U5"))
        assert(q.price.get.equals(100.99))
      }
      case _ => fail()
    }    
        
  }
 
}