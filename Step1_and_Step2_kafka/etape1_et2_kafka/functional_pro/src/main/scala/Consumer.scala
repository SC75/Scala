import scala.io.Source
import org.apache.kafka.clients.producer._
import java.util.Properties
//import play.api.libs.functional.syntax._
import java.util.Timer
import java.util
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import scala.collection.JavaConverters._
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Message, Session}

import java.io._

object Consumer extends App {

  val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
  //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "consumergroup")

  val consumer = new KafkaConsumer[String, String](props)

  val topic ="dronesmessage"
  consumer.subscribe(util.Collections.singletonList(topic))

  //********************configurations for email send START***********************
  val host = "smtp.gmail.com"
  val port = "587"

  val address = "zhan.chen@efrei.net"
  val username = "dronemessage.efrei@gmail.com"
  val password = "drone@efrei.123"

  def sendEmail(mailSubject: String, mailContent: String) = {
    val properties = new Properties()
    properties.put("mail.smtp.port", port)
    properties.put("mail.smtp.auth", "true")
    properties.put("mail.smtp.starttls.enable", "true")

    val session = Session.getDefaultInstance(properties, null)
    val message = new MimeMessage(session)
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
    message.setSubject(mailSubject)
    message.setContent(mailContent, "text/html")


    val transport = session.getTransport("smtp")
    transport.connect(host, username, password)
    transport.sendMessage(message, message.getAllRecipients)}
  //********************configurations for email send END***********************


  def receiveStream(period: Int): Unit =  {

    val receiv = new java.util.TimerTask {
      def run() = {
        println("receiving data now...")
        val records=consumer.poll(3)
          /*for (record<-records.asScala){
            println(record)*/

        //on a changé 'for' pour 'foreach'
        //l'alerte dans producer est supprimé, l'alerte est maintenant dans consumer.

        records.asScala.foreach{record => println(record.value())

          if ((record.value().split(",")(3))=="1"){
            sendEmail("alterte","one drone needs your intervention")
            println("need police intervention, an altert is send")

          }

        }


      }
    }

    val interval = new java.util.Timer()
    interval.schedule(receiv, period, period)
  }

  receiveStream(5000)

}




