package neil.demo.mayday2018;

public class Constants {

    // Hazelcast
    public static final String COMMAND_NOUN_EGEST1 = "EGEST1";
    public static final String COMMAND_NOUN_EGEST2 = "EGEST2";
    public static final String COMMAND_NOUN_INGEST1 = "INGEST1";
    public static final String COMMAND_NOUN_INGEST2 = "INGEST2";
    public static final String COMMAND_VERB_START = "start";
    public static final String COMMAND_VERB_STOP = "stop";

    public static final String IMAP_NAME_COMMAND = "command";
    public static final String IMAP_NAME_FX = "fx";
    public static final String[] IMAP_NAMES = new String[] { 
                    	IMAP_NAME_COMMAND, IMAP_NAME_FX
                    };
 
    // Kafka
    public static final int TOPIC_CURRENCY_PARTITIONS = 3;
    public static final String TOPIC_NAME_FX = "fx";
    public static final String TOPIC_NAME_USD = "usd";
    
}
