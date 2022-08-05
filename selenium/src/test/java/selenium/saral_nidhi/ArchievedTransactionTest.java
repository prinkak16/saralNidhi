package selenium.saral_nidhi;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import pageObjects.LandingPage;
import resources.Base;

public class ArchievedTransactionTest  extends Base {
	public WebDriver driver;
	@Test 
	
	public void filterTransaction() throws IOException, InterruptedException {
		driver = initializeDriver();

		driver.get(url);

		//ngDriver = new NgWebDriver((JavascriptExecutor) driver);
		//ngDriver.withRootSelector("root-app").waitForAngularRequestsToFinish();
		
		//LocalStorage storage = ((WebStorage) driver).getLocalStorage();
		//new SetLocalStorage(storage, driver, context);
		ArrayList<String> a = new ArrayList<String>();

		UserManagementDataDriven dd = new UserManagementDataDriven();
		ArrayList<String> excel_data = dd.getData("addUser", a);

		LandingPage lp = new LandingPage(driver);
		
		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		  lp.login_email().sendKeys("kumar.vikastreasurer@jarvis.consulting");
		  lp.login_password().sendKeys("Test@123");
		  
		  WebElement sendOTP =
		  wait.until(ExpectedConditions.elementToBeClickable(lp.send_OTP()));
		  sendOTP.click();
		  
		  WebElement enterOTP =
		  wait.until(ExpectedConditions.visibilityOfElementLocated(lp.enter_otp()));
		  enterOTP.sendKeys("227244");
		  
		  WebElement loginButton =
		  wait.until(ExpectedConditions.elementToBeClickable(lp.login_btn()));
		  loginButton.click();
		  
		  
		  Thread.sleep(2000);
			
		  driver.findElement(By.xpath("(//*[@class='action-text'])[3]")).click();
		  
		  Thread.sleep(6000);
		  
		  WebElement calender = driver.findElement(By.xpath("//*[@formcontrolname='start_date']"));
		  calender.click();
		 
		  /*
		  WebElement calender = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//*[@class='mat-datepicker-toggle-default-icon ng-star-inserted']"))));
		 
		 ((JavascriptExecutor)driver).executeScript("arguments[0].click();",calender);
		  
		  Thread.sleep(1000);
		  //mat-calendar-body-cell-content mat-focus-indicator
		  
		  WebElement selectDate = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("(//*[@class='mat-calendar-body-cell-content mat-focus-indicator'])[28]"))));
		  ((JavascriptExecutor)driver).executeScript("arguments[0].click();",selectDate);
		  
		  */
		  // mat-calendar
		  
		  Thread.sleep(2000);
		  //driver.findElement(By.xpath("(//*[contains(text(),'28'])")).click();
		  
		  WebElement selectDate = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("(//*[@class='mat-calendar-body-cell-content mat-focus-indicator'])[26]"))));
		  ((JavascriptExecutor)driver).executeScript("arguments[0].click();",selectDate);
		  Thread.sleep(2000);
		  WebElement selectDate2 = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("(//*[@class='mat-calendar-body-cell-content mat-focus-indicator'])[27]"))));
		  ((JavascriptExecutor)driver).executeScript("arguments[0].click();",selectDate2);
	}

}
