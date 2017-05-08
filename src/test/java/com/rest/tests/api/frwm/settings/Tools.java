package com.rest.tests.api.frwm.settings;

import com.rest.tests.api.frwm.testcase.Testcase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by msolosh on 4/7/2016.
 */
public class Tools {

    public static void printFixLineString(String text, String pattern){

        System.out.println();
        int size = text.length();
        int p = (120 - size - 2);
        if (p > 0)
        {
            String patt = new String(new char[p]).replace('\0', pattern.toCharArray()[0]);
            System.out.println(patt.substring(0, p/2) + " " + text + " " + patt.substring(p/2, patt.length()));
        }
        else
        {
            System.out.println(text);
        }
    }


    public static String replaceVariables(String expect, String name, HashMap<String, HashMap<String, String>> variables)
    {
        String pattern1 = "(%[\\w]*)";
        Pattern p = Pattern.compile(pattern1);


        HashMap<String, String> map = variables.get(name);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        if (map != null)
            list.add(map);
        list.add(variables.get("all"));
        try {
            Matcher m = p.matcher(expect);

            while (m.find()) {
                String variable = m.group();

                for(HashMap<String, String> lst : list) {
                    Set set = lst.entrySet();
                    Iterator iterator = set.iterator();
                    while(iterator.hasNext()) {
                        Map.Entry mentry = (Map.Entry)iterator.next();
                        String var = mentry.getKey().toString().toLowerCase();
                        if (var.equals(variable.substring(1).toLowerCase()))
                        {
                            expect = expect.replace(variable, (CharSequence) mentry.getValue());
                        }
                    }
                }
            }
        } catch (Exception e) {}

        return expect;
    }

    public static Comparator<Testcase> ALPHABETICAL_ORDER = new Comparator<Testcase>() {
        @Override
        public int compare(Testcase o1, Testcase o2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getNAME(), o2.getNAME());
            if (res == 0) {
                res = o1.getNAME().compareTo(o2.getNAME());
            }
            return res;
        }
    };

    public static Comparator<File> FILE_ALPHABETICAL_ORDER = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
            if (res == 0) {
                res = f1.getName().compareTo(f2.getName());
            }
            return res;
        }
    };

    public static JSONObject replaceVariables(JSONObject expect, String name, HashMap<String, HashMap<String, String>> variables)
    {
        if (expect == null)
            return null;
        String pattern1 = "(%[\\w]*)";
        Pattern p = Pattern.compile(pattern1);

        HashMap<String, String> map = variables.get(name);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        if (map != null)
            list.add(map);
        list.add(variables.get("all"));


        for(Iterator iter = expect.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            String value = "";
            if (expect.get(key) instanceof String)
            {
                value = (String)expect.get(key);
            }
            else if (expect.get(key) instanceof JSONArray)
            {
                JSONArray array = (JSONArray)expect.get(key);
                value = array.toString();
            }

            if (!key.equalsIgnoreCase("type"))
            {
                try {
                    Matcher m = p.matcher(value);

                    while (m.find()) {
                        String variable = m.group();

                        for (HashMap<String, String> lst : list) {
                            Set set;
                            if (lst != null) {
                                set = lst.entrySet();
                                Iterator iterator = set.iterator();
                                while(iterator.hasNext()) {
                                    Map.Entry mentry = (Map.Entry)iterator.next();
                                    String var = mentry.getKey().toString().toLowerCase();
                                    if (var.equals(variable.substring(1).toLowerCase()))
                                    {
                                        value = value.replace(variable, (CharSequence) mentry.getValue());
                                        expect.put(key, value);
                                        break;
                                    }
                                }
                            }
                            else
                                break;
                        }
                    }
                } catch (Exception e) {}
            }
        }

        return expect;
    }

    public static String generateVariables(String expect)
    {
        String pattern = "\\{(.*?)\\}";
        Pattern p = Pattern.compile(pattern);

        try {
            Matcher m = p.matcher(expect);

            while (m.find()) {
                String variable = m.group();


                    if (variable.toLowerCase().equals("{guid}"))
                    {
                        return expect.replace(variable, UUID.randomUUID().toString().replace("-", ""));
                    }
                    else if (variable.toLowerCase().equals("{emailh}"))
                    {
                        return expect.replace(variable, UUID.randomUUID().toString().replace("-", "").substring(0, 25));
                    }
                    else if (variable.toLowerCase().startsWith("{date("))
                    {
                        String format = variable.substring(6, variable.length() - 2);
                        String timeStamp = new SimpleDateFormat(format).format(new Date());

                        return timeStamp;
                    }
                }
        } catch (Exception e) {}

        return expect;
    }


}
