package com.rest.tests.api.frwm.response;

import com.rest.tests.api.frwm.testcase.Response;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by msolosh on 3/28/2016.
 */
public interface IExpectationValidator {

//    public HashMap<String, String> validation(Object response, String file) throws IOException;
    public HashMap<String, String> validation(Response response, String file) throws IOException;
}
