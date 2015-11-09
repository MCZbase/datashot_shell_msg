/**
 * Main.java
 * 
 * Copyright © 2011-2013 President and Fellows of Harvard College
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of Version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author: Paul J. Morris
 */

package imagecapture_shell_msg;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 * Very simple java application to send a message to a hard coded chat topic as a 
 * privileged (server) user to a JMS system on localhost.
 *
 * @author Paul J. Morris
 */
public class Main {

	/** Must equal MessageBean.SERVER_MESSAGE_SOURCE for privileged handling to work.
	 * 
	 */
    public static final String SERVER_MESSAGE_SOURCE = "Server";

	private Topic insectChatTopic;
	private ConnectionFactory insectChatTopicFactory;
	private Destination destination;

    public Main() {
		insectChatTopic = new Topic() {
			@Override
			public String getTopicName() throws JMSException {
				return "jms/InsectChatTopic";
			}
		};

		insectChatTopicFactory = new ConnectionFactory();
		try {
			insectChatTopicFactory.setProperty(ConnectionConfiguration.imqAddressList, "127.0.0.1:7676, 127.0.0.1:5000, 127.0.0.1:9999");
		} catch (JMSException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

    /**
     * Launch the application.  Send arg[0] as a message to the InsectChatTopic, if
     * no arguments are supplied, send a test message.
     * 
     * @param args the command line arguments
     * 
     * 
     */
    public static void main(String[] args) {
		Main m = new Main();
		String message = "Automated Server Message: Testing message injection from server.";
		if (args.length>0) {
			message = "Automated Server Message: " + args[0];
		}
		try {
			m.sendJMSMessageToInsectChatTopic(message, SERVER_MESSAGE_SOURCE);
		} catch (JMSException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
    }

	private void sendJMSMessageToInsectChatTopic(Object messageData, String originator) throws JMSException {
		Connection connection = null;
		Session session = null;
		try {
			connection = insectChatTopicFactory.createConnection();
			destination = new com.sun.messaging.Topic("InsectChatTopic");
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session.createProducer(destination);
			messageProducer.send(createJMSMessageForjmsInsectChatTopic(session, messageData, originator));
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					//logger.log(Level.WARNING, "Cannot close session", e);
			        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
				}
			}
			if (connection != null) {
				connection.close();
			}
		}
	}

	private Message createJMSMessageForjmsInsectChatTopic(Session session, Object messageData, String originator) throws JMSException {
		TextMessage tm = session.createTextMessage();
		tm.setStringProperty("Originator", originator);
		tm.setText(messageData.toString());
		return tm;
	}

}
