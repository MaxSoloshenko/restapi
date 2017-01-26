package com.javelin.tests.api.response.expectation;

import com.javelin.tests.api.response.looking.ILookingObject;
import com.javelin.tests.api.response.looking.LookingForArray;
import net.minidev.json.JSONArray;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Created by msolosh on 3/30/2016.
 */
public class ExpectationNull implements IExpectation {

    @Override
    public void validate(ILookingObject detected) {
        if (detected instanceof LookingForArray)
        {
            JSONArray arr = (JSONArray)detected.getDetected();
            assertEquals(0, arr.size(), "Object is not empty");
        }
        else
        {
            assertNull(detected, "Expected object is not null.");
        }
    }
}
