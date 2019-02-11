package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Created by msolosh on 3/29/2016.
 */
public class JPathArraySIZE implements IExpectation {

    private int expected;
    private boolean failOnTruth = false;

    public JPathArraySIZE(Object expected) {
        this.expected = (int) ((JSONObject) expected).get("value");
        Object fail = ((JSONObject) expected).get("failOnTruth");
        if (fail != null)
            this.failOnTruth = (boolean)fail;
    }

    @Override
    public void validate(ILookingObject detectedArray){
        if (!failOnTruth)
            assertEquals(expected, ((JSONArray)detectedArray.getDetected()).size());
        else
            assertNotSame(expected, ((JSONArray)detectedArray.getDetected()).size());
    }
}
