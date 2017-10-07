package com.rest.tests.api.frwm.tests;


import com.rest.tests.api.frwm.listeners.ResultListeners;
import com.rest.tests.api.frwm.request.RequestFactory;
import com.rest.tests.api.frwm.response.ExpectedFactory;
import com.rest.tests.api.frwm.response.IExpectationValidator;
import com.rest.tests.api.frwm.response.StatusValidation;
import com.rest.tests.api.frwm.settings.*;
import com.rest.tests.api.frwm.testcase.Expectations.Expectation;
import com.rest.tests.api.frwm.testcase.Response;
import com.rest.tests.api.frwm.testcase.TC;
import com.rest.tests.api.frwm.testcase.Testcase;
import com.rest.tests.api.frwm.testcase.TestcaseType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;
import com.jayway.jsonpath.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by msolosh on 3/25/2016.
 */
@Listeners(ResultListeners.class)
public class DataDrivenParallelTests {

    private Settings api;
    private ArrayList<String> suites = new ArrayList<>();
    private ArrayList<String> failed = new ArrayList<>();
    private HashMap<String, Map<String, String>> variables = new HashMap<String, Map<String, String>>();
    private ArrayList<String> skipNames = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {

        api = new Settings();
        setVariable("all", api.getGlobalVariables());

        Filewalker fll = new Filewalker();
        ArrayList<String> setup = fll.walk("TestSuite/_SetUp/");

        for (String filename : setup)
        {
            TestParser suite = new TestParser(filename);
            setVariable(filename, suite.getTSVariables());
            execTestSuite(filename, "global");
        }

        ArrayList<String> files = fll.walk(); //list of files

        for (String filename : files)
        {
            if (filename.contains("TestSuite/_SetUp") || filename.contains("TestSuite/_TearDown"))
                continue;
            TestParser suite = new TestParser(filename);

            if (Tools.arrayContains(suite.getTcsuite().getTags(), api.getTags()))
            {
                suites.add(filename);
                setVariable(filename, suite.getTSVariables());
            }
            else
            {
                JSONObject tests[] = suite.getTcsuite().getTests();
                for(JSONObject test : tests)
                {
                    TC tc = suite.parseTest(test);
                    if (Tools.arrayContains(tc.getTags(), api.getTags()))
                    {
                        suites.add(filename);
                        setVariable(filename, suite.getTSVariables());
                        break;
                    }
                }
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {

        Filewalker fll = new Filewalker();
        ArrayList<String> setup = fll.walk("TestSuite/_TearDown/");


        for (String filename : setup)
        {
//                TestParser suite = new TestParser(filename);
                execTestSuite(filename, "global");
        }

        for (String failure : failed)
        {
            System.out.println(Tools.printFixLineString("LOGS", "≠"));
            System.out.println(Tools.readFile(failure+".log"));
        }
        if (failed.size() > 0)
            System.out.println(Tools.printFixLineString("Failed files", "•"));
        for (String failure : failed)
        {
            System.out.println(failure);
        }
        if (skipNames.size() > 0)
            System.out.println(Tools.printFixLineString("Skipped files", "•"));
        for (String skip : skipNames)
        {
            System.out.println(skip);
        }

        System.out.println(Tools.printFixLineString("", "•"));
        System.out.println(Tools.printFixLineString("", "|"));
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(Object[] data) {

        String file = data[0].toString();
        try {
//            TestParser suite = new TestParser(file);
            execTestSuite(file, "setup");
        } catch (Exception e) {
            System.out.println("Skipped Before operation");
        } catch(Throwable t)
        {
            System.out.println("Skipped Before operation");
        }

    }

    @AfterMethod(alwaysRun = true)
    public void teardown(Object[] data) throws Exception {

        String file = data[0].toString();
//        TestParser suite = new TestParser(file);
        try {
        execTestSuite(file, "teardown");
    } catch (Exception e) {
        System.out.println("Skipped Before operation");
    } catch(Throwable t)
    {
        System.out.println("Skipped Before operation");
    }
    }

    @DataProvider(name = "REST", parallel = true)
    public Object[][] createData() throws Exception {


        if (suites.size() > 0)
        {
            Object[][] testList = new Object[suites.size()][1];
            for (int k = 0; k < suites.size(); k++) {
                testList[k][0] = suites.get(k);
            }
            return testList;
        }

        return null;
    }


    @Test(dataProvider = "REST")
    public void RestAPI(String file) throws Exception {
        if (skipNames.contains(file)) {
            Reporter.getCurrentTestResult().setAttribute("result", ITestResult.SKIP);
            System.out.println(file + " - SKIPPED");
            return;
        }

        execTestSuite(file, "test");
    }

    private void execTestSuite(String filename, String type) throws Exception {

        String log="";
        TestParser parser = new TestParser(filename);
        String file = parser.getTcsuite().getFile();
        JSONObject[] tests = new JSONObject[]{};
        switch (type)
        {
            case "setup":
                tests = parser.getTcsuite().getSetUp();
                log = Tools.printFixLineString("", "=");
                log = log + Tools.printFixLineString("SETUP", "=");
                log = log + Tools.printFixLineString("", "=");
                break;
            case "teardown":
                tests = parser.getTcsuite().getTearDown();
                log = Tools.printFixLineString("", "=");
                log = log + Tools.printFixLineString("TEARDOWN", "=");
                log = log + Tools.printFixLineString("", "=");
                break;
            case "test":
                tests = parser.getTcsuite().getTests();
                log = Tools.printFixLineString("TEST", "*");
                break;
            case "global":
                tests = parser.getTcsuite().getTests();
                log = Tools.printFixLineString("TEST", "*");
                break;
        }
        JSONParser par = new JSONParser();

        for (JSONObject testObj : tests) {


            TC test = parser.parseTest(testObj);

            if (!test.getEnabled())
                continue;

            Tools.writeToFile(file, log + "\n");
            long loop = test.getLoop();

            while (loop > 0) {
                --loop;

                try {
                    Thread.sleep(test.getTimeout());

                    test = Tools.replaceVaraibles(test, mergeVariables(file));

                    long start = System.nanoTime();
                    HttpResponse rspns = RequestFactory.getRequest(test).sendRequest();
                    long elapsed = System.nanoTime() - start;
                    Tools.writeToFile(file, "Elapsed ms --> " + elapsed / 1e6 + "\n", true);

                    Response response = new Response(rspns);

                    log = Tools.PrintHeaders(rspns, api);
                    Tools.writeToFile(file, log + "\n");
                    String body = null;

                    Assert.assertNotNull("Response is null", response);

                    log = Tools.printFixLineString("RESPONSE", "-");
                    Tools.writeToFile(file, log + "\n");
                    Tools.writeToFile(file, String.format("STATUS is %d\n", response.getStatus()));

                    if (body != null) {
                        Tools.writeToFile(file, "BODY is ");
                        Tools.writeToFile(file, body + "\n");
                    }

                    log = Tools.printFixLineString("", "-");
                    Tools.writeToFile(file, log + "\n");

                    for (JSONObject expect : test.getExpectations()) {

                        expect = (JSONObject) par.parse(Tools.replaceVariable(expect.toString(), mergeVariables(file)));

                        Tools.writeToFile(file, expect.toJSONString());
                        HashMap testvar = new HashMap();
                        IExpectationValidator expectedValidator = ExpectedFactory.getExpectedObject(expect);

                        if (expectedValidator != null) {

                            testvar = expectedValidator.validation(response, file);

                            if (testvar != null) {
                                if (file.contains("TestSuite/_SetUp") || file.contains("TestSuite/_TearDown")) {
                                    setVariable("all", testvar);
                                } else {
                                    setVariable(file, testvar);
                                }

                            }
                        } else {
                            Tools.writeToFile(file, "\n");
                        }
                        Tools.writeToFile(file, " - DONE" + "\n");
                    }
                    break;
                }catch(VException v)
                {
                    if (!type.equalsIgnoreCase("setup") && !type.equalsIgnoreCase("teardown"))
                        failed.add(file);
                    else
                        skipNames.add(file);
                    Tools.writeToFile(file, v.getMessage() + "\n");
                    System.out.println(test.getName() + "\n" + v.getMessage());
                    Assert.fail(test.getName() + "\n" + v.getMessage());
                }
                catch (Throwable t) {
                    Tools.writeToFile(file, " - FAILED\n");
                    if ((test.getLoop() > 1) && (loop > 0)) {
                        Tools.writeToFile(file, String.format("\n%s more tries remain to get result.\n", loop));
                    } else {
                        if (type.equalsIgnoreCase("test"))
                            failed.add(file);
                        else  if (type.equalsIgnoreCase("setup"))
                            skipNames.add(file);
                        Tools.writeToFile(file, t.getMessage() + "\n");
                        Assert.fail(test.getName() + "\n" + t.getMessage());
                    }

                    Thread.sleep(test.getLoopTimeout() * 1000);
                }
            }
        }
    }

    private synchronized void setVariable(String name, Map<String, String> value) {

        Map<String, String> map;

        map = variables.get(name);
        if (map == null) {
            if (value.size() > 0)
                variables.put(name, value);
        } else {
            Set<String> keys = value.keySet();
            for (String key : keys) {
                String val = value.get(key);

                if (key.equalsIgnoreCase("token") && !val.startsWith("Bearer") && name.equalsIgnoreCase("all")) {
                    val = "Bearer " + val;
                }

                map.put(key.toLowerCase(), val);
            }
        }
    }

    private Map<String, String> mergeVariables(String filename)
    {
        if (filename.equalsIgnoreCase("all"))
            return variables.get("all");
        HashMap<String, String> vars = new HashMap<>(variables.get("all"));
        try
        {
            HashMap<String, String> map = new HashMap<>(variables.get(filename));
            Set<String> keys = map.keySet();
            for (String key : keys) {
                String val = map.get(key);

                vars.put(key.toLowerCase(), val);
            }
        }
        catch(Exception e)
        {}
        return vars;
    }

}
