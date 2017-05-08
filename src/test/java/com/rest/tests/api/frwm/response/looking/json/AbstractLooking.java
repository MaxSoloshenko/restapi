package com.rest.tests.api.frwm.response.looking.json;

import com.rest.tests.api.frwm.response.looking.ILookingObject;

/**
 * Created by msolosh on 3/31/2016.
 */
public class AbstractLooking implements ILookingObject {
    @Override
    public String getType() {
        return null;
    }

    @Override
    public Object getDetected() {
        return null;
    }
}
