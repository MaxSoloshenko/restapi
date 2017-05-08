package com.rest.tests.api.frwm.response.looking.html;

import com.jayway.jsonpath.JsonPath;
import com.rest.tests.api.frwm.response.looking.ILookingObject;

/**
 * Created by msolosh on 3/29/2016.
 */
public class LookingForString implements ILookingObject {

    private String detected;
    private String type = "String";

    public LookingForString(String detected) {
        this.detected = detected;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public Object getDetected() {
        return this.detected;
    }
}
