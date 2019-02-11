package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import com.rest.tests.api.frmw.response.looking.html.LookingForArray;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import static org.testng.Assert.*;

/**
 * Created by msolosh on 3/30/2016.
 */
public class JPathPATH implements IExpectation {

    private Object expected;
    private String un_expected;
    private boolean skipOnOther;
    private boolean failOnTruth = false;

    public JPathPATH(Object expected) {
        JSONObject expect = (JSONObject)expected;
        this.expected = (Object) expect.get("value");
        this.un_expected = (String)expect.get("valueFail");
        if (expect.get("skipOnOther") != null)
        {
            this.skipOnOther = (boolean)expect.get("skipOnOther");
        }

        if (expect.get("failOnTruth") != null)
        {
            this.failOnTruth = (boolean)expect.get("failOnTruth");
        }
    }

    @Override
    public void validate(ILookingObject detected) {

        Object det = "";

        if(detected instanceof LookingForArray)
        {
            JSONArray arr = (JSONArray)detected.getDetected();
            assertTrue(arr.size() > 0);
            det = (String)arr.get(0);
        }
        else
            det = detected.getDetected();

        if (failOnTruth)
            assertNotEquals(det, expected);
        else
            assertEquals(det, expected);
    }
}
