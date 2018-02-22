package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by msolosh on 3/30/2016.
 */
public class ExpectationInteger implements IExpectation {

    private int expected;

    public ExpectationInteger(String expected) {
        this.expected = Integer.parseInt(expected);
    }

    @Override
    public void validate(ILookingObject detected) {
        assertEquals(expected, detected.getDetected());
    }
}
