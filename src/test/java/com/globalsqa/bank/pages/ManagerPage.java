package com.globalsqa.bank.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ManagerPage {
    private WebDriver driver;
    private WebDriverWait wait;
    // Locators for elements on the Manager Account Page
    private By addCustomerTab = By.xpath("//button[@ng-click='addCust()']");
    private By openAccountTab = By.xpath("//button[@ng-click='openAccount()']");
    private By customersTab = By.xpath("//button[@ng-click='showCust()']");
    private By homeBtn = By.xpath("//button[@ng-click='home()']");

    // --- Add Customer Form Locators ---
    public By firstNameInput = By.xpath("//input[@placeholder='First Name']");
    public By lastNameInput = By.xpath("//input[@placeholder='Last Name']");
    public By postCodeInput = By.xpath("//input[@placeholder='Post Code']");
    private By addCustomerSubmitButton = By.xpath("//button[@type='submit']");

    // --- Open Account From Locators ---
    private By customerDropdown = By.id("userSelect");
    private By currencyDropdown = By.id("currency");
    private By processButton = By.xpath("//button[@type='submit']");

    // --- Customers Tab Locators ---
    private By customerTable = By.xpath("//table[@class='table table-bordered table-striped']");
    private By customerTableRows = By.xpath("//table[@class='table table-bordered table-striped']/tbody/tr");
    private By customerTableSeach = By.xpath("//input[@placeholder = 'Search Customer']");
    private By deleteButtonInRow = By.xpath("//button[@ng-click = 'deleteCust(cust)']");


    // --- Constructor ---
    public ManagerPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        wait.until(ExpectedConditions.visibilityOfElementLocated(addCustomerTab));
    }

    // --- Actions Methods ---
    public void fillAddCustomerForm(String firstName, String lastName, String postCode) {
        if (firstName != null) {

            wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput)).sendKeys(firstName);
        }
        if (lastName != null) {

            wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameInput)).sendKeys(lastName);
        }
        if (postCode != null) {

            wait.until(ExpectedConditions.visibilityOfElementLocated(postCodeInput)).sendKeys(postCode);
        }


    }

    public void clickAddCustomerTab() {
        wait.until(ExpectedConditions.elementToBeClickable(addCustomerTab)).click();
    }

    public void clickOpenAccountTab() {
        wait.until(ExpectedConditions.elementToBeClickable(openAccountTab)).click();
    }

    public void clickCustomersTab() {
        wait.until(ExpectedConditions.elementToBeClickable(customersTab)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(customerTable));
    }

    public void clickHomeButton() {
        wait.until(ExpectedConditions.elementToBeClickable(homeBtn)).click();
    }

    public void clickAddCustomerSubmitButton() {
        wait.until(ExpectedConditions.elementToBeClickable(addCustomerSubmitButton)).click();
    }

    /**
     * Handles any JavaScript alert that appears after adding a customer.
     * This alert typically confirms successful customer addition.
     *
     * @return The text of the alert.
     */

    public String getAlertTextAndAccept() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            String alertText = driver.switchTo().alert().getText();
            driver.switchTo().alert().accept();
            return alertText;
        } catch (NoAlertPresentException e) {
            System.out.println("No alert appeared.");
            return null;
        }

    }

    public boolean isAddCustomerTabDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(addCustomerTab)).isDisplayed();
    }

    public String getFieldValidationMessage(By inputElementLocator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(inputElementLocator));
        return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].validationMessage;", element);

    }

    /**
     * Selects a customer from the dropdown.
     *
     * @param customerName The full name of the customer to select.
     */
    public void selectCustomerForAccount(String customerName) {
        WebElement dropdownElement = wait.until(ExpectedConditions.visibilityOfElementLocated(customerDropdown));
        Select select = new Select(dropdownElement);
        select.selectByVisibleText(customerName);
    }

    /**
     * Selects a currency from the dropdown.
     *
     * @param currency The currency to select (e.g., "Dollar", "Pound", "Rupee").
     */
    public void selectCurrencyForAccount(String currency) {
        WebElement dropdownElement = wait.until(ExpectedConditions.visibilityOfElementLocated(currencyDropdown));
        Select select = new Select(dropdownElement);
        select.selectByVisibleText(currency);

    }

    /**
     * Clicks the Process button to open the account.
     */
    public void clickProcessButton() {
        wait.until(ExpectedConditions.elementToBeClickable(processButton)).click();
    }

    /**
     * Searches for a customer by first name, last name, or postcode.
     * Note: This assumes the search input filters the table.
     *
     * @param searchText The text to enter in the search field.
     */
    public void searchCustomer(String searchText) {
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(customerTableSeach));

        String currentText = searchField.getAttribute("value");
        if (currentText != null && !currentText.isEmpty()) {
            // Arama kutusunu tamamen temizle
            searchField.sendKeys(Keys.CONTROL + "a"); // Tüm metni seç
            searchField.sendKeys(Keys.DELETE);         // Metni sil

            // Eğer üstteki komut çalışmazsa alternatif olarak backspace kullanabiliriz:
            // for (int i = 0; i < currentText.length(); i++) {
            //     searchField.sendKeys(Keys.BACK_SPACE);
            // }
            // try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        if (!searchText.isEmpty()) {
            searchField.sendKeys(searchText);
            try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } // Arama sonucunun filtrelenmesi için daha uzun bekleme
        } else {
            // Eğer boş arama yapılıyorsa ve arama çubuğu temizlendiyse, tablonun yeniden yüklenmesini bekleyelim
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(customerTableRows));
        }
    }

    /**
     * Finds a customer in the table by first name, last name, and postcode.
     *
     * @param firstName Expected first name.
     * @param lastName  Expected last name.
     * @param postCode  Expected post code.
     * @return true if the customer is found, false otherwise.
     */
    public boolean isCustomerPresentTable(String firstName, String lastName, String postCode) {
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(customerTableRows));

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));

            if (cells.size() >= 3) {
                String actualFirstName = cells.get(0).getText();
                String actualLastName = cells.get(1).getText();
                String actualPostCode = cells.get(2).getText();

                if (actualFirstName.equals(firstName) && actualLastName.equals(lastName) && actualPostCode.equals(postCode)) {
                    return true;
                }
            }
        }
        return false;
    }

   /**
           * Deletes a customer from the list by their full name and waits for their disappearance.
            * @param firstName First name of the customer to delete.
            * @param lastName Last name of the customer to delete.
            * @return true if the customer was successfully deleted and no longer present, false otherwise.
     */
    public boolean deleteCustomerAndVerifyDisappearance(String firstName, String lastName, String postCode) {
        // Müşterinin bulunduğu satırı bulup silme butonuna tıkla
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(customerTableRows));
        WebElement customerRow = null;
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 2 && cells.get(0).getText().equals(firstName) && cells.get(1).getText().equals(lastName)) {
                customerRow = row;
                break;
            }
        }

        if (customerRow != null) {
            WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(customerRow.findElement(deleteButtonInRow)));
            deleteButton.click();

            // SİLME İŞLEMİNDEN SONRA ÖNEMLİ BEKLEME: Müşterinin tablodan kaybolmasını bekle
            // Bu, tablonun yeniden render edildiği veya verinin kaybolduğu durumlarda kritiktir.
            // Bu beklemeyi eklemek, stale element reference hatalarını veya yanlış okumaları önler.
            try {
                // Müşterinin artık tabloda olmamasını bekle (negatif beklenti)
                wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(customerTable, firstName + " " + lastName)));
                System.out.println("Customer '" + firstName + " " + lastName + "' successfully disappeared from table.");
                return true;
            } catch (Exception e) {
                System.err.println("Customer '" + firstName + " " + lastName + "' did not disappear from table after deletion attempt: " + e.getMessage());
                return false;
            }
        }
        System.out.println("Customer row not found for deletion: " + firstName + " " + lastName);
        return false; // Müşteri bulunamadı
    }


}
