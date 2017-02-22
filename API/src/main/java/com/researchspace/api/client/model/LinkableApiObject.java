package com.researchspace.api.client.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class LinkableApiObject {
	
	@JsonProperty("_links")
	protected List<ApiLinkItem> links = new ArrayList<ApiLinkItem>();
	
	public LinkableApiObject(List<ApiLinkItem> links) {
		this.links = links;
	}

	public LinkableApiObject addLink(String link, String linkType) {
		links.add(new ApiLinkItem(link, linkType));
		return this;
	}
	
	public LinkableApiObject addSelfLink(String link) {
		links.add(new ApiLinkItem(link, ApiLinkItem.SELF_REL));
		return this;
	}

}
