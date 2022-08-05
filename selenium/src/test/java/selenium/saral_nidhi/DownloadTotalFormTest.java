package selenium.saral_nidhi;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.paulhammant.ngwebdriver.NgWebDriver;

import pageObjects.Cheque_ModePage;
import pageObjects.LandingPage;
import resources.Base;

public class DownloadTotalFormTest extends Base {
	public WebDriver driver;
	public NgWebDriver ngDriver;
	public static Logger log = LogManager.getLogger(DownloadTotalFormTest.class);
	
	
	@Test
	public void downloadField(ITestContext context) throws IOException, InterruptedException {
		
		driver = initializeDriver();
        
		System.out.println("driver from addUser"+driver);
		ngDriver = new NgWebDriver((JavascriptExecutor) driver);
		System.out.println("driver After :"+driver);
		System.out.println("ngDriver After :"+ngDriver);
		
		driver.get(url);

		ArrayList<String> a = new ArrayList<String>();

		DownloadTotalFormDataDriven dd = new DownloadTotalFormDataDriven();
		
		ArrayList<String> excel_data = dd.getData("Cheque", a);
		Cheque_ModePage chequePage = new Cheque_ModePage(driver);

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
		
		log.info("Login successfully in UserManagementTest");
		
		WebElement heading1 = wait.until(ExpectedConditions.visibilityOfElementLocated(chequePage.getFirstHeading()));
		Assert.assertEquals(heading1.getText(), "Nidhi Collection");
		
		log.info("action test");
		driver.findElement(By.cssSelector("[class='header-title-span']")).click();
		
		WebElement heading2 = wait.until(ExpectedConditions.visibilityOfElementLocated(chequePage.getFirstHeading()));
		Assert.assertEquals(heading2.getText(), "Nidhi Collection");
		WebElement c = driver.findElement(By.cssSelector("[class='total-count-div']"));
		WebElement count = wait.until(ExpectedConditions.elementToBeClickable(c));
		count.click();
		ngDriver.waitForAngularRequestsToFinish();
		
		String header = driver.findElement(By.className("header-text")).getText();
		Assert.assertEquals(header,"Donation List");
		
		// click on cheque tab
			WebElement paymentMode = driver.findElement(By.xpath("(//*[@class='tab-text'])[2]"));
			paymentMode.click();
			
			ngDriver.waitForAngularRequestsToFinish();
			Thread.sleep(2000);
			
			// array of name,phone,pan,Instrument No
			String[] filterItemArr = {"VIKAS YADAV","5566644455","FOO4V3231K","138863"};
			
			for(int i=0;i<filterItemArr.length;i++) {
				driver.findElement(By.xpath("(//*[@formcontrolname='query'])")).sendKeys(filterItemArr[i]);
				Thread.sleep(1000);
				driver.findElement(By.xpath("//span[contains(text(), 'Search')]")).click();
				ngDriver.waitForAngularRequestsToFinish();
				Thread.sleep(1000);
				
				if(i!=3) {
					driver.findElement(By.xpath("//span[contains(text(), 'Clear')]")).click();
					ngDriver.waitForAngularRequestsToFinish();
				}
			}
			
			
			WebElement downloadButton = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//span[contains(text(),'Download')]"))));
			((JavascriptExecutor)driver).executeScript("arguments[0].click();", downloadButton);
			
			//Thread.sleep(2000);
			ngDriver.waitForAngularRequestsToFinish();
			String getDownloadSectionTopText = driver.findElement(By.tagName("b")).getText();
			
			Assert.assertEquals(getDownloadSectionTopText, "Donation Lists Download");
			System.out.println(driver.findElement(By.tagName("b")).getText());
			
			
			String Select_All_Field = excel_data.get(1);
			String State = excel_data.get(2);
			String Transaction_Type = excel_data.get(3);
			String Date_of_transaction = excel_data.get(4);
			
			String Financial_Year = excel_data.get(5);
			String Mode_of_Payment = excel_data.get(6);
			String Account_Number = excel_data.get(7);
			String IFSC_Code = excel_data.get(8);
			
			String Bank_Name = excel_data.get(9);
			String Branch_Name = excel_data.get(10);
			String Branch_Address = excel_data.get(11);
			String Name = excel_data.get(12);
			
			String Phone = excel_data.get(13);
			String Email = excel_data.get(14);
			String Date_of_Cheque = excel_data.get(15);
			String Cheque_Number = excel_data.get(16);
			
			String Date_of_Draft = excel_data.get(17);
			String Draft_Number = excel_data.get(18);
			String UTR_No = excel_data.get(19);
			String Category = excel_data.get(20);
			
			String Proprietorship = excel_data.get(21);
			String Proprietorship_Name = excel_data.get(22);
			String House = excel_data.get(23);
			String Locality = excel_data.get(24);
			
			String District = excel_data.get(25);
			String Pan_Card = excel_data.get(26);
			String Pan_Card_Remark = excel_data.get(27);
			String Amount = excel_data.get(28);
			
			String Amount_in_Words = excel_data.get(29);
			String Collector_Name = excel_data.get(30);
			String Collector_Phone = excel_data.get(31);
			String Nature_of_Donation = excel_data.get(32);
			
			String Party_Unit = excel_data.get(33);
			String Location = excel_data.get(34);
			String Payment_Realize_date = excel_data.get(35);
			String Receipt_Number = excel_data.get(36);
			
			String Transaction_Valid = excel_data.get(37);
			String Created_By = excel_data.get(38);
			String Created_At = excel_data.get(39);
			String Cheque_Bounce_Remark = excel_data.get(40);
			
			String Reverse_Remark = excel_data.get(41);
			String Pan_Card_Photo = excel_data.get(42);
			String Cheque_or_DD_Photo1 = excel_data.get(43);
			String Cheque_or_DD_Photo2 = excel_data.get(44);
			
			Thread.sleep(3000);
			
			WebElement Select_All_Field_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent"));
			WebElement State_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(1)"));
			WebElement Transaction_Type_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(2)"));
			WebElement Date_of_transaction_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(3)"));
			WebElement Financial_Year_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(4)"));
			
			WebElement Mode_of_Payment_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(5)"));
			WebElement Account_Number_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(6)"));
			WebElement IFSC_Code_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(7)"));
			WebElement Bank_Name_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(8)"));
			
			WebElement Branch_Name_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(9)"));
			WebElement Branch_Address_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(10)"));
			WebElement Name_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(11)"));
			WebElement Phone_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(12)"));
			WebElement Email_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(13)"));
		
			WebElement Date_of_Cheque_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(14)"));
			WebElement Cheque_Number_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(15)"));
			WebElement Date_of_Draft_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(16)"));
			WebElement Draft_Number_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(17)"));
			
			
			WebElement UTR_No_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(18)"));
			WebElement Category_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(19)"));
			WebElement Proprietorship_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(20)"));
			WebElement Proprietorship_Name_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(21)"));
			
			WebElement House_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(22)"));
			WebElement Locality_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(23)"));
			WebElement District_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(24)"));
			WebElement Pan_Card_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(25)"));
			WebElement Pan_Card_Remark_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(26)"));
			
			WebElement Amount_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(27)"));
			WebElement Amount_in_words_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(28)"));
			WebElement Collector_Name_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(29)"));
			WebElement Collector_Phone_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(30)"));
					
			WebElement Nature_of_Donation_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(31)"));
			WebElement Party_Unit_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(32)"));
			WebElement Location_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(33)"));
			WebElement Payment_Realize_date_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(34)"));
			
			WebElement Receipt_Number_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(35)"));
			WebElement Transaction_Valid_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(36)"));
			WebElement Created_By_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(37)"));
			WebElement Created_At_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(38)"));
			
			WebElement Cheque_Bounce_Remark_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(39)"));
			WebElement Reverse_Remark_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(40)"));
			WebElement Pan_Card_Photo_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(41)"));
			WebElement Cheque_or_DD_Photo1_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(42)"));
			
			WebElement Cheque_or_DD_Photo2_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(43)"));
			   
		   if(Select_All_Field.equals("yes")) {
			   
			   Select_All_Field_Element.click();
			   
			   Thread.sleep(3000);
			   
			   String Select_All_Field_checked=Select_All_Field_Element.getAttribute("class");
			   Assert.assertTrue(Select_All_Field_checked.contains("mat-checkbox-checked"));
			   
			   Assert.assertEquals(State_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Transaction_Type_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Date_of_transaction_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Financial_Year_Element.getAttribute("ng-reflect-checked"),"true");
			   
			   Assert.assertEquals(Mode_of_Payment_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Account_Number_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(IFSC_Code_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Bank_Name_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Branch_Name_Element.getAttribute("ng-reflect-checked"),"true");
			   
			   Assert.assertEquals(Branch_Address_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Name_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Phone_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Email_Element.getAttribute("ng-reflect-checked"),"true");
			   
			   Assert.assertEquals(Date_of_Cheque_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Cheque_Number_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Date_of_Draft_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Draft_Number_Element.getAttribute("ng-reflect-checked"),"true");
			   //---
			   Assert.assertEquals(UTR_No_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Category_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Proprietorship_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Proprietorship_Name_Element.getAttribute("ng-reflect-checked"),"true");
			   
			   Assert.assertEquals(House_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Locality_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(District_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Pan_Card_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Pan_Card_Remark_Element.getAttribute("ng-reflect-checked"),"true");
			   
			   Assert.assertEquals(Amount_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Amount_in_words_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Collector_Name_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Collector_Phone_Element.getAttribute("ng-reflect-checked"),"true");
			   
			   Assert.assertEquals(Nature_of_Donation_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Party_Unit_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Location_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Payment_Realize_date_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Receipt_Number_Element.getAttribute("ng-reflect-checked"),"true");
			   
			   Assert.assertEquals(Transaction_Valid_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Created_By_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Created_At_Element.getAttribute("ng-reflect-checked"),"true");
               Assert.assertEquals(Cheque_Bounce_Remark_Element.getAttribute("ng-reflect-checked"),"true");
               
			   Assert.assertEquals(Reverse_Remark_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Pan_Card_Photo_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Cheque_or_DD_Photo1_Element.getAttribute("ng-reflect-checked"),"true");
			   Assert.assertEquals(Cheque_or_DD_Photo2_Element.getAttribute("ng-reflect-checked"),"true");
			   
		   }
		  
		   
		   if(State.equals("yes")) {
			   State_Element.click();
		   }
		   if(Transaction_Type.equals("yes")) {
			   Transaction_Type_Element.click();
		   }
		   if(Date_of_transaction.equals("yes")) {
			   Date_of_transaction_Element.click();
		   }
		   if(Financial_Year.equals("yes")) {
			   Financial_Year_Element.click();
		   }
		   
		   
		   if(Mode_of_Payment.equals("yes")) {
			   Mode_of_Payment_Element.click();
		   }
		   if(Account_Number.equals("yes")) {
			   Account_Number_Element.click();
		   }
		   if(IFSC_Code.equals("yes")) {
			   IFSC_Code_Element.click();
		   }
		   if(Bank_Name.equals("yes")) {
			   Bank_Name_Element.click();
		   }
		   
		   if(Branch_Name.equals("yes")) {
			   Branch_Name_Element.click();
		   }
		   
		   
		   if(Branch_Address.equals("yes")) {
			   Branch_Address_Element.click();
		   }
		   if(Name.equals("yes")) {
			   Name_Element.click();
		   }
		   if(Phone.equals("yes")) {
			   Phone_Element.click();
		   }
		   if(Email.equals("yes")) {
			   Email_Element.click();
		   }
		   
		   if(Date_of_Cheque.equals("yes")) {
			   Date_of_Cheque_Element.click();
		   }
		   if(Cheque_Number.equals("yes")) {
			   Cheque_Number_Element.click();
		   }
		   if(Date_of_Draft.equals("yes")) {
			   Date_of_Draft_Element.click();
		   }
		   if(Draft_Number.equals("yes")) {
			   Draft_Number_Element.click();
		   }
		   
		   
		   if(UTR_No.equals("yes")) {
			   UTR_No_Element.click();
		   }
		   if(Category.equals("yes")) {
			   Category_Element.click();
		   }
		   if(Proprietorship.equals("yes")) {
			   Proprietorship_Element.click();
		   }
		   if(Proprietorship_Name.equals("yes")) {
			   Proprietorship_Name_Element.click();
		   }
		   
		   
		   if(House.equals("yes")) {
			   House_Element.click();
		   }
		   if(Locality.equals("yes")) {
			   Location_Element.click();
		   }
		   if(District.equals("yes")) {
			   District_Element.click();
		   }
		   if(Pan_Card.equals("yes")) {
			   Pan_Card_Element.click();
		   }
		   
		   
		   if(Pan_Card_Remark.equals("yes")) {
			   Pan_Card_Remark_Element.click();
		   }
		   if(Amount.equals("yes")) {
			   Amount_Element.click();
		   }
		   if(Amount_in_Words.equals("yes")) {
			   Amount_in_words_Element.click();
		   }
		   if(Collector_Name.equals("yes")) {
			   Collector_Name_Element.click();
		   }
		   
		   if(Collector_Phone.equals("yes")) {
			   Collector_Phone_Element.click();
		   }
		   if(Nature_of_Donation.equals("yes")) {
			   Nature_of_Donation_Element.click();
		   }
		   if(Party_Unit.equals("yes")) {
			   Party_Unit_Element.click();
		   }
		   if(Locality.equals("yes")) {
			   Locality_Element.click();
		   }
		   
		   
		   if(Payment_Realize_date.equals("yes")) {
			   Payment_Realize_date_Element.click();
		   }
		   if(Receipt_Number.equals("yes")) {
			   Receipt_Number_Element.click();
		   }
		   if(Transaction_Valid.equals("yes")) {
			   Transaction_Valid_Element.click();
		   }
		   if(Created_By.equals("yes")) {
			   Created_By_Element.click();
		   }
		   
		   
		   if(Created_At.equals("yes")) {
			   Created_At_Element.click();
		   }
		   if(Cheque_Bounce_Remark.equals("yes")) {
			   Cheque_Bounce_Remark_Element.click();
		   }
		   if(Reverse_Remark.equals("yes")) {
			   Reverse_Remark_Element.click();
		   }
		   if(Pan_Card_Photo.equals("yes")) {
			   Pan_Card_Photo_Element.click();
		   }
		   
		   if(Cheque_or_DD_Photo1.equals("yes")) {
			   Cheque_or_DD_Photo1_Element.click();
		   }
		   if(Cheque_or_DD_Photo2.equals("yes")) {
			   Cheque_or_DD_Photo2_Element.click();
		   }
		   
		   
	  }
	
	}
