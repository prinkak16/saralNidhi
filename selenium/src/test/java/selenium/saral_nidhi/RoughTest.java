package selenium.saral_nidhi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.paulhammant.ngwebdriver.NgWebDriver;
import pageObjects.LandingPage;
import resources.Base;

public class RoughTest extends Base {

	public WebDriver driver;
	public NgWebDriver ngDriver;
	public static Logger log = LogManager.getLogger(RoughTest.class);
	
	
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

		//LocalStorage storage = ((WebStorage) driver).getLocalStorage();
		//new SetLocalStorage(storage, driver, context);
		ArrayList<String> a = new ArrayList<String>();

		UserManagementDataDriven dd = new UserManagementDataDriven();
		ArrayList<String> excel_data = dd.getData("addUser", a);
		
		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
          
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
		  
		
		log.info("Login successfully");
		
		ngDriver.waitForAngularRequestsToFinish();
		
		
		/*
		
		System.out.println("&&&& downloadDonationListTestForRTGS section ----");
		ArrayList<String> downloadingFields = new ArrayList<String>();
	
		ArrayList<String> a1 = new ArrayList<String>();

		DownloadTotalFormDataDriven dd1 = new DownloadTotalFormDataDriven();

		ArrayList<String> excel_data_for_download_total_form = dd1.getData("RTGS", a1);

        
		// click on भारतीय जनता पार्टी for home
		driver.findElement(By.cssSelector("[class='header-title-span']")).click();
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);
		
		driver.findElement(By.cssSelector("[class='count']")).click();
		ngDriver.waitForAngularRequestsToFinish();
		
		// click on RTGS tab
		WebElement paymentMode = driver.findElement(By.xpath("(//*[@class='tab-text'])[3]"));
		paymentMode.click();

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		Random rand = new Random();
		int randomNumForFilter = rand.nextInt(4 + 1) + 1;
		String filterElement = "";

		// by name if 1
		if (randomNumForFilter == 1) {
			filterElement = "SUMAN SUMIT";
		}
		// by pan no
		else if (randomNumForFilter == 2) {
			filterElement = "FOOJV2321K";
		}
		// by Phone no
		else if (randomNumForFilter == 3) {
			filterElement = "5566644455";
		}
		// by Instrument No.
		else if (randomNumForFilter == 4) {
			filterElement = "rt771505";
		}

		driver.findElement(By.xpath("(//*[@formcontrolname='query'])")).sendKeys(filterElement);

		driver.findElement(By.xpath("(//*[@class='ng-arrow-wrapper'])[1]")).click();
		Thread.sleep(1000);

		String filterByState ="Andhra Pradesh";
		driver.findElement(By.xpath("//span[contains(text(), '" + filterByState + "')]")).click();

		driver.findElement(By.xpath("//span[contains(text(), 'Search')]")).click();

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		// --------------------------

		WebElement downloadBtn = wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//span[contains(text(),'Download')]"))));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", downloadBtn);

		Thread.sleep(4000);

		// Thread.sleep(2000);
		ngDriver.waitForAngularRequestsToFinish();
		String getDownloadSectionTopText = driver.findElement(By.tagName("b")).getText();

		Assert.assertEquals(getDownloadSectionTopText, "Donation Lists Download");
		System.out.println(driver.findElement(By.tagName("b")).getText());

		String Select_All_Field = excel_data_for_download_total_form.get(1);
		String State = excel_data_for_download_total_form.get(2);
		String Transaction_Type = excel_data_for_download_total_form.get(3);
		String Date_of_transaction = excel_data_for_download_total_form.get(4);

		String Financial_Year = excel_data_for_download_total_form.get(5);
		String Mode_of_Payment = excel_data_for_download_total_form.get(6);
		String Account_Number = excel_data_for_download_total_form.get(7);
		String IFSC_Code = excel_data_for_download_total_form.get(8);

		String Bank_Name = excel_data_for_download_total_form.get(9);
		String Branch_Name = excel_data_for_download_total_form.get(10);
		String Branch_Address = excel_data_for_download_total_form.get(11);
		String Name = excel_data_for_download_total_form.get(12);

		String Phone = excel_data_for_download_total_form.get(13);
		String Email = excel_data_for_download_total_form.get(14);
		String Date_of_Cheque = excel_data_for_download_total_form.get(15);
		String Cheque_Number = excel_data_for_download_total_form.get(16);

		String Date_of_Draft = excel_data_for_download_total_form.get(17);
		String Draft_Number = excel_data_for_download_total_form.get(18);
		String UTR_No = excel_data_for_download_total_form.get(19);
		String Category = excel_data_for_download_total_form.get(20);

		String Proprietorship = excel_data_for_download_total_form.get(21);
		String Proprietorship_Name = excel_data_for_download_total_form.get(22);
		String House = excel_data_for_download_total_form.get(23);
		String Locality = excel_data_for_download_total_form.get(24);

		String District = excel_data_for_download_total_form.get(25);
		String Pan_Card = excel_data_for_download_total_form.get(26);
		String Pan_Card_Remark = excel_data_for_download_total_form.get(27);
		String Amount = excel_data_for_download_total_form.get(28);

		String Amount_in_Words = excel_data_for_download_total_form.get(29);
		String Collector_Name = excel_data_for_download_total_form.get(30);
		String Collector_Phone = excel_data_for_download_total_form.get(31);
		String Nature_of_Donation = excel_data_for_download_total_form.get(32);

		String Party_Unit = excel_data_for_download_total_form.get(33);
		String Location = excel_data_for_download_total_form.get(34);
		String Payment_Realize_date = excel_data_for_download_total_form.get(35);
		String Receipt_Number = excel_data_for_download_total_form.get(36);

		String Transaction_Valid = excel_data_for_download_total_form.get(37);
		String Created_By = excel_data_for_download_total_form.get(38);
		String Created_At = excel_data_for_download_total_form.get(39);
		String Cheque_Bounce_Remark = excel_data_for_download_total_form.get(40);

		String Reverse_Remark = excel_data_for_download_total_form.get(41);
		String Pan_Card_Photo = excel_data_for_download_total_form.get(42);
		String Cheque_or_DD_Photo1 = excel_data_for_download_total_form.get(43);
		String Cheque_or_DD_Photo2 = excel_data_for_download_total_form.get(44);

		Thread.sleep(3000);

		WebElement Select_All_Field_Element = driver.findElement(By.cssSelector(".mat-checkbox.mat-accent"));
		WebElement State_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(1)"));
		WebElement Transaction_Type_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(2)"));
		WebElement Date_of_transaction_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(3)"));
		WebElement Financial_Year_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(4)"));

		WebElement Mode_of_Payment_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(5)"));
		WebElement Account_Number_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(6)"));
		WebElement IFSC_Code_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(7)"));
		WebElement Bank_Name_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(8)"));

		WebElement Branch_Name_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(9)"));
		WebElement Branch_Address_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(10)"));
		WebElement Name_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(11)"));
		WebElement Phone_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(12)"));
		WebElement Email_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(13)"));

		WebElement Date_of_Cheque_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(14)"));
		WebElement Cheque_Number_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(15)"));
		WebElement Date_of_Draft_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(16)"));
		WebElement Draft_Number_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(17)"));

		WebElement UTR_No_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(18)"));
		WebElement Category_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(19)"));
		WebElement Proprietorship_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(20)"));
		WebElement Proprietorship_Name_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(21)"));

		WebElement House_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(22)"));
		WebElement Locality_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(23)"));
		WebElement District_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(24)"));
		WebElement Pan_Card_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(25)"));
		WebElement Pan_Card_Remark_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(26)"));

		WebElement Amount_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(27)"));
		WebElement Amount_in_words_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(28)"));
		WebElement Collector_Name_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(29)"));
		WebElement Collector_Phone_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(30)"));

		WebElement Nature_of_Donation_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(31)"));
		WebElement Party_Unit_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(32)"));
		WebElement Location_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(33)"));
		WebElement Payment_Realize_date_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(34)"));

		WebElement Receipt_Number_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(35)"));
		WebElement Transaction_Valid_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(36)"));
		WebElement Created_By_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(37)"));
		WebElement Created_At_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(38)"));

		WebElement Cheque_Bounce_Remark_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(39)"));
		WebElement Reverse_Remark_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(40)"));
		WebElement Pan_Card_Photo_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(41)"));
		WebElement Cheque_or_DD_Photo1_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(42)"));

		WebElement Cheque_or_DD_Photo2_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(43)"));

		boolean flag_Select_All_Field = false;
		if (Select_All_Field.equals("yes")) {

			Select_All_Field_Element.click();

			// ---- Add all fields to arrayList initially
			downloadingFields.add("State");
			downloadingFields.add("Transaction Type");
			downloadingFields.add("Date of transaction");
			downloadingFields.add("Financial Year");

			downloadingFields.add("Mode of Payment");
			downloadingFields.add("Account Number");
			downloadingFields.add("IFSC Code");
			downloadingFields.add("Bank Name");

			downloadingFields.add("Branch Name");
			downloadingFields.add("Branch Address");
			downloadingFields.add("Name");
			downloadingFields.add("Phone");

			downloadingFields.add("Email");
			downloadingFields.add("Date of Cheque");
			downloadingFields.add("Cheque Number");
			downloadingFields.add("Date of Draft");

			downloadingFields.add("Draft Number");
			downloadingFields.add("UTR No");
			downloadingFields.add("Category");
			downloadingFields.add("Proprietorship");

			downloadingFields.add("Proprietorship Name");
			downloadingFields.add("House");
			downloadingFields.add("Locality");
			downloadingFields.add("District");

			downloadingFields.add("Pan Card");
			downloadingFields.add("Pan Card Remark");
			downloadingFields.add("Amount");
			downloadingFields.add("Amount in Words");

			downloadingFields.add("Collector Name");
			downloadingFields.add("Collector Phone");
			downloadingFields.add("Nature of Donation");
			downloadingFields.add("Party Unit");

			downloadingFields.add("Location");
			downloadingFields.add("Payment realize date");
			downloadingFields.add("Receipt Number");
			downloadingFields.add("Transaction Valid");

			downloadingFields.add("Created By");
			downloadingFields.add("Created At");
			downloadingFields.add("Cheque Bounce Remark");
			downloadingFields.add("Reverse Remark");

			downloadingFields.add("Pan Card Photo");
			downloadingFields.add("Cheque/DD photo1");
			downloadingFields.add("Cheque/DD photo2");

			// --------

			Thread.sleep(3000);

			String Select_All_Field_checked = Select_All_Field_Element.getAttribute("class");
			Assert.assertTrue(Select_All_Field_checked.contains("mat-checkbox-checked"));

			flag_Select_All_Field = true;

			// From state check box
			Assert.assertEquals(State_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Transaction_Type_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Date_of_transaction_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Financial_Year_Element.getAttribute("ng-reflect-checked"), "true");

			Assert.assertEquals(Mode_of_Payment_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Account_Number_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(IFSC_Code_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Bank_Name_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Branch_Name_Element.getAttribute("ng-reflect-checked"), "true");

			Assert.assertEquals(Branch_Address_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Name_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Phone_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Email_Element.getAttribute("ng-reflect-checked"), "true");

			Assert.assertEquals(Date_of_Cheque_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Cheque_Number_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Date_of_Draft_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Draft_Number_Element.getAttribute("ng-reflect-checked"), "true");
			// ---
			Assert.assertEquals(UTR_No_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Category_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Proprietorship_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Proprietorship_Name_Element.getAttribute("ng-reflect-checked"), "true");

			Assert.assertEquals(House_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Locality_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(District_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Pan_Card_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Pan_Card_Remark_Element.getAttribute("ng-reflect-checked"), "true");

			Assert.assertEquals(Amount_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Amount_in_words_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Collector_Name_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Collector_Phone_Element.getAttribute("ng-reflect-checked"), "true");

			Assert.assertEquals(Nature_of_Donation_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Party_Unit_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Location_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Payment_Realize_date_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Receipt_Number_Element.getAttribute("ng-reflect-checked"), "true");

			Assert.assertEquals(Transaction_Valid_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Created_By_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Created_At_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Cheque_Bounce_Remark_Element.getAttribute("ng-reflect-checked"), "true");

			Assert.assertEquals(Reverse_Remark_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Pan_Card_Photo_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Cheque_or_DD_Photo1_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(Cheque_or_DD_Photo2_Element.getAttribute("ng-reflect-checked"), "true");

		}

		if (State.equals("yes")) {
			State_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("State");
				Assert.assertEquals(State_Element.getAttribute("ng-reflect-checked"), "false");

			} else {
				downloadingFields.add("State");
				Assert.assertEquals(State_Element.getAttribute("ng-reflect-checked"), "true");
			}
		}
		if (Transaction_Type.equals("yes")) {
			Transaction_Type_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Transaction Type");
				Assert.assertEquals(Transaction_Type_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Transaction_Type_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Transaction Type");
			}
		}
		if (Date_of_transaction.equals("yes")) {
			Date_of_transaction_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Date of transaction");
				Assert.assertEquals(Date_of_transaction_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Date_of_transaction_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Date of transaction");
			}
		}
		if (Financial_Year.equals("yes")) {
			Financial_Year_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Financial Year");
				Assert.assertEquals(Financial_Year_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Financial_Year_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Financial Year");
			}
		}

		if (Mode_of_Payment.equals("yes")) {
			Mode_of_Payment_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Mode of Payment");
				Assert.assertEquals(Mode_of_Payment_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Mode_of_Payment_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Mode of Payment");
			}
		}
		if (Account_Number.equals("yes")) {
			Account_Number_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Account Number");
				Assert.assertEquals(Account_Number_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Account_Number_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Account Number");
			}
		}
		if (IFSC_Code.equals("yes")) {
			IFSC_Code_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("IFSC Code");
				Assert.assertEquals(IFSC_Code_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(IFSC_Code_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("IFSC Code");
			}
		}
		if (Bank_Name.equals("yes")) {
			Bank_Name_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Bank Name");
				Assert.assertEquals(Bank_Name_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Bank_Name_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Bank Name");
			}
		}
		if (Branch_Name.equals("yes")) {
			Branch_Name_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Branch Name");
				Assert.assertEquals(Branch_Name_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Branch_Name_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Branch Name");
			}
		}

		if (Branch_Address.equals("yes")) {
			Branch_Address_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Branch Address");
				Assert.assertEquals(Branch_Address_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Branch_Address_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Branch Address");
			}
		}
		if (Name.equals("yes")) {
			Name_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Name");
				Assert.assertEquals(Name_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Name_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Name");
			}
		}
		if (Phone.equals("yes")) {
			Phone_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Phone");
				Assert.assertEquals(Phone_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Phone_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Phone");
			}
		}
		if (Email.equals("yes")) {
			Email_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Email");
				Assert.assertEquals(Email_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Email_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Email");
			}
		}

		if (Date_of_Cheque.equals("yes")) {
			Date_of_Cheque_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Date of Cheque");
				Assert.assertEquals(Date_of_Cheque_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Date_of_Cheque_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Date of Cheque");
			}
		}
		if (Cheque_Number.equals("yes")) {
			Cheque_Number_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Cheque Number");
				Assert.assertEquals(Cheque_Number_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Cheque_Number_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Cheque Number");
			}
		}
		if (Date_of_Draft.equals("yes")) {
			Date_of_Draft_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Date of Draft");
				Assert.assertEquals(Date_of_Draft_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Date_of_Draft_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Date of Draft");
			}
		}
		if (Draft_Number.equals("yes")) {
			Draft_Number_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Draft Number");
				Assert.assertEquals(Draft_Number_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Draft_Number_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Draft Number");
			}
		}

		if (UTR_No.equals("yes")) {
			UTR_No_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("UTR No");
				Assert.assertEquals(UTR_No_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(UTR_No_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("UTR No");
			}
		}
		if (Category.equals("yes")) {
			Category_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Category");
				Assert.assertEquals(Category_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Category_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Category");
			}
		}
		if (Proprietorship.equals("yes")) {
			Proprietorship_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Proprietorship");
				Assert.assertEquals(Proprietorship_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Proprietorship_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Proprietorship");
			}
		}
		if (Proprietorship_Name.equals("yes")) {
			Proprietorship_Name_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Proprietorship Name");
				Assert.assertEquals(Proprietorship_Name_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Proprietorship_Name_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Proprietorship Name");
			}
		}

		if (House.equals("yes")) {
			House_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("House");
				System.out.println("removing House from arrayList");
				System.out.println("House is not selected");
				Assert.assertEquals(House_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(House_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("House");
				System.out.println("House is selected");
			}
		}
		if (Locality.equals("yes")) {
			Locality_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Locality");
				System.out.println("removing Locality from arrayList");
				System.out.println("Locality is not selected");
				Assert.assertEquals(Locality_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Locality_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Locality");
				System.out.println("Locality is selected");
			}
		}
		if (District.equals("yes")) {
			District_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("District");
				Assert.assertEquals(District_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(District_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("District");
			}
		}
		if (Pan_Card.equals("yes")) {
			Pan_Card_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Pan Card");
				Assert.assertEquals(Pan_Card_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Pan_Card_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Pan Card");
			}
		}

		if (Pan_Card_Remark.equals("yes")) {
			Pan_Card_Remark_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Pan Card Remark");
				Assert.assertEquals(Pan_Card_Remark_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Pan_Card_Remark_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Pan Card Remark");
			}
		}
		if (Amount.equals("yes")) {
			Amount_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Amount");
				Assert.assertEquals(Amount_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Amount_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Amount");
			}
		}
		if (Amount_in_Words.equals("yes")) {
			Amount_in_words_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Amount in Words");
				Assert.assertEquals(Amount_in_words_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Amount_in_words_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Amount in Words");
			}
		}
		if (Collector_Name.equals("yes")) {
			Collector_Name_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Collector Name");
				Assert.assertEquals(Collector_Name_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Collector_Name_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Collector Name");
			}
		}

		if (Collector_Phone.equals("yes")) {
			Collector_Phone_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Collector Phone");
				Assert.assertEquals(Collector_Phone_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Collector_Phone_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Collector Phone");
			}
		}
		if (Nature_of_Donation.equals("yes")) {
			Nature_of_Donation_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Nature of Donation");
				Assert.assertEquals(Nature_of_Donation_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Nature_of_Donation_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Nature of Donation");
			}
		}
		if (Party_Unit.equals("yes")) {
			Party_Unit_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Party Unit");
				Assert.assertEquals(Party_Unit_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Party_Unit_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Party Unit");
			}
		}
		if (Location.equals("yes")) {
			Location_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Location");
				Assert.assertEquals(Location_Element.getAttribute("ng-reflect-checked"), "false");
				System.out.println("Location_Element should be false");
			} else {
				Assert.assertEquals(Location_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Location");
				System.out.println("Location_Element should be true");
			}
		}

		if (Payment_Realize_date.equals("yes")) {
			Payment_Realize_date_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Payment realize date");
				Assert.assertEquals(Payment_Realize_date_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Payment_Realize_date_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Payment realize date");
			}
		}
		if (Receipt_Number.equals("yes")) {
			Receipt_Number_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Receipt Number");
				Assert.assertEquals(Receipt_Number_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Receipt_Number_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Receipt Number");
			}
		}
		if (Transaction_Valid.equals("yes")) {
			Transaction_Valid_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Transaction Valid");
				Assert.assertEquals(Transaction_Valid_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Transaction_Valid_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Transaction Valid");
			}
		}
		if (Created_By.equals("yes")) {
			Created_By_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Created By");
				Assert.assertEquals(Created_By_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Created_By_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Created By");
			}
		}

		if (Created_At.equals("yes")) {
			Created_At_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Created At");
				Assert.assertEquals(Created_At_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Created_At_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Created At");
			}
		}
		if (Cheque_Bounce_Remark.equals("yes")) {
			Cheque_Bounce_Remark_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Cheque Bounce Remark");
				Assert.assertEquals(Cheque_Bounce_Remark_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Cheque_Bounce_Remark_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Cheque Bounce Remark");
			}
		}
		if (Reverse_Remark.equals("yes")) {
			Reverse_Remark_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Reverse Remark");
				Assert.assertEquals(Reverse_Remark_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Reverse_Remark_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Reverse Remark");
			}
		}
		if (Pan_Card_Photo.equals("yes")) {
			Pan_Card_Photo_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Pan Card Photo");
				Assert.assertEquals(Pan_Card_Photo_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Pan_Card_Photo_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Pan Card Photo");
			}
		}

		if (Cheque_or_DD_Photo1.equals("yes")) {
			Cheque_or_DD_Photo1_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Cheque/DD photo1");
				Assert.assertEquals(Cheque_or_DD_Photo1_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Cheque_or_DD_Photo1_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Cheque/DD photo1");
			}
		}
		if (Cheque_or_DD_Photo2.equals("yes")) {
			Cheque_or_DD_Photo2_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Cheque/DD photo2");
				Assert.assertEquals(Cheque_or_DD_Photo2_Element.getAttribute("ng-reflect-checked"), "false");
			} else {
				Assert.assertEquals(Cheque_or_DD_Photo2_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Cheque/DD photo2");
			}
		}

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,310)");

		WebElement downloadBtn2 = wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//span[contains(text(),'Submit')]"))));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", downloadBtn2);

		Thread.sleep(4000);

		File folder = new File(System.getProperty("user.dir") + "\\downloadTestFolder");

		// List the files on that folder
		File[] listOfFiles = folder.listFiles();
		boolean found = false;
		String fileName = null;
		for (File listOfFile : listOfFiles) {
			if (listOfFile.isFile()) {
				fileName = listOfFile.getName();
				System.out.println("fileName " + fileName);
				if (fileName.contains("NidhiCollection")) {
					found = true;
				}
			}
		}
		Assert.assertTrue(found, "Downloaded document is not found");
		ArrayList<String> fieldsArrayList = new ArrayList<>();
		ArrayList<String> fields = getDownloadFields(fieldsArrayList, fileName);

		System.out.println("downloadingFields size :" + downloadingFields.size());
		System.out.println("fields size :" + fields.size());

		Assert.assertEquals(true, downloadingFields.equals(fields), "downloading fields are not matching..");

		File file = new File(System.getProperty("user.dir") + "\\downloadTestFolder\\" + fileName);

		System.out.println("delete file Absolute path :" + file.getAbsolutePath());

		if (file.delete()) {
			System.out.println("file deleted success");
		} else {
			System.out.println("file delete fail");
		}
	}
	
	private static ArrayList<String> getDownloadFields(ArrayList<String> fieldsArrayList, String fileName)
			throws IOException {
		File file = new File(System.getProperty("user.dir") + "\\downloadTestFolder\\" + fileName);
		// ‪
		FileInputStream fis = new FileInputStream(file);

		@SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook(fis);

		int sheets = workbook.getNumberOfSheets();
		for (int i = 0; i < sheets; i++) {
			if (workbook.getSheetName(i).equalsIgnoreCase("work_book")) {

				XSSFSheet sheet = workbook.getSheetAt(i);

				Iterator<Row> rows = sheet.iterator(); // sheet is collection of rows
				Row firstrow = rows.next();

				Iterator<Cell> ce = firstrow.cellIterator(); // row is collection of cells
				while (ce.hasNext()) {
					Cell value = ce.next();
					fieldsArrayList.add(value.getStringCellValue());
				}
			}

		}
		System.out.println("print fields..");
		for (int i = 0; i < fieldsArrayList.size(); i++) {
			System.out.println(fieldsArrayList.get(i));
		}

		fis.close();
		return fieldsArrayList;
		*/
		
