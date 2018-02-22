package com.rest.tests.api.frmw.response.expectation.html;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;

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

        Object[] detectedArray = (Object[]) detected.getDetected();

        for (int p = 0; p < detectedArray.length; p++)
        {
            String det = "";
            if (detectedArray[p] instanceof Integer)
            {
                det = Integer.toString((Integer) detectedArray[p]);
            }
            else if (detectedArray[p] instanceof Boolean)
            {
                det = Boolean.toString((Boolean) detectedArray[p]);
            }
            else
            {
                det = (String) detectedArray[p];
            }
            assertEquals(det, expected.get(p).replace("\"", ""));
        }
        assertNotEquals(detectedArray.length, 0);
    }
}
