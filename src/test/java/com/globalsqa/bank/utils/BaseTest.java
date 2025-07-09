package com.globalsqa.bank.utils;

import com.globalsqa.bank.pages.LoginPage;
import com.globalsqa.bank.pages.ManagerPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

public  class BaseTest {
    public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected LoginPage loginPage;
    protected ManagerPage managerPage;

    protected Logger log = LogManager.getLogger(getClass());

    protected final String BASE_URL = "https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login";



    @BeforeMethod
    @Parameters("browser")
    public void setup(@Optional("chrome") String browser) {
        System.out.println("Setting up browser: " + browser + " for thread: " + Thread.currentThread().getId());
        if (browser.equalsIgnoreCase("chrome")) {
            tlDriver.set(new ChromeDriver());
        } else if (browser.equalsIgnoreCase("firefox")) {
            tlDriver.set(new FirefoxDriver());
        } else {
            System.out.println("Please pass the correct browser value: chrome or firefox");
        }

        driver = getDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");
    }

    public static synchronized WebDriver getDriver() {
        return tlDriver.get();
    }




    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            log.info("Closing browser..."); // Log info message
            driver.quit();
            tlDriver.remove();
        }
        log.info("Test teardown complete.");
    }

    // Override the setup method from BaseTest

}
