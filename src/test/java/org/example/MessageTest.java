package org.example;

import org.example.client.Client;
import org.example.server.Server;
import org.example.server.WSImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test starting a server endpoint and sending a request message to it.
 */
public class MessageTest {

	@Test
	public void testSendMessage() throws Exception {
		System.out.println("Starting test server...");

		Server server = new Server();
		server.start();

		System.out.println("Server started. Sending message...");

		Client client = new Client();
		client.sendTestMessage();

		System.out.println("Message sent. Shutting down Server...");

		server.stop();

		System.out.println("Server shutdown complete.");

		Assert.assertFalse("The logged raw REQUEST message MUST NOT contain the clear-text data",
				TestAppender.contains(Client.CONFIDENTIAL_REQUEST_DATA));

		Assert.assertTrue("The logged raw RESPONSE message MUST contain the clear-text data",
				TestAppender.contains(WSImpl.NON_CONFIDENTIAL_RESPONSE_DATA));
	}

}
