package com.rest.tests.api.frmw.response.looking.json;

import com.jayway.jsonpath.JsonPath;
import com.rest.tests.api.frmw.response.looking.ILookingObject;

import java.util.LinkedHashMap;

/**
 * Created by msolosh on 3/29/2016.
 */
public class LookingForJSONObject implements ILookingObject {

    private String type = "LinkedHashMap";
    private LinkedHashMap detected;

    public LookingForJSONObject(Object response, String xpath) {
        this.detected = JsonPath.read(response, xpath);
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public LinkedHashMap getDetected() {
        return this.detected;
    }
}
