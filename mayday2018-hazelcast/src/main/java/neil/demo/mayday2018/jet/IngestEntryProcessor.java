package neil.demo.mayday2018.jet;

import java.util.Map.Entry;

import com.hazelcast.map.AbstractEntryProcessor;

import lombok.extern.slf4j.Slf4j;
import neil.demo.mayday2018.Fx;

@SuppressWarnings("serial")
@Slf4j
public class IngestEntryProcessor extends AbstractEntryProcessor<String, Fx> {
	
	private Fx item;
	
	IngestEntryProcessor(Fx arg0) {
		this.item = arg0;
	}
	
    @Override
    public Void process(Entry<String, Fx> entry) {
    		String key = entry.getKey();
    		Fx value = entry.getValue();

    		// Value already exists or not
    		if (value == null) {
    			// Insert, simple save
    			log.info("First value for '{}'", key);
    			entry.setValue(this.item);
    		} else {
    			// Update, merge somehow
    			if (value.getPrice() == this.item.getPrice()) {
    				// This creates no event journal entry, data doesn't change in IMDG
    				log.info("No price change on {}, doing nothing", key);
    			} else {
    				if (value.getPrice() < this.item.getPrice()) {
    	    			log.info("Price increase for '{}'", key);
    	    			entry.setValue(this.item);
    				} else {
    	    			log.info("Price decrease for '{}'", key);
    	    			entry.setValue(this.item);
    				}
    			}
    		}

    		// Caller doesn't care what happened
            return null;
    }


}
