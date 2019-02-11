package com.rest.tests.api.frmw.response.looking.json;

import com.jayway.jsonpath.JsonPath;
import com.rest.tests.api.frmw.response.looking.ILookingObject;

import java.math.BigDecimal;

/**
 * Created by msolosh on 3/29/2016.
 */
public class LookingForBigDecimal implements ILookingObject {

    private String type = "BigDecimal";
    private BigDecimal detected;

    public LookingForBigDecimal(Object response, String xpath) {
        this.detected = JsonPath.read(response, xpath);
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
