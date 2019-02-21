package org.example.server;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The implementation class that actually "does something". In this case, it
 * just creates a dummy response.
 */
@ServiceMode(value = Service.Mode.MESSAGE)
@WebServiceProvider()
public class WSImpl implements Provider<SOAPMessage> {

	public static final String NON_CONFIDENTIAL_RESPONSE_DATA = "Non-confidential response Data.";

	public SOAPMessage invoke(SOAPMessage request) {

		SOAPMessage response = null;

		try {
			response = createResponseMessage();
		} catch (Exception e) {
			String errorMsg = "Unable to create response message: " + e.getMessage();
			throw new RuntimeException(errorMsg, e);
		}

		return response;
	}

	private SOAPMessage createResponseMessage() throws SOAPException, ParserConfigurationException {
		final MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
		SOAPMessage soapMessage = messageFactory.createMessage();

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		Element responseElement = doc.createElementNS("http://example.org/v1", "ResponseElement");
		doc.appendChild(responseElement);

		Element dataElement = doc.createElement("exampleData");
		dataElement.appendChild(doc.createTextNode(NON_CONFIDENTIAL_RESPONSE_DATA));
		responseElement.appendChild(dataElement);

		soapMessage.getSOAPBody().addDocument(doc);
		soapMessage.saveChanges();

		return soapMessage;
	}

}
