package com.globalsqa.bank.tests;
import com.globalsqa.bank.pages.TransactionsPage;
import com.globalsqa.bank.utils.BaseCustomerTest;
import com.globalsqa.bank.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class CustomerLoginTests extends BaseCustomerTest {
    // No need for BASE_URL here, it's in BaseCustomerTest
    // private final String BASE_URL = "https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login";
    // No need for Faker here, it's in BaseCustomerTest
    // private Faker faker = new Faker();


    /**
     * Test Scenario: Verify successful customer login.
     * Login is handled by BaseCustomerTest.setup().
     */
    @Test(priority = 1)
    public void testCustomerLogin() {
        // Customer is already logged in via BaseCustomerTest.setup()
        // And customerAccountPage is initialized.
        Assert.assertTrue(driver.getCurrentUrl().contains("/account"), "Customer should be redirected to account page.");
        Assert.assertTrue(customerAccountPage.getCurrentBalance() >= 0, "Initial balance should be displayed.");
        // We can add a logout here if each customer test needs to explicitly logout
        // Or simply let tearDown close the browser.
    }

    /**
     * Test Scenario: Verify successful deposit into a customer account.
     * Customer login and setup is handled by BaseCustomerTest.setup().
     */
    @Test(priority = 2)
    public void testCustomerDeposit() {
        // Customer is already logged in and customerAccountPage is initialized by BaseCustomerTest.setup().
        // The specific customer details for this test are available via customerFirstName, customerLastName, etc.
        log.info("Starting test: testCustomerDeposit for customer " + customerFullName); // Log start of test
        int initialBalance = customerAccountPage.getCurrentBalance();
        int depositAmount = 100;

        customerAccountPage.clickDepositTab();
        log.debug("Clicked Deposit tab."); // Use debug for more detailed steps
        customerAccountPage.enterDepositAmount(String.valueOf(depositAmount));
        log.info("Entered deposit amount: " + depositAmount);
        customerAccountPage.clickSubmitButton();
        log.info("Clicked Deposit submit button.");

        String statusMessage = customerAccountPage.getTransactionStatusMessage();
        log.info("Deposit status message received: " + statusMessage);
        Assert.assertNotNull(statusMessage, "Transaction status message should be displayed.");
        Assert.assertTrue(statusMessage.contains("Deposit Successful"), "Deposit should be successful.");
        log.info("Deposit successful for customer " + customerFullName);
        int finalBalance = customerAccountPage.getCurrentBalance();
        Assert.assertEquals(finalBalance, initialBalance + depositAmount, "Balance should be updated correctly after deposit.");
        log.info("Current balance after deposit: " + finalBalance);
        log.info("Finished test: testCustomerDeposit"); // Log end of test
    }

    @Test(priority = 3)
    public void testSuccessWithdrawal() throws InterruptedException {
        // --- Step 1: Deposit an initial amount to ensure sufficient balance for withdrawal ---
        log.info("Starting test: testCustomerDeposit for customer " + customerFullName); // Log start of test
        int initialDepositAmount = 200;
        customerAccountPage.clickDepositTab();
        log.debug("Clicked Deposit tab."); // Use debug for more detailed steps
        customerAccountPage.enterDepositAmount(String.valueOf(initialDepositAmount));
        log.info("Entered deposit amount: " + initialDepositAmount);
        customerAccountPage.clickSubmitButton();
        log.info("Clicked Deposit submit button.");
        String depositStatusMessage = customerAccountPage.getTransactionStatusMessage();
        log.info("Deposit status message received: " + depositStatusMessage);
        Assert.assertNotNull(depositStatusMessage, "Deposit status message should be displayed.");
        Assert.assertTrue(depositStatusMessage.contains("Deposit Successful"), "Initial deposit should be successful.");

        // --- Step 2: Perform the withdrawal ---
        int balanceAfterDeposit = customerAccountPage.getCurrentBalance();
        int withdrawalAmount = 50; // Ensure this is less than or equal to balanceAfterDeposit

        customerAccountPage.clickWithdrawalTab();
        log.debug("Clicked Withdrawl tab."); // Use debug for more detailed steps
        Thread.sleep(100);
        customerAccountPage.enterWithdrawalAmount(String.valueOf(withdrawalAmount));
        log.info("Entered withdrawl amount: " + withdrawalAmount);
        customerAccountPage.clickSubmitButton();
        log.info("Clicked Withdrawl submit button.");
        // --- Step 3: Verify the withdrawal status message and updated balance ---
        String withdrawalStatusMessage = customerAccountPage.getTransactionStatusMessage();
        log.info("Withdrawl status message received: " + withdrawalStatusMessage);
        Assert.assertNotNull(withdrawalStatusMessage, "Withdrawal status message should be displayed.");
        Assert.assertTrue(withdrawalStatusMessage.contains("Transaction successful"), "Withdrawal should be successful.");

        int finalBalance = customerAccountPage.getCurrentBalance();
        Assert.assertEquals(finalBalance, balanceAfterDeposit - withdrawalAmount, "Balance should be updated correctly after withdrawal.");
        log.info("Current balance after deposit: " + finalBalance);
        log.info("Finished test: testCustomerDeposit"); // Log end of test

    }


    /**
     * Test Scenario: Verify that deposit and withdrawal transactions are recorded in the transaction history.
     * This test first performs a deposit and a withdrawal, then navigates to the transactions page
     * to verify the details.
     */
    @Test(priority = 4)
    public void testTransactionsHistory() throws InterruptedException {
        // --- Step 1: Perform a Deposit ---
        log.info("Starting test: testCustomerDeposit for customer " + customerFullName); // Log start of test
        int depositAmount = 150;
        customerAccountPage.clickDepositTab();
        log.debug("Clicked Deposit tab."); // Use debug for more detailed steps
        customerAccountPage.enterDepositAmount(String.valueOf(depositAmount));
        log.info("Entered deposit amount: " + depositAmount);
        customerAccountPage.clickSubmitButton();
        log.info("Clicked Deposit submit button.");
        String depositStatusMessage = customerAccountPage.getTransactionStatusMessage();
        log.info("Deposit status message received: " + depositStatusMessage);
        Assert.assertTrue(depositStatusMessage.contains("Deposit Successful"), "Deposit should be successful.");

        // --- Step 2: Perform a Withdrawal ---
        int withdrawalAmount = 50;
        customerAccountPage.clickWithdrawalTab();
        log.debug("Clicked Withdrawl tab."); // Use debug for more detailed steps
        Thread.sleep(100);
        customerAccountPage.enterWithdrawalAmount(String.valueOf(withdrawalAmount));
        log.info("Entered withdrawl amount: " + withdrawalAmount);
        customerAccountPage.clickSubmitButton();
        log.info("Clicked Withdrawl submit button.");
        Thread.sleep(3000);
        String withdrawalStatusMessage = customerAccountPage.getTransactionStatusMessage();
        log.info("Withdrawl status message received: " + withdrawalStatusMessage);
        Assert.assertTrue(withdrawalStatusMessage.contains("Transaction successful"), "Withdrawal should be successful.");
        Thread.sleep(3000);
        // --- Step 3: Navigate to Transactions and Verify ---
        TransactionsPage transactionPage = customerAccountPage.clickTransactionsTab();
        log.debug("Clicked Transaction tab."); // Use debug for more detailed steps
        Thread.sleep(2000);
        // IMPORTANT: Now we wait for 2 transactions to be present before reading
        List<Map<String, String>> transactions = transactionPage.getAllTransactions(2);

        Assert.assertEquals(transactions.size(), 2, "There should be exactly 2 transactions in the history.");

        // Verify the last transaction (withdrawal)
        Map<String, String> lastTransaction = transactions.get(transactions.size() - 1);
        Assert.assertEquals(lastTransaction.get("Amount"), String.valueOf(withdrawalAmount), "Last transaction amount should match withdrawal.");
        Assert.assertEquals(lastTransaction.get("Type"), "Debit", "Last transaction type should be Debit (Withdrawal).");

        // Verify the first transaction (deposit)
        Map<String, String> firstTransaction = transactions.get(0);
        Assert.assertEquals(firstTransaction.get("Amount"), String.valueOf(depositAmount), "First transaction amount should match deposit.");
        Assert.assertEquals(firstTransaction.get("Type"), "Credit", "First transaction type should be Credit (Deposit).");

        customerAccountPage = transactionPage.clickBackButton();


        log.info("Finished test: testCustomerDeposit"); // Log end of test
    }

    /**
     * Test Scenario: Verify that the transaction history can be reset.
     * This test performs some transactions and then resets the history.
     */
    @Test(priority = 5)
    public void testResetTransactionsHistory() throws InterruptedException {

        // --- Step 1: Perform some transactions to populate history ---
        log.info("Starting test: testCustomerDeposit for customer " + customerFullName); // Log start of test
        customerAccountPage.clickDepositTab();
        log.debug("Clicked Deposit tab."); // Use debug for more detailed steps
        customerAccountPage.enterDepositAmount("100");
        log.info("Entered deposit amount: " + "100");
        customerAccountPage.clickSubmitButton();
        log.info("Clicked Deposit submit button.");
        customerAccountPage.getTransactionStatusMessage();
        log.info("Deposit status message received: " + customerAccountPage.getTransactionStatusMessage());

        customerAccountPage.clickWithdrawalTab();
        log.debug("Clicked Withdrawl tab."); // Use debug for more detailed steps
        Thread.sleep(100);
        customerAccountPage.enterWithdrawalAmount("20");
        log.info("Entered withdrawl amount: " + "20");
        customerAccountPage.clickSubmitButton();
        log.info("Clicked Withdrawl submit button.");
        customerAccountPage.getTransactionStatusMessage();
        log.info("Withdrawl status message received: " + customerAccountPage.getTransactionStatusMessage());
        Thread.sleep(5000);
        // --- Step 2: Navigate to Transactions and Reset History ---
        TransactionsPage transactionPage = customerAccountPage.clickTransactionsTab();
        log.debug("Clicked Transaction tab."); // Use debug for more detailed steps
        Thread.sleep(7000);
        // Wait for transactions to appear before resetting
        List<Map<String, String>> transactionsBeforeReset = transactionPage.getAllTransactions(2);
        Assert.assertTrue(transactionsBeforeReset.size() > 0, "There should be transactions before reset.");

        transactionPage.clickResetButton();

        // The clickResetButton() method now internally waits for 0 elements.
        // So, directly call getAllTransactions and verify the count.
        List<Map<String, String>> transactionsAfterReset = transactionPage.getAllTransactions(0); // Expect 0 transactions
        Assert.assertEquals(transactionsAfterReset.size(), 0, "Transaction history should be empty after reset.");

        customerAccountPage = transactionPage.clickBackButton();
                log.info("Finished test: testCustomerDeposit"); // Log end of test
    }

}

