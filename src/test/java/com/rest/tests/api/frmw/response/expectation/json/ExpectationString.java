package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import com.rest.tests.api.frmw.response.looking.json.LookingForArray;
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
