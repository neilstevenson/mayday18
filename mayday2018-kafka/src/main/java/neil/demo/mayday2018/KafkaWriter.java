package neil.demo.mayday2018;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Write file contents to a Kafka topic for later processing.
 * For real we would expect this to be a stream where content
 * arrives continuously rather than all arrive at once.
 * </p>
 */
@Component
@Slf4j
public class KafkaWriter implements CommandLineRunner {

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private KafkaTemplate<String, Fx> kafkaTemplate;

	/**
	 * <p>The purpose of this is just to load up a Kafka topic with test data to process.
	 * We could make it better by injecting prices from multiple currencies into Kafka
	 * concurrently (not one file at a time).
	 * </p>
	 * <p>As Hazelcast is reading from the Kafka topic, it has no knowledge of whether
	 * the topic has been loaded up in a batch or has a true stream of prices, nor
	 * what prices are in it.
	 * </p>
	 */
	@Override
	public void run(String... args) throws Exception {
		
		List<Fx> prices = this.loadPriceFile();

		AtomicLong onFailureCount = new AtomicLong(0);
		AtomicLong onSuccessCount = new AtomicLong(0);
		CountDownLatch countDownLatch = new CountDownLatch(prices.size());

		// Iterate through sending, using async counters for success
		for (int i = 0 ; i < prices.size() ; i++) {
			Fx fx = prices.get(i);
			String key = fx.getPair();
			int partition = key.hashCode() % Constants.TOPIC_CURRENCY_PARTITIONS;

			ListenableFuture<SendResult<String, Fx>> sendResult =
					 this.kafkaTemplate.sendDefault(partition, key, fx);

			sendResult.addCallback(new ListenableFutureCallback<SendResult<String, Fx>>() {
                @Override
                public void onSuccess(SendResult<String, Fx> sendResult) {
                	onSuccessCount.incrementAndGet();
                    ProducerRecord<String, Fx> producerRecord = 
                        		sendResult.getProducerRecord();
                    RecordMetadata recordMetadata = sendResult.getRecordMetadata();
                    countDownLatch.countDown();
                    log.info("onSuccess(), offset {} partition {} timestamp {} for '{}'=='{}'",
                    		recordMetadata.offset(),
                    		recordMetadata.partition(), recordMetadata.timestamp(), 
                    		producerRecord.key(), producerRecord.value());
                }

                @Override
                public void onFailure(Throwable t) {
                	onFailureCount.incrementAndGet();
                    countDownLatch.countDown();
                    log.error("onFailure()", t);
                }
        });}

		// Await callbacks, all sends complete
		countDownLatch.await();
		
		if (onFailureCount.get() > 0) {
			throw new RuntimeException(onFailureCount.get() + " failures writing to Kafka");
		} else {
			log.info("Wrote {} price{}", onSuccessCount.get(), (onSuccessCount.get() == 1 ? "" : "s"));
		}
		
	}


	/**
	 * <p>Turn "prices.txt" into a list of FX prices
	 * </p>
	 * 
	 * @return A list of dated prices
	 * @throws IOException If file not found
	 * @throws InterruptedException On sleep
	 */
	private List<Fx> loadPriceFile() 
			throws IOException, InterruptedException {
		
		List<Fx> result = new ArrayList<>();
		
		Resource resource = this.applicationContext.getResource("classpath:prices.txt");

		try (InputStream inputStream = resource.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);) {

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] tokens = line.split(",");
				
				String base = tokens[0];
				String quote = tokens[1];
				double rate = Double.parseDouble(tokens[2]);
			
				// Simiulate delay
				TimeUnit.MILLISECONDS.sleep(100);
				Date now = new Date();
				
				result.add(new Fx(base,quote,rate,now));
			}
		}

		return result;
	}
}
