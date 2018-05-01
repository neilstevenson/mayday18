package neil.demo.mayday2018;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Add some custom commands to the Spring Shell CLI.
 * </p>
 */
@ShellComponent
@Slf4j
public class MyCLI {
	
    private HazelcastInstance hazelcastInstance;
    private IMap<String, List<String>> commandMap;
    private JetInstance jetInstance;
    private String bootstrapServers;

	public MyCLI(JetInstance jetInstance,
                 @Value("${bootstrap-servers}") String bootstrapServers
                 ) {
    	this.jetInstance = jetInstance;
        this.hazelcastInstance = jetInstance.getHazelcastInstance();
        this.bootstrapServers = bootstrapServers;

        // Initialise all maps
        for (String iMapName : Constants.IMAP_NAMES) {
            log.info("Initialize IMap '{}'", iMapName);
            this.hazelcastInstance.getMap(iMapName);
        }

        this.commandMap = this.hazelcastInstance.getMap(Constants.IMAP_NAME_COMMAND);
    }

    /**
     * <p>Find and list all Jet jobs, running or completed, by name.
     * </p>
     * <p>In Jet, job names can be null or duplicated, but in this
     * demo they're unique and {@code @NonNull} so we can simplify
     * this logic using that assumption.
     * </p>
     *
     * @throws Exception
     */
    @ShellMethod(key = "JOBS", value = "List Jet jobs")
    public void jobs() throws Exception {
    	// Alphabetical order
        Set<String> jobNames = 
        		this.jetInstance.getJobs()
        		.stream()
        		.map(job -> job.getName())
        		.collect(Collectors.toCollection(TreeSet::new));
        
        jobNames
        .stream()
        .forEach(jobName -> {
        	Job job = this.jetInstance.getJob(jobName);
        	
        	System.out.printf("Job name '%s' id '%d' status '%s'%n",
        			job.getName(), job.getId(), job.getStatus());
        });

        System.out.printf("[%d job%s]%n", jobNames.size(), (jobNames.size() == 1 ? "" : "s"));
        System.out.println("");
    }
    
    /**
     * <p>List the maps in our cluster.</p>
     * <p>Hide the internal ones beginning "@{code __jet}" from the output.</p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @ShellMethod(key = "LIST", value = "List map entries")
    public void listIMaps() {
        Set<String> iMapNames = this.hazelcastInstance.getDistributedObjects().stream()
        		.filter(distributedObject -> distributedObject instanceof IMap)
        		.filter(distributedObject -> !distributedObject.getName().startsWith(Jet.INTERNAL_JET_OBJECTS_PREFIX))
        		.map(distributedObject -> distributedObject.getName()).collect(Collectors.toCollection(TreeSet::new));

        iMapNames.stream().forEach(name -> {
            IMap<?, ?> iMap = this.hazelcastInstance.getMap(name);

            System.out.println("");
            System.out.printf("IMap: '%s'%n", name);

            // Sort if possible
            Set<?> keys = iMap.keySet();
            if (!keys.isEmpty() && keys.iterator().next() instanceof Comparable) {
                keys = new TreeSet(keys);
            }

            keys.stream().forEach(key -> {
                System.out.printf("    -> '%s' -> %s%n", key, iMap.get(key));
            });

            System.out.printf("[%d entr%s]%n", iMap.size(), (iMap.size() == 1 ? "y" : "ies"));
        });

        System.out.println("");
        System.out.printf("[%d IMap%s]%n", iMapNames.size(), (iMapNames.size() == 1 ? "" : "s"));
        System.out.println("");
    }

    // START Requests
    @ShellMethod(key = "EGEST1_START", value = "Start fake 'kafka' output job")
    public String egest1Start() {
        String noun = Constants.COMMAND_NOUN_EGEST1;

        List<String> params = new ArrayList<>();
        params.add(Constants.COMMAND_VERB_START);

        this.commandMap.put(noun, params);

        return String.format("Requested %s job '%s'", params.get(0), noun);
    }
    @ShellMethod(key = "EGEST2_START", value = "Start real 'kafka' output job")
    public String egest2Start() {
        String noun = Constants.COMMAND_NOUN_EGEST2;

        List<String> params = new ArrayList<>();
        params.add(Constants.COMMAND_VERB_START);
        params.add(this.bootstrapServers);

        this.commandMap.put(noun, params);

        return String.format("Requested %s job '%s' with %s", params.get(0), noun, params.get(1));
    }
    @ShellMethod(key = "INGEST1_START", value = "Start simple Kafka input job")
    public String ingest1Star() {
        String noun = Constants.COMMAND_NOUN_INGEST1;

        List<String> params = new ArrayList<>();
        params.add(Constants.COMMAND_VERB_START);
        params.add(this.bootstrapServers);

        this.commandMap.put(noun, params);

        return String.format("Requested %s job '%s' with %s", params.get(0), noun, params.get(1));
    }
    @ShellMethod(key = "INGEST2_START", value = "Start slightly more complicated Kafka input job")
    public String ingest2Start() {
        String noun = Constants.COMMAND_NOUN_INGEST2;

        List<String> params = new ArrayList<>();
        params.add(Constants.COMMAND_VERB_START);
        params.add(this.bootstrapServers);

        this.commandMap.put(noun, params);

        return String.format("Requested %s job '%s' with %s", params.get(0), noun, params.get(1));
    }

    // STOP Requests
    @ShellMethod(key = "EGEST1_STOP", value = "Stop 'kafka' output job")
    public String egest1Stop() {
        String noun = Constants.COMMAND_NOUN_EGEST1;

        List<String> params = new ArrayList<>();
        params.add(Constants.COMMAND_VERB_STOP);

        this.commandMap.put(noun, params);

        return String.format("Requested %s job '%s'", params.get(0), noun);
    }
    @ShellMethod(key = "EGEST2_STOP", value = "Stop 'kafka' output job")
    public String egest2Stop() {
        String noun = Constants.COMMAND_NOUN_EGEST2;

        List<String> params = new ArrayList<>();
        params.add(Constants.COMMAND_VERB_STOP);

        this.commandMap.put(noun, params);

        return String.format("Requested %s job '%s'", params.get(0), noun);
    }
    @ShellMethod(key = "INGEST1_STOP", value = "Stop simple Kafka input job")
    public String ingest1Stop() {
        String noun = Constants.COMMAND_NOUN_INGEST1;

        List<String> params = new ArrayList<>();
        params.add(Constants.COMMAND_VERB_STOP);

        this.commandMap.put(noun, params);

        return String.format("Requested %s job '%s'", params.get(0), noun);
    }
    @ShellMethod(key = "INGEST2_STOP", value = "Stop slightly more complicated Kafka input job")
    public String ingest2Stop() {
        String noun = Constants.COMMAND_NOUN_INGEST2;

        List<String> params = new ArrayList<>();
        params.add(Constants.COMMAND_VERB_STOP);

        this.commandMap.put(noun, params);

        return String.format("Requested %s job '%s'", params.get(0), noun);
    }

}

