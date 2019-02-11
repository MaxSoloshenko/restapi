package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import net.minidev.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * Created by msolosh on 3/30/2016.
 */
public class JPathINTEGER implements IExpectation {

    private int expected;
    private boolean failOnTruth = false;

    public JPathINTEGER(Object expected) {
        this.expected = (Integer) ((JSONObject) expected).get("value");
        Object fail = ((JSONObject) expected).get("failOnTruth");
        if (fail != null)
            this.failOnTruth = (boolean)fail;
    }

    @Override
    public void validate(ILookingObject detected) {

        if (!failOnTruth)
            assertEquals(expected, detected.getDetected());
        else
            assertNotEquals(expected, detected.getDetected());
    }
}
