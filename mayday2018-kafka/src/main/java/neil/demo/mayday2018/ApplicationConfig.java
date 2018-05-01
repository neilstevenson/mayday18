package neil.demo.mayday2018;

import java.util.HashMap;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * <p>Configuration</p>
 */
@Configuration
public class ApplicationConfig {

	/**
	 * <p>Create a template, for use in {@link KafkaWriter}, for writing
	 * to a Kafka topic with the specified key type, value type and
	 * topic name. JSON does the serialization of content to text.
	 * </p>
	 *
	 * @param bootstrapServers From "{@code application.yml}"
	 * @return A template to use in {@link KafkaWriter}
	 */
	@Bean
	public KafkaTemplate<String, Fx> kafkaTemplate(@Value("${bootstrap-servers}") String bootstrapServers) {
		Map<String, Object> producerConfigs = new HashMap<>();
		
		producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		ProducerFactory<String, Fx> producerFactory = new DefaultKafkaProducerFactory<>(producerConfigs);

		KafkaTemplate<String, Fx> kafkaTemplate = new KafkaTemplate<>(producerFactory);
				
		kafkaTemplate.setDefaultTopic(Constants.TOPIC_NAME_FX);
		
		return kafkaTemplate;
	}
}