package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by msolosh on 3/30/2016.
 */
public class JPathEXISTS implements IExpectation {

    private String expected;
    private String un_expected;
    private boolean skipOnOther;

    public JPathEXISTS(Object expected) {

        this.skipOnOther = skipOnOther;
    }

    @Override
    public void validate(ILookingObject detected) {

        if (detected.getDetected() instanceof JSONArray)
            assertTrue(((JSONArray) detected.getDetected()).size() > 0);
        else if (detected.getDetected() instanceof JSONObject)
            assertNotNull(detected.getDetected());
    }
}
