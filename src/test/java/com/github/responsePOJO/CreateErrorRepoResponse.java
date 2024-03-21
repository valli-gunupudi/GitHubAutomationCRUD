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
public class CreateErrorRepoResponse {
	@JsonProperty(value = "message")
	public String message;
	@JsonProperty(value = "errors")
	public Errors[] errors;
	@JsonProperty(value = "documentation_url")
	public String documentation_url;
}
