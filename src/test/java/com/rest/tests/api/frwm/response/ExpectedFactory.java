package com.rest.tests.api.frwm.response;

import com.rest.tests.api.frwm.response.expectation.html.HpathValidation;
import com.rest.tests.api.frwm.response.expectation.json.XpathValidation;
import org.json.simple.JSONObject;

/**
 * Created by msolosh on 3/30/2016.
 */
public class ExpectedFactory {

    public static IExpectationValidator getExpectedObject(JSONObject expect) throws Exception {

        String type = (String)expect.get("type");

        if (type.toLowerCase().startsWith("j")) {
            return new XpathValidation(expect);
        }
        else if (type.startsWith("REGEX")) {
            return new REGEXValidation(expect);
        }
        else if (type.startsWith("h")) {
            return new HpathValidation(expect);
        }
        else if (type.toLowerCase().equals("status")) {
            Long status = (Long)expect.get("value");
            return new StatusValidation(status.intValue());
        }
        else if (type.equalsIgnoreCase("contains")) {
            return new StringValidation(expect);
        }
        else {
            throw new Exception("Unknown jexpectation type: " + type);
        }
    }
}
