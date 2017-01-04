# DataShot shell message 

Simple command line application to send messages to a running DataShot web application.

Assumes JMS on 127.0.0.1 with a topic jms/InsectChatTopic

# To build: 

     mvn package

# To run:

     cd target
     java -jar datashot_shellmsg-1.0.0-SNAPSHOT-jar-with-dependencies.jar "text of message"
