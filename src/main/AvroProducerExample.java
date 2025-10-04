import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.*;

import java.io.File;
import java.util.Properties;

public class AvroProducerExample {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:29092");
        props.put("acks", "all"); // attendre l'ACK du broker
        props.put("retries", 0);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081");

        Producer<String, GenericRecord> producer = new KafkaProducer<>(props);

        String topic = "users";

        Schema schema = new Schema.Parser().parse(new File("src/main/avro/User.avsc"));
        GenericRecord user = new GenericData.Record(schema);
        user.put("name", "Alice");
        user.put("age", 30);

        ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topic, "user1", user);

        // Callback pour voir si c’est bien envoyé
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
                System.out.printf("Record envoyé -> topic=%s partition=%d offset=%d%n",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });

        // Forcer l’envoi
        producer.flush();
        producer.close();
    }
}
