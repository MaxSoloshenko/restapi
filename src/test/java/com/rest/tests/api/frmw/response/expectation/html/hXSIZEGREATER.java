package com.rest.tests.api.frmw.response.expectation.html;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Created by msolosh on 3/29/2016.
 */
public class hXSIZEGREATER implements IExpectation {

    private int expected;

    public hXSIZEGREATER(String expected) {
        this.expected = Integer.parseInt(expected);
    }

    @Override
    public void validate(ILookingObject detectedArray){
        assertThat(((Object[])detectedArray.getDetected()).length, greaterThan(expected));
    }

}
