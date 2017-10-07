package com.rest.tests.api.frwm.testcase.Expectations;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.json.simple.JSONObject;


/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status extends Expectation{

    public String type = "";
    public long value = 0;

//    public void setDetected(Object detected)
//    {
//        this.detected = (String)detected;
//    }

//    public String toString() {
//        return xpath;
//    }

//    public void setParams(Object obj) {
//        this.xpath = (String)obj;
//    }

    public String toLog() {
        return String.format("Type: Status\nValue: %s", value);
    }

//    public String getXpath() {
//        return "";
//    }

    public String getValue()
    {
        return String.valueOf(value);
    }
}
