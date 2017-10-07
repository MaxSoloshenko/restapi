package com.rest.tests.api.frwm.testcase.Expectations;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Xcontains extends Expectation{

    public String type = "";
    public String xpath = "";
    public String[] value = new String[]{};

    protected Xcontains(JSONObject value) {
        System.out.println();
    }

    public String toLog() {
        return String.format("Type: Xcontains\nValues: %s", value.toString());
    }

    public void setParams(Object obj) {
        String line = (String)obj;
        System.out.println();
    }

    public String getValue() {
        return value.toString();
    }

    public String toString() {
        return value.toString();
    }
}
