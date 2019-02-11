package com.rest.tests.api.frmw.response.looking.html;

import com.rest.tests.api.frmw.response.looking.ILookingObject;

/**
 * Created by msolosh on 5/7/17.
 */
public class LookingForArray implements ILookingObject {

    private String type = "Array";
    private Object[] detected;

    public LookingForArray(Object[] nodes) {
        this.detected = nodes;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public Object[] getDetected() {
        return this.detected;
    }
}
