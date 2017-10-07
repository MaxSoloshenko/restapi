package com.rest.tests.api.frwm.testcase.Expectations;

import com.rest.tests.api.frwm.response.looking.ILookingObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.junit.Assert;

import java.util.HashMap;


/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Xvariable extends Expectation{

    public String xpath = "";
    public String value = "";
    public String detected = "";


    public void setDetected(Object detected)
    {
        this.detected = (String)detected;
    }

    public String toString() {
        return xpath;
    }

    public void setParams(Object obj) {
        this.xpath = (String)obj;
    }

    public String toLog() {
        return String.format("Type: Xvariable\nXpath: %s\nValue: %s", xpath, value);
    }

    public String getXpath() {
        return xpath;
    }

    public String getValue() {
        return value;
    }
}
