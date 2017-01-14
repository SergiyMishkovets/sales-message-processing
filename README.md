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
Testing with an embedded ActiveMQ broker
```
mvn test
```
### Test Data Examples
Message Type 1
```
<?xml version="1.0" encoding="UTF-8"?>
<Sale>
   <Product>Apple</Product>
   <Value>9.24</Value>
</Sale>
```

Message Type 2
```
<?xml version="1.0" encoding="UTF-8"?>
<Sale>
   <Product>Peach</Product>
   <Value>4.33</Value>
   <Occurrences>89</Occurrences>
</Sale>
```

Message Type 3
```
<?xml version="1.0" encoding="UTF-8"?>
<Sale>
   <Product>Grapes</Product>
   <Value>48.15</Value>
   <Adjustment>
      <AdjustmentOperation>ADD</AdjustmentOperation>
      <AdjustmentValue>21.90</AdjustmentValue>
   </Adjustment>
</Sale>
```

### Report Log Examples
Sales Reports
```
Reporting Sales...
Product: GRAPES; Number of Sales: 1; Total Value of Sales: 48.15
Product: APPLE; Number of Sales: 2; Total Value of Sales: 21.84
Product: PEACH; Number of Sales: 85; Total Value of Sales: 4221.95
Product: MANGO; Number of Sales: 2; Total Value of Sales: 144.99
Product: BANANA; Number of Sales: 99; Total Value of Sales: 5032.17
Product: TOMATO; Number of Sales: 1; Total Value of Sales: 55.70
Product: ORANGE; Number of Sales: 93; Total Value of Sales: 816.57
```
```
Reporting Sales...
Product: GRAPES; Number of Sales: 33; Total Value of Sales: 1416.36
Product: PEACH; Number of Sales: 1; Total Value of Sales: 44.36
Product: MANGO; Number of Sales: 3; Total Value of Sales: 114.63
Product: WATERMELON; Number of Sales: 1; Total Value of Sales: 85.11
Product: TOMATO; Number of Sales: 97; Total Value of Sales: 3999.74
Product: ORANGE; Number of Sales: 1; Total Value of Sales: 28.54

```
Adjustment Reports
```
Adjustment instruction received: adding 61.05 to each sale recorded for product BANANA...
Product: BANANA; Number of Sales: 1; Total Value before Adjustment: 97.34; Total Value after Adjustment: 158.39
Product: BANANA; Number of Sales: 1; Total Value before Adjustment: 93.73; Total Value after Adjustment: 154.78
Total Value of Sales after ADD adjustment for product BANANA: 313.17
```
```
Adjustment instruction received: substracting 75.69 to each sale recorded for product TOMATO...
Product: TOMATO; Number of Sales: 1; Total Value before Adjustment: 33.08; Total Value after Adjustment: -42.61
Product: TOMATO; Number of Sales: 1; Total Value before Adjustment: 43.98; Total Value after Adjustment: -31.71
Total Value of Sales after SUBSTRACT adjustment for product TOMATO: -74.32
```
```
Adjustment instruction received: multiplying 13.19 to each sale recorded for product WATERMELON...
Product: WATERMELON; Number of Sales: 1; Total Value before Adjustment: 55.24; Total Value after Adjustment: 728.6156
Product: WATERMELON; Number of Sales: 15; Total Value before Adjustment: 134.85; Total Value after Adjustment: 1778.6715
Product: WATERMELON; Number of Sales: 1; Total Value before Adjustment: 58.87; Total Value after Adjustment: 776.4953
Product: WATERMELON; Number of Sales: 1; Total Value before Adjustment: 62.23; Total Value after Adjustment: 820.8137
Total Value of Sales after MULTIPLY adjustment for product WATERMELON: 4104.5961
```