		System.out.println("&&& actionRequiredForPanCardRTGS section ----");
        
		// click on भारतीय जनता पार्टी for home
		driver.findElement(By.cssSelector("[class='header-title-span']")).click();
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);
		
		// click on Action Required for Pan card
		driver.findElement(By.xpath("(//*[@class='action-text'])[2]")).click();
		ngDriver.waitForAngularRequestsToFinish();
		
		
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//h2[contains(text(),'Action Required for PanCard')]"))));
		
		Random rand = new Random();
		int randomNumForFilter = rand.nextInt(4 + 1) + 1;
		String filterElement = "";
		
		// by name if 1
				if (randomNumForFilter == 1) {
					//filterElement = filterByName;
					filterElement="vikas kumar";
				}
				// by pan no
				else if (randomNumForFilter == 2) {
					//filterElement = filterByPan;
					filterElement="FOOVL5634K";
				}
				// by Phone no
				else if (randomNumForFilter == 3) {
					//filterElement = filterByPhone;
					filterElement="FOOVL5634K";
				}
				// by Instrument No.
				else if (randomNumForFilter == 4) {
					//filterElement = filterByInstrumentNo;
					filterElement="vikas kumar";
				}
				
				Thread.sleep(3000);
				driver.findElement(By.xpath("(//*[@type='text'])")).sendKeys(filterElement);
				
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Search')]"))).click();
				
				ngDriver.waitForAngularRequestsToFinish();
				Thread.sleep(2000);
				
				int[] countArr = countMatchingActionRequiredForPanCard(driver);
				
				int allCountNumber = countArr[0];
				int invalidCountNumber = countArr[1];
				int approvedCountNumber = countArr[2];
				int rejectedCountNumber = countArr[3];
				
				int addOtherThanAll = invalidCountNumber+approvedCountNumber+rejectedCountNumber;
				System.out.println("all count :"+allCountNumber);
				System.out.println("all count after adding :"+addOtherThanAll);
				
				Assert.assertEquals(allCountNumber, addOtherThanAll, "Count is mismatching");
				
				// click on invalid tab
				driver.findElement(By.xpath("(//*[@class='tab-text1'])[3]")).click();
				ngDriver.waitForAngularRequestsToFinish();
				Thread.sleep(2000);
				
				//copy status
				String status = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell cdk-column-status mat-column-status ng-star-inserted'])[1]")).getAccessibleName();
				
				System.out.println("status :"+status);
				Assert.assertEquals(status, "Invalid","Status is not Invalid");
				
				// click on Action
				driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell cdk-column-action mat-column-action ng-star-inserted'])[1]")).click();
				// randomly generate true for approve and false for reject
				boolean approve = Math.random() < 0.5;
				
				WebElement remarkInput = driver.findElement(By.tagName("textarea"));
				remarkInput.clear();
				String remarkText;
				if(approve) {
					System.out.println(" go for approve");
					remarkText = "Appr";
					remarkInput.sendKeys(remarkText);
					driver.findElement(By.className("approve_btn")).click();
				}
				else {
					System.out.println("go for reject");
					remarkText="rej";
					remarkInput.sendKeys(remarkText);
					driver.findElement(By.className("reject_btn")).click();
				}
				
				WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));

				String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();

				System.out.println("submitText for Unarchive:" + submitText);

				Assert.assertTrue(submitText.contains("Updated Successfully"));
                
				ngDriver.waitForAngularRequestsToFinish();
				Thread.sleep(5000);
				
				if(approve) {
					// click on Approved tab
					driver.findElement(By.xpath("(//*[@class='tab-text1'])[5]")).click();
				}
				else {
					// click on Rejected tab
					driver.findElement(By.xpath("(//*[@class='tab-text1'])[7]")).click();
				}
				
				ngDriver.waitForAngularRequestsToFinish();
				Thread.sleep(2000);
				
				int[] countArrAfter = countMatchingActionRequiredForPanCard(driver);
				
				int allCountNumberAfter = countArrAfter[0];
				int invalidCountNumberAfter = countArrAfter[1];
				int approvedCountNumberAfter = countArrAfter[2];
				int rejectedCountNumberAfter = countArrAfter[3];
				
				System.out.println("allCountNumberAfter :"+allCountNumberAfter);
				System.out.println("invalidCountNumberAfter :"+invalidCountNumberAfter);
				System.out.println("approvedCountNumberAfter :"+approvedCountNumberAfter);
				System.out.println("rejectedCountNumberAfter :"+rejectedCountNumberAfter);
				
				//copy status 
				String statusAfter = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell cdk-column-status mat-column-status ng-star-inserted'])[1]")).getAccessibleName();
				
				//copy pan card remark
				String copiedPanReamrk = driver.findElement(By.xpath("(//*[@class='mat-cell cdk-cell cdk-column-pan_card_remark mat-column-pan_card_remark ng-star-inserted'])[1]")).getAccessibleName();
				
				System.out.println("copiedPanReamrk :"+copiedPanReamrk);
				
				if(approve) {
					Assert.assertEquals(approvedCountNumberAfter, approvedCountNumber+1,"Approved count mismatch after approval");
					
					Assert.assertEquals(statusAfter, "Approved","Not fount Approved status");
				}
				else {
					Assert.assertEquals(rejectedCountNumberAfter, rejectedCountNumber+1,"Rejected count mismatch after rejection");
					Assert.assertEquals(statusAfter, "Rejected","Not fount Rejected status");
				}
				
				// invalid count will be decreased by 1 after approve or reject action
				Assert.assertEquals(invalidCountNumberAfter, invalidCountNumber-1,"Invalid count mismatch after approval or rejection");
				
				//Assert.assertEquals(copiedPanReamrk, remarkText);
	}
	
	private static int[] countMatchingActionRequiredForPanCard(WebDriver driver) {
		//get count no for All 
		String allCount = driver.findElement(By.xpath("(//*[@class='tab-text1'])[2]")).getText();
		
		//get count no for Invalid 
		String InvalidCount = driver.findElement(By.xpath("(//*[@class='tab-text1'])[4]")).getText();
		
		//get count no for Approved
		String approvedCount = driver.findElement(By.xpath("(//*[@class='tab-text1'])[6]")).getText();
		
		//get count no for Rejected
		String rejectedCount = driver.findElement(By.xpath("(//*[@class='tab-text1'])[8]")).getText();
		
		int allCountNumber = Integer.parseInt(allCount);
		int invalidCountNumber = Integer.parseInt(InvalidCount);
		int approvedCountNumber = Integer.parseInt(approvedCount);
		int rejectedCountNumber = Integer.parseInt(rejectedCount);
		
		int count[] = {allCountNumber,invalidCountNumber,approvedCountNumber,rejectedCountNumber};
		
		return count;
	
	}
	
}
