package com.amediamanager.util;

import java.beans.PropertyEditorSupport;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.amediamanager.domain.Tag;

public class CommaDelimitedTagEditor extends PropertyEditorSupport {

	// Convert a string to a Set of Tags
    public void setAsText(String text) {
        Set<Tag> tags = new HashSet<Tag>();
        String[] strings = text.split(",");
        for(int i =0; i < strings.length; i++) {
            String tag = strings[i].trim();
            tags.add(new Tag(tag));
        }
        setValue(tags);
    }
    
    // Convert a tag list to a comma-delimited string
    @SuppressWarnings("unchecked")
	public String getAsText() {
    	String concatenated = new String();
    	if(null != getValue()) {
    		concatenated = StringUtils.join((Set<Tag>)getValue(), ", ");
    	}
    	return concatenated;
    }
}