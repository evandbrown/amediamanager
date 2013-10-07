package com.amediamanager.util;

import java.beans.PropertyEditorSupport;
import java.util.Set;

import com.amediamanager.domain.Privacy;
import com.amediamanager.domain.TagSet;

public class PrivacyEditor extends PropertyEditorSupport {

    public void setAsText(String text) {
        setValue(Privacy.fromName(text));
    }
}