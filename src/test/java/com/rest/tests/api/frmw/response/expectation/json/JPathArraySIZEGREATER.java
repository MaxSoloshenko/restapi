package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

/**
 * Created by msolosh on 3/29/2016.
 */
public class JPathArraySIZEGREATER implements IExpectation {

    private int expected;
    private boolean failOnTruth = false;

    public JPathArraySIZEGREATER(Object expected) {

        this.expected = (int) ((JSONObject) expected).get("value");
        Object fail = ((JSONObject) expected).get("failOnTruth");
        if (fail != null)
            this.failOnTruth = (boolean)fail;
    }

    @Override
    public void validate(ILookingObject detectedArray){
        if (!failOnTruth)
            assertThat(((JSONArray)detectedArray.getDetected()).size(), greaterThan(expected));
        else
            assertThat(((JSONArray)detectedArray.getDetected()).size(), lessThanOrEqualTo(expected));
    }
}
