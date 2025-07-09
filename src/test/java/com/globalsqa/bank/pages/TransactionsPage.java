package com.globalsqa.bank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionsPage {
    private WebDriver driver;
    private WebDriverWait wait;
    // --- Locators for elements on the Transactions Page ---
    private By transactionTable = By.xpath("//table[@class='table table-bordered table-striped']");
    private By transactionTableRows = By.xpath("//table[@class='table table-bordered table-striped']/tbody/tr");
    private By backBtn = By.xpath("//button[@ng-click='back()']");
    private By resetBtn = By.xpath("//button[@ng-click='reset()']");

// Bu, Add Customer formunun submit butonu

    //--- Constructor ---
    public TransactionsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        wait.until(ExpectedConditions.visibilityOfElementLocated(transactionTable));
    }

    // --- Verification Methods ---
    public boolean isTransactionTableDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(transactionTable)).isDisplayed();
    }

    public List<WebElement> getTransactionRows() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(transactionTable));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(transactionTableRows, 0));
        return driver.findElements(transactionTableRows);
    }

    /**
     * Verifies if a specific transaction (amount and type) is present in the table.
     *
     * @param amount The transaction amount.
     * @param type   The transaction type (e.g., "Credit", "Debit").
     * @return true if the transaction is found, false otherwise.
     */
    public boolean isTransactionPresent(String amount, String type) {
        List<WebElement> rows = getTransactionRows();
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 3) {
                String actualAmount = cells.get(1).getText();
                String actualType = cells.get(2).getText();
                if (actualAmount.equals(amount) && actualType.equals(type)) {
                    System.out.println("Found transaction - Amount: " + actualAmount + ", Type: " + actualType);
                    return true;
                }
                /**
                 * Fills out the Add Customer form.
                 * @param firstName Customer's first name.
                 * @param lastName Customer's last name.
                 * @param postCode Customer's post code.
                 */


            }
        }

        return false;

    }

    // --- Action Methods ---
    /**
     * Reads all transactions from the table.
     * This method now waits for a specified number of transactions to appear.
     * @param expectedTransactionCount The number of transactions expected to be in the table.
     * @return A list of maps, where each map represents a transaction.
     */
    public List<Map<String, String>> getAllTransactions(int expectedTransactionCount) {
        // Wait until the expected number of transaction rows are present in the table.
        // This is a more robust way to ensure data has loaded.
        wait.until(ExpectedConditions.numberOfElementsToBe(transactionTableRows, expectedTransactionCount));

        List<Map<String, String>> transactions = new ArrayList<>();
        List<WebElement> rows = driver.findElements(transactionTableRows); // Use driver.findElements after explicit wait

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 3) {
                Map<String, String> transaction = new HashMap<>();
                transaction.put("Amount", cells.get(1).getText());
                transaction.put("Type", cells.get(2).getText());
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    /**
     * Clicks the Back button on the transactions page.
     * This action navigates back to the Customer Account Page.
     *
     * @return A new instance of CustomerAccountPage.
     */
    public CustomerAccountPage clickBackButton() {
        wait.until(ExpectedConditions.elementToBeClickable(backBtn)).click();
        // After clicking back, expect to return to the account summary page
        // The calling test method will then receive a new CustomerAccountPage object
        return new CustomerAccountPage(driver, wait);
    }

    /**
     * Clicks the Reset button on the transactions page to clear history.
     */
    public void clickResetButton() {
        wait.until(ExpectedConditions.elementToBeClickable(resetBtn)).click();
        // After clicking reset, wait for the transaction table rows to disappear.
        // This is a more explicit wait for the condition we expect.
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(transactionTableRows)); // Wait for any row to disappear
        } catch (Exception e) {
            // This catch block is mostly for debugging. If it still fails, the locator might be wrong
            // or the UI doesn't completely remove the elements, just makes them empty.
            // For now, we expect them to be invisible.
            System.err.println("Warning: Transaction rows did not become invisible after reset. This might indicate an issue or a different UI behavior.");
        }
    }


}
