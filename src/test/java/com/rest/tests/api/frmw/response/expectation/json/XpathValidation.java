package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.IExpectationValidator;
import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import com.rest.tests.api.frmw.response.looking.json.LookingFactory;
import com.rest.tests.api.frmw.settings.Tools;
import com.rest.tests.api.frmw.testcase.Response;
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
        if (expect.get("value") instanceof JSONArray)
        {
            expected = ((JSONArray) expect.get("value")).toJSONString();
        }
        else if (expect.get("value") instanceof String)
        {
            expected = (String)expect.get("value");
        }
        else if (expect.get("value") instanceof JSONObject)
        {
            expected = ((JSONObject) expect.get("value")).toJSONString();
        }
        else if (expect.get("value") instanceof Long)
        {
            expected = expect.get("value").toString();
        }
        else if (expect.get("value") == null)
        {
            expected = null;
        }
        else if (expect.get("value") instanceof Boolean)
        {
            expected = expect.get("value").toString();
        }
        else
        {
            System.out.println("***************:" + expect.toJSONString());
        }

     }

    private void validation(ILookingObject look) {
        IExpectation expect = null;

        if (type.equalsIgnoreCase("JPathSIZE")) {
            expect = new ExpectationSize(expected);
        } else if (type.equalsIgnoreCase("JPathCONTAINS")) {
            expect = new ExpectationContains(expected);
        } else if (type.equalsIgnoreCase("JPathEQUAL")) {
            expect = new ExpectationEqual(expected);
        } else if (type.equalsIgnoreCase("JPathPATH")) {
            expect = new ExpectationString(expected);
        } else if (type.equalsIgnoreCase("JPathBOOLEAN")) {
            expect = new ExpectationBoolean(expected);
        } else if (type.equalsIgnoreCase("JPathINTEGER")) {
            expect = new ExpectationInteger(expected);
        }
        else if (type.equalsIgnoreCase("JPathNULL")) {
            expect = new ExpectationNull();
        }
        else if (type.equalsIgnoreCase("JPathSIZELESS")) {
            expect = new ExpectationSizeLess(expected);
        }
        else if (type.equalsIgnoreCase("JPathGREATER") || type.equalsIgnoreCase("JPathSIZEGREATER")) {
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
    public HashMap<String, String> validation(Response response, String file) throws IOException {

        Object document = response.getDocument();

        ILookingObject detectedObject = LookingFactory.getLookingNode(document, xpath);

        if (!type.equalsIgnoreCase("JPathnull"))
            Assert.assertNotNull("Expected JsonPath='" + xpath + "' not found", detectedObject);

        if (!type.equalsIgnoreCase("JPathvariable")) {
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
                    org.springframework.util.Assert.notNull(null, "Nothing found for Jsonpath: " + xpath);
                }
            }
            else
                var = detectedObject.getDetected().toString();
            hm.put(((String)expected).toLowerCase(), var);
            Tools.writeToFile(file," = " + var);
            return hm;
        }
    }
}
