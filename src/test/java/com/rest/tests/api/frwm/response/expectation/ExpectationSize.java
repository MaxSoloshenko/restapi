package com.rest.tests.api.frwm.response.expectation;

import com.rest.tests.api.frwm.response.looking.ILookingObject;
import net.minidev.json.JSONArray;

import static org.junit.Assert.assertEquals;

/**
 * Created by msolosh on 3/29/2016.
 */
public class ExpectationSize implements IExpectation{

    private int expected;

    public ExpectationSize(String expected) {
        this.expected = Integer.parseInt(expected);
    }

    @Override
    public void validate(ILookingObject detectedArray){
        assertEquals(expected, ((JSONArray)detectedArray.getDetected()).size());
    }

}
