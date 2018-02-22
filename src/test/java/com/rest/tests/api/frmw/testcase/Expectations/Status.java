package com.rest.tests.api.frmw.testcase.Expectations;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status extends Expectation{

    public String type = "";
    public long value = 0;

    public String toLog() {
        return String.format("Type: Status\nValue: %s", value);
    }

    public String getValue()
    {
        return String.valueOf(value);
    }
}
