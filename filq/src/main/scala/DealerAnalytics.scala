import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import TypeDefs._

object DealerAnalytics {

  private def createStreamContext(batchInterval: Int) = {
    val sparkConf = new SparkConf().setAppName("DealerAnalytics")
    sparkConf.setMaster("local[4]")
    val ssc = new StreamingContext(sparkConf, Milliseconds(batchInterval))
    ssc.checkpoint("./checkpoints")
    ssc
  }

  def main(args: Array[String]) {
    if (args.length < 3) {
      val usage = ("Usage: DealerAnalytics <hostname> <port>"
        ++ " <batch interval in ms>")
      System.err.println(usage)
      System.exit(1)
    }

    val ssc = createStreamContext(args(2).toInt)
    
    val histData = createHistData(ssc.sparkContext)
    println(histData.toDebugString)

    val quoteStream = createQuoteStream(args(0), args(1).toInt, ssc)
    
    val dlrSecIvlCounts = quoteStream        
        .map(s => (s.dealer+s.securityId, 1))
    dlrSecIvlCounts.print()
        
    val dlrSecIvlAggr = dlrSecIvlCounts.reduceByKeyAndWindow(_ + _, _ - _, Seconds(60 * 1), Seconds(9))        
    dlrSecIvlCounts.print() 
    
    val historicCount = dlrSecIvlAggr.updateStateByKey[Int]{(newValues: Seq[Int], runningCount: Option[Int]) => 
        Some(newValues.sum + runningCount.getOrElse(0))}
    historicCount.print()

    /*
    val dlrSecHistCounts = histData
        .map(s => (s._1._1.dealer+s._1._1.securityId, 1))
        .transform{ rdd => rdd.union(defaultRdd)}.reduceByKey( _+_ )
*/
   
    val lastQuotes = quoteStream
        .map(q => (q.securityId, q))
        .transform(q => q.join(histData))
    lastQuotes.print()     

    ssc.start()
    ssc.awaitTermination()
  }

  private def createQuoteStream(
    host: String,
    port: Int,
    ssc: StreamingContext) =
  {
    val lines = ssc.socketTextStream(host, port)
    val quoteStream = lines.flatMap(x => new ParseJS().parseQuotes(x))
    quoteStream
  }

  private def createHistData(sc: SparkContext) = {

    val securityData = sc.parallelize(List(
      ("CIBM1U5", "Security 1"),
      ("CIBM1U7", "Security 2")).map(s => s));

    val quoteData = sc.parallelize(List(
      Quote("AAA", "CIBM1U5", List(Recip(4169985, 1), Recip(6064, 2)), "A", Some(100.99), Some(250)),
      Quote("ABB", "CIBM1U5", List(Recip(4169985, 1), Recip(6064, 2)), "A", Some(98.99), Some(250)), 
      Quote("AAA", "CIBM1U7", List(Recip(4169985, 1), Recip(6064, 2)), "A", Some(105.99), Some(250)), 
      Quote("ABB", "CIBM1U7", List(Recip(4169985, 1), Recip(6064, 2)), "A", Some(97.99), Some(250)) ))
      .map(q => (q.securityId, q));

    // (securityId, (Name, QuoteData))
    var securityAndQuoteData = securityData.join(quoteData)

    //securityAndQuoteData.collect.map(println(_))
    securityAndQuoteData

  }

}
