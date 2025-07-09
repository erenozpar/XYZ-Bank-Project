package com.globalsqa.bank.utils;

import com.globalsqa.bank.pages.LoginPage;
import com.globalsqa.bank.pages.ManagerPage; // Needed to add customer as prerequisite
import com.globalsqa.bank.pages.CustomerAccountPage; // Needed for customer account page
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.github.javafaker.Faker; // For generating test data
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;


public class BaseCustomerTest extends BaseTest {
    protected LoginPage loginPage;
    protected ManagerPage managerPage; // To create customer as prerequisite
    protected CustomerAccountPage customerAccountPage; // For direct access in customer tests
    protected Faker faker = new Faker(); // Faker instance for test data generation

    protected Logger log = LogManager.getLogger(getClass());
    protected final String BASE_URL = "https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login";

    // Variables to hold customer details created in setup
    protected String customerFirstName;
    protected String customerLastName;
    protected String customerPostCode;
    protected String customerFullName;

    @BeforeMethod
    @Override
    @Parameters("browser")
    public void setup(@Optional("chrome") String browser) {
        super.setup(browser);
        // Initialize Page Objects for Customer tests
        loginPage = new LoginPage(driver, wait);
        loginPage.loginAsManager(BASE_URL);
        managerPage = new ManagerPage(driver, wait);
        managerPage.clickAddCustomerTab();

        customerFirstName = faker.name().firstName();
        customerLastName = faker.name().lastName();
        customerPostCode = faker.address().zipCode();
        customerFullName = customerFirstName + " " + customerLastName;

        managerPage.fillAddCustomerForm(customerFirstName, customerLastName, customerPostCode);
        managerPage.clickAddCustomerSubmitButton();
        managerPage.getAlertTextAndAccept();

        managerPage.clickOpenAccountTab();
        managerPage.selectCustomerForAccount(customerFullName);
        managerPage.selectCurrencyForAccount("Dollar");
        managerPage.clickProcessButton();
        managerPage.getAlertTextAndAccept();

        managerPage.clickHomeButton();

        // --- ACTUAL CUSTOMER LOGIN ---
        customerAccountPage = loginPage.loginAsCustomer(BASE_URL, customerFullName);
    }



    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            log.info("Closing browser..."); // Log info message
            driver.quit();
        }
        log.info("Test teardown complete.");
    }
}
