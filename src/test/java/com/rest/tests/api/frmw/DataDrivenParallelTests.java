package com.rest.tests.api.frmw;


import com.rest.tests.api.frmw.request.RequestFactory;
import com.rest.tests.api.frmw.response.ExpectedFactory;
import com.rest.tests.api.frmw.response.IExpectationValidator;
import com.rest.tests.api.frmw.settings.*;
import com.rest.tests.api.frmw.settings.TestParser;
import com.rest.tests.api.frmw.testcase.Response;
import com.rest.tests.api.frmw.testcase.TC;
//import listeners.ResultListeners;
import org.apache.http.HttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by msolosh on 3/25/2016.
 */
//@Listeners(ResultListeners.class)
public class DataDrivenParallelTests {

    private DecimalFormat formatter = (DecimalFormat)NumberFormat.getNumberInstance(Locale.US);
    private Settings api;
    private double full;
    private double executed = 0;
    private ArrayList<String> suites = new ArrayList<>();
    private ArrayList<String> failed = new ArrayList<>();
    private HashMap<String, Map<String, String>> variables = new HashMap<String, Map<String, String>>();
    private ArrayList<String> skipNames = new ArrayList<>();
    final Logger logger = LoggerFactory.getLogger(DataDrivenParallelTests.class);

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        formatter.applyPattern("00.00");
        api = new Settings();
        setVariable("all", api.getGlobalVariables());

        Filewalker fll = new Filewalker();
        ArrayList<String> setup = fll.walk(Paths.get("TestSuite","_SetUp").toString());

        System.out.println(">>>> Read TestSuite/_SetUp/ files:");

        if (setup != null)
        {
            full = setup.size();
            for (String filename : setup)
            {
//                System.out.println(filename.substring(filename.indexOf("TestSuite")));
                TestParser suite = new TestParser(filename);
                setVariable(filename, suite.getTSVariables());
                execTestSuite(filename, "global");
            }
        }

        ArrayList<String> files = fll.walk(); //list of files

