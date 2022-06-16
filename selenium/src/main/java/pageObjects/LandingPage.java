package pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LandingPage {

	public WebDriver driver;

	public LandingPage(WebDriver driver) {
		this.driver = driver;

	}

	By login_email = By.name("email");
	By login_pass = By.name("password");
	By sendOTP = By.tagName("button");
	By enterOTP = By.name("otp");
	By loginButton = By.tagName("button");

	public WebElement login_email() {

		return driver.findElement(login_email);
	}

	public WebElement login_password() {

		return driver.findElement(login_pass);
	}

	public WebElement send_OTP() {

		return driver.findElement(sendOTP);
	}
	public By enter_otp() {

		//return driver.findElement(enterOTP);
		return enterOTP;
	}

	public WebElement login_btn() {

		return driver.findElement(loginButton);
	}

}
