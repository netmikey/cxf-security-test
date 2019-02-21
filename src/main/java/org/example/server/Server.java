package org.example.server;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import org.apache.cxf.jaxws.EndpointImpl;
import org.example.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The server component.
 */
public class Server {

	private static final transient Logger LOGGER = LoggerFactory.getLogger(Server.class);

	private Endpoint endpoint;

	public void start() {
		if (endpoint == null) {
			createEndpoint();
		}
	}

	public void stop() {
		if (endpoint != null) {
			endpoint.stop();
			endpoint = null;
		}
	}

	private void createEndpoint() {

		WSImpl wsImpl = new WSImpl();

		endpoint = Endpoint.create(wsImpl);

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Endpoint.WSDL_PORT, new QName("http://example.org/v1", "ExamplePort"));
		properties.put(Endpoint.WSDL_SERVICE, CommonUtils.SERVICE_QNAME);
		endpoint.setProperties(properties);

		if (!(endpoint instanceof EndpointImpl)) {
			throw new IllegalStateException("Created endpoint instance not of expected type "
					+ EndpointImpl.class.getName() + " but was " + endpoint.getClass().getName());
		}

		EndpointImpl endpointImpl = (EndpointImpl) endpoint;
		endpointImpl.setWsdlLocation(CommonUtils.WSDL_URL);

		endpointImpl.setAddress(CommonUtils.SERVICE_URL);
		endpointImpl.setPublishedEndpointUrl(CommonUtils.SERVICE_URL);

		CommonUtils.configureCrypto(endpointImpl.getProperties());

		LOGGER.info("publishing service: " + CommonUtils.SERVICE_URL);

		endpoint.publish(CommonUtils.SERVICE_URL);
	}
}
