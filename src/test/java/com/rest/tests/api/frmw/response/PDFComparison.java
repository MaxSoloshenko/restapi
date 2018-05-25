package com.rest.tests.api.frmw.response;

import com.rest.tests.api.frmw.testcase.Response;
import de.redsix.pdfcompare.PdfComparator;
import org.json.simple.JSONObject;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by msolosh on 3/28/2016.
 */
public class PDFComparison implements IExpectationValidator {

    private String expected;
    private String detected;
    ClassLoader classLoader = getClass().getClassLoader();

    public PDFComparison(JSONObject expect) {
        this.expected = (String)expect.get("expected");
        this.detected = (String)expect.get("detected");
     }

    @Override
    public HashMap<String, String> validation(Response response, String file) throws IOException {

        String base = classLoader.getResource("SourceFiles/").toString().substring(5);
        File fileDet =new File(base + detected);
        File fileExp =new File(base + expected);

        Assert.assertTrue(fileDet.length() - fileExp.length() < 500, "File size is too different. Exp: " + fileExp.length() + " Det: " + fileDet.length());
        Assert.assertTrue(fileExp.length() - fileDet.length() < 500, "File size is too different. Exp: " + fileExp.length() + " Det: " + fileDet.length());

        boolean isEquals = new PdfComparator().compare(base + detected, base + expected).writeTo(base + "diffOutput");
        Assert.assertTrue(isEquals, "Pdf files are not equal.");
        return null;
    }
}
