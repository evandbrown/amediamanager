package com.amediamanager.util;

import java.beans.PropertyEditorSupport;

import com.amediamanager.domain.Privacy;

public class PrivacyEditor extends PropertyEditorSupport {

    public void setAsText(String text) {
        setValue(Privacy.fromName(text));
    }
}