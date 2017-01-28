package com.rest.tests.api.frwm.response.expectation;

import com.rest.tests.api.frwm.response.looking.ILookingObject;
import com.rest.tests.api.frwm.response.looking.LookingForArray;
import net.minidev.json.JSONArray;

import static org.testng.Assert.assertEquals;

/**
 * Created by msolosh on 3/30/2016.
 */
public class ExpectationString implements IExpectation {

    private String expected;

    public ExpectationString(String expected) {
        this.expected = expected;
    }

    @Override
    public void validate(ILookingObject detected) {

        String det = "";
        if(detected instanceof LookingForArray)
        {
            JSONArray arr = (JSONArray)detected.getDetected();
            det = (String)arr.get(0);
        }
        else
            det = detected.getDetected().toString();
        assertEquals(det.toLowerCase(), expected.toLowerCase(), "Expected value is not equal detected.");
    }
}
