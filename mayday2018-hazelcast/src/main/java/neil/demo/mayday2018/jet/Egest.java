package neil.demo.mayday2018.jet;

import com.hazelcast.jet.function.DistributedFunction;
import com.hazelcast.jet.function.DistributedFunctions;
import com.hazelcast.jet.function.DistributedPredicate;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.map.journal.EventJournalMapEvent;

import neil.demo.mayday2018.Constants;
import neil.demo.mayday2018.Fx;

public class Egest {
	
    private static final DistributedPredicate<EventJournalMapEvent<String, Fx>>
        NO_SELECTION_FILTER = DistributedFunctions.alwaysTrue();
    private static final DistributedFunction<EventJournalMapEvent<String, Fx>, EventJournalMapEvent<String, Fx>>
    	NO_PROJECTION_FILTER = DistributedFunctions.wholeItem();

	/**
	 * <p>Read from Hazelcast's event journal (sequence of IMap changes).
	 * Just print to the screen, though it's easy enough to write to
	 * Kafka.
	 * </p>
	 * 
	 * @return
	 */
	public static Pipeline build() {
        Pipeline pipeline = Pipeline.create();
        
        pipeline
        // Read from journal
        .drawFrom(Sources.mapJournal(Constants.IMAP_NAME_FX,
                NO_SELECTION_FILTER,
                NO_PROJECTION_FILTER,
                JournalInitialPosition.START_FROM_OLDEST))
        // Filter 1
        .filter(eventJournalMapEntry -> eventJournalMapEntry.getOldValue() != null)
        // Reformat
        .map(eventJournalMapEntry -> 
        	new String(
        			eventJournalMapEntry.getKey() + " : " +
        			eventJournalMapEntry.getOldValue().getPrice() + " => " +
        			eventJournalMapEntry.getNewValue().getPrice() +
        			(eventJournalMapEntry.getOldValue().getPrice()==eventJournalMapEntry.getNewValue().getPrice()? " **SAME**":"")
        	)
        )
        // Filter 2
        .filter(s -> s.startsWith("U"))
        // Print
        .drainTo(Sinks.logger());
        ;
                
        return pipeline;
	}
}
