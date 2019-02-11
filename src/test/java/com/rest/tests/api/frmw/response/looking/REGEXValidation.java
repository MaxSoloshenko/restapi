package com.rest.tests.api.frmw.response.looking;

import com.rest.tests.api.frmw.testcase.Response;
import net.minidev.json.JSONObject;
import org.testng.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by msolosh on 3/28/2016.
 */
public class REGEXValidation implements IExpectationValidator {

    private String regex;
    private String expected;
    private String variable;
    private String type;

    public REGEXValidation(JSONObject expect) {

        this.type = (String) expect.get("type");
        if (((String) expect.get("type")).equalsIgnoreCase("REGEXVARIABLE"))
        {
            this.variable = (String)expect.get("value");
        }
        else if (((String) expect.get("type")).equalsIgnoreCase("REGEXEQUAL"))
        {
            this.expected = (String)expect.get("value");
        }
        else if (((String) expect.get("type")).equalsIgnoreCase("REGEXCONTAINS"))
        {
            this.expected = "";
        }
        this.regex = (String)expect.get("regex");
     }

    @Override
    public HashMap<String, String> validation(Response response, String file) throws IOException {

        Object document = response.getDocument();

        String var = null;
        String body = document.toString();
        Pattern p = Pattern.compile(regex);

        try {
            Matcher m = p.matcher(body);

            while (m.find()) {
                var = m.group(1);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        if (variable == null) {
            if (type.equals("REGEXCONTAINS"))
            {
                Assert.assertNotNull(var, "Expected object is not found: " + regex);
            }
            else if (type.equals("REGEXEQUAL"))
            {
                Assert.assertEquals(expected, var, "Expected object '" + expected + "' is not equal detected: " + var);
            }
            return null;
        }
        else {
            HashMap<String, String> hm = new HashMap();
            hm.put(variable, var);
            return hm;
        }
    }
}
