package com.rest.tests.api.frmw.response.expectation.json;

import com.rest.tests.api.frmw.response.expectation.IExpectation;
import com.rest.tests.api.frmw.response.looking.ILookingObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * Created by msolosh on 3/30/2016.
 */
public class JPathArrayEQUAL implements IExpectation {

    private Object[] expected;
    private boolean failOnTruth = false;

    public JPathArrayEQUAL(Object expected) {

        this.expected = sortArray(((JSONArray) ((JSONObject) expected).get("value")).toArray());

        Object fail = ((JSONObject) expected).get("failOnTruth");
        if (fail != null)
            this.failOnTruth = (boolean)fail;
    }

    @Override
    public void validate(ILookingObject detected) {

        JSONArray detectedArray = ((JSONArray) detected.getDetected());

        Object[] dtcd = sortArray(detectedArray.toArray());

        if (!failOnTruth)
            assertEquals(dtcd, expected, "Detected array is not equal to expected.");
        else
            assertNotEquals(dtcd, expected, "Detected array is not equal to expected.");
    }

    private Object[] sortArray(Object[] input)
    {
        HashMap<String, Object> map = new HashMap<>();

        for (int i = 0; i < input.length; i++)
        {

            String class_name = input[i].getClass().getName().substring(input[i].getClass().getName().lastIndexOf(".") + 1);

            ArrayList<Object> objs = (ArrayList<Object>) map.get(class_name);

            if (objs == null)
            {
                objs = new ArrayList<Object>();
            }

            objs.add(input[i]);
            map.put(class_name, objs);
        }

        // TreeMap to store values of HashMap
        TreeMap<String, Object> sorted = new TreeMap<>();
//        Object[] fnl = new Object[input.length];
        Object[] fnl = new Object[0];

        // Copy all data from hashMap into TreeMap
        sorted.putAll(map);

        // Display the TreeMap which is naturally sorted

        for (Map.Entry<String, Object> entry : sorted.entrySet())
        {
            Object[] obj = ((ArrayList) map.get(entry.getKey())).toArray();
            Arrays.sort(obj);
            fnl = (Object[]) ArrayUtils.addAll(fnl, obj);
        }

        return fnl;
    }
}
