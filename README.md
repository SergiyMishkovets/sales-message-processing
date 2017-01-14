# Sales Message Processing App

A message processing app to consume sales message, compute sales and log reports.

## Installation

System Requirements
- Java 8
- Maven 3
- [ActiveMQ](http://activemq.apache.org/)

## Usage
Running from Command line
```
mvn exec:java -Dexec.mainClass="com.ontomix.smp.App" -Dexec.args="brokerUrl, username, password"
```

## Test
Test with an embedded ActiveMQ broker
```
mvn test
```