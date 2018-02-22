package com.rest.tests.api.frmw.response;

import com.rest.tests.api.frmw.testcase.Response;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by msolosh on 3/28/2016.
 */
public interface IExpectationValidator {

    public HashMap<String, String> validation(Response response, String file) throws IOException;
}
