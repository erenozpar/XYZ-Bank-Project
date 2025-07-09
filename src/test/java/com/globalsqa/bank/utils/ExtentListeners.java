package com.globalsqa.bank.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentListeners implements ITestListener {
    //ExtentReports Instance
    private static ExtentReports extentReports = ExtentManager.getInstance();
    //ThreadLocal ensures ExtentTest object is unique for each thread when running tests in parallel
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();


    @Override
    public void onStart(ITestContext context) {
        System.out.println("Test Suite started: " + context.getName());
        // No need to create ExtentReports instance here as ExtentManager handles it.
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Test Suite finished: " + context.getName());
        // Flush the report at the end of the entire test suite execution
        ExtentManager.flushReport();
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Test Started:  " + result.getMethod().getMethodName());
        // Create a new test entry in the report for each test method
        ExtentTest extentTest = extentReports.createTest(result.getMethod().getMethodName());
        test.set(extentTest); // Store in ThreadLocal
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test Passed: " + result.getMethod().getMethodName());
        // Log test status as PASS
        test.get().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test failed: " + result.getMethod().getMethodName());
        // Log test status as FAIL
        test.get().log(Status.FAIL, "Test Failed");
        test.get().log(Status.FAIL, result.getThrowable());// Log the exception/error

        // Capture screenshot on failure
        try {
            // Get the WebDriver instance from BaseTest (or BaseCustomerTest)
            // This requires access to the driver from the test context
            WebDriver driver = ((BaseTest) result.getInstance()).driver; // Cast to BaseTest to access driver
            String screenshotPath = takeScreenshot(driver, result.getMethod().getMethodName());
            test.get().fail("Screenshot: " + test.get().addScreenCaptureFromPath(screenshotPath));
            // Note: This casting works if all test classes extend BaseTest.
            // If you have BaseCustomerTest extending BaseTest, this cast will still work.
        } catch (NullPointerException | IOException e) {

            test.get().fail("Could not capture screeenshot: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test skipped: " + result.getMethod().getMethodName());
        // Log test status as SKIP
        test.get().log(Status.SKIP, "Test Skipped");
        test.get().log(Status.SKIP, result.getThrowable()); // Log the reason for skipping
    }
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not typically used for general test automation
    }



       /**
     * Takes a screenshot and saves it to the "test-output/Screenshots" directory.
     * @param driver The WebDriver instance.
     * @param methodName The name of the test method for naming the screenshot.
     * @return The absolute path to the saved screenshot file.
     * @throws IOException If there's an error saving the screenshot.
     */
    private String takeScreenshot(WebDriver driver, String methodName) throws IOException {
        if (driver == null) {
            System.err.println("WebDriver instance is null, cannot take screenshot.");
            return "";
        }

        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String screenshotName = methodName + "_" + timestamp + ".png";
        String screenshotDir = System.getProperty("user.dir") + "/test-output/Screenshots/";

        Path targetPath = Paths.get(screenshotDir + screenshotName);
        Files.createDirectories(targetPath.getParent()); // Create directory if it doesn't exist
        Files.copy(screenshotFile.toPath(), targetPath);

        System.out.println("Screenshot saved: " + targetPath.toString());
        return targetPath.toString(); // Return absolute path for ExtentReports
    }
}
