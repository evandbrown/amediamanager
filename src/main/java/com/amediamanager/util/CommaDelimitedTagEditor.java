package com.amediamanager.util;

import java.beans.PropertyEditorSupport;
import java.util.Set;

import com.amediamanager.domain.TagSet;

public class CommaDelimitedTagEditor extends PropertyEditorSupport {

    public void setAsText(String text) {
        Set<String> tags = new TagSet<String>();
        String[] strings = text.split(", ");
        for(int i =0; i < strings.length; i++) {
            String tag = strings[i];
            tags.add(tag);
        }
        setValue(tags);
    }
}