package com.javelin.tests.api.tests;

import com.javelin.tests.api.request.RequestFactory;
import com.javelin.tests.api.response.ExpectedFactory;
import com.javelin.tests.api.response.IExpectationValidator;
import com.javelin.tests.api.response.StatusValidation;
import com.javelin.tests.api.rest.Settings;
import com.javelin.tests.api.settings.Filewalker;
import com.javelin.tests.api.settings.TestParser;
import com.javelin.tests.api.settings.Tools;
import com.javelin.tests.api.testcase.Testcase;
import com.jayway.jsonpath.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertNotNull;

/**
 * Created by msolosh on 3/25/2016.
 */
public class DataDrivenTests {

    private ArrayList<String> suites;
    private Settings api;
    private HashMap<String, HashMap<String, String>> variables = new HashMap<String, HashMap<String, String>>();

    @BeforeClass(alwaysRun = true)
    public void setUp() throws IOException {

        api = new Settings();
        getVariables();

        ArrayList<String> setup;
        Filewalker fl = new Filewalker();

        setup = fl.walk("TestSuite/_SetUp/");
        if (setup != null) {ArrayList<Testcase> setups = parseCases(setup, true);

        setupCases(setups);}

        Filewalker fll = new Filewalker();
        suites = fll.walk();
    }

    @DataProvider(name = "REST")
    public Object[][] createData() throws Exception {

        ArrayList<Testcase> testcases = parseCases(suites, false);

        System.out.println();
        Object[][] testList = new Object[testcases.size()][2];
        for (int k = 0; k < testcases.size(); k++) {
            testList[k][1] = testcases.get(k);
            testList[k][0] = testcases.get(k).getNAME();
        }

        return testList;
    }

    private Testcase replaceVaraibles(String file, Testcase test){
        int ind = test.getNAME().lastIndexOf(":");
        file = file.substring(0, ind);
        test.setURL(Tools.replaceVariables(test.getURL(), file, variables));
        test.setBODY(Tools.replaceVariables(test.getBODY(), file, variables));
        test.setPARAMS(Tools.replaceVariables(test.getPARAMS(), file, variables));
        test.setBOUNDARY(Tools.replaceVariables(test.getBOUNDARY(), file, variables));

        Tools.printFixLineString(test.getNAME(), "-");
        System.out.println("File: " + file);
        System.out.println("Test: " + test.getNAME());
        System.out.println("URL: " + test.getURL());
        System.out.println("METHOD: " + test.getMETHOD());
        System.out.println("BODY: " + test.getBODY());
        System.out.println("PARAMS: ");
        System.out.println(test.getPARAMS());

        if (test.getBOUNDARY() != null )
        {
            System.out.println("BOUNDARY: ");
            System.out.println(test.getBOUNDARY());
        }

        System.out.println();

        return test;
    }


    @Test(dataProvider = "REST")
    public void RestAPI(String file, Testcase test) throws Exception {

        Thread.sleep(test.getTimeout());

        Tools.printFixLineString("TESTCASE", "-");
        test = replaceVaraibles(file, test);
        int ind = test.getNAME().lastIndexOf(":");
        file = file.substring(0, ind);

        int loop = test.getLoop();

        while (loop > 0 ) {
            --loop;
            long start = System.nanoTime();
            HttpResponse response = RequestFactory.getRequest(test).sendRequest(); //send request!!!
            long elapsed = System.nanoTime() - start;
            System.out.println("Elapsed ms --> " + elapsed / 1000000);

            assertNotNull("Response is NULL. Probably there is a problem with network.", response);

            PrintHeaders(response);

            String status = String.valueOf(response.getStatusLine().getStatusCode());
            String body = null;
            Object document = null;
            if (response.getEntity() != null) {
                body = EntityUtils.toString(response.getEntity());
                document = Configuration.defaultConfiguration().jsonProvider().parse(body);
            }

            Tools.printFixLineString("RESPONSE", "=");
            System.out.println(String.format("STATUS is %d", Integer.parseInt(status)));
            System.out.print("BODY is ");
            if (body != null) {
                if (body.length() > 10000) {
                    System.out.println(body.substring(0, 10000) + "...");
                } else {
                    System.out.println(body);
                }
            }

            Tools.printFixLineString("", "=");

            System.out.println("Check Expectations: ");
            try {
                for (JSONObject expect : test.getEXPECTATION()) {

                    if (!expect.get("type").equals("STATUS"))
                        expect = Tools.replaceVariables(expect, file, variables);

                    System.out.print(expect);
                    HashMap testvar = new HashMap();

                    IExpectationValidator expectedValidator = ExpectedFactory.getExpectedObject(expect);
                    if (expectedValidator instanceof StatusValidation)
                    {
                        expectedValidator.validation(status);
                    }
                    else if (expectedValidator != null && document != null)
                    {
                        testvar = expectedValidator.validation(document);
                        if (testvar != null) {
                            if (variables.get(file) != null){
                            variables.get(file).putAll(testvar);}
                            else
                            {
                                variables.put(file, testvar);
                            }
                        }
                    }
                    else
                    {
                        System.out.println();
                    }
                    System.out.println(" - DONE");
                }
                break;
            } catch (Throwable t) {
                if ((test.getLoop() > 1) && (loop > 0)){
                    System.out.println();
                    System.out.println(String.format("%s more tries remain to get result.", loop));
                }
                else {
                    Assert.fail(t.getMessage());
                }

                Thread.sleep(test.getLoopTimeout()*1000);
            }

            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
        }
    }

