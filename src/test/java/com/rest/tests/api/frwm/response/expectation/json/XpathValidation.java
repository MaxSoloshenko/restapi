package com.rest.tests.api.frwm.response.expectation.json;

import com.rest.tests.api.frwm.response.IExpectationValidator;
import com.rest.tests.api.frwm.response.expectation.IExpectation;
import com.rest.tests.api.frwm.response.looking.ILookingObject;
import com.rest.tests.api.frwm.response.looking.json.LookingFactory;
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
public class XpathValidation implements IExpectationValidator {

    private String xpath;
    private String expected;
    private String type;

    public XpathValidation(JSONObject expect) {
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
        else if (expect.get("value") instanceof Boolean)
        {
            Boolean val = (Boolean)expect.get("value");
            expected = val.toString();
        }
     }

    private void validation(ILookingObject look) {
        IExpectation expect = null;

        if (type.equalsIgnoreCase("jXSIZE")) {
            expect = new ExpectationSize(expected);
        } else if (type.equalsIgnoreCase("jXCONTAINS")) {
            expect = new ExpectationContains(expected);
        } else if (type.equalsIgnoreCase("jXEQUAL")) {
            expect = new ExpectationEqual(expected);
        } else if (type.equalsIgnoreCase("jXPATH")) {
            expect = new ExpectationString(expected);
        } else if (type.equalsIgnoreCase("jXBOOLEAN")) {
            expect = new ExpectationBoolean(expected);
        } else if (type.equalsIgnoreCase("jXINTEGER")) {
            expect = new ExpectationInteger(expected);
        }
        else if (type.equalsIgnoreCase("jXNULL")) {
            expect = new ExpectationNull();
        }
        else if (type.equalsIgnoreCase("jXSIZELESS")) {
            expect = new ExpectationSizeLess(expected);
        }
        else if (type.equalsIgnoreCase("jXGREATER") || type.equalsIgnoreCase("jXSIZEGREATER")) {
            expect = new ExpectationSizeGreater(expected);
        }
        else {
            assertTrue(false, String.format("Unknown jexpectation '%s'\n" +
                    "Known expectations:\n" +
                    "'size()'\n" +
                    "'contains()'\n" +
                    "'equal()'\n" +
                    "String value\n" +
                    "Integer value\n" +
                    "sizegreaterthan()\n" +
                    "sizelessthan()", expected));
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

        if (!type.equalsIgnoreCase("xnull"))
            Assert.assertNotNull("Expected xpath='" + xpath + "' not found", detectedObject);

        if (!type.equalsIgnoreCase("JXVARIABLE")) {
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
                    org.springframework.util.Assert.notNull(null, "Nothing found for xpath: " + xpath);
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
