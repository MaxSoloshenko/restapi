package com.rest.tests.api.frwm.testcase.Expectations;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.json.simple.JSONObject;


/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Xpath extends Expectation{

    public String xpath = "";
    public String value = "";

    protected Xpath(JSONObject value) {
        System.out.println();
    }

    public String getValue() {
        return value;
    }
}
