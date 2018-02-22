package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import com.rest.tests.api.frmw.response.looking.json.LookingForArray;
import net.minidev.json.JSONArray;

import static java.lang.Boolean.parseBoolean;
import static org.testng.Assert.assertEquals;

/**
 * Created by msolosh on 3/30/2016.
 */
public class ExpectationBoolean implements IExpectation {

    private boolean expected;

    public ExpectationBoolean(String expected) {
        this.expected = parseBoolean(expected);
    }

    @Override
    public void validate(ILookingObject detected) {
        if (detected instanceof LookingForArray)
        {
            JSONArray arr =  (JSONArray)detected.getDetected();
            boolean bool = (boolean)arr.get(0);
            assertEquals(bool, expected);
        }
        else
        {
            assertEquals(detected.getDetected(), expected,"Expected objects are not equal: " + detected + " != " + expected);
        }
    }
}
