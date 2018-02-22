package com.rest.tests.api.frmw.response.looking.html;

import com.rest.tests.api.frmw.response.looking.ILookingObject;

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
