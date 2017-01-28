package com.rest.tests.api.frwm.response;

import org.json.simple.JSONObject;

/**
 * Created by msolosh on 3/30/2016.
 */
public class ExpectedFactory {

    public static IExpectationValidator getExpectedObject(JSONObject expect) throws Exception {

        String type = (String)expect.get("type");

        if (type.toLowerCase().startsWith("x")) {
            return new XpathValidation(expect);
        }
        else if (type.startsWith("REGEX")) {
            return new REGEXValidation(expect);
        }
        else if (type.toLowerCase().equals("status")) {
            Long status = (Long)expect.get("value");
            return new StatusValidation(status.intValue());
        }
        else {
            throw new Exception("Unknown expectation type: " + type);
        }
    }
}
