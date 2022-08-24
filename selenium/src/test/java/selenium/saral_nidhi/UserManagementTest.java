package selenium.saral_nidhi;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.paulhammant.ngwebdriver.NgWebDriver;
import pageObjects.LandingPage;
import resources.Base;
import static org.openqa.selenium.support.locators.RelativeLocator.with;


public class UserManagementTest extends Base {
	public WebDriver driver;
	public NgWebDriver ngDriver;
	public static Logger log = LogManager.getLogger(UserManagementTest.class);


	String fullName = "";
	String phoneNo="";
	String email="";
	String password="";
	String givenRole="";

	@Test
	public void addUser(ITestContext context) throws IOException, InterruptedException {

		driver = initializeDriver();
		ngDriver = new NgWebDriver((JavascriptExecutor) driver);
		driver.get(url);

		LocalStorage storage = ((WebStorage) driver).getLocalStorage();
		new SetLocalStorage(storage, driver, context);
		ArrayList<String> a = new ArrayList<String>();

		UserManagementDataDriven dd = new UserManagementDataDriven();
		ArrayList<String> excel_data = dd.getData("addUser", a);

		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
       /*
       LandingPage lp = new LandingPage(driver);
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


		log.info("Login successfully in UserManagementTest");
		*/
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		// click on userManagement
		driver.findElement(By.xpath("(//*[@class='action-text'])[1]")).click();
		ngDriver.waitForAngularRequestsToFinish();

		WebElement addUserButton = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//span[contains(text(),'Add User')]"))));
		((JavascriptExecutor)driver).executeScript("arguments[0].click();", addUserButton);

		ngDriver.waitForAngularRequestsToFinish();

		fullName = excel_data.get(1);
		phoneNo = excel_data.get(2);
		givenRole=excel_data.get(5);

		 Random rand = new Random();
		 int randInt = rand.nextInt((9999 - 100) + 1) + 10;
		 String resRandom = String.valueOf(randInt);

		email = excel_data.get(3);

		email=givenRole+resRandom+email;

		password = excel_data.get(4);

		driver.findElement(By.xpath("(//*[@formcontrolname='name'])")).sendKeys(fullName);
		driver.findElement(By.xpath("(//*[@formcontrolname='phone_no'])")).sendKeys(phoneNo);
		driver.findElement(By.xpath("(//*[@formcontrolname='email'])")).sendKeys(email);
		driver.findElement(By.xpath("(//*[@formcontrolname='password'])")).sendKeys(password);

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,510)");


	    String[] rollArr = {"national_accountant","state_treasurer","state_accountant"};
		//String[] rollArr = {"national_accountant"};

	    String classAftercheck;
	    for(String rollOption : rollArr) {
	     WebElement rolls = driver.findElement(By.xpath("//mat-radio-button[@value='"+rollOption+"']"));
	     rolls.click();
	      Thread.sleep(1000);
	       classAftercheck = rolls.getAttribute("class");
	      System.out.println(classAftercheck);
			// After check mat-radio-checked will be added
			Assert.assertTrue(classAftercheck.contains("mat-radio-checked"));
	    }
	    // click on given role
	    try {
	    	Thread.sleep(1000);
	    	WebElement roll = driver.findElement(By.xpath("//mat-radio-button[@value='"+givenRole+"']"));
			roll.click();

			 classAftercheck = roll.getAttribute("class");
	    }catch (org.openqa.selenium.StaleElementReferenceException ex) {
	    	Thread.sleep(1000);
	    	WebElement roll = driver.findElement(By.xpath("//mat-radio-button[@value='"+givenRole+"']"));
			roll.click();

			 classAftercheck = roll.getAttribute("class");
		}
	    // After check mat-radio-checked will be added
	 	Assert.assertTrue(classAftercheck.contains("mat-radio-checked"));
	    driver.findElement(By.xpath("(//*[@formcontrolname='location_ids'])")).click();


	     try {
			    WebElement selectState = driver.findElement(By.xpath("//span[contains(text(),'"+excel_data.get(6)+"')]"));
			    selectState.click();

			}
			catch(org.openqa.selenium.StaleElementReferenceException ex)
			{
				WebElement selectState = driver.findElement(By.xpath("//span[contains(text(),'"+excel_data.get(6)+"')]"));
			    selectState.click();
			}
	     driver.findElement(By.xpath("//div[@role='listbox']")).sendKeys(Keys.TAB);
	     ngDriver.waitForAngularRequestsToFinish();

