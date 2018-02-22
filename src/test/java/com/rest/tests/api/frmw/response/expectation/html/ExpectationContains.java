package com.rest.tests.api.frmw.response.expectation.html;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import com.rest.tests.api.frmw.response.looking.json.LookingForArray;
import com.rest.tests.api.frmw.response.looking.json.LookingForString;

import static org.testng.AssertJUnit.assertTrue;

/**
 * Created by msolosh on 3/30/2016.
 */
public class ExpectationContains implements IExpectation {

    private Object[] expected;

    public ExpectationContains(String expected) {
        this.expected = expected.substring(1, expected.length() - 1).split(",");
    }

    @Override
    public void validate(ILookingObject detected) {

        if (detected instanceof LookingForArray) {
            Object[] detectedArray = (Object[]) detected.getDetected();

            for (int k = 0; k < expected.length; k++) {
                boolean result = false;
                String exp = (String) expected[k];
                if (exp.startsWith("\"")) {
                    exp = exp.substring(1, exp.length() - 1);

                    for (int i = 0; i < detectedArray.length; i++) {

                        String det = (String) detectedArray[i];

                        if (exp.toLowerCase().equals(det.toLowerCase())) {
                            result = true;
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < detectedArray.length; i++) {

                        Integer det = (Integer) detectedArray[i];

                        if (Integer.parseInt(exp) == det) {
                            result = true;
                            break;
                        }
                    }
                }
                assertTrue("Array does not contain value: '" + exp + "'", result);
            }
        } else if (detected instanceof LookingForString) {
            String det = (String) detected.getDetected();
            String exp = (String) expected[0];
            if (exp.startsWith("\"")) {
                exp = exp.substring(1, exp.length() - 1);
            }
            assertTrue(String.format("Detected %s does not contain %s", det, exp),
                    det.toLowerCase().contains(exp.toLowerCase()));
        }
    }
}
