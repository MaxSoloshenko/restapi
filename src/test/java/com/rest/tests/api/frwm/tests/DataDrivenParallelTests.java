package com.rest.tests.api.frwm.tests;


import com.rest.tests.api.frwm.listeners.ResultListeners;
import com.rest.tests.api.frwm.request.RequestFactory;
import com.rest.tests.api.frwm.response.ExpectedFactory;
import com.rest.tests.api.frwm.response.IExpectationValidator;
import com.rest.tests.api.frwm.response.StatusValidation;
import com.rest.tests.api.frwm.settings.*;
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
import com.jayway.jsonpath.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by msolosh on 3/25/2016.
 */
@Listeners(ResultListeners.class)
public class DataDrivenParallelTests {

    private Settings api;
    private ArrayList<String> suites;
    private ArrayList<String> failed = new ArrayList<>();
    private ArrayList<Testcase> cases = new ArrayList<>();
    private HashMap<String, HashMap<String, String>> variables = new HashMap<String, HashMap<String, String>>();
    private String filename = "";
    private ArrayList<String> skipNames = new ArrayList<>();

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {

        api = new Settings();
        getVariables();

        Filewalker fll = new Filewalker();
        suites = fll.walk();
        cases = parseCases(suites, false);

        if (cases.size() > 0)
        {
            ArrayList<String> setup;
            Filewalker fl = new Filewalker();

            setup = fl.walk("TestSuite/_SetUp/");
            if (setup != null) {

                ArrayList<Testcase> setups = parseCases(setup, true);
                Tools.printFixLineString("_SetUp GLOBAL", "▄");
                setupCases(setups);
                Tools.printFixLineString("", "▄");
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {

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
        System.out.println(Tools.printFixLineString("", "≠"));
        ArrayList<String> setup;
        Filewalker fl = new Filewalker();

        setup = fl.walk("TestSuite/_TearDown/");
        if (setup != null) {
            ArrayList<Testcase> setups = parseCases(setup, true);
            setupCases(setups);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(Object[] data) {

        String file = data[0].toString();
        try {
            SetUpTearDown(file, TestcaseType.SETUP);
        } catch (Exception e) {
            System.out.println("Skipped Before operation");
        }
    }

    @AfterMethod(alwaysRun = true)
    public void teardown(Object[] data) throws Exception {

        String file = data[0].toString();
        SetUpTearDown(file, TestcaseType.TEARDOWN);
    }

    @DataProvider(name = "REST", parallel = true)
    public Object[][] createData() throws Exception {

        ArrayList<String> files = new ArrayList<>();
        String file;
        if (cases.size() > 0)
        {
            for (Testcase test : cases) {
                file = test.getNAME().substring(0, test.getNAME().indexOf(":"));
                if (!files.contains(file)) {
                    files.add(file);
                }
            }

            Object[][] testList = new Object[files.size()][1];
            for (int k = 0; k < files.size(); k++) {
                testList[k][0] = files.get(k);
            }
            System.out.println("Files: " + testList.length);
            System.out.println("Cases: " + cases.size());
            return testList;
        }

        return null;
    }


    @Test(dataProvider = "REST")
    public void RestAPI(String file) throws Exception {
        long startFile = System.nanoTime();

        if (skipNames.contains(file)) {
            Reporter.getCurrentTestResult().setAttribute("result", ITestResult.SKIP);
            System.out.println(file + " - SKIPPED");
            return;
        }

        for (int l = 0; l < cases.size(); l++) {
            Testcase test = cases.get(l);

            if (test.getType().equals(TestcaseType.TEST) && test.getNAME().startsWith(file)) {
                Thread.sleep(test.getTimeout());

                Tools.writeToFile(file, Tools.printFixLineString("TESTCASE", "=") + "\n");
                try {
                    test = Tools.replaceVaraibles(file, test, variables);
                } catch (Exception | VException e) {
                    failed.add(file);
                    Assert.fail(e.getMessage());
                }
                int ind = test.getNAME().lastIndexOf(":");
                file = file.substring(0, ind);

                int loop = test.getLoop();

                boolean success;
                while (loop > 0) {
                    success = true;
                    --loop;
                    long start = System.nanoTime();
                    HttpResponse response = RequestFactory.getRequest(test).sendRequest();
                    long elapsed = System.nanoTime() - start;
                    Tools.writeToFile(file, "Elapsed ms --> " + elapsed / 1e6 + "\n", true);

                    Assert.assertNotNull("Response is NULL. Probably there is a problem with network.", response);

                    String log = Tools.PrintHeaders(response, api);
                    Tools.writeToFile(file, log + "\n");

                    int status = response.getStatusLine().getStatusCode();
                    String body = null;
                    Object document = null;

                    if (response.getEntity() != null) {
                        body = EntityUtils.toString(response.getEntity());

                        if (response.getEntity().getContentType() != null && response.getEntity().getContentType().toString().contains("text/csv")) {
                            document = body;
                        } else {
                            document = Configuration.defaultConfiguration().jsonProvider().parse(body);
                        }
                    }

                    Tools.writeToFile(file, Tools.printFixLineString("RESPONSE", "-") + "\n");
                    if (body != null) {
                        Tools.writeToFile(file, "BODY:\n" + body + "\n");
                    }

                    log = Tools.printFixLineString("", "-");

                    Tools.writeToFile(file, log + "\nCheck Expectations: \n");

                    for (JSONObject expect : test.getEXPECTATION()) {
                        try {
                            expect = Tools.replaceVariables(expect, file, variables);

                            Tools.writeToFile(file, expect.toJSONString());
                            HashMap testvar = new HashMap();
                            IExpectationValidator expectedValidator = ExpectedFactory.getExpectedObject(expect);
                            if (expectedValidator instanceof StatusValidation) {
                                expectedValidator.validation(status, file);
                            } else if (expectedValidator != null && document != null) {
                                testvar = expectedValidator.validation(document, file);
                                if (testvar != null) {
                                    if (variables.get(file) != null) {
                                        variables.get(file).putAll(testvar); //TODO:check how it works
                                    } else {
                                        setVariable(file, testvar);
                                    }
                                }
                            } else {
                                Tools.writeToFile(file, "\n");
                            }
                            Tools.writeToFile(file, " - SUCCESSFULL\n");
                        } catch (Throwable t) {
                            Tools.writeToFile(file, " - FAILED\n");

                            if ((test.getLoop() > 1) && (loop > 0)) {
                                success = false;

                            } else {
                                long endTest = System.nanoTime();
                                System.out.println("- " + file.substring(file.indexOf("TestSuite")) + " << " + (endTest - startFile) / 1e6 + "ms");
                                failed.add(file);
                                Assert.fail(test.getNAME() + "  " + expect + "  " + t.getMessage());
                            }
                        }
                    }

                    if (success)
                        break;
                    else {
                        Tools.writeToFile(file, String.format("\n☻☻☻☻☻ %s more tries remain to get result.\n", loop));
                        Thread.sleep(test.getLoopTimeout() * 1000);
                    }
                    HttpEntity entity = response.getEntity();
                    EntityUtils.consume(entity);
                }
            }
        }
        long endFile = System.nanoTime();
        System.out.println("••••• " + file.substring(file.indexOf("TestSuite")) + " << " + (endFile - startFile) / 1e6 + "ms");
    }


    private void getVariables() {

        String name = "all";
        try {
            System.out.print("Read Settings/variables.json file - ");
            File file = new File(api.getClassLoader().getResource("Settings/variables.json").getFile());
            System.out.println("OK");

            TestParser parser = new TestParser(file.getAbsolutePath(), true);
            HashMap<String, String> fileMap = new HashMap<String, String>();
            fileMap = parser.getVariablesTests();

            if ((variables.get(name) != null) && (fileMap.size() > 0)) {
                HashMap<String, String> tmpMap = variables.get(name);
                Set<String> keys = fileMap.keySet();
                for (String key : keys) {
                    String value = fileMap.get(key);
                    tmpMap.put(key, value);
                }
                setVariable(name, tmpMap);
            } else if (fileMap.size() > 0)
                setVariable(name, fileMap);
        } catch (NullPointerException e) {
            System.out.println("Settings/variables.json is not found");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList parseCases(ArrayList<String> suites, boolean goal) {
        ArrayList<Testcase> testcases = new ArrayList<Testcase>();

        for (String file : suites) {
            TestParser parser = new TestParser(file, goal);
            HashMap<String, String> fileMap = new HashMap<String, String>();
            String name = file;
            try {

                ArrayList<Testcase> cases = parser.parseSuite();
                if (cases.size() > 0)
                {
                    testcases.addAll(cases);
                    fileMap = parser.getVariablesTests();
                    if (fileMap.size() > 0)
                        setVariable(name, fileMap);
                }

            } catch (Exception e) {
                Tools.writeToFile(file, " - SKIPPED");
                Tools.writeToFile(file, e.getStackTrace().toString());
                continue;
            }
        }

        return testcases;
    }

    private String setupCases(ArrayList<Testcase> testcases) throws Exception {

        for (int k = 0; k < testcases.size(); k++) {

            String log = Tools.printFixLineString("SETUP/TEARDOWN", ">");

            Testcase test = testcases.get(k);

            String file = test.getNAME().split(":")[0];

            Tools.writeToFile(file, log + "\n");
            int loop = test.getLoop();

            while (loop > 0) {
                --loop;

                try {
                Thread.sleep(test.getTimeout());

                String test_name = test.getNAME();
                String name = test_name.substring(0, test_name.lastIndexOf(":"));

                test = Tools.replaceVaraibles(name, test, variables);

                long start = System.nanoTime();
                HttpResponse response = RequestFactory.getRequest(test).sendRequest();
                long elapsed = System.nanoTime() - start;
                Tools.writeToFile(file, "Elapsed ms --> " + elapsed / 1e6 + "\n", true);


                log = Tools.PrintHeaders(response, api);
                Tools.writeToFile(file, log + "\n");
                String body = null;
                Object document = null;
                Assert.assertNotNull("Response is null", response);
                if (response.getEntity() != null) {
                    body = EntityUtils.toString(response.getEntity());
                    document = Configuration.defaultConfiguration().jsonProvider().parse(body);
                }

                int status = response.getStatusLine().getStatusCode();
                log = Tools.printFixLineString("RESPONSE", "-");
                Tools.writeToFile(file, log + "\n");
                Tools.writeToFile(file, String.format("STATUS is %d\n", status));
                Tools.writeToFile(file, "BODY is ");
                if (body != null) {
                    Tools.writeToFile(file, body + "\n");
                }

                log = Tools.printFixLineString("", "-");
                Tools.writeToFile(file, log + "\n");

                    for (JSONObject expect : test.getEXPECTATION()) {

                        int ind = test.getNAME().lastIndexOf(":");
                        name = name.substring(0, ind);
                        expect = Tools.replaceVariables(expect, name, variables);

                        Tools.writeToFile(file, expect.toJSONString());
                        HashMap testvar = new HashMap();
                        IExpectationValidator expectedValidator = ExpectedFactory.getExpectedObject(expect);
                        if (expectedValidator instanceof StatusValidation) {
                            expectedValidator.validation(status, file);
                        } else if (expectedValidator != null && document != null) {

                            testvar = expectedValidator.validation(document, file);

                            if (testvar != null) {
                                if (file.contains("_SetUp") || file.contains("_TearDown")) {
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
                    failed.add(file);
                    skipNames.add(file);
                    Tools.writeToFile(file, v.getMessage() + "\n");
                    System.out.println(test.getNAME() + "\n" + v.getMessage());
                    Assert.fail(test.getNAME() + "\n" + v.getMessage());
                }
                catch (Throwable t) {
                    Tools.writeToFile(file, " - FAILED\n");
                    if ((test.getLoop() > 1) && (loop > 0)) {
                        Tools.writeToFile(file, String.format("\n%s more tries remain to get result.\n", loop));
                    } else {
                        failed.add(file);
                        skipNames.add(file);
                        Tools.writeToFile(file, t.getMessage() + "\n");
//                        System.out.println(test.getNAME() + "\n" + t.getMessage());
                        Assert.fail(test.getNAME() + "\n" + t.getMessage());
                    }

                    Thread.sleep(test.getLoopTimeout() * 1000);
                }
            }
        }
        return null;
    }

    private void SetUpTearDown(String file, TestcaseType type) throws Exception {

        if (!skipNames.contains(file)) {
            for (int l = 0; l < cases.size(); l++) {
                Testcase test = cases.get(l);

                if (test.getType().equals(type) && test.getNAME().startsWith(file)) {
                    ArrayList<Testcase> list = new ArrayList<Testcase>();
                    list.add(test);

                    String log = Tools.printFixLineString(type.toString(), "▄");
                    Tools.writeToFile(file, log + "\n");
                    setupCases(list);
                    log = Tools.printFixLineString(type.toString() + " end", "*");
                    Tools.writeToFile(file, log + "\n");
                }
            }
        }
    }

    private synchronized void setVariable(String name, HashMap<String, String> value) {

        HashMap<String, String> map;

        map = variables.get(name);
        if (map == null) {
            variables.put(name, value);
        } else {
            Set<String> keys = value.keySet();
            for (String key : keys) {
                String val = value.get(key);

                if (key.equalsIgnoreCase("token") && !val.startsWith("Bearer") && name.equalsIgnoreCase("all")) {
                    val = "Bearer " + val;
                }

                map.put(key, val);
            }
            variables.put(name, map);
        }
    }

}
