package com.rest.tests.api.frmw.response.expectation;

import com.rest.tests.api.frmw.response.looking.ILookingObject;

/**
 * Created by msolosh on 3/30/2016.
 */
public interface IExpectation {

    void validate(ILookingObject detectedArray);
}
