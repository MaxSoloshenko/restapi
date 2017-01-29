package com.rest.tests.api.frwm.listeners;

import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

/**
 * Created by msolosh on 1/29/2017.
 */
public class ResultListeners extends TestListenerAdapter {

    @Override
    public void onTestSuccess(ITestResult result) {

        try {
            if ((int)result.getAttribute("result") == 3)
            {
                Reporter.getCurrentTestResult().setStatus(ITestResult.SKIP);
            }
        } catch (Exception e) {

        }
    }
}
