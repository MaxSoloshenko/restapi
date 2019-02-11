package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import com.rest.tests.api.frmw.response.looking.html.LookingForArray;
import com.rest.tests.api.frmw.response.looking.html.LookingForString;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.Arrays;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Created by msolosh on 3/30/2016.
 */
public class JPathArrayCONTAINS implements IExpectation {

    private JSONArray expected;
    private boolean failOnTruth = false;

    public JPathArrayCONTAINS(Object expected) {
        this.expected = (JSONArray) ((JSONObject) expected).get("value");
        Object fail = ((JSONObject) expected).get("failOnTruth");
        if (fail != null)
            this.failOnTruth = (boolean)fail;
    }

    @Override
    public void validate(ILookingObject detected) {

        if (detected instanceof LookingForArray) {
            JSONArray detectedArray = (JSONArray) detected.getDetected();
            if (!failOnTruth)
                assertTrue(Arrays.asList(detectedArray.toArray()).containsAll(Arrays.asList(expected.toArray())));
            else
                assertFalse(Arrays.asList(detectedArray.toArray()).containsAll(Arrays.asList(expected.toArray())));

        } else if (detected instanceof LookingForString) {
            String det = (String) detected.getDetected();
            String exp = (String) expected.get(0);
            if (exp.startsWith("\"")) {
                exp = exp.substring(1, exp.length() - 1);
            }
            if (!failOnTruth)
                assertTrue(String.format("Detected %s does not contain %s", det, exp),
                    det.toLowerCase().contains(exp.toLowerCase()));
            else
                assertFalse(String.format("Detected %s contains %s", det, exp),
                        det.toLowerCase().contains(exp.toLowerCase()));
        }
    }
}
