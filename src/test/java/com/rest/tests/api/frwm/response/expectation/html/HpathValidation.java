package com.rest.tests.api.frwm.response.expectation.html;

import com.rest.tests.api.frwm.response.IExpectationValidator;
import com.rest.tests.api.frwm.response.expectation.IExpectation;
import com.rest.tests.api.frwm.response.expectation.html.*;
import com.rest.tests.api.frwm.response.looking.ILookingObject;
import com.rest.tests.api.frwm.response.looking.html.LookingFactory;
import com.rest.tests.api.frwm.settings.Tools;
import junit.framework.Assert;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by msolosh on 3/28/2016.
 */
public class HpathValidation implements IExpectationValidator {

    private String xpath;
    private String expected;
    private String type;

    public HpathValidation(JSONObject expect) {
        xpath = (String)expect.get("xpath");
        type = (String)expect.get("type");
        if (expect.get("value") instanceof String)
        {
            expected = (String)expect.get("value");
        }
        else if (expect.get("value") instanceof JSONArray)
        {
            JSONArray array = (JSONArray)expect.get("value");
            expected = array.toString();
        }
        else if (expect.get("value") instanceof Long)
        {
            Long val = (Long)expect.get("value");
            expected = val.toString();
        }
     }

    private void validation(ILookingObject look) {
        IExpectation expect = null;

        if (type.equalsIgnoreCase("hXSIZE")) {
            expect = new ExpectationSize(expected);
        }
        else if (type.equalsIgnoreCase("hXCONTAINS")) {
            expect = new ExpectationContains(expected);
        }
        else if (type.equalsIgnoreCase("hXEQUAL")) {
            expect = new ExpectationEqual(expected);
        }
        else if (type.equalsIgnoreCase("hXINTEGER")) {
            expect = new ExpectationInteger(expected);
        }
        else if (type.equalsIgnoreCase("hXNULL")) {
            expect = new ExpectationNull();
        }
        else if (type.equalsIgnoreCase("hXPATH")) {
            expect = new ExpectationString(expected);
        }
        else if (type.equalsIgnoreCase("hXSIZELESS")) {
            expect = new ExpectationSizeLess(expected);
        }
        else if (type.equalsIgnoreCase("hXGREATER") || type.equalsIgnoreCase("hXSIZEGREATER")) {
            expect = new ExpectationSizeGreater(expected);
        }
        else {
            assertTrue(false, String.format("Unknown jexpectation '%s'"));
        }

        if ((look != null) || (look == null && expect instanceof ExpectationNull))
            expect.validate(look);
        else
        {
            assertNotNull(look);
        }
    }

    @Override
    public HashMap<String, String> validation(Object response, String file) throws IOException {

        ILookingObject detectedObject = LookingFactory.getLookingNode(response, xpath);

        if (!type.equalsIgnoreCase("hxnull"))
            Assert.assertNotNull("Expected xpath='" + xpath + "' not found", detectedObject);
        if (!type.equalsIgnoreCase("hXVARIABLE")) {
            validation(detectedObject);
            return null;
        }
        else {
            HashMap<String, String> hm = new HashMap();
            String var;

            if (detectedObject.getType().equals("Array")) {
                var = detectedObject.getDetected().toString();
                if (var.startsWith("[\""))
                {
                    var = var.substring(2, var.length() - 2);
                }
                else if (var.equals("[]")) {
//                    System.out.println();
                    System.out.println("Nothing found for xpath: " + xpath);
                    return null;
                }
            }
            else
                var = detectedObject.getDetected().toString();
            hm.put(expected, var);
            Tools.writeToFile(file," = " + var);
            return hm;
        }
    }
}
