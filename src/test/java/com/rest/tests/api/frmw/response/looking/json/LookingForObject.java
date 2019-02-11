package com.rest.tests.api.frmw.response.looking.json;


import com.rest.tests.api.frmw.response.looking.ILookingObject;

/**
 * Created by msolosh on 3/29/2016.
 */
public class LookingForObject extends AbstractLooking implements ILookingObject {

    private Object response;
    private String xpath;
    private String type = "Object";

    public LookingForObject(Object response, String xpath) {
        this.response = response;
        this.xpath = xpath;
    }

}
