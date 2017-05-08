package com.rest.tests.api.frwm.response.expectation.json;

import com.rest.tests.api.frwm.response.expectation.IExpectation;
import com.rest.tests.api.frwm.response.looking.ILookingObject;
import com.rest.tests.api.frwm.response.looking.json.LookingForArray;
import com.rest.tests.api.frwm.response.looking.json.LookingForString;
import net.minidev.json.JSONArray;

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
            JSONArray detectedArray = (JSONArray) detected.getDetected();

            for (int k = 0; k < expected.length; k++) {
                boolean result = false;
                String exp = (String) expected[k];
                if (exp.startsWith("\"")) {
                    exp = exp.substring(1, exp.length() - 1);

                    for (int i = 0; i < detectedArray.size(); i++) {

                        String det = (String) detectedArray.get(i);

                        if (exp.toLowerCase().equals(det.toLowerCase())) {
                            result = true;
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < detectedArray.size(); i++) {

                        Integer det = (Integer) detectedArray.get(i);

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
