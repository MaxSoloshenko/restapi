package com.rest.tests.api.frmw.response.expectation.html;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;

import static org.junit.Assert.assertEquals;

/**
 * Created by msolosh on 3/29/2016.
 */
public class ExpectationSize implements IExpectation {

    private int expected;

    public ExpectationSize(String expected) {
        this.expected = Integer.parseInt(expected);
    }

    @Override
    public void validate(ILookingObject detectedArray){
        assertEquals(expected, ((Object[])detectedArray.getDetected()).length);
    }

}
