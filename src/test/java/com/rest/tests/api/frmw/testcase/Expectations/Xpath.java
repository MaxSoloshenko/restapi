package com.rest.tests.api.frmw.testcase.Expectations;

import net.minidev.json.JSONObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


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
