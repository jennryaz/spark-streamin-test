import scala.util.parsing.combinator._
import scala.language.dynamics
import TypeDefs._;

object TestWorksheet {

	val pjs = new ParseJS()                   //> pjs  : ParseJS = ParseJS@60f82f98

	//pjs.parseQuotes("\n")

  val map = pjs.parseMap("""{
        |"dlr": "AAA",
        |"security": "CIBM1U5",
        |"recips": [
            |4169985:1,
            |6064:2
          |],
        |"side" : "A",
        |"price" : 100.99,
        |"spread" : 250
        |}""".stripMargin)                        //> map  : Map[String,Any] = Map(side -> A, spread -> 250.0, security -> CIBM1U5
                                                  //| , price -> 100.99, recips -> List(Recip(4169985,1), Recip(6064,2)), dlr -> A
                                                  //| AA)
        
    val oplist = pjs.parseList("""[{
        |"dlr": "AAA",
        |"security": "CIBM1U5",
        |"recips": [
            |4169985:1,
            |6064:2
          |],
        |"side" : "A",
        |"price" : 100.99,
        |"spread" : 250
        |}]""".stripMargin)                       //> oplist  : Option[List[Map[String,Any]]] = Some(List(Map(side -> A, spread ->
                                                  //|  250.0, security -> CIBM1U5, price -> 100.99, recips -> List(Recip(4169985,1
                                                  //| ), Recip(6064,2)), dlr -> AAA)))
       
  println(map)                                    //> Map(side -> A, spread -> 250.0, security -> CIBM1U5, price -> 100.99, recips
                                                  //|  -> List(Recip(4169985,1), Recip(6064,2)), dlr -> AAA)
  println(oplist)                                 //> Some(List(Map(side -> A, spread -> 250.0, security -> CIBM1U5, price -> 100.
                                                  //| 99, recips -> List(Recip(4169985,1), Recip(6064,2)), dlr -> AAA)))

	map.getOrElse("price", Option(None))      //> res0: Any = 100.99
  val list = oplist.get                           //> list  : List[Map[String,Any]] = List(Map(side -> A, spread -> 250.0, securit
                                                  //| y -> CIBM1U5, price -> 100.99, recips -> List(Recip(4169985,1), Recip(6064,2
                                                  //| )), dlr -> AAA))
  list(0).getOrElse("price", Option(None))        //> res1: Any = 100.99
                                                                                                     
	
	println(TypeDefs.Quote.map2Quote(map))    //> Quote(AAA,CIBM1U5,List(Recip(4169985,1), Recip(6064,2)),A,Some(100.99),Some
                                                  //| (250))
	
	val quotes = pjs.parseQuotes("""[{
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
        |}]""".stripMargin)                       //> quotes  : List[TypeDefs.Quote] = List(Quote(AAA,CIBM1U5,List(Recip(4169985,
                                                  //| 1), Recip(6064,2)),A,Some(100.99),Some(250)), Quote(ABB,CIBM1U5,List(Recip(
                                                  //| 4169985,1), Recip(6064,2)),A,Some(100.99),Some(250)), Quote(AAA,CIBM1u7,Lis
                                                  //| t(Recip(4169985,1), Recip(6064,2)),A,Some(100.99),Some(250)))
	
	
	val qmByDealer = quotes.groupBy(_.dealer) //> qmByDealer  : scala.collection.immutable.Map[String,List[TypeDefs.Quote]] =
                                                  //|  Map(AAA -> List(Quote(AAA,CIBM1U5,List(Recip(4169985,1), Recip(6064,2)),A,
                                                  //| Some(100.99),Some(250)), Quote(AAA,CIBM1u7,List(Recip(4169985,1), Recip(606
                                                  //| 4,2)),A,Some(100.99),Some(250))), ABB -> List(Quote(ABB,CIBM1U5,List(Recip(
                                                  //| 4169985,1), Recip(6064,2)),A,Some(100.99),Some(250))))
	
	val qmBySec = quotes.groupBy(_.securityId)//> qmBySec  : scala.collection.immutable.Map[String,List[TypeDefs.Quote]] = Ma
                                                  //| p(CIBM1U5 -> List(Quote(AAA,CIBM1U5,List(Recip(4169985,1), Recip(6064,2)),A
                                                  //| ,Some(100.99),Some(250)), Quote(ABB,CIBM1U5,List(Recip(4169985,1), Recip(60
                                                  //| 64,2)),A,Some(100.99),Some(250))), CIBM1u7 -> List(Quote(AAA,CIBM1u7,List(R
                                                  //| ecip(4169985,1), Recip(6064,2)),A,Some(100.99),Some(250))))
}