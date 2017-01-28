package com.rest.tests.api.frwm.response.expectation;

import com.rest.tests.api.frwm.response.looking.ILookingObject;
import net.minidev.json.JSONArray;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * Created by msolosh on 3/30/2016.
 */
public class ExpectationEqual implements IExpectation {

    private List<String> expected;

    public ExpectationEqual(String expected) {
        this.expected = Arrays.asList(expected.substring(1, expected.length() - 1).split("\\s*,\\s*"));
    }
    @Override
    public void validate(ILookingObject detected) {

        JSONArray detectedArray = (JSONArray) detected.getDetected();

        for (int p = 0; p < detectedArray.size(); p++)
        {
            String det = "";
            if (detectedArray.get(p) instanceof Integer)
            {
                det = Integer.toString((Integer) detectedArray.get(p));
            }
            else if (detectedArray.get(p) instanceof Boolean)
            {
                det = Boolean.toString((Boolean) detectedArray.get(p));
            }
            else
            {
                det = (String) detectedArray.get(p);
            }
            assertEquals(det, expected.get(p).replace("\"", ""));
        }
        assertNotEquals(detectedArray.size(), 0);
    }
}
