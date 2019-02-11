package com.rest.tests.api.frmw.response.looking;

import com.rest.tests.api.frmw.response.expectation.html.HpathValidation;
import com.rest.tests.api.frmw.response.expectation.json.XpathValidation;
import net.minidev.json.JSONObject;

/**
 * Created by msolosh on 3/30/2016.
 */
public class ExpectedFactory {

    public static IExpectationValidator getExpectedObject(JSONObject expect) throws Exception {

        String type = (String)expect.get("type");

        if (type.toLowerCase().startsWith("jpath")) {
            return new XpathValidation(expect);
        }
        else if (type.startsWith("REGEX")) {
            return new REGEXValidation(expect);
        }
        else if (type.toLowerCase().equals("savebody")) {
            return new SaveBody(expect);
        }
        else if (type.toLowerCase().equals("comparepdf")) {
            return new PDFComparison(expect);
        }
        else if (type.toLowerCase().equals("comparezip")) {
            return new ZIPComparison(expect);
        }
        else if (type.startsWith("h")) {
            return new HpathValidation(expect);
        }
        else if (type.toLowerCase().equals("status")) {
            Integer status = (Integer)expect.get("value");
            return new StatusValidation(status.intValue());
        }
        else if (type.equalsIgnoreCase("contains")) {
            return new StringValidation(expect);
        }
        else {
            throw new Exception("Unknown jexpectation type: " + type);
        }
    }
}
