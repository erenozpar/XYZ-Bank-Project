package com.globalsqa.bank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CustomerAccountPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // --- Customer Account Page Locators ---
    private By depositTab = By.xpath("//button[@ng-click='deposit()']");
    private By withdrawlTab = By.xpath("//button[@ng-click='withdrawl()']");
    private By transactionsTab = By.xpath("//button[@ng-click='transactions()']");
    // Common elements for amount input and submit button on deposit/withdraw forms
    private By amountInput = By.xpath("//input[@placeholder='amount']");
    private By submitButton = By.xpath("//button[@type='submit']"); // Generic submit button
    private By balanceValue = By.xpath("//strong[@class='ng-binding'][2]");

    private By logoutBtn = By.xpath("//button[@ng-show='logout']");
    private By homeBtn = By.xpath("//button[@ng-click='home()']");
    private By accountNumberLabel = By.xpath("//div[text()='Account Number : ']");

    private By welcomeMessage = By.xpath("//span[@class='fontBig ng-binding']");

    // For messages after deposit/withdrawal
    private By transactionMessage = By.xpath("//span[@ng-show='message']"); // This span shows "Deposit Successful" or "Transaction Failed"

    public CustomerAccountPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        // Wait until the balance display is visible to confirm page load
        wait.until(ExpectedConditions.visibilityOfElementLocated(balanceValue));
    }

    // --- Action Methods ---

    public void clickDepositTab() {
        wait.until(ExpectedConditions.elementToBeClickable(depositTab)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(amountInput));
    }
    /**
     * Enters the deposit amount into the amount input field.
     * @param amount The amount to deposit as a String.
     */

    public void enterDepositAmount(String amount) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(amountInput)).sendKeys(amount);
    }



    public void clickSubmitButton() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
    }

    /**
     * Gets the current account balance displayed on the page.
     * @return The current balance as an integer.
     */
    public int getCurrentBalance() {
        // Wait for the balance to be updated/visible, then get text
        String balanceText = wait.until(ExpectedConditions.visibilityOfElementLocated(balanceValue)).getText();
        return Integer.parseInt(balanceText); // Convert text to integer
    }

    /**
     * Handles any success/error message that appears after an action (like deposit/withdrawal).
     * @return The text of the message displayed on the page, or null if not found.
     */
    public String getTransactionStatusMessage() {
        // This locator might need to be adjusted based on where the message appears
        // On this application, messages usually appear as "Deposit Successful", "Withdrawal Successful" above the form.
        try {
            // Wait until the message is visible, but not too long if it doesn't appear
            return wait.until(ExpectedConditions.visibilityOfElementLocated(transactionMessage)).getText();
        } catch (Exception e) {
            return null; // No message found within timeout
        }
    }

    public void clickWithdrawalTab() {
        wait.until(ExpectedConditions.elementToBeClickable(withdrawlTab)).click();
        wait.until(ExpectedConditions.elementToBeClickable(amountInput));
    }

    public void enterWithdrawalAmount(String amount) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(amountInput)).sendKeys(amount);

    }


    public TransactionsPage clickTransactionsTab() {
        wait.until(ExpectedConditions.elementToBeClickable(transactionsTab)).click();
        // After clicking, the page will transition to the transactions page.
        // Return a new TransactionPage object to interact with the new page.
        return new TransactionsPage(driver, wait);// Returns the next page object
    }





    /**
     * Performs a deposit operation.
     *
     * @param amount The amount to deposit.
     */
    public void depositFunds(String amount) {
        clickDepositTab();
         enterDepositAmount(amount);
        clickSubmitButton();
    }

    /**
     * Performs a withdrawal operation.
     *
     * @param amount The amount to withdraw.
     */
    public void withdrawFunds(String amount) throws InterruptedException {
        clickWithdrawalTab();
        Thread.sleep(3000);
             enterWithdrawalAmount(amount);
        clickSubmitButton();

    }

    public void clickHomeButton() {
        wait.until(ExpectedConditions.elementToBeClickable(homeBtn)).click();
    }

    public String getWithdrawalErrorMessage() {
        // Re-confirming the exact locator for "Transaction Failed" message.
        // It uses the same ng-show='message' span.
        return wait.until(ExpectedConditions.visibilityOfElementLocated(transactionMessage)).getText();
    }

    // Verification methods
    public boolean isLogoutButtonDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(logoutBtn)).isDisplayed();
    }

    public boolean isAccountNumberLabelDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(accountNumberLabel)).isDisplayed();
    }

    public String getWelcomeMessageText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeMessage)).getText();
    }

    public double getBalance() {
        String balanceText = wait.until(ExpectedConditions.visibilityOfElementLocated(balanceValue)).getText();
        return Double.parseDouble(balanceText);
    }

    public String getTransactionStatusMessage(String expectedText) {
        // Wait for the message element to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(transactionMessage));
        // Wait for the text in the message element to contain the expected text.
        // This is more robust than waiting for it to simply not be empty.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(transactionMessage, expectedText));

        // Re-find the element to get its text, just to be safe from stale element.
        return driver.findElement(transactionMessage).getText();
    }

}

