package com.consumer.kafka;

import com.database.cassandra.CassandraConnector;
import com.database.cassandra.CassandraRepository;
import com.datastax.driver.core.Session;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class App {
    //default values
    private static String brokers = "localhost:9092";
    private static String groupId = "kafkaToCassandraConsumer";
    private static String topic = "stock";
    private static Session session;

    public static void main(String[] args) throws Exception {
        if (args.length == 3) {
            brokers = args[0];
            groupId = args[1];
            topic = args[2];
        }

        runConsumer();


    }

    static void runConsumer() throws Exception {
        KafkaConsumer<String, String> consumer = createConsumer();
        consumer.subscribe(Collections.singletonList(topic));
        try {
            while (true) {
                final ConsumerRecords<String, String> consumerRecords = consumer
                        .poll(Duration.ofMillis(100));
                consumerRecords.forEach(record -> {
                    System.out.printf("Consumer Record:(%s, %s, %d, %d)\n",
                            record.key(), record.value(), record.partition(),
                            record.offset());
                    boolean isStored = store(record.value());
                    if (isStored) {
                        consumer.commitAsync();
                    } else {
                        System.out.println("Data is not stored");
                    }

                });
            }
        } finally {
            consumer.close();
        }
    }

    private static boolean store(String recordStr) {
        Gson gson = new Gson();
        Stock stock = gson.fromJson(recordStr, Stock.class);
        CassandraConnector client = new CassandraConnector();
        client.connect("localhost", 9042);
        session = client.getSession();
        CassandraRepository repo = new CassandraRepository(session);
        repo.insertStock(stock);
        return Boolean.TRUE;
    }


    private static KafkaConsumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }


}
