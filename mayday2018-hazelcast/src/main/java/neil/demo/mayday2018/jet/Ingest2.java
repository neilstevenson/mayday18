package neil.demo.mayday2018.jet;

import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.hazelcast.jet.function.DistributedFunctions;
import com.hazelcast.jet.kafka.KafkaSources;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.map.EntryProcessor;

import neil.demo.mayday2018.Constants;
import neil.demo.mayday2018.Fx;

public class Ingest2 {

	/**
	 * <p>Read from Kafka, write to Hazelcast map, merging content into
	 * what is existing using a function
	 * </p>
	 * 
	 * @param bootstrapServers
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
        // Source gives a Map.Entry
        .drawFrom(KafkaSources.kafka(properties, Constants.TOPIC_NAME))
        // Sink merges into Map.Entry with same key using a separate method
        .drainTo(
                Sinks.mapWithEntryProcessor(
                 Constants.IMAP_NAME_FX,
                 DistributedFunctions.entryKey(),
                 item -> ((EntryProcessor) new IngestEntryProcessor((Fx) item.getValue()))
              )
             )
        ;
                
        return pipeline;
	}

}
