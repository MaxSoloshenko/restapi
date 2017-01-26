package com.javelin.tests.api.response;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by msolosh on 3/28/2016.
 */
public class StatusValidation implements IExpectationValidator{

    int status;

    public StatusValidation(int expect) {
        this.status = expect;
    }

    @Override
    public HashMap<String, String> validation(Object response){
        assertEquals(String.valueOf("Response Status Code is wrong."), status, Integer.parseInt(response.toString()));
        return null;
    }
}
