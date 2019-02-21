package org.example.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.ServiceImpl;
import org.example.CommonUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The client component.
 */
public class Client {

	public static final String CONFIDENTIAL_REQUEST_DATA = "Confidential request Data.";

	public SOAPMessage sendTestMessage() throws Exception {
		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
		docBuildFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
		Document xmlDocument = docBuilder.newDocument();

		Element requestElement = xmlDocument.createElementNS("http://example.org/v1", "exampleOperation");
		Element dataElement = xmlDocument.createElement("exampleData");
		dataElement.appendChild(xmlDocument.createTextNode(CONFIDENTIAL_REQUEST_DATA));
		requestElement.appendChild(dataElement);
		xmlDocument.appendChild(requestElement);

        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage request = factory.createMessage();
        request.getSOAPBody().addDocument(xmlDocument);

        return dispatchMessage(request);
	}

	private SOAPMessage dispatchMessage(SOAPMessage soapMessage) throws MalformedURLException {

		ServiceImpl service = null;

		Bus bus = BusFactory.newInstance().createBus();
		service = new ServiceImpl(bus, new URL(CommonUtils.WSDL_URL), CommonUtils.SERVICE_QNAME, null);

		Iterator<QName> ports = service.getPorts();
		if (!ports.hasNext()) {
			throw new IllegalArgumentException("No ports defined in wsdl!");
		}
		QName somePort = ports.next();

		List<WebServiceFeature> featureList = new ArrayList<WebServiceFeature>();
		featureList.add(new LoggingFeature());

		Dispatch<SOAPMessage> dispatch = service.createDispatch(somePort, SOAPMessage.class, Service.Mode.MESSAGE,
				featureList.toArray(new WebServiceFeature[0]));

		CommonUtils.configureCrypto(dispatch.getRequestContext());

		SOAPMessage responseMessage = dispatch.invoke(soapMessage);

		return responseMessage;
	}

}
