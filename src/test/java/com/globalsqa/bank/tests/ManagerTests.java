package com.globalsqa.bank.tests;


import com.github.javafaker.Faker;
import com.globalsqa.bank.pages.LoginPage;
import com.globalsqa.bank.pages.ManagerPage;
import com.globalsqa.bank.utils.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class ManagerTests extends BaseTest {
    protected LoginPage loginPage;
    protected ManagerPage managerPage;
    protected Faker faker = new Faker(); // Add Faker here if ManagerTests uses it

    @BeforeMethod
    @Override
    @Parameters("browser")
    public void setup(@Optional("chrome") String browser) {
        super.setup(browser);
        // Initialize Page Objects for Manager tests
loginPage = new LoginPage(driver, wait); // Use 'driver' and 'wait' from BaseTest's ThreadLocal
        loginPage.loginAsManager(BASE_URL);
        managerPage = new ManagerPage(driver, wait);
//

        // No need to call loginAsManager here in setup if each test starts from login.
        // Or if you want to login manager once for all manager tests, do it here:
        // loginPage.loginAsManager(BASE_URL);
        // This depends on whether each Manager test method should start from login or just once.
        // For simplicity and independence, it's often better to login within each test method if not too long,
        // or have a specific @BeforeClass for login if it applies to ALL tests in the class.
    }



    /**
     * Test Scenario: Verify that a new customer can be successfully added by the Bank Manager.
     * This test is now cleaner as login is handled by BaseTest.
     */
    @Test(priority = 1)
    public void testAddCustomer() {
        // Assume test starts from login page or homepage, and you need to login as manager for this test
        loginPage.loginAsManager(BASE_URL); // Ensure login happens within the test context
        managerPage.clickAddCustomerTab();

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String postCode = faker.address().zipCode();

        managerPage.fillAddCustomerForm(firstName, lastName, postCode);
        managerPage.clickAddCustomerSubmitButton();

        String alertMessage = managerPage.getAlertTextAndAccept();
        Assert.assertTrue(alertMessage.contains("Customer added successfully"), "Customer should be added successfully.");
        // Add logging here, if not handled by Page Object methods
    }


    /**
     * Test Scenario: Verify that a new account can be opened for an existing customer.
     * This test adds its own unique customer first to ensure independence.
     */
    @Test(priority = 3)
    public void testOpenAccountForNewCustomer() {
        // managerPage object is already initialized and logged in via BaseTest.setup()

        // --- ADD CUSTOMER FOR THIS TEST ---
        managerPage.clickAddCustomerTab();

        String currentTestFirstName = faker.name().firstName();
        String currentTestLastName = faker.name().lastName();
        String currentTestPostCode = faker.address().zipCode();
        String currentTestFullName = currentTestFirstName + " "+ currentTestLastName;

        managerPage.fillAddCustomerForm(currentTestFirstName, currentTestLastName, currentTestPostCode);
        managerPage.clickAddCustomerSubmitButton();
        managerPage.getAlertTextAndAccept(); // Accept the "Customer added successfully" alert

        // --- PROCEED TO OPEN ACCOUNT ---
        managerPage.clickOpenAccountTab();

        managerPage.selectCustomerForAccount(currentTestFullName);
        managerPage.selectCurrencyForAccount("Dollar");

        managerPage.clickProcessButton();

        String openAccountAlert = managerPage.getAlertTextAndAccept();

        Assert.assertNotNull(openAccountAlert, "An alert should be present after opening an account.");
        Assert.assertTrue(openAccountAlert.contains("Account created successfully"), "Alert message should indicate successful account creation.");

        managerPage.clickHomeButton();
    }
    /**
     * Test Scenario: Verify that a newly added customer appears correctly in the customer list.
     * This test adds its own unique customer to ensure independence.
     */

    @Test(priority = 4)
    public void testVerifyCustomerInList() {
        // managerPage object is already initialized and logged in via BaseTest.setup()

        // --- ADD CUSTOMER FOR THIS TEST ---
        managerPage.clickAddCustomerTab();

        String customerToVerifyFirstName = faker.name().firstName();
        String customerToVerifyLastName = faker.name().lastName();
        String customerToVerifyPostCode = faker.address().zipCode();

        managerPage.fillAddCustomerForm(customerToVerifyFirstName, customerToVerifyLastName, customerToVerifyPostCode);
        managerPage.clickAddCustomerSubmitButton();
        managerPage.getAlertTextAndAccept(); // Accept the "Customer added successfully" alert

        // --- GO TO CUSTOMERS LIST AND VERIFY ---
        managerPage.clickCustomersTab();

        managerPage.searchCustomer(customerToVerifyFirstName);

        boolean customerFound = managerPage.isCustomerPresentTable(
                customerToVerifyFirstName,
                customerToVerifyLastName,
                customerToVerifyPostCode
        );

        Assert.assertTrue(customerFound, "Newly added customer should be present in the customer list.");

        managerPage.clickHomeButton();
    }
    /**
     * Test Scenario: Verify that a customer can be successfully deleted from the list.
     * This test adds its own unique customer first to ensure independence.
     */

    @Test(priority = 5)
    public void testDeleteCustomer()  {
        // managerPage object is already initialized and logged in via BaseTest.setup()

        // --- ADD CUSTOMER FOR THIS TEST ---
        managerPage.clickAddCustomerTab();

        String customerToDeleteFirstName = faker.name().firstName();
        String customerToDeleteLastName = faker.name().lastName();
        String customerToDeletePostCode = faker.address().zipCode();
        // String customerToDeleteFullName = customerToDeleteFirstName + " " + customerToDeleteLastName; // Not used directly in deletion method

        managerPage.fillAddCustomerForm(customerToDeleteFirstName, customerToDeleteLastName, customerToDeletePostCode);
        managerPage.clickAddCustomerSubmitButton();
        managerPage.getAlertTextAndAccept(); // Accept the "Customer added successfully" alert

        // --- GO TO CUSTOMERS LIST, DELETE AND VERIFY DISAPPEARANCE ---
        managerPage.clickCustomersTab();

        managerPage.searchCustomer(customerToDeleteFirstName);

        boolean customerFoundBeforeDelete = managerPage.isCustomerPresentTable(
                customerToDeleteFirstName,
                customerToDeleteLastName,
                customerToDeletePostCode
        );
        Assert.assertTrue(customerFoundBeforeDelete, "Customer to be deleted should be present in the list before deletion.");

        boolean deletedAndVerified = managerPage.deleteCustomerAndVerifyDisappearance(
                customerToDeleteFirstName,
                customerToDeleteLastName,
                customerToDeletePostCode
        );
        Assert.assertTrue(deletedAndVerified, "Customer should be successfully deleted and disappear from the table.");

        // Clear search to re-check the full list (important for robust verification)
        managerPage.searchCustomer("");

        boolean customerFoundAfterDelete = managerPage.isCustomerPresentTable(
                customerToDeleteFirstName,
                customerToDeleteLastName,
                customerToDeletePostCode
        );
        Assert.assertFalse(customerFoundAfterDelete, "Customer should NOT be present in the list after deletion (after clearing search).");

        managerPage.clickHomeButton();
    }



    @Test(priority = 6)
    public void testAddCustomerWithMissingLastName() {
        // managerPage object is already initialized and logged in via BaseTest.setup()

        managerPage.clickAddCustomerTab();

        String firstName = faker.name().firstName();
        String lastName = null;
        String postCode = faker.address().zipCode();

        managerPage.fillAddCustomerForm(firstName, lastName, postCode);
        managerPage.clickAddCustomerSubmitButton();

        String validationMessage = managerPage.getFieldValidationMessage(managerPage.lastNameInput);

        System.out.println("Validation message for Last Name:" + validationMessage);

        Assert.assertNotNull(validationMessage, "Validation message should be displayed for missing Last Name.");
        Assert.assertTrue(validationMessage.contains("fill out this field") || validationMessage.contains("doldurun"),
                "Validation message for Last Name is incorrect or not as expected.");

        managerPage.clickHomeButton();
    }


}
