package selenium.saral_nidhi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.paulhammant.ngwebdriver.NgWebDriver;

import static org.openqa.selenium.support.locators.RelativeLocator.with;

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

		ngDriver = new NgWebDriver((JavascriptExecutor) driver);

		// ArrayList<String> downloadingFields = new ArrayList<String>();
		driver.get(url);

		// ArrayList<String> a = new ArrayList<String>();

		// DownloadTotalFormDataDriven dd = new DownloadTotalFormDataDriven();

		// ArrayList<String> excel_data_for_download_total_form = dd.getData("Cash", a);
		// Cheque_ModePage chequePage = new Cheque_ModePage(driver);

		LandingPage lp = new LandingPage(driver);

		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		lp.login_email().sendKeys("kumar.vikastreasurer@jarvis.consulting");
		lp.login_password().sendKeys("Test@123");

		WebElement sendOTP = wait.until(ExpectedConditions.elementToBeClickable(lp.send_OTP()));
		sendOTP.click();

		WebElement enterOTP = wait.until(ExpectedConditions.visibilityOfElementLocated(lp.enter_otp()));
		enterOTP.sendKeys("227244");

		WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(lp.login_btn()));
		loginButton.click();

		// explicit wait
		// WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(5000);

		System.out.println("&&&& downloadDonationListTestForCash section ----");
		ArrayList<String> downloadingFields = new ArrayList<String>();

		ArrayList<String> a = new ArrayList<String>();

		DownloadTotalFormDataDriven dd = new DownloadTotalFormDataDriven();

		ArrayList<String> excel_data_for_download_total_form = dd.getData("Cheque", a);

		// click on भारतीय जनता पार्टी for home
		driver.findElement(By.cssSelector("[class='header-title-span']")).click();
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		driver.findElement(By.cssSelector("[class='count']")).click();
		ngDriver.waitForAngularRequestsToFinish();

		// click on Cash tab
		WebElement paymentMode = driver.findElement(By.xpath("(//*[@class='tab-text'])[2]"));
		paymentMode.click();

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		Random rand = new Random();
		int randomNumForFilter = rand.nextInt(3) + 1;
		String filterElement = "";

		String filterByName = "kumar";
		String filterByPan = "FOOPK5636K";
		String filterByPhone = "8825137191";
		String filterByState = "Andhra Pradesh";
		// by name if 1
		if (randomNumForFilter == 1) {
			filterElement = filterByName;
		}
		// by pan no
		else if (randomNumForFilter == 2) {
			filterElement = filterByPan;
		}
		// by Phone no
		else {
			filterElement = filterByPhone;
		}

		driver.findElement(By.xpath("(//*[@formcontrolname='query'])")).sendKeys(filterElement);

		driver.findElement(By.xpath("(//*[@class='ng-arrow-wrapper'])[1]")).click();
		Thread.sleep(1000);

		driver.findElement(By.xpath("//span[contains(text(), '" + filterByState + "')]")).click();

		driver.findElement(By.xpath("//span[contains(text(), 'Search')]")).click();

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		// --------------------------

		WebElement downloadBtn = wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//span[contains(text(),'Download')]"))));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", downloadBtn);

		ngDriver.waitForAngularRequestsToFinish();

		Thread.sleep(2000);
		String getDownloadSectionTopText = driver.findElement(By.tagName("b")).getText();

		Assert.assertEquals(getDownloadSectionTopText, "Donation Lists Download");
		log.info("Donation Lists Download visible");
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

		String PinCode = excel_data_for_download_total_form.get(26);
		String AddressState = excel_data_for_download_total_form.get(27);
		
		String Pan_Card = excel_data_for_download_total_form.get(28);
		String Pan_Card_Remark = excel_data_for_download_total_form.get(29);
		String Amount = excel_data_for_download_total_form.get(30);

		String Amount_in_Words = excel_data_for_download_total_form.get(31);
		String Collector_Name = excel_data_for_download_total_form.get(32);
		String Collector_Phone = excel_data_for_download_total_form.get(33);
		String Nature_of_Donation = excel_data_for_download_total_form.get(34);

		String Party_Unit = excel_data_for_download_total_form.get(35);
		String Location = excel_data_for_download_total_form.get(36);
		String Payment_Realize_date = excel_data_for_download_total_form.get(37);
		String Receipt_Number = excel_data_for_download_total_form.get(38);

		String Transaction_Valid = excel_data_for_download_total_form.get(39);
		String Created_By = excel_data_for_download_total_form.get(40);
		String Created_At = excel_data_for_download_total_form.get(41);
		String Cheque_Bounce_Remark = excel_data_for_download_total_form.get(42);

		String Reverse_Remark = excel_data_for_download_total_form.get(43);
		String Pan_Card_Photo = excel_data_for_download_total_form.get(44);
		String Cheque_or_DD_Photo1 = excel_data_for_download_total_form.get(45);

		String Cheque_or_DD_Photo2 = excel_data_for_download_total_form.get(46);

		Thread.sleep(3000);

		WebElement Select_All_Field_Element = driver.findElements(By.cssSelector(".mat-checkbox.mat-accent")).get(11);
		System.out.println("Select_All_Field_Element text :" + Select_All_Field_Element.getText());

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

		WebElement PinCode_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(25)"));
		WebElement AddressState_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(26)"));

		WebElement Pan_Card_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(27)"));
		WebElement Pan_Card_Remark_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(28)"));

		WebElement Amount_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(29)"));
		WebElement Amount_in_words_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(30)"));
		WebElement Collector_Name_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(31)"));
		WebElement Collector_Phone_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(32)"));

		WebElement Nature_of_Donation_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(33)"));
		WebElement Party_Unit_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(34)"));
		WebElement Location_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(35)"));
		WebElement Payment_Realize_date_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(36)"));

		WebElement Receipt_Number_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(37)"));
		WebElement Transaction_Valid_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(38)"));
		WebElement Created_By_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(39)"));
		WebElement Created_At_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(40)"));

		WebElement Cheque_Bounce_Remark_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(41)"));
		WebElement Reverse_Remark_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(42)"));
		WebElement Pan_Card_Photo_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(43)"));
		WebElement Cheque_or_DD_Photo1_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(44)"));

		WebElement Cheque_or_DD_Photo2_Element = driver
				.findElement(By.cssSelector(".mat-checkbox.mat-accent.ng-star-inserted:nth-child(45)"));

		boolean flag_Select_All_Field = false;
		int numberOfFieldsSelected = 0;
		if (Select_All_Field.equals("yes")) {

			Select_All_Field_Element.click();
			// Number of fields need to selected is 45
			numberOfFieldsSelected = 45;

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

			downloadingFields.add("PinCode");
			downloadingFields.add("Address State");

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

			Assert.assertEquals(PinCode_Element.getAttribute("ng-reflect-checked"), "true");
			Assert.assertEquals(AddressState_Element.getAttribute("ng-reflect-checked"), "true");

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
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				downloadingFields.add("State");
				Assert.assertEquals(State_Element.getAttribute("ng-reflect-checked"), "true");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Transaction_Type.equals("yes")) {
			Transaction_Type_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Transaction Type");
				Assert.assertEquals(Transaction_Type_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Transaction_Type_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Transaction Type");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Date_of_transaction.equals("yes")) {
			Date_of_transaction_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Date of transaction");
				Assert.assertEquals(Date_of_transaction_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Date_of_transaction_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Date of transaction");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Financial_Year.equals("yes")) {
			Financial_Year_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Financial Year");
				Assert.assertEquals(Financial_Year_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Financial_Year_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Financial Year");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (Mode_of_Payment.equals("yes")) {
			Mode_of_Payment_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Mode of Payment");
				Assert.assertEquals(Mode_of_Payment_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Mode_of_Payment_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Mode of Payment");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Account_Number.equals("yes")) {
			Account_Number_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Account Number");
				Assert.assertEquals(Account_Number_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Account_Number_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Account Number");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (IFSC_Code.equals("yes")) {
			IFSC_Code_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("IFSC Code");
				Assert.assertEquals(IFSC_Code_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(IFSC_Code_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("IFSC Code");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Bank_Name.equals("yes")) {
			Bank_Name_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Bank Name");
				Assert.assertEquals(Bank_Name_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Bank_Name_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Bank Name");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Branch_Name.equals("yes")) {
			Branch_Name_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Branch Name");
				Assert.assertEquals(Branch_Name_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Branch_Name_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Branch Name");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (Branch_Address.equals("yes")) {
			Branch_Address_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Branch Address");
				Assert.assertEquals(Branch_Address_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Branch_Address_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Branch Address");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Name.equals("yes")) {

			Name_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Name");
				Assert.assertEquals(Name_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Name_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Name");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Phone.equals("yes")) {
			Phone_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Phone");
				Assert.assertEquals(Phone_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Phone_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Phone");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Email.equals("yes")) {
			Email_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Email");
				Assert.assertEquals(Email_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Email_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Email");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (Date_of_Cheque.equals("yes")) {
			Date_of_Cheque_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Date of Cheque");
				Assert.assertEquals(Date_of_Cheque_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Date_of_Cheque_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Date of Cheque");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Cheque_Number.equals("yes")) {
			Cheque_Number_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Cheque Number");
				Assert.assertEquals(Cheque_Number_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Cheque_Number_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Cheque Number");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Date_of_Draft.equals("yes")) {
			Date_of_Draft_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Date of Draft");
				Assert.assertEquals(Date_of_Draft_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Date_of_Draft_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Date of Draft");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Draft_Number.equals("yes")) {
			Draft_Number_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Draft Number");
				Assert.assertEquals(Draft_Number_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Draft_Number_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Draft Number");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (UTR_No.equals("yes")) {
			UTR_No_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("UTR No");
				Assert.assertEquals(UTR_No_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(UTR_No_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("UTR No");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Category.equals("yes")) {
			Category_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Category");
				Assert.assertEquals(Category_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Category_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Category");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Proprietorship.equals("yes")) {
			Proprietorship_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Proprietorship");
				Assert.assertEquals(Proprietorship_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Proprietorship_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Proprietorship");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Proprietorship_Name.equals("yes")) {
			Proprietorship_Name_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Proprietorship Name");
				Assert.assertEquals(Proprietorship_Name_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Proprietorship_Name_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Proprietorship Name");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (House.equals("yes")) {
			House_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("House");
				Assert.assertEquals(House_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(House_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("House");
				System.out.println("House is selected");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Locality.equals("yes")) {
			Locality_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Locality");
				Assert.assertEquals(Locality_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Locality_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Locality");
				System.out.println("Locality is selected");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (District.equals("yes")) {
			District_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("District");
				Assert.assertEquals(District_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(District_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("District");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (PinCode.equals("yes")) {
			PinCode_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("PinCode");
				Assert.assertEquals(PinCode_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(PinCode_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("PinCode");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (AddressState.equals("yes")) {
			AddressState_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Address State");
				Assert.assertEquals(AddressState_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(AddressState_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Address State");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (Pan_Card.equals("yes")) {
			Pan_Card_Element.click();
			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Pan Card");
				Assert.assertEquals(Pan_Card_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Pan_Card_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Pan Card");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (Pan_Card_Remark.equals("yes")) {
			Pan_Card_Remark_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Pan Card Remark");
				Assert.assertEquals(Pan_Card_Remark_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Pan_Card_Remark_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Pan Card Remark");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Amount.equals("yes")) {
			Amount_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Amount");
				Assert.assertEquals(Amount_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Amount_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Amount");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Amount_in_Words.equals("yes")) {
			Amount_in_words_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Amount in Words");
				Assert.assertEquals(Amount_in_words_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Amount_in_words_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Amount in Words");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Collector_Name.equals("yes")) {
			Collector_Name_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Collector Name");
				Assert.assertEquals(Collector_Name_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Collector_Name_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Collector Name");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (Collector_Phone.equals("yes")) {
			Collector_Phone_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Collector Phone");
				Assert.assertEquals(Collector_Phone_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Collector_Phone_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Collector Phone");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Nature_of_Donation.equals("yes")) {
			Nature_of_Donation_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Nature of Donation");
				Assert.assertEquals(Nature_of_Donation_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Nature_of_Donation_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Nature of Donation");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Party_Unit.equals("yes")) {
			Party_Unit_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Party Unit");
				Assert.assertEquals(Party_Unit_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Party_Unit_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Party Unit");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Location.equals("yes")) {
			Location_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Location");
				Assert.assertEquals(Location_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Location_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Location");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (Payment_Realize_date.equals("yes")) {
			Payment_Realize_date_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Payment realize date");
				Assert.assertEquals(Payment_Realize_date_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Payment_Realize_date_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Payment realize date");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Receipt_Number.equals("yes")) {
			Receipt_Number_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Receipt Number");
				Assert.assertEquals(Receipt_Number_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Receipt_Number_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Receipt Number");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Transaction_Valid.equals("yes")) {
			Transaction_Valid_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Transaction Valid");
				Assert.assertEquals(Transaction_Valid_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Transaction_Valid_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Transaction Valid");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Created_By.equals("yes")) {
			Created_By_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Created By");
				Assert.assertEquals(Created_By_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Created_By_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Created By");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (Created_At.equals("yes")) {
			Created_At_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Created At");
				Assert.assertEquals(Created_At_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Created_At_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Created At");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Cheque_Bounce_Remark.equals("yes")) {
			Cheque_Bounce_Remark_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Cheque Bounce Remark");
				Assert.assertEquals(Cheque_Bounce_Remark_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Cheque_Bounce_Remark_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Cheque Bounce Remark");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Reverse_Remark.equals("yes")) {
			Reverse_Remark_Element.click();
			Thread.sleep(1000);

			if (flag_Select_All_Field) {
				downloadingFields.remove("Reverse Remark");
				Assert.assertEquals(Reverse_Remark_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Reverse_Remark_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Reverse Remark");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}
		if (Pan_Card_Photo.equals("yes")) {
			Pan_Card_Photo_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Pan Card Photo");
				Assert.assertEquals(Pan_Card_Photo_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Pan_Card_Photo_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Pan Card Photo");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		if (Cheque_or_DD_Photo1.equals("yes")) {

			Thread.sleep(1000);
			Cheque_or_DD_Photo1_Element.click();
			Thread.sleep(4000);
			if (flag_Select_All_Field) {

				downloadingFields.remove("Cheque/DD photo1");
				Assert.assertEquals(Cheque_or_DD_Photo1_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				System.out.println("inside else block of Cheque_or_DD_Photo1_Element...");
				Assert.assertEquals(Cheque_or_DD_Photo1_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Cheque/DD photo1");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;

			}
		}

		if (Cheque_or_DD_Photo2.equals("yes")) {
			Cheque_or_DD_Photo2_Element.click();

			Thread.sleep(1000);
			if (flag_Select_All_Field) {
				downloadingFields.remove("Cheque/DD photo2");
				Assert.assertEquals(Cheque_or_DD_Photo2_Element.getAttribute("ng-reflect-checked"), "false");
				numberOfFieldsSelected = numberOfFieldsSelected - 1;
			} else {
				Assert.assertEquals(Cheque_or_DD_Photo2_Element.getAttribute("ng-reflect-checked"), "true");
				downloadingFields.add("Cheque/DD photo2");
				numberOfFieldsSelected = numberOfFieldsSelected + 1;
			}
		}

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,310)");

		// Submit button
		WebElement downloadBtn2 = wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//span[contains(text(),'Submit')]"))));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", downloadBtn2);

		System.out.println("numberOfFieldsSelected :" + numberOfFieldsSelected);

		// Select All Field is clicked but all fields are deselected
		// or Select All Field is not clicked but all fields are deselected
		if ((flag_Select_All_Field && numberOfFieldsSelected == 0)
				|| (!flag_Select_All_Field && numberOfFieldsSelected == 0)) {

			WebElement submitPopUp = driver
					.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));
			String submitPopUpText = wait.until(ExpectedConditions.visibilityOf(submitPopUp)).getText();
			System.out.println("submitPopUpText :" + submitPopUpText);
			Assert.assertEquals(submitPopUpText.contains("Please select at least one field"), true,
					"submitPopUpText not matching");
		} else {
			ngDriver.waitForAngularRequestsToFinish();
			Thread.sleep(10000);

			File folder = new File(System.getProperty("user.dir") + "\\downloadTestFolder");

			// List the files on that folder
			File[] listOfFiles = folder.listFiles();
			boolean found = false;
			String fileName = null;
			for (File listOfFile : listOfFiles) {
				if (listOfFile.isFile()) {
					fileName = listOfFile.getName();
					System.out.println("fileName is is :" + fileName);
					if (fileName.contains("NidhiCollection")) {
						found = true;
						log.info("downloaded file :" + fileName);
						System.out.println("###################### fileName is found.............." + fileName);
					}
				}
			}
			Assert.assertTrue(found, "Downloaded document is not found");
			ArrayList<String> fieldsArrayList = new ArrayList<>();
			ArrayList<String> fields = getDownloadFields(fieldsArrayList, fileName);

			System.out.println("downloadingFields size :" + downloadingFields.size());
			System.out.println("fields size :" + fields.size());
			System.out.println("fields 1st :" + fields.get(0));
			System.out.println("downloadingFields 1st :" + downloadingFields.get(0));
			
			Assert.assertEquals(true, downloadingFields.equals(fields), "downloading fields are not matching..");

			File file = new File(System.getProperty("user.dir") + "\\downloadTestFolder\\" + fileName);

			System.out.println("delete file Absolute path :" + file.getAbsolutePath());

			if (file.delete()) {
				System.out.println("file deleted success");
				log.info(fileName + " file deleted success");
			} else {
				System.out.println("file delete fail");
				log.error(fileName + " file delete fail");
			}

		}

	}

	private static ArrayList<String> getDownloadFields(ArrayList<String> fieldsArrayList, String fileName)
			throws IOException {

		System.out.println("fileName is is :" + fileName);

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
		System.out.println(
				"-------------------------------------print fields ---------------------------------------------");
		for (int i = 0; i < fieldsArrayList.size(); i++) {
			System.out.println(fieldsArrayList.get(i));
		}

		fis.close();
		return fieldsArrayList;
	}

}
