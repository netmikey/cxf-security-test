package org.example;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;

import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.wss4j.common.ext.WSPasswordCallback;

/**
 * Parts and constants that Server and Client have in common.
 */
public final class CommonUtils {

	public static final String SERVICE_URL = "http://localhost:8081/ExampleService/v1/ExamplePortType";

	public static final String WSDL_URL = "file:src/test/resources/org/example/wsdl/ExampleService.wsdl";

	public static final QName SERVICE_QNAME = new QName("http://example.org/v1", "ExampleService");

	public static void configureCrypto(Map<String, Object> context) {
		Properties cryptoConfig = new Properties();
		cryptoConfig.put("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");
		cryptoConfig.put("org.apache.wss4j.crypto.merlin.keystore.type", "jks");
		cryptoConfig.put("org.apache.wss4j.crypto.merlin.keystore.password", "changeit");
		cryptoConfig.put("org.apache.wss4j.crypto.merlin.keystore.alias", "myAlias");
		cryptoConfig.put("org.apache.wss4j.crypto.merlin.keystore.file", "src/test/resources/keystore.jks");

		context.put(SecurityConstants.CALLBACK_HANDLER, new CommonUtils.StaticPwdCallback());
		context.put(SecurityConstants.USERNAME, "myAlias");
		context.put(SecurityConstants.SIGNATURE_PROPERTIES, cryptoConfig);
		context.put(SecurityConstants.ENCRYPT_PROPERTIES, cryptoConfig);
	}

	public static class StaticPwdCallback implements CallbackHandler {
		@Override
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (int i = 0; i < callbacks.length; i++) {
				if (callbacks[i] instanceof WSPasswordCallback) {
					((WSPasswordCallback) callbacks[i]).setPassword("changeit");
				} else {
					throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
				}
			}
		}
	}
}
