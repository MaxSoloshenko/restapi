package com.rest.tests.api.frwm.tests;

import com.jayway.jsonpath.Configuration;
import com.rest.tests.api.frwm.listeners.ResultListeners;
import com.rest.tests.api.frwm.request.RequestFactory;
import com.rest.tests.api.frwm.response.ExpectedFactory;
import com.rest.tests.api.frwm.response.IExpectationValidator;
import com.rest.tests.api.frwm.response.StatusValidation;
import com.rest.tests.api.frwm.rest.Settings;
import com.rest.tests.api.frwm.settings.Filewalker;
import com.rest.tests.api.frwm.settings.TestParser;
import com.rest.tests.api.frwm.settings.Tools;
import com.rest.tests.api.frwm.testcase.Testcase;
import com.rest.tests.api.frwm.testcase.TestcaseType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

/**
 * Created by msolosh on 3/25/2016.
 */
@Listeners(ResultListeners.class)
public class DataDrivenTests {

    ArrayList<Testcase> testcases = new ArrayList<Testcase>();
    private Settings api;
    private HashMap<String, HashMap<String, String>> variables = new HashMap<String, HashMap<String, String>>();
    private HashMap<String, ArrayList<Testcase>> test_set_tear = new HashMap<String, ArrayList<Testcase>>();
    private int caseNumber = 0;
    private String filename = "";
    private boolean success = true;
    private String skipName = "";

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {

        api = new Settings();
        getVariables();

        ArrayList<String> setup;
        Filewalker fl = new Filewalker();

        setup = fl.walk("TestSuite/_SetUp/");
        if (setup != null) {

            ArrayList<Testcase> setups = parseCases(setup, true);
            Tools.printFixLineString("_SetUp GLOBAL", "▄");
            setupCases(setups);
            Tools.printFixLineString("", "▄");
        }

        Filewalker fll = new Filewalker();
        ArrayList<String> suites = fll.walk();
        ArrayList<Testcase> testcases = parseCases(suites, false);
        ArrayList<Testcase> setp = new ArrayList<Testcase>();

        for (int l = 0; l < testcases.size(); l++)
        {
            Testcase test = testcases.get(l);
            String name = test.getNAME().substring(0,test.getNAME().lastIndexOf(":"));

            setp = test_set_tear.get(name);

            if (test.getType().equals(TestcaseType.TEST))
            {
                this.testcases.add(test);
            }
            else if (test.getType().equals(TestcaseType.SETUP) || test.getType().equals(TestcaseType.TEARDOWN))
            {
                if (setp == null)
                {
                    setp = new ArrayList<Testcase>();
                }
                setp.add(test);
                test_set_tear.put(name, setp);
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {

        ArrayList<String> setup;
        Filewalker fl = new Filewalker();

        setup = fl.walk("TestSuite/_TearDown/");
        if (setup != null) {
            ArrayList<Testcase> setups = parseCases(setup, true);
            Tools.printFixLineString("TearDown GLOBAL", "▄");
            setupCases(setups);
            Tools.printFixLineString("", "▄");
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {

        Testcase test = testcases.get(caseNumber);
        caseNumber++;
        if (success)
        {
            success = false;

            if (!filename.equalsIgnoreCase(test.getNAME().substring(0, test.getNAME().lastIndexOf(":"))))
            {
                filename = test.getNAME().substring(0, test.getNAME().lastIndexOf(":"));


                ArrayList<Testcase> arr = test_set_tear.get(filename);

                if (arr != null) {
                    for (int k = 0; k < arr.size(); k++)
                    {
                        Testcase tst = arr.get(k);
                        if (tst.getType().equals(TestcaseType.SETUP))
                        {
                            ArrayList<Testcase> list = new ArrayList<Testcase>();
                            list.add(tst);
                            Tools.printFixLineString("_SetUp", "▄");
                            try {
                                setupCases(list);
                            } catch (AssertionError e)
                            {
                                e.printStackTrace();
                                skipName = filename;
                                break;
                            }

                            Tools.printFixLineString("", "*");
                        }
                    }
                }
            }

            success = true;
        }

    }

    @AfterMethod(alwaysRun = true)
    public void teardown() throws Exception {

        Testcase test;

        try
        {
            test = testcases.get(caseNumber);
        }
        catch(IndexOutOfBoundsException e){
            test = testcases.get(caseNumber - 1);
        }
        if (!filename.equalsIgnoreCase(test.getNAME().substring(0, test.getNAME().lastIndexOf(":"))) || testcases.size() < caseNumber + 1)
        {
            success = true;
            ArrayList<Testcase> arr = test_set_tear.get(filename);

            if (arr != null) {
                for (int k = 0; k < arr.size(); k++)
                {
                    Testcase tst = arr.get(k);
                    if (tst.getType().equals(TestcaseType.TEARDOWN))
                    {
                        ArrayList<Testcase> list = new ArrayList<Testcase>();
                        list.add(tst);
                        Tools.printFixLineString("_TearDown", "▄");
                        setupCases(list);
                        Tools.printFixLineString("", "*");
                    }
                }
            }
        }
    }

    @DataProvider(name = "REST")
    public Object[][] createData() throws Exception {

        System.out.println();

        Object[][] testList = new Object[testcases.size()][2];
        for (int k = 0; k < testcases.size(); k++) {
            testList[k][1] = testcases.get(k);
            testList[k][0] = testcases.get(k).getNAME();
        }

        return testList;
    }

    @Test(dataProvider = "REST")
    public void RestAPI(String file, Testcase test) throws Exception {

        if (file.substring(0, file.lastIndexOf(":")).equalsIgnoreCase(skipName))
        {
            Reporter.getCurrentTestResult().setAttribute("result", ITestResult.SKIP);
            return;
        }

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
                            if (variables.get(file) != null)
                            {
                                variables.get(file).putAll(testvar);
                            }
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

    private String setupCases(ArrayList<Testcase> testcases){

        try {

            for (int k = 0; k < testcases.size(); k++)
            {
                Testcase test = testcases.get(k);

                int loop = test.getLoop();

                while (loop > 0 ) {
                    --loop;

                    Thread.sleep(test.getTimeout());

                    if (test.getBOUNDARY() != null) {
                        System.out.println("BOUNDARY: ");
                        System.out.println(test.getBOUNDARY());
                    }


                    String test_name = test.getNAME();
                    String name = test_name.substring(0, test_name.lastIndexOf(":"));

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

                    try {
                        for (JSONObject expect : test.getEXPECTATION()) {

                            if (!expect.get("type").equals("STATUS")) {
                                int ind = test.getNAME().lastIndexOf(":");
                                name = name.substring(0, ind);
                                expect = Tools.replaceVariables(expect, name, variables);
                            }

                            System.out.print(expect);
                            HashMap testvar = new HashMap();
                            IExpectationValidator expectedValidator = ExpectedFactory.getExpectedObject(expect);
                            if (expectedValidator instanceof StatusValidation) {
                                expectedValidator.validation(status);
                            } else if (expectedValidator != null && document != null) {

                                testvar = expectedValidator.validation(document);
                                if ((testvar != null) && (variables.get("all").size() > 0)) {
                                    HashMap<String, String> tmpMap = variables.get("all");
                                    Set<String> keys = testvar.keySet();
                                    for (String key : keys) {
                                        String value = (String) testvar.get(key);
                                        ;
                                        tmpMap.put(key, value);
                                    }
                                    variables.put("all", tmpMap);
                                }
                            } else {
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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
