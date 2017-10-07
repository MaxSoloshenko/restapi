package com.rest.tests.api.frwm.testcase.Expectations;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.json.simple.JSONObject;


/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Xequal extends Expectation{

    public String xpath = "";
    public String[] value = new String[]{};

    protected Xequal(JSONObject value) {
        System.out.println();
//        super(value);
    }

}