        System.out.println(">>>> Parse files:");
        if (files != null)
        {

            for (String filename : files)
            {
                if (filename.contains(Paths.get("TestSuite","_SetUp").toString()) || filename.contains(Paths.get("TestSuite","_TearDown").toString()))
                    continue;
                System.out.print(filename.substring(filename.indexOf("TestSuite")));
                TestParser suite = new TestParser(filename);

                if (Tools.arrayContains(suite.getTcsuite().getTags(), api.getTags()))
                {
                    System.out.print(" - success");
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
                            System.out.print(" - success");
                            suites.add(filename);
                            setVariable(filename, suite.getTSVariables());
                            break;
                        }
                    }
                }
                System.out.println();
            }
        }
        else
            System.out.println("NO FILES");
        System.out.println(Tools.printFixLineString("STATS", "*"));
        System.out.println("Files: " + suites.size());
        System.out.println(Tools.printFixLineString("RUN", "*"));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {

        Filewalker fll = new Filewalker();
        ArrayList<String> setup = fll.walk(Paths.get("TestSuite","_TearDown").toString());

        if (setup != null)
        {
            for (String filename : setup)
            {
                execTestSuite(filename, "global");
            }

            for (String failure : failed)
            {
                System.out.println(Tools.printFixLineString("LOGS", "≠"));
                System.out.println(Tools.readFile(failure+".log"));
            }
            if (failed.size() > 0)
                System.out.println(Tools.printFixLineString("Failed files", "|"));
            for (String failure : failed)
            {
                System.out.println(failure);
            }
            if (skipNames.size() > 0)
                System.out.println(Tools.printFixLineString("Skipped files", "|"));
            for (String skip : skipNames)
            {
                System.out.println(skip);
            }
            System.out.println(Tools.printFixLineString("", "•"));
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(Object[] data) {

        String file = data[0].toString();
        try {
            execTestSuite(file, "setup");
        } catch (Throwable e) {
        }
    }

    @AfterMethod(alwaysRun = true)
    public void teardown(Object[] data) throws Exception {

        String file = data[0].toString();
        try {
            execTestSuite(file, "teardown");
        } catch (Exception e) {
        }
    }

    @DataProvider(name = "REST", parallel = true)
    public Object[][] createData() throws Exception {

        if (suites.size() > 0)
        {
            Object[][] testList = new Object[suites.size()][1];
            for (int k = 0; k < suites.size(); k++) {
                testList[k][0] = suites.get(k);

                TestParser parser = new TestParser(suites.get(k));
                parser.writeTestListIntoFile();
            }
            full = testList.length;
            executed = 0;
            return testList;
        }
        else
            System.out.println("There is no valid cases for testing.");

        return null;
    }


    @Test(dataProvider = "REST")
    public void RestAPI(String file) throws Exception {
        if (skipNames.contains(file)) {
            Reporter.getCurrentTestResult().setAttribute("result", ITestResult.SKIP);
            return;
        }
        execTestSuite(file, "test");
    }

    private void execTestSuite(String filename, String type) throws Exception {

        long startFile = System.nanoTime();
        String log="";
        TestParser parser = new TestParser(filename);
        String file = parser.getTcsuite().getFile();
        JSONObject[] tests = new JSONObject[]{};
        switch (type)
        {
            case "setup":
                tests = parser.getTcsuite().getSetUp();
                log = log + Tools.printFixLineString("", "*") + "\n";
                log = log + Tools.printFixLineString("SETUP", "*") + "\n";
                log = log + Tools.printFixLineString("", "*");
                break;
            case "teardown":
                tests = parser.getTcsuite().getTearDown();
                log = log + Tools.printFixLineString("", "*") + "\n";
                log = log + Tools.printFixLineString("TEARDOWN", "*") + "\n";
                log = log + Tools.printFixLineString("", "*");
                break;
            case "test":
                tests = parser.getTcsuite().getTests();
                log = log + Tools.printFixLineString("", "*") + "\n";
                log = log + Tools.printFixLineString("TESTS", "*") + "\n";
                log = log + Tools.printFixLineString("", "*");
                break;
            case "global":
                tests = parser.getTcsuite().getTests();
                log = Tools.printFixLineString("TESTS", "*");
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

                    log = Tools.printFixLineString("TEST", "*");

                    Tools.writeToFile(file, log + "\n");
                    test = Tools.replaceVaraibles(test, mergeVariables(file));

                    long start = System.nanoTime();
                    HttpResponse response = RequestFactory.getRequest(test).sendRequest();
                    long elapsed = System.nanoTime() - start;
                    Tools.writeToFile(file, "Elapsed ms --> " + elapsed / 1e6 + "\n", true);
                    log = Tools.PrintHeaders(response, api);
                    Tools.writeToFile(file, log + "\n");

                    Assert.assertNotNull("Response is NULL. Probably there is a problem with network.", response);

                    Response rsps = new Response(response);

                    int status = rsps.getStatus();

                    Tools.writeToFile(file, Tools.printFixLineString("RESPONSE", "-") + "\n" +
                            "STATUS: " + status + "\n");

                    if (rsps.getContenType() != null)
                    {

                        if (!rsps.getContenType().contains("octet-stream"))
                            Tools.writeToFile(file, "BODY:\n" + rsps.getDocument() + "\n");
                        else
                            Tools.writeToFile(file, "BODY: null or binary\n");
                    }
                    else
                        Tools.writeToFile(file, "BODY: null or binary\n");

                    log = Tools.printFixLineString("EXPECTATIONS", "-");

                    Tools.writeToFile(file, log + "\nCheck Expectations: \n");

                    for (JSONObject expect : test.getExpectations()) {

                        expect = (JSONObject) par.parse(Tools.replaceVariable(expect.toString(), mergeVariables(file)));

                        Tools.writeToFile(file, expect.toJSONString());
                        HashMap testvar = new HashMap();
                        IExpectationValidator expectedValidator = ExpectedFactory.getExpectedObject(expect);

                        if (expectedValidator != null) {

                            testvar = expectedValidator.validation(rsps, file);

                            if (testvar != null) {
                                if (file.contains(Paths.get("TestSuite","_TearDown").toString()) || file.contains(Paths.get("TestSuite","_SetUp").toString())) {
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
                }
                catch (Throwable t) {
                    Tools.writeToFile(file, " - FAILED\n");
                    if ((test.getLoop() > 1) && (loop > 0)) {
                        Tools.writeToFile(file, String.format("\n%s more tries remain to get result.\n", loop));
                    } else {
                        long endTest = System.nanoTime();
                        executed = executed + 1;
                        System.out.print("[" + formatter.format(executed/(full/100)) + "%] ");
                        if (type.equalsIgnoreCase("test")) {
                            System.out.println("••FAILED•• " + file.substring(file.indexOf("TestSuite")) + " << " + (endTest - startFile) / 1e6 + "ms");
                            failed.add(file);
                        }
                        else  if (type.equalsIgnoreCase("setup")) {
                            System.out.println("*SKIPPED** " + file.substring(file.indexOf("TestSuite")) + " << " + (endTest - startFile) / 1e6 + "ms");
                            skipNames.add(file);
                        }
                        Tools.writeToFile(file, t.getMessage() + "\n");
                        Assert.fail(test.getName() + "\n" + t.getMessage());
                    }

                    Thread.sleep(test.getLoopTimeout() * 1000);
                }
            }
        }
        long endTest = System.nanoTime();
        if (type.equalsIgnoreCase("test") || type.equalsIgnoreCase("global")) {
            executed = executed + 1;
            System.out.println("[" + formatter.format(executed/(full/100)) + "%] " +
                    "----OK---- " + file.substring(file.indexOf("TestSuite")) + " << " + (endTest - startFile) / 1e6 + "ms");
        }
    }

    private synchronized void setVariable(String name, Map<String, String> value) {

        Map<String, String> map;

        map = variables.get(name);
        if (map == null) {
            if (value.size() > 0)
            {

                Set<String> keys = value.keySet();
                for (String key : keys) {
                    String val = value.get(key);

                    String upd = Tools.replaceVariable(val, this.variables.get("all"));
                    value.put(key, upd);
                }
            }
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
        if (variables.containsKey(filename)) {
            HashMap<String, String> map = new HashMap<>(variables.get(filename));
            Set<String> keys = map.keySet();
            for (String key : keys) {
                String val = map.get(key);

                vars.put(key.toLowerCase(), val);
            }
        }
        return vars;
    }

}
