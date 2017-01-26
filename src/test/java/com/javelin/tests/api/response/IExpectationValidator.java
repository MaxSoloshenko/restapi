package com.javelin.tests.api.response;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by msolosh on 3/28/2016.
 */
public interface IExpectationValidator {

    public HashMap<String, String> validation(Object response) throws IOException;
}
