package com.javelin.tests.api.response.expectation;

import com.javelin.tests.api.response.looking.ILookingObject;

/**
 * Created by msolosh on 3/30/2016.
 */
public interface IExpectation {

    void validate(ILookingObject detectedArray);
}
