package com.rest.tests.api.frmw.response.looking.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

/**
 * Created by msolosh on 3/29/2016.
 */
public class LookingFactory{

    public static ILookingObject getLookingNode(Object doc, String xpath) {

        Object obj = null;
        Object document = Configuration.defaultConfiguration().jsonProvider().parse((String)doc);
        try {
            obj = JsonPath.read(document, xpath);
        } catch (Exception e) {
            return null;
        }

        if (obj instanceof JSONArray) {
            return new LookingForArray(document, xpath);
        }
        else if (obj instanceof JSONObject)
        {
            return new LookingForObject(document, xpath);
        }
        else if (obj instanceof Integer)
        {
            return new LookingForInteger(document, xpath);
        }
        else if (obj instanceof String)
        {
            return new LookingForString(document, xpath);
        }
        else if (obj instanceof Boolean)
        {
            return new LookingForBoolean(document, xpath);
        }
        else if (obj instanceof BigDecimal)
        {
            return new LookingForBigDecimal(document, xpath);
        }
        else if (obj instanceof LinkedHashMap)
        {
            return new LookingForJSONObject(document, xpath);
        }
        else
        {
            return null;
        }
    }
}
