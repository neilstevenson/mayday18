package neil.demo.mayday2018.jet;

import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;

import neil.demo.mayday2018.Constants;
import neil.demo.mayday2018.Fx;

public class Ingest1 {

	/**
	 * <p>Read from Kafka, write to Hazelcast map, replacing content.
	 * </p>
	 * 
	 * @param bootstrapServers
	 * @return
	 */
	public static Pipeline build(String bootstrapServers) {
		
        Properties properties = new Properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getCanonicalName());
        properties.setProperty(JsonDeserializer.VALUE_DEFAULT_TYPE, Fx.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Pipeline pipeline = Pipeline.create();
        
        pipeline
        .drawFrom(KafkaSources.kafka(properties, Constants.TOPIC_NAME))
        .drainTo(Sinks.map(Constants.IMAP_NAME_FX));
        ;
                
        return pipeline;
	}
}
