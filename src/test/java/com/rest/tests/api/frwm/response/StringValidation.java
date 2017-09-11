package com.rest.tests.api.frwm.response;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import static org.testng.Assert.assertTrue;

/**
 * Created by msolosh on 3/28/2016.
 */
public class StringValidation implements IExpectationValidator{

    private String expected;

    public StringValidation(JSONObject expect) {
        this.expected = (String)expect.get("value");
     }

    private void validation(String look) {

//        if (expected.startsWith("contains")) {
            assertTrue(look.contains(expected), "Does not contain.");
//        } else {
//            assertTrue(false, String.format("Unknown expectation '%s'\n"));
//        }
    }

    @Override
    public HashMap<String, String> validation(Object response, String file) throws IOException {

        String detectedObject = response.toString();

        validation(detectedObject);
        return null;
    }
}
