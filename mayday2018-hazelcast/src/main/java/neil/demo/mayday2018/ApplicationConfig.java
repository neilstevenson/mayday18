package neil.demo.mayday2018;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Configure things as Spring beans.
 * </p>
 * <p>Until the fix to {@link https://github.com/spring-projects/spring-boot/issues/8863 8863}
 * is merged, beans for Jet have to be added by hand.
 * </p>
 */
@Configuration
public class ApplicationConfig {

	/**
	 * <p>Configuration for Hazelcast IMDG comes from
	 * "{@code hazelcast.xml}".
	 * </p>
	 * 
	 * @return In-Memory Data Grid configuration
	 */
    @Bean
    public Config config() {
        return new ClasspathXmlConfig("hazelcast.xml");
    }

    /**
     * <p>Create a Jet instance. We don't need to specify
     * any Jet configuration, but do for the In-Memory Data Grid
     * contained within.
     * </p>
     * 
     * @param config Created above
     * @return A Jet instance, embedding an IMDG
     */
    @Bean
    public JetInstance jetInstance(Config config) {
        JetConfig jetConfig = new JetConfig().setHazelcastConfig(config);
        return Jet.newJetInstance(jetConfig);
    }

    /**
     * <p>Extract the Hazelcast IMDG instance inside the Jet
     * instance, and expose this as a separate bean.
     * </p>
     * <p>Attach a listener to the "{@code command}" map. We
     * could do this from the XML file, but it's easier this
     * way as the command listener is itself a Spring bean.
     * </p>
     * 
     * @param commandListener See {@link CommandListener}
     * @param jetInstance Created above
     * @return Extracted not created
     */
    @Bean
    public HazelcastInstance hazelcastInstance(CommandListener commandListener, JetInstance jetInstance) {
        HazelcastInstance hazelcastInstance = jetInstance.getHazelcastInstance();

        // React to map changes
        IMap<?, ?> commandMap = hazelcastInstance.getMap(Constants.IMAP_NAME_COMMAND);
        commandMap.addLocalEntryListener(commandListener);

        return hazelcastInstance;
    }
}
