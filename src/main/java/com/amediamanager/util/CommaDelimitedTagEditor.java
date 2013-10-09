package com.amediamanager.util;

import java.beans.PropertyEditorSupport;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class CommaDelimitedTagEditor extends PropertyEditorSupport {

	// Convert a string to a Set of strings
    public void setAsText(String text) {
        Set<String> tags = new HashSet<String>();
        String[] strings = text.split(", ");
        for(int i =0; i < strings.length; i++) {
            String tag = strings[i];
            tags.add(tag);
        }
        setValue(tags);
    }
    
    // Convert a tag list to a comma-delimited string
    @SuppressWarnings("unchecked")
	public String getAsText() {
    	String concatenated = new String();
    	if(null != getValue()) {
    		concatenated = StringUtils.join((Set<String>)getValue(), ", ");
    	}
    	return concatenated;
    }
}