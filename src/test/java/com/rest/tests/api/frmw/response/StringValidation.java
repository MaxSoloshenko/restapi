package com.rest.tests.api.frmw.response;

import com.rest.tests.api.frmw.testcase.Response;
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

            assertTrue(look.contains(expected), look + "\nDOES NOT CONTAIN:\n" + expected);
    }

    @Override
    public HashMap<String, String> validation(Response response, String file) throws IOException {

        Object document = response.getDocument();

        String detectedObject = document.toString();

        validation(detectedObject);
        return null;
    }
}
