
package selenium.saral_nidhi;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import pageObjects.Cheque_ModePage;
import pageObjects.LandingPage;
import resources.Base;

public class LoginPartTest extends Base {

	public WebDriver driver;
	public static Logger log = LogManager.getLogger(LoginPartTest.class);

	@BeforeSuite
	public void perfirmLogin(ITestContext context) throws IOException {
		driver = initializeDriver();

		driver.get(url);

		ArrayList<String> a = new ArrayList<String>();

		DataDriven dd = new DataDriven();
		ArrayList<String> excel_data = dd.getData("Cheque_ModeTest", a);

		LandingPage lp = new LandingPage(driver);
		Cheque_ModePage cashPage = new Cheque_ModePage(driver);

		lp.login_email().sendKeys(excel_data.get(1));
		lp.login_password().sendKeys(excel_data.get(2));

		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		WebElement sendOTP = wait.until(ExpectedConditions.elementToBeClickable(lp.send_OTP()));
		sendOTP.click();

		WebElement enterOTP = wait.until(ExpectedConditions.visibilityOfElementLocated(lp.enter_otp()));
		enterOTP.sendKeys(excel_data.get(3));

		WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(lp.login_btn()));
		loginButton.click();

		WebElement heading1 = wait.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getFirstHeading()));
		Assert.assertEquals(heading1.getText(), "Nidhi Collection");

		LocalStorage storage = ((WebStorage) driver).getLocalStorage();
		log.info("Printing local storage " + storage.getItem("authStatus"));

		log.info("key set :" + storage.keySet());

		// context.setAttribute("storageObj", storage.keySet());
		// context.setAttribute("userEmail", storage.getItem("userEmail"));

		for (String item : storage.keySet()) {
			System.out.println("item form login :" + item);

			context.setAttribute(item, storage.getItem(item));
		}
		// context.setAttribute("userEmail", storage.getItem("userEmail"));
		log.info("Login successfully from Login Part");
	}

	@AfterSuite
	public void terminate() {
		driver.close();
		log.info("login page driver is closed--##---$$--&&-");
	}

}