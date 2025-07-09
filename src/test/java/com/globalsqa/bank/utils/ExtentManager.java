package com.globalsqa.bank.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {
    private static ExtentReports extent;
    private  static String reportFileName = "Test-Automation Report-"+  new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".html";
    private static String reportFilePath = System.getProperty("user.dir")+ "/test-output/ExtentReports/" + reportFileName;

    /**
     * Initializes and returns the ExtentReports instance.
     * If the instance is null, it creates a new one.
     * @return ExtentReports instance.
     */
    public static ExtentReports getInstance(){
        if (extent == null){
            createInstance();
        }
        return  extent;
    }

    /**
     * Creates a new ExtentReports instance and configures the Spark Reporter.
     * The report will be generated in the "test-output/ExtentReports" directory.
     */

    private static synchronized void createInstance(){
        //Create the directory if it doesn't exist
        File reportDir = new File(System.getProperty("user.dir")+ "test-output/ExtentReports");
        if (!reportDir.exists()){
            reportDir.mkdirs();
        }
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);
        sparkReporter.config().setTheme(Theme.DARK); // Use DARK theme for better readability
        sparkReporter.config().setDocumentTitle("XYZ Bank Automation Report"); // Set report title
        sparkReporter.config().setReportName("Test Automation Results"); // Set report name
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss"); // Timestamp format

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Add system information to the report
        extent.setSystemInfo("Host Name", "Localhost");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Selenium Version", "4.x"); // Adjust if specific version is known
        extent.setSystemInfo("TestNG Version", "7.x"); // Adjust if specific version is known
    }

    /**
     * Flushes the report, writing all test data to the HTML file.
     * This method should be called once after all tests are finished.
     */
    public static void flushReport(){
        if (extent != null){
            extent.flush();
        }
    }
}
