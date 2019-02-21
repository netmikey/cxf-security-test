
An example project that tests CXF/WSS4J cryptography with minimal overhead.

## The purpose

Recently, we ran into what appeared to be an issue with JAX-WS WebService Security in the CXF/WSS4J stack. However, our production project has grown a lot over the years. The complexity of the whole SOAP/WebService Security stack has grown to the point where it's really difficult to pinpoint the issue's cause, let alone provide the framework maintainers with a standalone working example that reproduces the issue.

That's why we ended up creating this minimalistic example project that is set to reproduce WebService Security related errors we encounter.

## Building / Testing

This project's goal being to reproduce issues, it revolves around JUnit tests. We use Gradle as our build system. This project includes the Gradle Wrapper so in order to run the tests, place yourself into the project directory and run:

    ./gradlew test

## Project layout

This should look familiar if you've used Gradle or Maven before, but just to make sure you find your way through the project:

* The example **Client** and **Server** implementations can be found in `src/main/java`.
* The test **keystore**, the example **WSDL** contract (containing the WS-Policy) and the **logback configuration** can be found in `src/test/resources`
* The actual **unit tests** along with some utility classes required for testing can be found in `src/test/java`

## The tests

### `org.example.MessageTest`

This test starts the example server with its `@WebServiceProvider` serving and endpoint implementation of the `ExampleService.wsdl` on `http://localhost:8081`. It then asks the example client to send a message to that endpoint.

Have a look at the `ExampleService.wsdl`. It contains 2 policies:

* a **SecurePolicy** that (among other things) contains an X509-based policy that signs and encrypts the message body
* an **EmptyPolicy** that is actually really empty, so should not cause any WebService Security mechanisms to trigger

Now have a look at the WSDL's binding: it contains a single operation named `exampleOperation`. Our intention is to have the *request* secured according to the **SecurePolicy** and the *response* come back without any security (we put the **EmptyPolicy** on it to make this intention more explicit). To test whether request and response messages have actually been encrypted or not, CXF's logging feature is used together with a custom logback `TestAppender` that stores the logged raw messages.

If we run the test with the WSDL as is, it will fail (see below). There are, however, a number of other places within the WSDL where the `<wsp:PolicyReference ... />` can be placed. For testing convenience, we placed a couple of them within the WSDL but commented them out. The following table shows a couple of possible combinations and the effect we expected them to achieve, along with the actual behavior.

| `PolicyReference` placement | Expected behavior | Actual behavior |
| --- | --- |
| `SecurePolicy` applied to `wsdl:input`, `EmptyPolicy` applied to `wsdl:output` (as checked into Git by default) | The request message should be secured but the response message should not be. | The client doesn't secure the request message. The Server expects it to be signed/encrypted though and so it rejects the request. |
| `SecurePolicy` applied to `wsdl:binding`, `EmptyPolicy` applied to `wsdl:output` | The request message should be secured but the response message should not be. | The client secures the request message, the server accepts it but replies with a secured response too. |
| `EmptyPolicy` applied to `wsdl:binding`, `SecurePolicy` applied to `wsdl:input` | The request message should be secured but the response message should not be. | The client doesn't secure the request message. The Server expects it to be signed/encrypted though and so it rejects the request. |
