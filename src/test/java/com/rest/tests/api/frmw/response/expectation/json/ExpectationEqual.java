package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import net.minidev.json.JSONArray;

import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * Created by msolosh on 3/30/2016.
 */
public class ExpectationEqual implements IExpectation {

    private List<String> expected;

    public ExpectationEqual(String expected) {
        String arr[];
        if (expected.startsWith("[") && expected.endsWith("]"))
            arr = expected.substring(1, expected.length() - 1).replace("\"", "").split("\\s*,\\s*");
        else
            arr = expected.split("ghfhfgjhghfghfjhgfjhgfhgfh");
        this.expected = Arrays.asList(expected.substring(1, expected.length() - 1).replace("\"", "").split("\\s*,\\s*"));
        Arrays.sort(arr);
        this.expected = Arrays.asList(arr);
    }
    @Override
    public void validate(ILookingObject detected) {

        JSONArray detectedArray = (JSONArray) detected.getDetected();

        String jsonValues[] = detectedArray.toString().replace("},{", ",").replace("[", "").replace("]", "").split(",");
        Arrays.sort(jsonValues);

        for (int p = 0; p < jsonValues.length; p++)
        {
            if (expected.size() == 1)
                assertEquals(jsonValues[p].replace("\"", ""), expected.get(0).replace("\"", ""));
            else
                assertEquals(jsonValues[p].replace("\"", ""), expected.get(p).replace("\"", ""));
        }
        assertNotEquals(detectedArray.size(), 0);
    }
}