	      WebElement allData = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[1]")));
	     //WebElement allData = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Allow Data Download')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", allData);

	      try {
	    	  WebElement days15 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[2]")));
		      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", days15);
	      }catch(org.openqa.selenium.StaleElementReferenceException ex)
			{
	    	  WebElement days15 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[2]")));
		      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", days15);
			}

	      WebElement days30 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'30 Days')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", days30);


	      WebElement Create = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Create')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Create);

	      WebElement Edit_within_15_Days= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[5]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit_within_15_Days);

	      WebElement Edit_within_30_Days = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Edit within 30 Days')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit_within_30_Days);

	      WebElement EditLifeTime = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Edit Lifetime')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", EditLifeTime);

	      WebElement SupplementaryEntry= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Supplementary Entry')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", SupplementaryEntry);

	      WebElement AllowReceiptPrint = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Allow Receipt Print')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", AllowReceiptPrint);

	    //------------- NRI Donation Form ------

	      if(givenRole.equals("state_treasurer") || givenRole == "state_treasurer") {
	    	  System.out.println("inside state_treasurer *********");
	    	  Thread.sleep(1000);
	    	  WebElement Edit = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[10]")));
		      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit);
	      }

	      //------------- Party Unit------

	      WebElement State = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),' State ')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", State);

	      WebElement Zila = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Zila')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Zila);

	      WebElement Mandal = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Mandal')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Mandal);

	  /*
	      Thread.sleep(1000);
	      //Submit
	     WebElement Submit = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Submit')]")));
	     ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Submit);


	     String getSubmitTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted"))).getText();

	     System.out.println("getSubmitTxt :"+getSubmitTxt);

	     Assert.assertTrue(getSubmitTxt.contains("User Created/Updated."));

	      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted")));
	      */
	}

	@Test(dependsOnMethods = {"addUser"})

	public void addAlreadyExistingUser() throws InterruptedException, IOException {
		// click on back icon
        //driver.findElement(By.className("back-icon")).click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		WebElement back = wait.until(ExpectedConditions.elementToBeClickable(By.className("back-icon")));
	     ((JavascriptExecutor)driver).executeScript("arguments[0].click();", back);

	    ngDriver.waitForAngularRequestsToFinish();

        Thread.sleep(2000);
        ArrayList<String> a = new ArrayList<String>();

		UserManagementDataDriven dd = new UserManagementDataDriven();
		ArrayList<String> excel_data = dd.getData("addUser", a);

		//driver.findElement(ByAngular.buttonText("Add User"));
		WebElement addUserButton = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//span[contains(text(),'Add User')]"))));
		addUserButton.click();
		ngDriver.waitForAngularRequestsToFinish();
		driver.findElement(By.xpath("(//*[@formcontrolname='name'])")).sendKeys(fullName);
		driver.findElement(By.xpath("(//*[@formcontrolname='phone_no'])")).sendKeys(phoneNo);
		driver.findElement(By.xpath("(//*[@formcontrolname='email'])")).sendKeys("state_treasurer1467kumar.vinay@jarvis.consulting");
		driver.findElement(By.xpath("(//*[@formcontrolname='password'])")).sendKeys(password);


		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,510)");

		WebElement roll = driver.findElement(By.xpath("//mat-radio-button[@value='"+givenRole+"']"));
		roll.click();

		String classAftercheck = roll.getAttribute("class");
		// After check mat-radio-checked will be added
		Assert.assertTrue(classAftercheck.contains("mat-radio-checked"));

		Thread.sleep(2000);
		WebElement clickState = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//*[@formcontrolname='location_ids'])")));
		clickState.click();

		Thread.sleep(2000);

	     try {
			    WebElement selectState = driver.findElement(By.xpath("//span[contains(text(),'"+excel_data.get(6)+"')]"));
			    selectState.click();

			}
			catch(org.openqa.selenium.StaleElementReferenceException ex)
			{
				WebElement selectState = driver.findElement(By.xpath("//span[contains(text(),'"+excel_data.get(6)+"')]"));
			    selectState.click();
			}
	     driver.findElement(By.xpath("//div[@role='listbox']")).sendKeys(Keys.TAB);

	     ngDriver.waitForAngularRequestsToFinish();
	      WebElement allData = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[1]")));

	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", allData);

	      try {
	    	  WebElement days15 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[2]")));
		      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", days15);
	      }catch(org.openqa.selenium.StaleElementReferenceException ex)
			{
	    	  WebElement days15 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[2]")));
		      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", days15);
			}

	      WebElement days30 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'30 Days')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", days30);


	      WebElement Create = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Create')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Create);

	      WebElement Edit_within_15_Days= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[5]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit_within_15_Days);

	      WebElement Edit_within_30_Days = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Edit within 30 Days')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit_within_30_Days);

	      WebElement EditLifeTime = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Edit Lifetime')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", EditLifeTime);

	      WebElement SupplementaryEntry= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Supplementary Entry')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", SupplementaryEntry);

	      WebElement AllowReceiptPrint = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Allow Receipt Print')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", AllowReceiptPrint);

	    //------------- NRI Donation Form ------

	      if(givenRole.equals("state_treasurer") || givenRole == "state_treasurer") {
	    	  System.out.println("inside state_treasurer *********");
	    	  Thread.sleep(1000);
	    	  WebElement Edit = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[10]")));
		      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit);
	      }

	      //------------- Party Unit------

	      WebElement State = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),' State ')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", State);

	      WebElement Zila = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Zila')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Zila);

	      WebElement Mandal = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Mandal')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Mandal);

	      Thread.sleep(1000);
	      //Submit
	     WebElement Submit = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Submit')]")));
	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Submit);


	      String getSubmitTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted"))).getText();

	      System.out.println("getSubmitTxt from existing user :"+getSubmitTxt);

	      Assert.assertTrue(getSubmitTxt.contains("This Email Already Exist"));

	      //wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted")));

	      ngDriver.waitForAngularRequestsToFinish();
	}

	@Test(dependsOnMethods = {"addAlreadyExistingUser"})

	public void userAction() throws InterruptedException, IOException {

		ArrayList<String> a = new ArrayList<String>();

		UserManagementDataDriven dd = new UserManagementDataDriven();

		ArrayList<String> excel_data = dd.getData("userAction", a);

		System.out.println("calling userAction:");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		WebElement back = wait.until(ExpectedConditions.elementToBeClickable(By.className("back-icon")));
	     ((JavascriptExecutor)driver).executeScript("arguments[0].click();", back);

	     ngDriver.waitForAngularRequestsToFinish();


        String[] userActions = {"Edit","Deactivate","Activate","Change Password","Disable","Enable"};

        WebElement row = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell action-container cdk-column-action mat-column-action ng-star-inserted'])[1]"));

        String getSubmitTxt;
        for(String action : userActions) {

        	if(action == "Edit") {

        		Thread.sleep(2000);
        		row = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell action-container cdk-column-action mat-column-action ng-star-inserted'])[1]"));

        		WebElement editAction = row.findElement(By.xpath("//a[contains(.,'Edit')]"));

        		wait.until(ExpectedConditions.elementToBeClickable(editAction)).click();

        		System.out.println("Edit.........");

        		ngDriver.waitForAngularRequestsToFinish();

        		WebElement name= driver.findElement(By.xpath("(//*[@formcontrolname='name'])"));
        		name.clear();
        		name.sendKeys(excel_data.get(1));

        		WebElement phoneNo = driver.findElement(By.xpath("(//*[@formcontrolname='phone_no'])"));
        		phoneNo.clear();
        		phoneNo.sendKeys(excel_data.get(2));

        		WebElement emailElement = driver.findElement(By.xpath("(//*[@formcontrolname='email'])"));
        		emailElement.clear();
        		emailElement.sendKeys(excel_data.get(3));


        		String changeRoleTo="";
        		String changeRole =excel_data.get(4);

        		//change role
                if(changeRole.equals("yes")) {
                	 changeRoleTo = excel_data.get(5);

                	System.out.println("changeRoleTo :"+changeRoleTo);
                	Thread.sleep(2000);

                	WebElement roll = driver.findElement(By.xpath("//mat-radio-button[@value='"+changeRoleTo+"']"));
            		roll.click();
                System.out.println("changedRole....");

            		ngDriver.waitForAngularRequestsToFinish();
            		Thread.sleep(2000);
            		String classAftercheck = roll.getAttribute("class");

            		System.out.println("getting classAftercheck....");
            		// After check mat-radio-checked will be added
            		Assert.assertTrue(classAftercheck.contains("mat-radio-checked"));
                System.out.println("yes mat-radio-checked added....");

            	    driver.findElement(By.xpath("(//*[@formcontrolname='location_ids'])")).click();

                  System.out.println("clicked on location_ids");
            	     try {
            			    WebElement selectState = driver.findElement(By.xpath("//span[contains(text(),'"+excel_data.get(6)+"')]"));
            			    selectState.click();
                     System.out.println("selectState try block");
            			}
            			catch(org.openqa.selenium.StaleElementReferenceException ex)
            			{
            				WebElement selectState = driver.findElement(By.xpath("//span[contains(text(),'"+excel_data.get(6)+"')]"));
            			    selectState.click();
            			    System.out.println("selectState catch block");
            			}

            	     driver.findElement(By.xpath("//div[@role='listbox']")).sendKeys(Keys.TAB);

            	     System.out.println("just pressed tab key to close the states option..");
                }


                JavascriptExecutor js = (JavascriptExecutor) driver;
        		js.executeScript("window.scrollBy(0,510)");


        		if(excel_data.get(7).equals("yes")) {
        			try {
        				WebElement AllDataDownload = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[1]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", AllDataDownload);

                	      System.out.println("inside try for AllDataDownload");
        			}
        			catch(org.openqa.selenium.StaleElementReferenceException ex)
        			{
        				WebElement AllDataDownload = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[1]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", AllDataDownload);
                	      System.out.println("inside chatch for AllDataDownload");
        			}
        		}

        		if(excel_data.get(8).equals("yes")) {
        			try {
          	    	  WebElement days15 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[2]")));
          		      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", days15);

          		      System.out.println("inside try for days15");
          	      }catch(org.openqa.selenium.StaleElementReferenceException ex)
          			{
          	    	  WebElement days15 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[2]")));
          		      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", days15);

          		      System.out.println("inside catch for days15");
          			}
        		}

        	    if(excel_data.get(9).equals("yes")) {
        	    	try {
        	    		WebElement days30 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'30 Days')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", days30);
                	      System.out.println("inside try for days30");
        	    	}catch (org.openqa.selenium.StaleElementReferenceException ex) {
        	    		WebElement days30 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'30 Days')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", days30);
                	      System.out.println("inside catch for days15");
					}

        	    }


        	    if(excel_data.get(10).equals("yes")) {

        	    	try {
        	    		WebElement Create = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Create')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Create);
                	      System.out.println("inside try for create");

        	    	}catch (org.openqa.selenium.StaleElementReferenceException ex) {
        	    		WebElement Create = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Create')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Create);

                	      System.out.println("inside catch for create");
					}
        	    }

        	    if(excel_data.get(11).equals("yes")) {
        	    	try {

        	    		WebElement Edit_within_15_Days= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[5]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit_within_15_Days);
                	      System.out.println("inside try for Edit_within_15_Days");

        	    	}catch (org.openqa.selenium.StaleElementReferenceException ex) {
        	    		WebElement Edit_within_15_Days= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[5]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit_within_15_Days);

                	      System.out.println("inside catch for Edit_within_15_Days");
					}
        	    }

        	    if(excel_data.get(12).equals("yes")) {

        	    	try {
        	    		WebElement Edit_within_30_Days = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Edit within 30 Days')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit_within_30_Days);

                	      System.out.println("inside try for Edit_within_30_Days");

        	    	} catch (org.openqa.selenium.StaleElementReferenceException ex) {
        	    		WebElement Edit_within_30_Days = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Edit within 30 Days')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit_within_30_Days);

                	      System.out.println("inside catch for Edit_within_30_Days");
					}
        	    }

        	    if(excel_data.get(13).equals("yes")) {

        	    	try {
        	    		WebElement EditLifeTime = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Edit Lifetime')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", EditLifeTime);

                      System.out.println("inside try for EditLifeTime");
        	    	} catch (org.openqa.selenium.StaleElementReferenceException ex) {
        	    		WebElement EditLifeTime = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Edit Lifetime')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", EditLifeTime);

                	      System.out.println("inside catch for EditLifeTime");
					}
          	    }

        	    if(excel_data.get(14).equals("yes")) {

        	    	try {
        	    		WebElement SupplementaryEntry= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Supplementary Entry')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", SupplementaryEntry);

                      System.out.println("inside try for SupplementaryEntry");
        	    	} catch (org.openqa.selenium.StaleElementReferenceException ex) {
        	    		WebElement SupplementaryEntry= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Supplementary Entry')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", SupplementaryEntry);

                	      System.out.println("inside catch for SupplementaryEntry");
					}

          	    }

        	    if(excel_data.get(15).equals("yes")) {

        	    	try {
        	    		WebElement AllowReceiptPrint = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Allow Receipt Print')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", AllowReceiptPrint);

                      System.out.println("inside try for AllowReceiptPrint");
        	    	} catch (org.openqa.selenium.StaleElementReferenceException ex) {
        	    		WebElement AllowReceiptPrint = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Allow Receipt Print')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", AllowReceiptPrint);

                	      System.out.println("inside catch for AllowReceiptPrint");
					}

          	    }



        	    //------------- NRI Donation Form ------
        	    if(excel_data.get(16).equals("yes")) {

        	      if(changeRoleTo.equals("state_treasurer") && changeRole.equals("yes")) {
        	    	  System.out.println("inside state_treasurer *********");

        	    	  try {
        	    		  Thread.sleep(1000);
            	    	  WebElement Edit = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[10]")));
            		      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit);

                      System.out.println("inside try for Edit");
          	    	} catch (org.openqa.selenium.StaleElementReferenceException ex) {
          	    		Thread.sleep(1000);
          	    	  WebElement Edit = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='mat-checkbox-label'])[10]")));
          		      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Edit);

          		      System.out.println("inside catch for Edit");
  					}
        	      }
        	    }

        	      //------------- Party Unit------

        	    if(excel_data.get(17).equals("yes")) {

        	    	try {
        	    		  WebElement State = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),' State ')]")));
                  	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", State);

                    System.out.println("inside try for State party unit");

          	    	} catch (org.openqa.selenium.StaleElementReferenceException ex) {
          	    		WebElement State = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),' State ')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", State);

                	      System.out.println("inside catch for State party unit");
  					}
        	    }

        	    if(excel_data.get(18).equals("yes")) {

        	    	try {
        	    		WebElement Zila = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Zila')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Zila);

                        System.out.println("inside try for Zila party unit");

        	    	} catch (org.openqa.selenium.StaleElementReferenceException ex) {
        	    		WebElement Zila = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Zila')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Zila);

                	      System.out.println("inside catch for Zila party unit");
					}

        	    }

        	    if(excel_data.get(19).equals("yes")) {

        	    	try {
        	    		WebElement Mandal = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Mandal')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Mandal);

                        System.out.println("inside try for Madal party unit");

        	    	} catch (org.openqa.selenium.StaleElementReferenceException ex) {
        	    		WebElement Mandal = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Mandal')]")));
                	      ((JavascriptExecutor)driver).executeScript("arguments[0].click();", Mandal);

                	      System.out.println("inside catch for Mandal party unit");
					}
        	    }

        	    Thread.sleep(3000);

        	    //WebElement Submit =driver.findElement(By.xpath("(//button[@class='mat-focus-indicator col-md-12 mat-flat-button mat-button-base mat-warn'])"));
                            WebElement Submit = driver.findElements(By.tagName("button")).get(1);

                      	    //String submitButtonDisabled = Submit.getAttribute("ng-reflect-disabled");

                      	    String submitButtonDisabled = Submit.getAttribute("class");

                             System.out.println("*********submitButtonDisabled :"+submitButtonDisabled);

                      	    //if(submitButtonDisabled.equals("false")) {
                      	    if(!submitButtonDisabled.contains("mat-button-disabled")) {
                      	    	System.out.println("button is enabled");
                      	    	Submit.click();
                      	    	getSubmitTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted"))).getText();

                            	     System.out.println("getSubmitTxt :"+getSubmitTxt);
                                   if(getSubmitTxt.contains("Please select any party unit")) {
                                      Thread.sleep(4000);
                                       WebElement back2 = driver.findElement(By.className("back-icon"));
                                       ((JavascriptExecutor)driver).executeScript("arguments[0].click();", back2);
                                   }
                                   else {
                                      Assert.assertTrue(getSubmitTxt.contains("User Created/Updated."));
                                   }
                      	    }
                      	    else {
                      	    	System.out.println("button is desabled");
                      	    	Thread.sleep(3000);
                         		 WebElement back2 = driver.findElement(By.className("back-icon"));
                         		((JavascriptExecutor)driver).executeScript("arguments[0].click();", back2);
              				}

        		Thread.sleep(3000);
        		ngDriver.waitForAngularRequestsToFinish();
        	}
        	else if(action == "Deactivate") {

        		Thread.sleep(2000);
        		row = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell action-container cdk-column-action mat-column-action ng-star-inserted'])[1]"));
        		//row.findElement(By.xpath("//a[contains(.,'Deactivate')]")).click();

                WebElement deactivateAction = row.findElement(By.xpath("//a[contains(.,'Deactivate')]"));

        		wait.until(ExpectedConditions.elementToBeClickable(deactivateAction)).click();

        		System.out.println("Deactived.........");

        		getSubmitTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted"))).getText();

          	     System.out.println("getSubmitTxt :"+getSubmitTxt);

          	     Assert.assertTrue(getSubmitTxt.contains("User Deactivated Successfully."));


          	   ngDriver.waitForAngularRequestsToFinish();
        	}
             else if(action == "Activate") {

        		Thread.sleep(6000);
        		row = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell action-container cdk-column-action mat-column-action ng-star-inserted'])[1]"));


        		WebElement activateAction = row.findElement(By.xpath("//a[contains(.,'Activate')]"));

        		wait.until(ExpectedConditions.elementToBeClickable(activateAction)).click();

        		System.out.println("Activate.........");

        		 getSubmitTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted"))).getText();

         	     System.out.println("getSubmitTxt :"+getSubmitTxt);

         	     Assert.assertTrue(getSubmitTxt.contains("User Activated Successfully."));

         	    ngDriver.waitForAngularRequestsToFinish();
        	}

             else if(action == "Change Password") {

         		Thread.sleep(2000);
         		row = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell action-container cdk-column-action mat-column-action ng-star-inserted'])[1]"));

         		WebElement ChangePasswordAction = row.findElement(By.xpath("//a[contains(.,'Change Password')]"));

        		wait.until(ExpectedConditions.elementToBeClickable(ChangePasswordAction)).click();

         		System.out.println("Change Password.........");
             Thread.sleep(1000);

         		WebElement updatePassword =  driver.findElement(By.xpath("//h5[contains(text(),'Update Password')]"));
         		wait.until(ExpectedConditions.visibilityOf(updatePassword));
         		System.out.println("Update Password visible");

         		WebElement password = driver.findElement(By.xpath("(//*[@formcontrolname='password'])"));
         		WebElement confirmPassword = driver.findElement(By.xpath("(//*[@formcontrolname='confirmPassword'])"));

         		password.sendKeys("23dd");
         		confirmPassword.click();
         		Thread.sleep(1000);
         		System.out.println("23dd them clicked confirmPassword");
         		String givenPassword = excel_data.get(20);
         		System.out.println("givenPassword :"+givenPassword);


         		By error = By.cssSelector("[role='alert']");

         		WebElement errorInfo = driver.findElement(with(error).below(password));
         		wait.until(ExpectedConditions.textToBePresentInElement(errorInfo, "Password should be min eight characters, having one uppercase, lowercase, number and special character."));

         		password.clear();
         		password.sendKeys(givenPassword);
         		confirmPassword.sendKeys(givenPassword);
         		System.out.println("confirming password with givenPassword:");

            WebElement Submit = driver.findElements(By.tagName("button")).get(10);
         		String submitButtonDisabled = Submit.getAttribute("class");

         		if(!submitButtonDisabled.contains("mat-button-disabled")) {
        	    	System.out.println("button is enabled for change password");
        	    	getSubmitTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted"))).getText();

              	     System.out.println("getSubmitTxt for change password:"+getSubmitTxt);

         		}else {
         			System.out.println("button is desabled for change password");
         			driver.findElement(By.xpath("//span[contains(text(), 'Clear')]")).click();

         			Thread.sleep(1000);
         		}

        	    ngDriver.waitForAngularRequestsToFinish();
         	}
        	else if(action == "Disable") {
        		Thread.sleep(2000);
        		row = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell action-container cdk-column-action mat-column-action ng-star-inserted'])[1]"));

         		WebElement DisableAction = row.findElement(By.xpath("//a[contains(.,'Disable')]"));

        		wait.until(ExpectedConditions.elementToBeClickable(DisableAction)).click();

         		System.out.println("DisableAction.........");

         		WebElement disablePopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted")));
         		//getSubmitTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted"))).getText();
        	    //System.out.println("getSubmitTxt Disable :"+getSubmitTxt);

        	    Assert.assertTrue(disablePopup.getText().contains("User Disabled Successfully."));

        	    ngDriver.waitForAngularRequestsToFinish();

			}
        	else if(action == "Enable") {
        		// click on Archive
        		Thread.sleep(2000);
        		driver.findElement(By.xpath("(//*[@class='mat-tab-label-content'])[2]")).click();

        		WebElement getRow;
        		try {
        			Thread.sleep(2000);
        			getRow = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell action-container cdk-column-action mat-column-action ng-star-inserted'])[1]"));

        			System.out.println("indide try for row selection");

        		}catch (org.openqa.selenium.StaleElementReferenceException ex) {
        			Thread.sleep(2000);
        			System.out.println("indide catch for row selection");
        			getRow = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell action-container cdk-column-action mat-column-action ng-star-inserted'])[1]"));
				}

                WebElement EnableAction = getRow.findElement(By.xpath("//a[contains(.,'Enable')]"));

        		wait.until(ExpectedConditions.elementToBeClickable(EnableAction)).click();

         		System.out.println("EnableAction.........");

         		getSubmitTxt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mat-simple-snackbar.ng-star-inserted"))).getText();

        	    System.out.println("getSubmitTxt EnableAction :"+getSubmitTxt);

        	    Assert.assertTrue(getSubmitTxt.contains("User Enabled Successfully."));

        	    ngDriver.waitForAngularRequestsToFinish();
        	}
        }
	}


	@Test(dependsOnMethods = {"userAction"})
	public void filterUsers() throws InterruptedException {


		applyFilterOnArchive_and_Active();

		Thread.sleep(2000);

		driver.findElement(By.xpath("//span[contains(text(), 'Clear')]")).click();

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);
		// click on Active
		driver.findElement(By.xpath("(//*[@class='mat-tab-label-content'])[1]")).click();
		applyFilterOnArchive_and_Active();

	}

	@Test(dependsOnMethods = {"filterUsers"})
	public void downloadTestFolderTest () throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		WebElement downloadButton = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//span[contains(text(),'Download')]"))));
		((JavascriptExecutor)driver).executeScript("arguments[0].click();", downloadButton);

		Thread.sleep(2000);

		File folder = new File(System.getProperty("user.dir")+"\\downloadTestFolder");
		//List the files on that folder
		File[] listOfFiles = folder.listFiles();
		boolean found = false;
		     //Look for the file in the files
		     // You should write smart REGEX according to the filename
		     for (File listOfFile : listOfFiles) {
		         if (listOfFile.isFile()) {
		              String fileName = listOfFile.getName();
		               System.out.println("File " + listOfFile.getName());
		               System.out.println("fileName " + fileName);
		               if (fileName.contains("Usermanagement")) {
		                   found = true;
		                }
		            }
		        }
		Assert.assertTrue(found, "Downloaded document is not found");

		// delete all files
		for(File file: folder.listFiles()) {
			file.delete();
		}
	}
	@AfterClass
	public void terminate() {
		driver.close();
	}

	private void applyFilterOnArchive_and_Active() throws InterruptedException {

		Thread.sleep(3000);
		WebElement getPhoneRow;
		try {
			 getPhoneRow = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell cdk-column-phone_number mat-column-phone_number ng-star-inserted'])[1]"));
		}
		catch (org.openqa.selenium.StaleElementReferenceException ex) {
			 getPhoneRow = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell cdk-column-phone_number mat-column-phone_number ng-star-inserted'])[1]"));
		}

		String copy1stPhoneNo = getPhoneRow.getText();
		System.out.println("copy1stPhoneNo :"+copy1stPhoneNo);

		WebElement getEmaiRow = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell mat-tooltip-trigger cdk-column-email mat-column-email ng-star-inserted'])[1]"));

		String copy1stEmail = getEmaiRow.getText();
		System.out.println("copy1stEmail :"+copy1stEmail);

		WebElement getRole = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell cdk-column-role mat-column-role ng-star-inserted'])[1]"));

		String copy1stRole = getRole.getText();

		System.out.println("copy1stRole :"+copy1stRole);


		if(copy1stRole.equals("state_accountant")) {
			copy1stRole="State Accountant";
		}
		else if(copy1stRole.equals("state_treasurer")) {
			copy1stRole = "State Treasurer";
		}
		else if(copy1stRole.equals("national_coountant")) {
			copy1stRole = "National Accountant";
		}


		Random rand = new Random();
		int randomNum = rand.nextInt((10 - 1) + 1) + 1;

		System.out.println("randomNum :"+randomNum);
		//if generated random no is odd then search with phone else with email
		if(randomNum % 2 == 0) {
			driver.findElement(By.xpath("(//*[@formcontrolname='query'])")).sendKeys(copy1stEmail);
		}
		else {
			driver.findElement(By.xpath("(//*[@formcontrolname='query'])")).sendKeys(copy1stPhoneNo);
		}

		Thread.sleep(2000);
		driver.findElement(By.xpath("(//*[@class='ng-arrow-wrapper'])[1]")).click();
	    Thread.sleep(1000);

		driver.findElement(By.xpath("//span[contains(text(), '"+copy1stRole+"')]")).click();
		driver.findElement(By.xpath("//span[contains(text(), 'Search')]")).click();

		ngDriver.waitForAngularRequestsToFinish();
		// After search ------------------------

		Thread.sleep(2000);

		WebElement getPhoneRowAfterSearch;
		try {
			System.out.println(" from inside try after search");
			getPhoneRowAfterSearch = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell cdk-column-phone_number mat-column-phone_number ng-star-inserted'])[1]"));
		}
		catch (org.openqa.selenium.StaleElementReferenceException ex) {
			getPhoneRowAfterSearch = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell cdk-column-phone_number mat-column-phone_number ng-star-inserted'])[1]"));
			System.out.println(" from inside catch after search");
		}

		String copy1stPhoneNoAfterSearch = getPhoneRowAfterSearch.getText();
		System.out.println("copy1stPhoneNoAfterSearch :"+copy1stPhoneNoAfterSearch);

		WebElement getEmaiRowAfterSearch = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell mat-tooltip-trigger cdk-column-email mat-column-email ng-star-inserted'])[1]"));

		String copy1stEmailAfterSearch = getEmaiRowAfterSearch.getText();
		System.out.println("copy1stEmailAfterSearch :"+copy1stEmailAfterSearch);

		WebElement getRoleAfterSearch = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell cdk-column-role mat-column-role ng-star-inserted'])[1]"));

		String copy1stRoleAfterSearch = getRoleAfterSearch.getText();

		System.out.println("copy1stRoleAfterSearch :"+copy1stRoleAfterSearch);


		if(copy1stRoleAfterSearch.equals("state_accountant")) {
			copy1stRoleAfterSearch="State Accountant";
		}
		else if(copy1stRoleAfterSearch.equals("state_treasurer")) {
			copy1stRoleAfterSearch = "State Treasurer";
		}
		else if(copy1stRoleAfterSearch.equals("national_coountant")) {
			copy1stRoleAfterSearch = "National Accountant";
		}


		Assert.assertEquals(copy1stEmail, copy1stEmailAfterSearch);

		Assert.assertEquals(copy1stPhoneNo, copy1stPhoneNoAfterSearch);

		Assert.assertEquals(copy1stRole, copy1stRoleAfterSearch);
	}


}
