package com.rest.tests.api.frmw.settings;

import com.rest.tests.api.frmw.testcase.TC;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by msolosh on 4/7/2016.
 */
public class Tools {

    public static String printFixLineString(String text, String pattern){

        int size = text.length();
        int p = (100 - size - 2);
        String res = "";
        if (p > 0)
        {
            String patt = new String(new char[p]).replace('\0', pattern.toCharArray()[0]);
            res = patt.substring(0, p/2) + " " + text + " " + patt.substring(p/2, patt.length());
        }
        else
        {
            res = text;
        }
        return res;
    }

    public static TC replaceVaraibles(TC test, Map variables) throws VException, ParseException {

        String file = test.getName().split(":")[0];
        JSONParser par = new JSONParser();

        Tools.writeToFile(file, "Test: " + test.getName() + "\n");
        test.setUrl(replaceVariable(test.getUrl(), variables));

        if (test.getBody() != null)
        {
            JSONObject body;
            if (test.getBody() instanceof HashMap)
            {
                body = new JSONObject((HashMap)test.getBody());
                test.setBODY(par.parse(replaceVariable(body.toJSONString(), variables)));
            }
            else if (test.getBody() instanceof ArrayList)
            {
                ArrayList<String> list;
                list = (ArrayList<String>) test.getBody();
                JSONArray bd = new JSONArray();
                bd.addAll(list);
                test.setBODY(par.parse(replaceVariable(bd.toJSONString(), variables)));
            }
        }
        test.setPARAMS((JSONObject) par.parse(replaceVariable(test.getPARAMS().toJSONString(), variables)));
        test.setBOUNDARY((JSONObject) par.parse(replaceVariable(test.getBOUNDARY().toJSONString(), variables)));
        test.setSourceFile(replaceVariable(test.getSourceFile(), variables));
        test.setFileEntity(replaceVariable(test.getFileEntity(), variables));

        String res = String.format("URL: %s\n" +
                        "METHOD: %s\n" ,
                test.getUrl(),
                test.getMethod()
                );

        if (test.getBody() != null)
            res = res + "BODY: " + test.getBody().toString() + "\n";
        if (!test.getPARAMS().toJSONString().equalsIgnoreCase("{}"))
            res = res + "PARAMS: " + test.getPARAMS().toJSONString() + "\n";
        if (!test.getBOUNDARY().toJSONString().equalsIgnoreCase("{}"))
            res = res + "BOUNDARY: " + test.getBOUNDARY().toJSONString() + "\n";
        if (!test.getFileEntity().equals(""))
            res = res + "FileEntity: " + test.getFileEntity() + "\n";

        writeToFile(file, res);
        return test;
    }

    public static String replaceVariable(String value, Map<String, String> variables) {
        String pattern1 = "\\$\\{(.*?)\\}";
        Pattern p = Pattern.compile(pattern1);

        if (value != null)
        {
                Matcher m = p.matcher(value);

                while(m.find()) {
                    String variable = m.group();

                    String vale = variables.get(variable.replace("${", "").replace("}", "").toLowerCase());
                    if (vale != null) {
                        while (value.contains(variable)) {
                            value = value.replace(variable, vale);
                        }

                    }
                }
        }

        return value;
    }

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

    public static String generateVariables(String expect)
    {
        String pattern = "\\{(.*?)\\}";
        Pattern p = Pattern.compile(pattern);


        Matcher m = p.matcher(expect);

        while (m.find()) {
            String variable = m.group();

            if (variable.toLowerCase().equals("{guid}"))
            {
                return expect.replace(variable, UUID.randomUUID().toString().replace("-", ""));
            }
            else if (variable.toLowerCase().equals("{g-u-i-d}"))
            {
                return expect.replace(variable, UUID.randomUUID().toString());
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

        return expect;
    }

    public static void writeToFile(String file, String text)
    {
        writeToFile(file, text, false);
    }

    public static void writeToFile(String file, String text, boolean date)
    {
        BufferedWriter bw = null;
        FileWriter fw = null;
        File fl = new File(file + ".log");
        try {
            if (!fl.exists()) {
                fl.createNewFile();
            }
            fw = new FileWriter(fl.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            String timeStamp = "";
            if (date)
            {
                timeStamp = "Date/Time: " + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()) + "\n";
            }

            bw.write(timeStamp + text);

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

        try {

            if (bw != null)
                bw.close();

            if (fw != null)
                fw.close();

        } catch (IOException ex) {

            ex.printStackTrace();

        }
    }
    }

    public static String readFile(String file) throws IOException {

        String fileString = new String(Files.readAllBytes(Paths.get(file)));
        return fileString;
    }

    public static String PrintHeaders(HttpResponse response, Settings api) {
        String res = Tools.printFixLineString("RESPONSE HEADERs", "-") + "\n";
        List<String> list = api.getHeadersForLogging();
        if (list != null) {
            for (String item : list)
            {
                try {

                    Header head = response.getHeaders(item)[0];
                    res = res + head.toString() + "\n";
                } catch (Exception e) {
                    res = res + "There is no headers with name: " + item + "\n";
                }
            }
        }
        return res;
    }

    public static boolean arrayContains(String[] main, String[] contains)
    {
        if (contains == null)
        {
            return false;
        }
        if (main.length == 0)
        {
            return false;
        }
        Set<String> VALUES = new HashSet<String>(Arrays.asList(main));
        for (String val : contains)
        {
            if (VALUES.contains(val))
                return true;
        }
        return false;
    }
}
