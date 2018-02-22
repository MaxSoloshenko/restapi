package com.rest.tests.api.frmw.testcase.Expectations;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class jpathVariable extends Expectation{

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
        return String.format("Type: jpathVariable\nXpath: %s\nValue: %s", xpath, value);
    }

    public String getXpath() {
        return xpath;
    }

    public String getValue() {
        return value;
    }
}
