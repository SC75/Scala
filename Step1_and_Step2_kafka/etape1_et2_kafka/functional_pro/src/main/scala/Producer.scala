import scala.io.Source
import org.apache.kafka.clients.producer._
import java.util.Properties
import java.util.Timer



//members in group : Francis Tran, Sebastien Chen, Jérémy Trullier, Zhan Chen

object Producer extends App {

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val producer = new KafkaProducer[String, String](props)

  val topic = "dronesmessage"



  def DroneSendStream(period: Int): Unit =  {

    val fileName = "/home/chen/Documents/dronemessages.csv"

    val send = new java.util.TimerTask {
      def run() = {
        println("info sent by drones!")


        Source.fromFile(fileName).getLines().drop(1).foreach{l =>
          val key = l.split(",") {0}

          if (l.size > 44 )
          {
            println("need intervention, an alert email has been send")

          }


          val record = new ProducerRecord(topic, key, l)
          producer.send(record)
        }

        println("End")
      }
    }
    val interval = new java.util.Timer()
    interval.schedule(send, period, period)
  }

  DroneSendStream(5000)

}


