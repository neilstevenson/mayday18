package neil.demo.mayday2018.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MyRestController {

	@Autowired
	private JetInstance jetInstance;
	
	@GetMapping(value = "/")
	public List<JetJobDTO> jobs() {
		log.info("jobs()");
		List<JetJobDTO> jobs = new ArrayList<>();
		
		for (Job job : this.jetInstance.getJobs()) {
			log.trace("Job {}", job);
			
			JetJobDTO jetJobDTO = new JetJobDTO(1, job.getName(), job.getStatus());
			
			jobs.add(jetJobDTO);
		}
		
		return jobs;
	}
	
}
