package neil.demo.mayday2018.jet;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.hazelcast.jet.kafka.KafkaSinks;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamStage;

import neil.demo.mayday2018.Constants;

public class Egest2 {
	
	/**
	 * <p>Read from Hazelcast's event journal (sequence of IMap changes).
	 * Just print to the screen, though it's easy enough to write to
	 * Kafka.
	 * </p>
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Pipeline build(String bootstrapServers) {
		
        Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		
		Pipeline pipeline = Pipeline.create();
        
		StreamStage source = pipeline.drawFrom(Sources.mapJournal(Constants.IMAP_NAME_FX,
                JournalInitialPosition.START_FROM_OLDEST))
				// Filter
				.filter(entry -> entry.getKey().toString().startsWith("U"))
				;
        
        // Write to Kafka and the screen
        source.drainTo(KafkaSinks.kafka(properties,Constants.TOPIC_NAME_USD));
        source.drainTo(Sinks.logger());
                
        return pipeline;
	}
}
