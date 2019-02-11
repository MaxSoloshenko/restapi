package com.rest.tests.api.frmw.response.looking.html;


import com.rest.tests.api.frmw.response.looking.ILookingObject;

/**
 * Created by msolosh on 3/29/2016.
 */
public class LookingForInteger implements ILookingObject {

    private String type = "Integer";
    private int detected;

    public LookingForInteger(Integer inte) {
        this.detected = inte;
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
