package com.rest.tests.api.frmw.response.expectation.html;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.IExpectationValidator;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import com.rest.tests.api.frmw.response.looking.html.LookingFactory;
import com.rest.tests.api.frmw.settings.Tools;
import com.rest.tests.api.frmw.testcase.Response;
import junit.framework.Assert;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.lang.reflect.Constructor;
import java.nio.file.FileSystems;
import java.util.HashMap;

import static org.testng.Assert.assertNotNull;

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

    private void validation(ILookingObject look) throws Exception {

        IExpectation expect = null;
        String path = this.getClass().getCanonicalName()
                .substring(0, this.getClass().getCanonicalName().lastIndexOf("."));

        try {

            Class<?> clazz = Class.forName(path + "." + type);
            Constructor<?> constructor = clazz.getConstructor(String.class);
            Object instance = constructor.newInstance(expected);
            expect = (IExpectation)instance;
        }
        catch (Throwable e) {

            String pathh = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + getClass().getName().replace(".", FileSystems.getDefault().getSeparator());
            String list = Tools.findClasses(pathh);

            throw new Tools.MyException(String.format("Unknown type of expectation '%s'\n" + "Known expectations:\n" + list, type));
        }


        if ((look != null) || (look == null && expect instanceof hXNULL))
            expect.validate(look);
        else
        {
            assertNotNull(look);
        }
    }

    @Override
    public HashMap<String, String> validation(Response response, String file) throws Exception {

        Object document = response.getDocument();

        ILookingObject detectedObject = LookingFactory.getLookingNode(document, xpath);

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
