package com.rest.tests.api.frmw.response.expectation.html;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

/**
 * Created by msolosh on 3/29/2016.
 */
public class hXSIZELESS implements IExpectation {

    private int expected;

    public hXSIZELESS(String expected) {
        this.expected = Integer.parseInt(expected);
    }

    @Override
    public void validate(ILookingObject detectedArray){
        assertThat(((Object[])detectedArray.getDetected()).length, lessThan(expected));
    }

}
