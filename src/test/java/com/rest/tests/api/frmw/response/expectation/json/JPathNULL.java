package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import com.rest.tests.api.frmw.response.looking.json.LookingForArray;
import net.minidev.json.JSONArray;

import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;


/**
 * Created by msolosh on 3/30/2016.
 */
public class JPathNULL implements IExpectation {

    public JPathNULL(Object expected) {
    }

    @Override
    public void validate(ILookingObject detected) {
        if (detected instanceof LookingForArray)
        {
            JSONArray arr = (JSONArray)detected.getDetected();
            if (arr.size() > 0)
            {
                for (int som = 0; som < arr.size(); som++)
                {
                    boolean res = arr.get(som) == null;
                    assertTrue(res);
                }

            }
        }
        else
        {
            assertNull(detected, "Expected object is not null.");
        }
    }
}
