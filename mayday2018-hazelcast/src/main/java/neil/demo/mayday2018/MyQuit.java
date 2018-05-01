package neil.demo.mayday2018;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.core.JobStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Override the built-in to reject the CLI quit if there
 * are still Jet jobs running. We could optionally stop the
 * jobs as part of the shutdown cycle.
 * </p>
 */
@ShellComponent
@Slf4j
public class MyQuit implements Quit.Command {

        @Autowired
        private JetInstance jetInstance;
        
    @ShellMethod(key= {"exit", "quit"}, value="Shutdown if idle")
    public void quit() {
                if (this.jetInstance.getHazelcastInstance().getLifecycleService().isRunning()) {
                        List<Job> jobs = this.jetInstance.getJobs();
                        
                        for (Job job : jobs) {
                                if (job.getStatus() == JobStatus.RUNNING) {
                                        log.error("Cannot stop, job still running '{}'", job);
                                        return;
                                }
                        }
                        
                        this.jetInstance.shutdown();
                }
                throw new ExitRequest();
        }
        
}