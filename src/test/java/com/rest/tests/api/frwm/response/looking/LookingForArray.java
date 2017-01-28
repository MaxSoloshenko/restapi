package com.rest.tests.api.frwm.response.looking;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

/**
 * Created by msolosh on 3/29/2016.
 */
public class LookingForArray implements ILookingObject{

    private String type = "Array";
    private JSONArray detected;

    public LookingForArray(Object response, String xpath) {
        this.detected = JsonPath.read(response, xpath);
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public JSONArray getDetected() {
        return this.detected;
    }
}
