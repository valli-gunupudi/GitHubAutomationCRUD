package com.github.responsePOJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetRepoResponse {
	@JsonProperty(value = "id")
	public String id;
	@JsonProperty(value = "name")
	public String name;
	@JsonProperty(value = "full_name")
	public String full_name;
	@JsonProperty(value = "default_branch")
	public String default_branch;
	@JsonProperty(value = "node_id")
	public String node_id;
	@JsonProperty(value = "private")
	public boolean Private;
}
