package com.rest.tests.api.frmw.response.expectation.html;


import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;

import static org.junit.Assert.assertEquals;

/**
 * Created by msolosh on 3/30/2016.
 */
public class hXINTEGER implements IExpectation {

    private int expected;

    public hXINTEGER(String expected) {
        this.expected = Integer.parseInt(expected);
    }

    @Override
    public void validate(ILookingObject detected) {
        assertEquals(expected, detected.getDetected());
    }
}