    private String setupCases(ArrayList<Testcase> testcases){

        try {
            Collections.sort(testcases, ALPHABETICAL_ORDER);

            for (int k = 0; k < testcases.size(); k++) {


                Tools.printFixLineString("_SetUp", "▄");
                Testcase test = testcases.get(k);

                Thread.sleep(test.getTimeout());

                if (test.getBOUNDARY() != null )
                {
                    System.out.println("BOUNDARY: ");
                    System.out.println(test.getBOUNDARY());
                }


                String test_name = test.getNAME();
                String name = test_name.substring(0,test_name.lastIndexOf(":"));

                test = replaceVaraibles(name, test);

                HttpResponse response = RequestFactory.getRequest(test).sendRequest();

                PrintHeaders(response);
                String body = null;
                Object document = null;
                if (response.getEntity() != null) {
                    body = EntityUtils.toString(response.getEntity());
                    document = Configuration.defaultConfiguration().jsonProvider().parse(body);
                }

                int status = response.getStatusLine().getStatusCode();
                Tools.printFixLineString("RESPONSE", "=");
                System.out.println(String.format("STATUS is %d", status));
                System.out.print("BODY is ");
                if (body != null) {
                    if (body.length() > 10000) {
                        System.out.println(body.substring(0, 10000));
                    } else {
                        System.out.println(body);
                    }
                }

                Tools.printFixLineString("", "=");

                for (JSONObject expect : test.getEXPECTATION()) {

                    if (!expect.get("type").equals("STATUS"))
                        expect = Tools.replaceVariables(expect, test.getNAME(), variables);

                    System.out.print(expect);
                    HashMap testvar = new HashMap();
                    IExpectationValidator expectedValidator = ExpectedFactory.getExpectedObject(expect);
                    if (expectedValidator instanceof StatusValidation)
                    {
                        expectedValidator.validation(status);
                    }
                    else if (expectedValidator != null && document != null)
                    {

                        testvar = expectedValidator.validation(document);
                        if ((testvar != null) && (variables.get("all").size() > 0))
                        {
                            HashMap<String, String> tmpMap = variables.get("all");
                            Set<String> keys = testvar.keySet();
                            for (String key : keys)
                            {
                                String value = (String)testvar.get(key);;
                                tmpMap.put(key, value);
                            }
                            variables.put("all", tmpMap);
                        }
                    }
                    else
                    {
                        System.out.println();
                    }
                    System.out.println(" - DONE");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Comparator<Testcase> ALPHABETICAL_ORDER = new Comparator<Testcase>() {
        @Override
        public int compare(Testcase o1, Testcase o2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getNAME(), o2.getNAME());
            if (res == 0) {
                res = o1.getNAME().compareTo(o2.getNAME());
            }
            return res;
        }
    };

    private void getVariables(){

        String name = "all";
        try {
            File file = new File(api.getClassLoader().getResource("TestSuite/variables.json").getFile());

            TestParser parser = new TestParser(file.getAbsolutePath(), true);
            HashMap<String, String> fileMap = new HashMap<String, String>();
            fileMap = parser.getVariablesTests();

            if ((variables.get(name) != null) && (fileMap.size() > 0))
            {
                HashMap<String, String> tmpMap = variables.get(name);
                Set<String> keys = fileMap.keySet();
                for (String key : keys)
                {
                    String value = fileMap.get(key);
                    tmpMap.put(key, value);
                }
                variables.put(name, tmpMap);
            }
            else if (fileMap.size() > 0)
                variables.put(name, fileMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList parseCases(ArrayList<String> suites, boolean goal)
    {
        ArrayList<Testcase> testcases = new ArrayList<Testcase>();

        for (String file : suites)
        {
            System.out.println("READ test suit file: " + file);
            TestParser parser = new TestParser(file, goal);
            HashMap<String, String> fileMap = new HashMap<String, String>();
            String[] names = file.split("\\\\");
            String name = file;
            try {
                fileMap = parser.getVariablesTests();
                testcases.addAll(parser.parseSuite());
            } catch (Exception e) {
                System.out.println("SKIPPED");
                e.printStackTrace();
                continue;
            }
            if (fileMap.size() > 0)
                variables.put(name, fileMap);
        }

        return testcases;
    }

    private void PrintHeaders(HttpResponse response) throws IOException {
        Tools.printFixLineString("RESPONSE HEADERs", "=");
        List<String> list = api.getHeadersForLogging();
        if (list != null) {
            for (String item : list)
            {
                try {
                    System.out.println(response.getHeaders(item)[0]);
                } catch (Exception e) {
                    System.out.println("There is no headers with name: " + item);
                }
            }
        }
    }
}
