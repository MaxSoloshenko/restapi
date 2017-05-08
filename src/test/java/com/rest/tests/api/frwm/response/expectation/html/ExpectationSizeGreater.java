package com.rest.tests.api.frwm.response.expectation.html;

import com.rest.tests.api.frwm.response.expectation.IExpectation;
import com.rest.tests.api.frwm.response.looking.ILookingObject;
import net.minidev.json.JSONArray;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Created by msolosh on 3/29/2016.
 */
public class ExpectationSizeGreater implements IExpectation {

    private int expected;

    public ExpectationSizeGreater(String expected) {
        this.expected = Integer.parseInt(expected);
    }

    @Override
    public void validate(ILookingObject detectedArray){
        assertThat(((Object[])detectedArray.getDetected()).length, greaterThan(expected));
    }

}
