package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import com.rest.tests.api.frmw.response.looking.html.LookingForArray;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * Created by msolosh on 3/30/2016.
 */
public class JPathBOOLEAN implements IExpectation {

    private boolean expected;
    private boolean failOnTruth = false;

    public JPathBOOLEAN(Object expected) {
        this.expected = (boolean) ((JSONObject) expected).get("value");
        Object fail = ((JSONObject) expected).get("failOnTruth");
        if (fail != null)
            this.failOnTruth = (boolean)fail;
    }

    @Override
    public void validate(ILookingObject detected) {
        if (detected instanceof LookingForArray)
        {
            JSONArray arr =  (JSONArray)detected.getDetected();
            boolean bool = (boolean)arr.get(0);

            if (!failOnTruth)
                assertEquals(bool, expected);
            else
                assertNotEquals(bool, expected);
        }
        else
        {
            if (!failOnTruth)
                assertEquals(detected.getDetected(), expected,"Expected objects are not equal: " + detected + " != " + expected);
            else
                assertNotEquals(detected.getDetected(), expected,"Expected objects are equal: " + detected + " != " + expected);
        }
    }
}
