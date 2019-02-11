package com.rest.tests.api.frmw.response.looking;

import com.rest.tests.api.frmw.testcase.Response;
import net.minidev.json.JSONObject;
import org.apache.http.Header;
import org.springframework.core.io.ClassPathResource;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by msolosh on 3/28/2016.
 */
public class SaveBody implements IExpectationValidator {

    private String value;


    public SaveBody(JSONObject expect) throws IOException {
        this.value = (String)expect.get("value");
     }

    @Override
    public HashMap<String, String> validation(Response response, String file) throws IOException {

        Object entity = response.getDocument();
        String ext = "";

        Header[] headers = response.getHeaders();// .getHeaders("Content-Disposition")[0].getValue();
        for (Header head : headers)
        {
            if (head.getName().equals("Content-Disposition"))
            {
                ext = head.getValue();
                ext = ext.substring(ext.lastIndexOf("."));continue;
            }
        }

        this.value = value + ext;

        if (entity != null) {
            String inFileName = (new ClassPathResource("SourceFiles").getURI().toString() + File.separatorChar + value).substring(5);

            try
            {
                DataOutputStream os = new DataOutputStream(new FileOutputStream(inFileName));
                os.writeBytes(entity.toString());
                os.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }
}
