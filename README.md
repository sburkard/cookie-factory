# cookie-factory
Example project to showcase integration testing with [Citrus][citrus-web] and [Cucumber][cucumber-web]

## Overview
The cookie factory is a typical integration service scenario. It is built with [Spring Boot][spring-boot-web] and [Camel][camel-web].

![Cookie factory overview](docs/cookie-factory.png)
* It offers an HTTP service to order cookies
* Under certain conditions, it calls a backend service (here to get a fortune when fortune cookies are ordered)
* The cookies are produced asynchronously (represented by a JMS message sent to a queue)

## Testing challenges
To test the cookie factory thoroughly, you have to:
* Send cookie orders
* Assert the order responses
* Assert the resulting JMS messages
* Assert the requests sent to the fortune service
* Control the fortune service responses to control the further processing inside the cookie factory

## Test setup
When the cookie factory is tested with [Citrus][citrus-web], the setup is as follows:

![Cookie factory under test](docs/cookie-factory-test.png)
* Citrus is the test driver
* It sends an order to the cookie factory, receives and asserts the response
* It also consumes and asserts the resulting JMS message (for positive tests, otherwise no message is sent)
* The fortune service URL of the cookie factory points to `localhost` for the tests
* Therefore Citrus can simulate the fortune service
* It receives and asserts the fortune request and sends a pre-defined response

## Project structure
The project is a Maven multi-module project with the following modules:
* cookie-factory: The system under test.
* citrus: Integration tests written with [Citrus][citrus-web].
* citrus-dry: The same tests as in `citrus`, but optimized with `Behavior`s to avoid code duplication.
* cucumber: The same tests again, but written as [Cucumber][cucumber-web] testspecs and gluecode written with [Citrus][citrus-web].
* cucumber-dry: The same tests as in `cucumber`, but optimized with `Background` and `Scenario Outline` to avoid duplication in testspecs.

## Run the cookie factory and the tests against it

### Start the JMS broker (ActiveMQ)
```
cd [your download location]\cookie-factory\cookie-factory
mvn activemq:run
```

### Start the cookie factory application
```
cd [your download location]\cookie-factory\cookie-factory
mvn spring-boot:run
```

### Run the integration tests
```
cd [your download location]\cookie-factory\citrus (or one of the other test modules)
mvn verify
```


[citrus-web]: http://citrusframework.org/
[cucumber-web]: https://cucumber.io/
[maven-web]: https://maven.apache.org/
[spring-boot-web]: https://spring.io/projects/spring-boot
[camel-web]: http://camel.apache.org/
