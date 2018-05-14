package neil.demo.mayday2018.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.hazelcast.jet.core.JobStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@JsonAutoDetect
@NoArgsConstructor
public class JetJobDTO {

	public int version;
	public String name;
	public JobStatus status;
	
}
