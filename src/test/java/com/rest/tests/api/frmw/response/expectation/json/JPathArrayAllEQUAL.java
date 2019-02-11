package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;


/**
 * Created by msolosh on 3/30/2016.
 */
public class JPathArrayAllEQUAL implements IExpectation {

    private Object expected;
    private boolean failOnTruth = false;

    public JPathArrayAllEQUAL(Object expected) {

        this.expected = (Object) ((JSONObject) expected).get("value");
        Object fail = ((JSONObject) expected).get("failOnTruth");
        if (fail != null)
            this.failOnTruth = (boolean)fail;
    }

    @Override
    public void validate(ILookingObject detected) {
        JSONArray detd = (JSONArray) detected.getDetected();

        for (int k=0; k < detd.size(); k++)
        {
            if (failOnTruth)
                assertNotEquals(((JSONArray) expected).get(0), detd.get(k));
            else
                assertEquals(((JSONArray) expected).get(0), detd.get(k));
        }
    }
}
