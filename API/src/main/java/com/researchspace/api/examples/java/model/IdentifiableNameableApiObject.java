package com.researchspace.api.examples.java.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public abstract class IdentifiableNameableApiObject extends LinkableApiObject {
	
	@JsonProperty("id")
	private Long id = null;
	
	@JsonProperty("globalId")
	private String globalId = null;
	
	@JsonProperty("name")
	private String name = null;

}
