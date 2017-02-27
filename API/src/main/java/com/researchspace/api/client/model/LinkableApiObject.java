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

    public String getLinkByType (String relType){
        for (ApiLinkItem link : links) {
            if (relType.equals(link.getRel())) {
                return link.getLink();
            }
        }
        return null;
    }
    
}
