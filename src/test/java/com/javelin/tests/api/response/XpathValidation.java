package com.javelin.tests.api.response;

import com.javelin.tests.api.response.expectation.*;
import com.javelin.tests.api.response.looking.ILookingObject;
import com.javelin.tests.api.response.looking.LookingFactory;
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
public class XpathValidation implements IExpectationValidator{

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
     }

    private void validation(ILookingObject look) {
        IExpectation expect = null;

        if (type.equalsIgnoreCase("XSIZE")) {
            expect = new ExpectationSize(expected);
        } else if (type.equalsIgnoreCase("XCONTAINS")) {
            expect = new ExpectationContains(expected);
        } else if (type.equalsIgnoreCase("XEQUAL")) {
            expect = new ExpectationEqual(expected);
        } else if (type.equalsIgnoreCase("XPATH")) {
            expect = new ExpectationString(expected);
        } else if (type.equalsIgnoreCase("XINTEGER")) {
            expect = new ExpectationInteger(expected);
        }
        else if (type.equalsIgnoreCase("XNULL")) {
            expect = new ExpectationNull();
        }
        else if (type.equalsIgnoreCase("XSIZELESS")) {
            expect = new ExpectationSizeLess(expected);
        }
        else if (type.equalsIgnoreCase("XGREATER") || type.equalsIgnoreCase("XSIZEGREATER")) {
            expect = new ExpectationSizeGreater(expected);
        } else {
            assertTrue(false, String.format("Unknown expectation '%s'\n" +
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
    public HashMap<String, String> validation(Object response) throws IOException {

        ILookingObject detectedObject = LookingFactory.getLookingNode(response, xpath);

        if (!type.equalsIgnoreCase("xnull"))
            Assert.assertNotNull("Expected xpath='" + xpath + "' not found", detectedObject);

        if (!type.equalsIgnoreCase("XVARIABLE")) {
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
                    System.out.println();
                    System.out.println("Nothing found for xpath: " + xpath);
                    return null;
                }
            }
            else
                var = detectedObject.getDetected().toString();
            hm.put(expected, var);
            System.out.println(" = " + var);
            return hm;
        }
    }
}
