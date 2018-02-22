package com.rest.tests.api.frmw.settings;

import java.util.HashMap;

/**
 * Created by msolosh on 3/25/2016.
 */
public class Variables {

    public String Environment = "";
    public HashMap<String, String> Variables = new HashMap<String, String>();

    public HashMap<String, String> getVariables() {
        return Variables;
    }

    public void setVariables(HashMap<String, String> variables) {
        Variables = variables;
    }
}
