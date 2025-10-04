import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

@QuarkusMain
public class AvroProducerExample {

    private static final Logger log = LoggerFactory.getLogger(AvroProducerExample.class);

    public static void main(String[] args) {
        produceAvroMessage();
    }

    static void produceAvroMessage() {
        try {
            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:29092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", KafkaAvroSerializer.class.getName());
            props.put("schema.registry.url", "http://localhost:8081");

            Producer<String, GenericRecord> producer = new KafkaProducer<>(props);

            String topic = "users";

            Schema schema = new Schema.Parser().parse(new File("src/main/avro/User.avsc"));
            GenericRecord user = new GenericData.Record(schema);
            user.put("name", "Yahya");
            user.put("age", 35);

            ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topic, "user2", user);

            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Error while producing record", exception);
                } else {
                    log.info("Record sent successfully {}", metadata);
                }
            });

            producer.flush();
            producer.close();
        } catch (Exception e) {
            log.error("Error sending Avro message", e);
        }
    }

}
