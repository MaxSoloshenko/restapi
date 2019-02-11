package com.rest.tests.api.frmw.testcase.Expectations;

import net.minidev.json.JSONObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Xequal extends Expectation{

    public String xpath = "";
    public String[] value = new String[]{};

    protected Xequal(JSONObject value) {
        System.out.println();
    }

}
