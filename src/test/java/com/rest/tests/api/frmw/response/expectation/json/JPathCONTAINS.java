package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import net.minidev.json.JSONObject;

import static org.junit.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Created by msolosh on 3/30/2016.
 */
public class JPathCONTAINS implements IExpectation {

    private String expected;
    private boolean failOnTruth = false;

    public JPathCONTAINS(Object expected) {
        this.expected = (String) ((JSONObject) expected).get("value");
        Object fail = ((JSONObject) expected).get("failOnTruth");
        if (fail != null)
            this.failOnTruth = (boolean)fail;
    }

    @Override
    public void validate(ILookingObject detected) {

        String det = detected.getDetected().toString();

        if (!failOnTruth)
            assertTrue(String.format("Detected %s does not contain %s", det, expected), det.contains(expected));
        else
            assertFalse(String.format("Detected %s contains %s", det, expected), det.contains(expected));
    }
}
