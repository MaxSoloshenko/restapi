package com.rest.tests.api.frwm.testcase.Expectations;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Status.class, name = "Status"),
        @JsonSubTypes.Type(value = Xvariable.class, name = "Xvariable"),
        @JsonSubTypes.Type(value = Xcontains.class, name = "Xcontains"),
        @JsonSubTypes.Type(value = Xequal.class, name = "Xequal"),
        @JsonSubTypes.Type(value = Xpath.class, name = "Xpath")}
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public abstract class Expectation{

    private String type = "jsonpath";
    public String value = "";

    public void setDetected(Object obj)
    {
    }

    public void setParams(Object obj) {
    }

    public String toLog() {
        return "";
    }

    public String getType() {
        return getClass().getName().toString().substring(getClass().getName().toString().lastIndexOf(".") + 1);
    }

    public String getXpath() {
        return "";
    }

    public String getValue() {
        return "";
    }
}
