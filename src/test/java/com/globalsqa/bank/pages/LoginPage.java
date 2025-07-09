package com.globalsqa.bank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    //Locators for element on the Login Page
    private By customerLoginBtn = By.xpath("//button[@ng-click='customer()']");
    private By bankManagerLoginBtn = By.xpath("//button[@ng-click='manager()']");
    private By customerSelectDropdown = By.id("userSelect");
    private By loginBtn = By.xpath("//button[.='Login']");

    //Constructor to initialize WebDriver and WebDriverWait
    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    //Methods on the Login Page

    /*
     * Navigates to the login page URL
     *  */
    public void navigateToLoginPage(String url) {
        driver.get(url);
    }
    /*
     * Clicks on the Customer Login button.
     */

    public void clickCustomerLoginButton() {
        driver.findElement(customerLoginBtn).click();
    }

    /*
     * Selects a customer from the dropdown.
     * @param customerName The visible text of the customer to select.
     */
    public void selectCustomer(String customerName) {
        WebElement dropdownElement = wait.until(ExpectedConditions.visibilityOfElementLocated(customerSelectDropdown));
        Select select = new Select(dropdownElement);
        select.selectByVisibleText(customerName);
    }

    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn)).click();

    }
    /**
     * Performs a full customer login flow.
     * @param url The application URL.
     * @param customerName The name of the customer to log in.
     * @return Returns a new instance of CustomerAccountPage after successful login.
     */
    public CustomerAccountPage loginAsCustomer (String url, String customerName){
        navigateToLoginPage(url);
        clickCustomerLoginButton();
        selectCustomer(customerName);
        clickLoginButton();
        return new CustomerAccountPage(driver,wait);
    }

    public ManagerPage loginAsManager (String url){
        driver.get(url);
        wait.until(ExpectedConditions.elementToBeClickable(bankManagerLoginBtn)).click();
        return new ManagerPage(driver, wait);
    }

    // Verification Methods (Assertions will be in Test Classes, but these return element states)

    /**
     * Checks if the customer select dropdown is displayed.
     * @return true if displayed, false otherwise.
     */

    public boolean isCustomerSelectDropdownDisplayed (){
        return driver.findElement(customerSelectDropdown).isDisplayed();
    }

    /**
     * Checks if the login button after selection is displayed.
     * @return true if displayed, false otherwise.
     */
    public boolean isLoginButtonAfterSelectionDisplayed(){
        return wait.until(ExpectedConditions.visibilityOfElementLocated(loginBtn)).isDisplayed();

    }

}
