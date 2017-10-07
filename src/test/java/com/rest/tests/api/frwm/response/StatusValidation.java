package com.rest.tests.api.frwm.response;

import com.jayway.jsonpath.Configuration;
import com.rest.tests.api.frwm.testcase.Expectations.Expectation;
import com.rest.tests.api.frwm.testcase.Response;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by msolosh on 3/28/2016.
 */
public class StatusValidation implements IExpectationValidator{

    int status;

    public StatusValidation(int value) {
        this.status = value;
    }

    @Override
    public HashMap<String, String> validation(Response response, String file) throws IOException {
        int status = response.getStatus();
        assertEquals(String.valueOf("Response Status Code is wrong."), this.status, status);
        return null;
    }
}
