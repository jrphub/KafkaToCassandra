### KafkaToCassandra

This java application consumes data from kafka topic and store the data in Cassandra table.

In this application, the usecase implemented is below.

Sample data present in Kafka Topic :

```json
{"name":"Audi","bidValue":"200","timestamp":"28 Nov 2019"}
```

The application consumes this data and store in Cassandra table called *bidding.stock*

#### Pre-requisites

Install and configure Kafka 

Install Cassandra

#### Setup

##### Cassandra

```cassandra
cqlsh

CREATE KEYSPACE bidding WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 };

use bidding;

CREATE TABLE stock (name text PRIMARY KEY, bidValue text, timestamp text);
```

#### Run

As it's a maven project, run 

```shell
mvn clean install
```

This will create a fat jar called "*KafkaToCassandra-1.0-SNAPSHOT.jar*"

```shell
java -jar KafkaToCassandra-1.0-SNAPSHOT.jar
```

The above command uses below confuguration

```
brokers = "localhost:9092";
groupId = "kafkaToCassandraConsumer";
topic = "stock";
```

If you want to override the above values, you can do by following command

```shell
java -jar KafkaToCassandra-1.0-SNAPSHOT.jar localhost:9092 cg1 stocks
```

For cassandra, it stores data in bidding.stock table mentioned in *CassandraRepository.java* file

