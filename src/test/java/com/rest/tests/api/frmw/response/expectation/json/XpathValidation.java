package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.IExpectationValidator;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import com.rest.tests.api.frmw.response.looking.json.LookingFactory;
import com.rest.tests.api.frmw.settings.Tools;
import com.rest.tests.api.frmw.testcase.Response;
import junit.framework.Assert;
import net.minidev.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.util.HashMap;

import static org.testng.Assert.assertNotNull;

/**
 * Created by msolosh on 3/28/2016.
 */
public class XpathValidation implements IExpectationValidator {

    private String xpath;
    private Object expected;
    private String type;
    private String valueFail;
    private boolean skipOnOther = false;
    private boolean failOnTruth = false;
    private Object exp;

    public XpathValidation(JSONObject expect) {
        exp = (Object)expect;

        xpath = (String)expect.get("xpath");
        type = (String)expect.get("type");
//        valueFail = (String)expect.get("valueFail");
//        if (expect.get("skipOnOther") != null)
//        {
//            this.skipOnOther = (boolean)expect.get("skipOnOther");
//        }
//
//        if (expect.get("failOnTruth") != null)
//        {
//            this.failOnTruth = (boolean)expect.get("failOnTruth");
//        }
//
        expected = (Object) expect.get("value");
     }

    private void validation(ILookingObject look) throws Exception {
        IExpectation expect = null;
        String path = this.getClass().getCanonicalName()
                .substring(0, this.getClass().getCanonicalName().lastIndexOf("."));

        try {

            Class<?> clazz = Class.forName(path + "." + type);
//            Constructor<?> constructor = clazz.getConstructor(Object.class, String.class, boolean.class);
            Constructor<?> constructor = clazz.getConstructor(Object.class);
            Object instance = constructor.newInstance(exp);
//            Object instance = constructor.newInstance(expected, valueFail, skipOnOther, failOnTruth);
            expect = (IExpectation)instance;
        }
        catch (InvocationTargetException t)
        {
            String msg = t.getTargetException().getMessage();
            throw new Tools.MyException(String.format("Expected value %s as %s", expected, msg) );
        }
        catch (Throwable e) {

            String pathh = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + getClass().getName().replace(".", FileSystems.getDefault().getSeparator());
            String list = Tools.findClasses(pathh);

            throw new Tools.MyException(String.format("Unknown type of expectation '%s'\n" + "Known expectations:\n" + list, type));
        }
        if ((look != null) || (look == null && expect instanceof JPathNULL))
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

        if (!type.equalsIgnoreCase("JPathnull")
                && !type.equalsIgnoreCase("JPathvariable"))
            Assert.assertNotNull("Expected JsonPath='" + xpath + "' not found", detectedObject);

        if (!type.equalsIgnoreCase("JPathvariable")) {
            validation(detectedObject);
            return null;
        }
        else if (type.equalsIgnoreCase("JPathEXISTS"))
        {
            return null;
        }
        else {
            HashMap<String, String> hm = new HashMap();
            String var;

            if (detectedObject != null)
            {

                if (detectedObject.getType().equals("Array")) {
                    var = detectedObject.getDetected().toString();
                    if (var.startsWith("[\""))
                    {
                        var = var.substring(2, var.length() - 2);
                    }
                    else if (var.equals("[]") || var == null) {
                        org.springframework.util.Assert.notNull(null, "Nothing found for Jsonpath: " + xpath);
                    }
                }
                else {
                    var = detectedObject.getDetected().toString();
                }
                hm.put(((String)expected.toString()).toLowerCase(), var);
                Tools.writeToFile(file," = " + var);
            }
            else
                org.springframework.util.Assert.notNull(null, "Nothing found for Jsonpath: " + xpath);
//                hm.put(((String)expected).toLowerCase(), null);
            return hm;
        }
    }
}
