package selenium.saral_nidhi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.paulhammant.ngwebdriver.NgWebDriver;

import pageObjects.Cash_ModePage;
import pageObjects.IMPS_ModePage;
import pageObjects.LandingPage;
import resources.Base;

public class IMPSRelatedTest extends Base {
	public WebDriver driver;
	public NgWebDriver ngDriver;
	public static Logger log = LogManager.getLogger(IMPSRelatedTest.class);

	int rowNo = 1;
	String reverseText;
	String approveText;
	String rejectText;

	String filterByName;
	String filterByPan;
	String filterByPhone;
	String filterByInstrumentNo;
	String filterByState = "";
	// if pan card is correct in edit then actionRequiredforPancardApplicable will
	// be false
	boolean actionRequiredforPancardApplicable = true;

	// ------ Indian Donation Form

	@Test
	public void basePageNavigationIMPS(ITestContext context) throws IOException, InterruptedException {
		driver = initializeDriver();
		ngDriver = new NgWebDriver((JavascriptExecutor) driver);

		driver.get(url);

		LocalStorage storage = ((WebStorage) driver).getLocalStorage();

		new SetLocalStorage(storage, driver, context);

		ArrayList<String> a = new ArrayList<String>();

		DataDriven dd = new DataDriven();
		ArrayList<String> excel_data = dd.getData("IMPS_ModeTest", a);

		IMPS_ModePage impsPage = new IMPS_ModePage(driver);

		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		/*
		  LandingPage lp = new LandingPage(driver);
		  lp.login_email().sendKeys(excel_data.get(1));
		  lp.login_password().sendKeys(excel_data.get(2));
		  
		  WebElement sendOTP =
		  wait.until(ExpectedConditions.elementToBeClickable(lp.send_OTP()));
		  sendOTP.click();
		  
		  WebElement enterOTP =
		  wait.until(ExpectedConditions.visibilityOfElementLocated(lp.enter_otp()));
		  enterOTP.sendKeys(excel_data.get(3));
		  
		  WebElement loginButton =
		  wait.until(ExpectedConditions.elementToBeClickable(lp.login_btn()));
		  loginButton.click();
		 */
		log.info("Login successfully in IMPS_ModeTest");

		WebElement heading1 = wait.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getFirstHeading()));
		Assert.assertEquals(heading1.getText(), "Nidhi Collection");

		impsPage.getIndianDonationForm().click();
		ngDriver.waitForAngularRequestsToFinish();

		WebElement paymentModeOption = wait
				.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getIMPSPaymentMode()));

		// imps should not be selected
		Assert.assertFalse(impsPage.getIMPSModeValue().isSelected());

		paymentModeOption.click();
		ngDriver.waitForAngularRequestsToFinish();

		// imps should be selected
		Assert.assertTrue(impsPage.getIMPSModeValue().isSelected());
		log.info("IMPS Mode of Payment selected");
		LocalDate now = LocalDate.now();

		int no_of_back_days = Integer.parseInt(excel_data.get(5));
		LocalDate transactionDate_generate = now.minusDays(no_of_back_days);

		System.out.println(now);
		System.out.println("transactionDate: " + transactionDate_generate);

		DateTimeFormatter dateFormating = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String transaction_Date = transactionDate_generate.format(dateFormating);

		impsPage.getTransactionDate().sendKeys(transaction_Date);

		// Wrong utr no
		impsPage.getUTRNumber().sendKeys("12345 new");

		impsPage.getAccountNumber().click();

		Assert.assertEquals(impsPage.getInvalidUTR().getText(), "UTR number is invalid");

		impsPage.getUTRNumber().clear();

		Random random = new Random();
		String random_utr_no = Integer.toString(random.nextInt(900000) + 100000);

		random_utr_no = "imps" + random_utr_no;
		filterByInstrumentNo = random_utr_no;
		impsPage.getUTRNumber().sendKeys(random_utr_no);
		impsPage.getAccountNumber().sendKeys(excel_data.get(6));

		impsPage.getIfscCode().sendKeys(excel_data.get(7));

		WebElement bankdetails = wait.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getbankDetails()));

		if (bankdetails.isDisplayed()) {

			System.out.println("Element is Visible");
			bankdetails.click();
			log.info("bankdetails is visible");
		}

		else {

			System.out.println("Element is InVisible");
			log.error("bankdetails is not visible");

		}

		String donor_name = excel_data.get(8);
		String donor_phone = excel_data.get(9);
		String donor_email = excel_data.get(10);

		impsPage.getDonorName().sendKeys(donor_name);

		String copied_name = impsPage.getDonorName().getAttribute("value");

		System.out.println("copied_name:" + copied_name);

		// Check name is converted to UpperCase or not
		Assert.assertEquals(donor_name.toUpperCase(), copied_name);
		// type wrong phone no
		impsPage.getDonorPhoneNumber().sendKeys("23222322");

		impsPage.getDonorEmail().click();

		Assert.assertEquals("Please enter a valid phone number", impsPage.getInvalidDonorPhone().getText());

		impsPage.getDonorPhoneNumber().clear();
		impsPage.getDonorPhoneNumber().sendKeys(donor_phone);

		// Enter wrong email
		impsPage.getDonorEmail().sendKeys("abc.com");

		impsPage.getHouse().click();

		Assert.assertEquals("Enter a valid email", impsPage.getInvalidDonorEmail().getText());

		impsPage.getDonorEmail().clear();
		impsPage.getDonorEmail().sendKeys(donor_email);
		impsPage.getHouse().sendKeys(excel_data.get(11));
		impsPage.getLocality().sendKeys(excel_data.get(12));
		impsPage.getPinCode().sendKeys(excel_data.get(13));

		boolean district_bool = wait
				.until(ExpectedConditions.textToBePresentInElementValue(impsPage.getDistrict(), excel_data.get(14)));
		String copied_district = null;

		if (district_bool) {
			copied_district = impsPage.getDistrict().getAttribute("value");
			log.info("district name present");
		} else {
			log.error("district name didn't come till 50 sec");
		}

		System.out.println("copied_district :" + copied_district);

		boolean state_bool = wait
				.until(ExpectedConditions.textToBePresentInElement(impsPage.getState(), excel_data.get(15)));

		String copied_state = null;
		if (state_bool) {
			copied_state = impsPage.getState().getText();
			log.info("state name present");
		} else {
			log.error("state name didn't come till 50 sec");
		}
		System.out.println("copied_state :" + copied_state);

		Assert.assertEquals(copied_district, excel_data.get(14));
		Assert.assertEquals(copied_state, excel_data.get(15));

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,310)");

		String[] arr_category = { "individual", "huf", "partnership", "trust", "corporation", "others" };
		for (String category : arr_category) {

			WebElement categoryElement = impsPage.selectCategory(category);
			categoryElement.click();

			Thread.sleep(500);

			String[] pan_no = { "F", "O", "O", "P", "K", "1", "2", "3", "4", "k" };

			String name_of_proprietorship = "";
			if (category == "individual") {

				impsPage.getNoProprietorship(categoryElement).click();

				for_yes_no_proprietorship(driver, pan_no, name_of_proprietorship, copied_name, category, impsPage);

				// Now for Yes
				// Thread.sleep(1000);

				String proprietorship_txt = impsPage.getProprietorship(categoryElement).getText();
				Assert.assertEquals(proprietorship_txt, "Is it a proprietorship? *");

				WebElement proprietorship_yes = impsPage.getYesProprietorship(categoryElement);
				proprietorship_yes.click();

				// Thread.sleep(1000);

				boolean b1 = impsPage.getYesProprietorshipSelect().isSelected();

				System.out.println("proprietorship_yes is selected or not --->:" + b1);
				Assert.assertTrue(b1);

				WebElement Proprietorship_txt2 = impsPage.getTextAfterYesProprietorship(proprietorship_yes);

				System.out.println(Proprietorship_txt2.getText());
				Assert.assertEquals(Proprietorship_txt2.getText(), "Write the name of the Proprietorship");

				name_of_proprietorship = excel_data.get(16);
				// type Name of proprietorship
				impsPage.getProprietorshipName().sendKeys(name_of_proprietorship);

				for_yes_no_proprietorship(driver, pan_no, name_of_proprietorship, copied_name, category, impsPage);

				// As individual category end so make --> name_of_proprietorship=""
				name_of_proprietorship = "";
			} else if (category == "others") {

				WebElement other_category = wait
						.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getOtherCategory()));

				boolean visible_other_category = other_category.isDisplayed();
				Assert.assertTrue(visible_other_category);
				other_category.sendKeys(excel_data.get(25));

				other_than_individual(driver, pan_no, name_of_proprietorship, copied_name, category, impsPage);
			}

			else {
				other_than_individual(driver, pan_no, name_of_proprietorship, copied_name, category, impsPage);
			}
		}

		Random rand = new Random();
		int amount = Integer.parseInt(excel_data.get(17));
		int random_int_amount = rand.nextInt(amount);
		String random_int_amount1 = Integer.toString(random_int_amount);

		WebElement amount_input = impsPage.getAmountInput();

		String amount_converted_in_words;
		amount_input.sendKeys(random_int_amount1);
		amount_converted_in_words = convertToIndianCurrency(random_int_amount1);

		// Remove extra whitespace if any
		amount_converted_in_words = amount_converted_in_words.replaceAll("\\s+", " ");

		String amount_in_words_txt = impsPage.getAmountInWords(amount_input).getText();

		System.out.println("amount_converted_in_words **** :" + amount_converted_in_words);
		System.out.println("amount_in_workds_txt **** :" + amount_in_words_txt);

		Assert.assertEquals(amount_converted_in_words, amount_in_words_txt);

		impsPage.getNarationInput().sendKeys(excel_data.get(18));
		WebElement collector_name_input = impsPage.getCollectorName();

		// collector_name_input.sendKeys("vk");
		WebElement collecter_phone = impsPage.getCollectorPhone();
		// collecter_phone.click();
		// wait.until(ExpectedConditions.textToBePresentInElement(impsPage.getWrongCollectorName(collector_name_input),"Please
		// enter a valid name"));

		// collector_name_input.clear();
		collector_name_input.sendKeys(excel_data.get(19));

		// wrong collector phone no
		collecter_phone.sendKeys("23222322");
		collector_name_input.click();
		wait.until(ExpectedConditions.textToBePresentInElement(impsPage.getWrongCollectorPhone(collecter_phone),
				"Please enter correct phone number"));

		collecter_phone.clear();

		collecter_phone.sendKeys(excel_data.get(20));
		jse.executeScript("window.scrollBy(0,310)");
		// Nature of donation
		String[] donation_nature = { "Voluntary Contribution", "Aajivan Sahyog Nidhi.", "Other" };

		for (String donation : donation_nature) {

			impsPage.getDonationNature(donation).click();
			Thread.sleep(1000);
			System.out.println("nature of donation :--->" + impsPage.getDonationNature2(donation).isSelected());

			Assert.assertTrue(impsPage.getDonationNature2(donation).isSelected());

			if (donation == "Other") {
				impsPage.getOtherNatureOfDonation().sendKeys(excel_data.get(21));
			}

		}

		JavascriptExecutor jse1 = (JavascriptExecutor) driver;
		jse1.executeScript("window.scrollBy(0,310)");
		// party unit
		String[] party_unit_arr = { "CountryState", "Zila", "Mandal" };

		for (String party_unit : party_unit_arr) {

			WebElement party_unit_type = impsPage.getPartyUnit(party_unit);
			party_unit_type.click();
			System.out.println(impsPage.getPartyUnit2(party_unit).isSelected());

			Assert.assertTrue(impsPage.getPartyUnit2(party_unit).isSelected());

			boolean state_exist = impsPage.isElementPresent(driver, "state").isDisplayed();

			System.out.println("state_exist :" + state_exist);
			Assert.assertEquals(state_exist, true);

			boolean zila_exist;
			boolean mandal_exist;

			String state_unit_name = excel_data.get(22);

			System.out.println("state_unit_name :" + state_unit_name);
			String zila_unit_name = excel_data.get(23);
			String mandal_unit_name = excel_data.get(24);
			System.out.println("zila_unit_name :" + zila_unit_name);
			System.out.println("mandal_unit_name :" + mandal_unit_name);

			if (party_unit == "CountryState") {
				Thread.sleep(2000);

				WebElement selectState = wait
						.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getSelectState()));

				selectState.click();

				WebElement state_unit = wait.until(
						ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenState(state_unit_name)));
				state_unit.click();

				log.info(state_unit_name + " selected");
			} else if (party_unit == "Zila") {

				zila_exist = impsPage.isElementPresent(driver, "zila").isDisplayed();
				System.out.println("zila_exist after click on zila:" + zila_exist);
				Assert.assertEquals(zila_exist, true);

				// select state
				WebElement selectState = wait
						.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getSelectState()));
				selectState.click();
				log.info("clicked on select state for zila unit");

				WebElement state_from_zila = wait.until(
						ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenState(state_unit_name)));

				try {
					state_from_zila.click();
				} catch (StaleElementReferenceException e) {
					state_from_zila = wait.until(
							ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenState(state_unit_name)));
					state_from_zila.click();
				}
				log.info(state_unit_name + " selected for zila unit");
				// select zila
				WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(impsPage.getSelectZila()));
				selectZila.click();

				log.info("clicked on select zila for zila unit");
				WebElement zila_unit = wait
						.until(ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenZila(zila_unit_name)));
				zila_unit.click();

				log.info(zila_unit_name + " selected for zila unit");
			} else {

				zila_exist = impsPage.isElementPresent(driver, "zila").isDisplayed();
				System.out.println("zila_exist after click on zila:" + zila_exist);
				Assert.assertEquals(zila_exist, true);
				// select state
				WebElement selectState = wait
						.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getSelectState()));
				selectState.click();
				log.info("clicked on select state for mandal unit");

				WebElement state_from_mandal = wait.until(
						ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenState(state_unit_name)));

				try {
					state_from_mandal.click();
				} catch (StaleElementReferenceException e) {
					state_from_mandal = wait.until(
							ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenState(state_unit_name)));
					state_from_mandal.click();
				}
				log.info(state_unit_name + " selected for mandal unit");

				// select zila
				WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(impsPage.getSelectZila()));
				selectZila.click();
				log.info("clicked on select zila for mandal unit");

				WebElement zila_unit = wait
						.until(ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenZila(zila_unit_name)));

				try {
					zila_unit.click();
				} catch (StaleElementReferenceException e) {
					zila_unit = wait.until(
							ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenZila(zila_unit_name)));
					zila_unit.click();
				}
				log.info(zila_unit_name + " selected for mandal unit");

				mandal_exist = impsPage.isElementPresent(driver, "mandal").isDisplayed();
				System.out.println("mandal_exist after click on mandal:" + mandal_exist);
				Assert.assertEquals(mandal_exist, true);

				// select mandal
				WebElement selectMandal = wait
						.until(ExpectedConditions.elementToBeClickable(impsPage.getSelectMandal()));
				selectMandal.click();
				log.info("clicked on select mandal unit");

				WebElement mandal_unit = wait.until(
						ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenMandal(mandal_unit_name)));
				mandal_unit.click();

				log.info(mandal_unit_name + " selected for mandal unit");
			}

		}

		driver.findElement(By.xpath("//button[@color='primary']")).click();

		WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));

		String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();

		System.out.println("submitText :" + submitText);

		Assert.assertTrue(submitText.contains("Record Saved Successfully"));
		ngDriver.waitForAngularRequestsToFinish();
		// success
		log.info("IMPS mode Transaction Created Sucessfully");

	}

	// --------- Action Related
	@Test(dependsOnMethods = { "basePageNavigationIMPS" })
	public void viewActionForIMPS(ITestContext context) throws IOException, InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		ArrayList<String> a = new ArrayList<String>();

		ActionReletedDataDriven dd = new ActionReletedDataDriven();
		ArrayList<String> excel_data = dd.getData("impsMode", a);

		IMPS_ModePage impsPage = new IMPS_ModePage(driver);
		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		Thread.sleep(6000);
		log.info("action test");
		// click on भारतीय जनता पार्टी for home
		driver.findElement(By.cssSelector("[class='header-title-span']")).click();

		WebElement heading2 = wait.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getFirstHeading()));
		Assert.assertEquals(heading2.getText(), "Nidhi Collection");

		WebElement c = driver.findElement(By.cssSelector("[class='total-count-div']"));
		WebElement count = wait.until(ExpectedConditions.elementToBeClickable(c));
		js.executeScript("arguments[0].click()", count);

		ngDriver.waitForAngularRequestsToFinish();

		String header = driver.findElement(By.tagName("h2")).getText();
		Assert.assertEquals(header, "Donation List");

		// click on IMPS tab
		WebElement paymentMode = driver.findElement(By.xpath("(//*[@class='tab-text'])[6]"));
		paymentMode.click();

		Thread.sleep(2000);

		// clicking on 3 dots for action
		try {
			WebElement row = driver.findElement(
					By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[" + rowNo + "]"));
			WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(row));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
			System.out.println("clicked successfuly on 3 dots");
		} catch (org.openqa.selenium.StaleElementReferenceException e1) {
			WebElement row = driver.findElement(
					By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[" + rowNo + "]"));
			WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(row));
			ele.click();
			System.out.println("exception in clicking 3 dots");
		}

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(3000);

		try {
			System.out.println("from try block for item");
			WebElement actionElement = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'View')]")));
			actionElement.click();
		} catch (org.openqa.selenium.StaleElementReferenceException e) {
			System.out.println("from catch block for item");
			WebElement actionElement = wait.until(
					ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//span[contains(text(),'View')]"))));
			actionElement.click();
		}
		log.info("View section started");

		String date = driver.findElement(By.xpath("//*[@formcontrolname='date_of_transaction']")).getAttribute("value");
		System.out.println("-------------------- total form count ---------------------");
		System.out.println("date :" + date);
		String utrNo = driver.findElement(By.xpath("//*[@formcontrolname='utr_number']")).getAttribute("value");
		System.out.println("utrNo :" + utrNo);
		String ifsc_code = driver.findElement(By.xpath("//*[@formcontrolname='ifsc_code']")).getAttribute("value");
		System.out.println("ifsc_code :" + ifsc_code);
		String bank_name = driver.findElement(By.xpath("//*[@formcontrolname='bank_name']")).getAttribute("value");
		System.out.println("bank_name :" + bank_name);
		String branch_name = driver.findElement(By.xpath("//*[@formcontrolname='branch_name']")).getAttribute("value");
		System.out.println("branch_name :" + branch_name);
		String branch_address = driver.findElement(By.xpath("//*[@formcontrolname='branch_address']"))
				.getAttribute("value");
		System.out.println("branch_address :" + branch_address);

		String donorName = driver.findElement(By.xpath("//*[@formcontrolname='name']")).getAttribute("value");
		System.out.println("donorName :" + donorName);

		String donor_phone = driver.findElement(By.xpath("//*[@formcontrolname='phone']")).getAttribute("value");
		System.out.println("donor_phone :" + donor_phone);

		String donor_email = driver.findElement(By.xpath("//*[@formcontrolname='email']")).getAttribute("value");
		System.out.println("donor_email :" + donor_email);

		String donor_house = driver.findElement(By.xpath("//*[@formcontrolname='house']")).getAttribute("value");
		System.out.println("donor_house :" + donor_house);

		String locality = impsPage.getLocality().getAttribute("value");
		System.out.println("locality :" + locality);

		String pincode = impsPage.getPinCode().getAttribute("value");
		System.out.println("pincode :" + pincode);

		js.executeScript("window.scrollBy(0,1350)", "");

		// String district = impsPage.getDistrict().getAttribute("value");
		// System.out.println("district :"+district);

		/*
		 * boolean district_bool = wait
		 * .until(ExpectedConditions.textToBePresentInElementValue(impsPage.getDistrict(
		 * ), "Pune")); String copied_district = null;
		 * 
		 * if (district_bool) { copied_district =
		 * impsPage.getDistrict().getAttribute("value"); } else {
		 * log.error("district name didn't come till 50 sec"); }
		 * 
		 * System.out.println(copied_district);
		 * 
		 * String state = impsPage.getState().getText();
		 * System.out.println("state :"+state);
		 * 
		 */

		String[] arr_category = { "individual", "huf", "partnership", "trust", "corporation", "others" };

		for (String category : arr_category) {
			// Thread.sleep(2000);

			WebElement selected2 = driver.findElement(By.xpath("(//input[@value='" + category + "'])"));

			if (selected2.isSelected()) {
				System.out.println("yes " + category + " is selected");
			}
		}

		Thread.sleep(2000);
		String pan1 = impsPage.get1stPanInput().getAttribute("value");
		String pan2 = impsPage.get2ndPanInput().getAttribute("value");
		String pan3 = impsPage.get3rdPanInput().getAttribute("value");
		String pan4 = impsPage.get4thPanInput().getAttribute("value");

		String pan5 = impsPage.get5thPanInput().getAttribute("value");
		String pan6 = impsPage.get6thPanInput().getAttribute("value");
		String pan7 = impsPage.get7thPanInput().getAttribute("value");
		String pan8 = impsPage.get8thPanInput().getAttribute("value");

		String pan9 = impsPage.get9thPanInput().getAttribute("value");
		String pan10 = impsPage.getLastPanInput().getAttribute("value");

		System.out.println("pan :" + pan1 + " " + pan2 + " " + pan3 + " " + pan4 + " " + pan5 + " " + pan6 + " " + pan7
				+ " " + pan8 + " " + pan9 + " " + pan10);

		String amount = driver.findElement(By.xpath("//*[@formcontrolname='amount']")).getAttribute("value");

		System.out.println("amount :" + amount);

		String amount_txt = driver.findElement(By.className("amount-in-text")).getText();

		System.out.println("amount_txt :" + amount_txt);

		String narration = driver.findElement(By.xpath("//*[@formcontrolname='narration']")).getAttribute("value");

		System.out.println("narration :" + narration);

		String collector_name = driver.findElement(By.xpath("//*[@formcontrolname='collector_name']"))
				.getAttribute("value");

		System.out.println("collector_name :" + collector_name);

		String collector_phone = driver.findElement(By.xpath("//*[@formcontrolname='collector_phone']"))
				.getAttribute("value");

		System.out.println("collector_phone :" + collector_phone);

		js.executeScript("window.scrollBy(0,750)", "");

		String[] arr_donation_nature = { "Voluntary Contribution", "Aajivan Sahyog Nidhi.", "Other" };

		for (String donationType : arr_donation_nature) {
			// Thread.sleep(2000);

			WebElement selected3 = driver.findElement(By.xpath("(//input[@value='" + donationType + "'])"));

			if (selected3.isSelected()) {
				System.out.println("yes " + donationType + " is selected");

				if (donationType == "Other") {
					String other_nature_of_donation = driver
							.findElement(By.xpath("//*[@formcontrolname='other_nature_of_donation']"))
							.getAttribute("value");

					System.out.println("other_nature_of_donation :" + other_nature_of_donation);
				}
			}
		}

		String[] arr_partyUnit = { "CountryState", "Zila", "Mandal" };

		for (String partyUnit : arr_partyUnit) {
			// Thread.sleep(2000);

			WebElement selected4 = driver.findElement(By.xpath("(//input[@value='" + partyUnit + "'])"));

			if (selected4.isSelected()) {
				System.out.println("yes " + partyUnit + " is selected");
			}
		}

		List<WebElement> inputDropDown = driver
				.findElements(By.xpath("(//*[@class='ng-value-label ng-star-inserted'])"));

		String stateName = "";
		String zilaName = "";
		String mandalName = "";

		stateName = driver.findElement(By.xpath("(//*[@class='ng-value-label ng-star-inserted'])[2]")).getText();
		if (inputDropDown.size() >= 3) {
			// bZila=true;
			zilaName = driver.findElement(By.xpath("(//*[@class='ng-value-label ng-star-inserted'])[3]")).getText();
		}

		if (inputDropDown.size() == 4) {
			// bMandal=true;
			mandalName = driver.findElement(By.xpath("(//*[@class='ng-value-label ng-star-inserted'])[4]")).getText();
		}

		System.out.println("stateName :" + stateName);
		System.out.println("zilaName :" + zilaName);
		System.out.println("mandalName :" + mandalName);

		log.info("View section end");
		// click on back icon
		driver.findElement(By.className("back-icon")).click();
		ngDriver.waitForAngularRequestsToFinish();

	}

	@Test(dependsOnMethods = { "viewActionForIMPS" })

	public void editActionForIMPS() throws InterruptedException, IOException {

		ArrayList<String> a = new ArrayList<String>();
		ActionReletedDataDriven dd = new ActionReletedDataDriven();
		ArrayList<String> excel_data = dd.getData("impsMode", a);
		IMPS_ModePage impsPage = new IMPS_ModePage(driver);
		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		Thread.sleep(5000);
		// clicking on 3 dots for action
		try {
			WebElement row = driver.findElement(
					By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[" + rowNo + "]"));
			WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(row));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
			System.out.println("clicked successfuly on 3 dots");
		} catch (org.openqa.selenium.StaleElementReferenceException e1) {
			WebElement row = driver.findElement(
					By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[" + rowNo + "]"));
			WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(row));
			ele.click();
			System.out.println("exception in clicking 3 dots");
		}

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(3000);

		try {
			System.out.println("from try block for item");
			WebElement actionElement = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Edit')]")));
			actionElement.click();
		} catch (org.openqa.selenium.StaleElementReferenceException e) {
			System.out.println("from catch block for item");
			WebElement actionElement = wait.until(
					ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//span[contains(text(),'Edit')]"))));
			actionElement.click();
		}

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(5000);

		log.info("Edit section");

		String random_int_amount1 = "123";
		String categoryChangeTo = excel_data.get(13);
		String proprietorshipChangeTo = excel_data.get(14);
		String name_of_proprietorship = excel_data.get(15);
		String donationNature = excel_data.get(22);
		String party_unit = excel_data.get(24);

		js.executeScript("window.scrollBy(0,750)", "");
		// impsPage.uploadFrontImage();
		// impsPage.uploadBackImage();

		impsPage.getAccountNumber().clear();
		impsPage.getAccountNumber().sendKeys(excel_data.get(6));

		impsPage.getIfscCode().clear();
		impsPage.getIfscCode().sendKeys(excel_data.get(4));

		WebElement bankdetails = wait.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getbankDetails()));

		if (bankdetails.isDisplayed()) {

			System.out.println("Element is Visible");
			bankdetails.click();
		} else {
			System.out.println("Element is InVisible");
		}

		String donor_name = excel_data.get(5);
		filterByName = donor_name;
		String donor_phone = excel_data.get(6);
		filterByPhone = donor_phone;
		String donor_email = excel_data.get(7);

		impsPage.getDonorName().clear();
		impsPage.getDonorName().sendKeys(donor_name);
		String copied_name = impsPage.getDonorName().getAttribute("value");

		System.out.println("copied_name:" + copied_name);
		impsPage.getDonorPhoneNumber().clear();
		impsPage.getDonorPhoneNumber().sendKeys(donor_phone);
		impsPage.getDonorEmail().clear();
		impsPage.getDonorEmail().sendKeys(donor_email);

		impsPage.getHouse().clear();
		impsPage.getHouse().sendKeys(excel_data.get(8));
		impsPage.getLocality().clear();
		impsPage.getLocality().sendKeys(excel_data.get(9));
		impsPage.getPinCode().clear();
		impsPage.getPinCode().sendKeys(excel_data.get(10));

		boolean district_bool = wait
				.until(ExpectedConditions.textToBePresentInElementValue(impsPage.getDistrict(), excel_data.get(11)));
		String copied_district = null;

		if (district_bool) {
			copied_district = impsPage.getDistrict().getAttribute("value");
		} else {
			log.error("district name didn't come till 50 sec");
		}

		System.out.println(copied_district);

		boolean state_bool = wait
				.until(ExpectedConditions.textToBePresentInElement(impsPage.getState(), excel_data.get(12)));

		String copied_state = null;
		if (state_bool) {
			copied_state = impsPage.getState().getText();
			System.out.println("copied_state :" + copied_state);
		} else {
			log.error("state name didn't come till 50 sec");
		}

		// Assert.assertEquals(copied_district, excel_data.get(14));
		// Assert.assertEquals(copied_state, excel_data.get(15));

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,310)");

		WebElement categoryElement = impsPage.selectCategory(categoryChangeTo);
		wait.until(ExpectedConditions.elementToBeClickable(categoryElement)).click();

		if (categoryChangeTo == "individual" || categoryChangeTo.equals("individual")) {
			if (proprietorshipChangeTo == "yes" || proprietorshipChangeTo.equals("yes")) {
				WebElement proprietorship_yes = impsPage.getYesProprietorship(categoryElement);
				proprietorship_yes.click();

				Thread.sleep(1000);

				boolean b1 = impsPage.getYesProprietorshipSelect().isSelected();

				System.out.println("proprietorship_yes is selected or not --->:" + b1);
				// Assert.assertTrue(b1);

				WebElement Proprietorship_txt2 = impsPage.getTextAfterYesProprietorship(proprietorship_yes);

				System.out.println(Proprietorship_txt2.getText());
				Assert.assertEquals(Proprietorship_txt2.getText(), "Write the name of the Proprietorship");
				// type Name of proprietorship
				impsPage.getProprietorshipName().clear();
				impsPage.getProprietorshipName().sendKeys(name_of_proprietorship);
			} else {
				WebElement proprietorship_no = impsPage.getNoProprietorship(categoryElement);
				proprietorship_no.click();
			}
		} else if (categoryChangeTo == "others" || categoryChangeTo.equals("others")) {
			WebElement other_category = wait
					.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getOtherCategory()));

			boolean visible_other_category = other_category.isDisplayed();
			Assert.assertTrue(visible_other_category);
			other_category.clear();
			other_category.sendKeys(excel_data.get(16));
		}

		String pan = excel_data.get(17);
		filterByPan = pan;
		String[] changePanNoTo = pan.split("");

		impsPage.get1stPanInput().clear();
		impsPage.get1stPanInput().sendKeys(changePanNoTo[0]);
		impsPage.get2ndPanInput().sendKeys(changePanNoTo[1]);
		impsPage.get3rdPanInput().sendKeys(changePanNoTo[2]);
		impsPage.get4thPanInput().sendKeys(changePanNoTo[3]);
		impsPage.get5thPanInput().sendKeys(changePanNoTo[4]);
		impsPage.get6thPanInput().sendKeys(changePanNoTo[5]);
		impsPage.get7thPanInput().sendKeys(changePanNoTo[6]);
		impsPage.get8thPanInput().sendKeys(changePanNoTo[7]);
		impsPage.get9thPanInput().sendKeys(changePanNoTo[8]);
		impsPage.getLastPanInput().sendKeys(changePanNoTo[9]);

		// last pan input
		WebElement last_pan_input = impsPage.getLastPanInput();

		// invalid pan no
		String text_for_invalid_pan = pan_letters_varification(driver, impsPage);
		System.out.println("text_for_invalid_pan :" + text_for_invalid_pan);

		// 4th and 5th letter
		String error_txt_for_4th_and_5th_letter = pan_letters_category_validation(categoryChangeTo,
				name_of_proprietorship, copied_name, changePanNoTo, proprietorshipChangeTo);
		System.out.println("error_txt_for_4th_and_5th_letter: " + error_txt_for_4th_and_5th_letter);

		if (text_for_invalid_pan != "") {

			System.out.println("from inside if condition..");

			WebElement pan_error_paragraph = impsPage.getPanErrorParagraph(last_pan_input);
			System.out.println("Assertion for text_for_invalid_pan pass....");

			impsPage.uploadWrongPanImage();
			impsPage.getWrongPanRemark().clear();
			impsPage.getWrongPanRemark().sendKeys("text_for_invalid_pan");

			Assert.assertEquals(text_for_invalid_pan, pan_error_paragraph.getText());
		} else if (error_txt_for_4th_and_5th_letter != "") {

			System.out.println("from inside elseif condition..");
			WebElement pan_error_paragraph = impsPage.getPanErrorParagraph(last_pan_input);
			System.out.println("Assertion for error_txt_for_4th_and_5th_letter pass....");

			impsPage.uploadWrongPanImage();
			impsPage.getWrongPanRemark().clear();
			impsPage.getWrongPanRemark().sendKeys("error_txt_for_4th_and_5th_letter");

			Assert.assertEquals(error_txt_for_4th_and_5th_letter, pan_error_paragraph.getText());
		}

		else {
			System.out.println("correct category & pan selection");
			actionRequiredforPancardApplicable = false;
		}

		Random rand = new Random();
		int amount = Integer.parseInt(excel_data.get(18));
		int random_int_amount = rand.nextInt(amount);
		random_int_amount1 = Integer.toString(random_int_amount);
		WebElement amount_input = impsPage.getAmountInput();

		String amount_converted_in_words;
		amount_input.clear();
		amount_input.sendKeys(random_int_amount1);
		amount_converted_in_words = convertToIndianCurrency(random_int_amount1);

		// Remove extra whitespace if any
		amount_converted_in_words = amount_converted_in_words.replaceAll("\\s+", " ");

		String amount_in_words_txt = impsPage.getAmountInWords(amount_input).getText();

		System.out.println("amount_converted_in_words **** :" + amount_converted_in_words);
		System.out.println("amount_in_workds_txt **** :" + amount_in_words_txt);

		Assert.assertEquals(amount_converted_in_words, amount_in_words_txt);
		impsPage.getNarationInput().clear();
		impsPage.getNarationInput().sendKeys(excel_data.get(19));

		WebElement collector_name_input = impsPage.getCollectorName();

		// collector_name_input.clear();
		// collector_name_input.sendKeys("vk");
		WebElement collecter_phone = impsPage.getCollectorPhone();
		// collecter_phone.click();

		// wait.until(ExpectedConditions.textToBePresentInElement(impsPage.getWrongCollectorName(collector_name_input),"Please
		// enter a valid name"));
		collector_name_input.clear();
		collector_name_input.sendKeys(excel_data.get(20));

		collecter_phone.clear();
		// wrong collector phone no
		collecter_phone.sendKeys("23222322");
		collector_name_input.click();
		wait.until(ExpectedConditions.textToBePresentInElement(impsPage.getWrongCollectorPhone(collecter_phone),
				"Please enter correct phone number"));
		collecter_phone.clear();
		collecter_phone.sendKeys(excel_data.get(21));

		jse.executeScript("window.scrollBy(0,310)");

		if (donationNature.equals("Aajivan Sahyog Nidhi")) {
			// add . at the end as Aajivan Sahyog Nidhi. is in value property
			donationNature = donationNature + '.';
		}
		impsPage.getDonationNature(donationNature).click();
		// Assert.assertTrue(impsPage.getDonationNature2(donationNature).isSelected());

		if (donationNature == "Other" || donationNature.equals("Other")) {
			impsPage.getOtherNatureOfDonation().clear();
			impsPage.getOtherNatureOfDonation().sendKeys(excel_data.get(23));
		}

		if (party_unit == "State" || party_unit.equals("State")) {
			party_unit = "CountryState";
		}

		WebElement party_unit_type = impsPage.getPartyUnit(party_unit);
		party_unit_type.click();
		System.out.println(impsPage.getPartyUnit2(party_unit).isSelected());

		Assert.assertTrue(impsPage.getPartyUnit2(party_unit).isSelected());

		String state_unit_name = excel_data.get(25);

		filterByState = state_unit_name;

		System.out.println("state_unit_name :" + state_unit_name);
		String zila_unit_name = excel_data.get(26);
		String mandal_unit_name = excel_data.get(27);
		System.out.println("zila_unit_name :" + zila_unit_name);
		System.out.println("mandal_unit_name :" + mandal_unit_name);

		if (party_unit == "CountryState") {
			Thread.sleep(2000);

			WebElement selectState = wait
					.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getSelectState()));

			selectState.click();

			WebElement state_unit = wait
					.until(ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenState(state_unit_name)));
			state_unit.click();

			log.info(state_unit_name + " selected");
		} else if (party_unit == "Zila" || party_unit.equals("Zila")) {
			Thread.sleep(2000);
			// select state
			WebElement selectState = wait
					.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getSelectState()));
			selectState.click();

			log.info("clicked on select state for zila unit");
			WebElement state_from_zila = wait
					.until(ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenState(state_unit_name)));

			try {
				state_from_zila.click();
			} catch (StaleElementReferenceException e) {
				state_from_zila = wait.until(
						ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenState(state_unit_name)));
				state_from_zila.click();
			}
			log.info(state_unit_name + " selected for zila unit");

			// select zila
			WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(impsPage.getSelectZila()));
			selectZila.click();
			log.info("clicked on select zila for zila unit");

			WebElement zila_unit = wait
					.until(ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenZila(zila_unit_name)));
			zila_unit.click();

			log.info(zila_unit_name + " selected for zila unit");
		} else {

			Thread.sleep(2000);
			// select state
			WebElement selectState = wait
					.until(ExpectedConditions.visibilityOfElementLocated(impsPage.getSelectState()));
			selectState.click();
			log.info("clicked on select state for mandal unit");

			WebElement state_from_mandal = wait
					.until(ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenState(state_unit_name)));

			try {
				state_from_mandal.click();
			} catch (StaleElementReferenceException e) {
				state_from_mandal = wait.until(
						ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenState(state_unit_name)));
				state_from_mandal.click();
			}
			log.info(state_unit_name + " selected for mandal unit");

			// select zila
			WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(impsPage.getSelectZila()));
			selectZila.click();
			log.info("clicked on select zila for mandal unit");

			WebElement zila_unit = wait
					.until(ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenZila(zila_unit_name)));

			try {
				zila_unit.click();
			} catch (StaleElementReferenceException e) {
				zila_unit = wait
						.until(ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenZila(zila_unit_name)));
				zila_unit.click();
			}
			log.info(zila_unit_name + " selected for mandal unit");

			// select mandal
			WebElement selectMandal = wait.until(ExpectedConditions.elementToBeClickable(impsPage.getSelectMandal()));
			selectMandal.click();
			log.info("clicked on select mandal for mandal unit");

			WebElement mandal_unit = wait
					.until(ExpectedConditions.visibilityOfElementLocated(impsPage.selectGivenMandal(mandal_unit_name)));
			mandal_unit.click();
			log.info(mandal_unit_name + " selected for mandal unit");
		}
		WebElement clickSubmitBtn = driver.findElement(By.xpath("//button[@color='primary']"));
		clickSubmitBtn.click();

		WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));

		String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();

		System.out.println("submitText Edit section:" + submitText);

		Assert.assertTrue(submitText.contains("Record Update successfully"),
				"Record Update successfully not found in edit");

		log.info("IMPS Mode Transaction edited successfully");

		// setting reverseText
		reverseText = excel_data.get(28);
		// setting approveText
		approveText = excel_data.get(30);
		// setting rejectText
		rejectText = excel_data.get(31);
		ngDriver.waitForAngularRequestsToFinish();
	}

	@Test(dependsOnMethods = { "editActionForIMPS" })
	public void reversedActionForIMPS() throws InterruptedException, IOException {

		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		Thread.sleep(5000);
		// clicking on 3 dots for action
		try {
			WebElement row = driver.findElement(
					By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[" + rowNo + "]"));
			WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(row));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
			System.out.println("clicked successfuly on 3 dots");
		} catch (org.openqa.selenium.StaleElementReferenceException e1) {
			WebElement row = driver.findElement(
					By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[" + rowNo + "]"));
			WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(row));
			ele.click();
			System.out.println("exception in clicking 3 dots");
		}

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(3000);

		try {
			System.out.println("from try block for item");
			WebElement actionElement = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Reversed')]")));
			actionElement.click();
		} catch (org.openqa.selenium.StaleElementReferenceException e) {
			System.out.println("from catch block for item");
			WebElement actionElement = wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.xpath("//span[contains(text(),'Reversed')]"))));
			actionElement.click();
		}

		log.info("reversed section");

		driver.findElement(By.xpath("//*[@formcontrolname='remark']")).sendKeys(reverseText);

		// Submit button
		driver.findElement(By.xpath("//button[contains(text(),'Submit')]")).click();

		WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));

		String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();

		System.out.println("submitText for reversed:" + submitText);

		Assert.assertTrue(submitText.contains("Updated Successfully"));
		log.info("IMPS Mode Reversed Successfully");
	}

	@Test(dependsOnMethods = { "reversedActionForIMPS" })
	public void archiveActionForIMPS() throws InterruptedException, IOException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(5000);
		// clicking on 3 dots for action from 1st row
		try {
			WebElement row = driver
					.findElement(By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[1]"));
			WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(row));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
			System.out.println("clicked successfuly on 3 dots");
		} catch (org.openqa.selenium.StaleElementReferenceException e1) {
			WebElement row = driver
					.findElement(By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[1]"));
			WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(row));
			ele.click();
			System.out.println("exception in clicking 3 dots");
		}

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(3000);

		try {
			System.out.println("from try block for item");
			WebElement actionElement = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Archive')]")));
			actionElement.click();
		} catch (org.openqa.selenium.StaleElementReferenceException e) {
			System.out.println("from catch block for item");
			WebElement actionElement = wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.xpath("//span[contains(text(),'Archive')]"))));
			actionElement.click();
		}
		log.info("Archive section");

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(3000);
		// Yes button
		driver.findElement(By.xpath(
				"//button[@class='mat-focus-indicator bg-primary text-white mat-raised-button mat-button-base']"))
				.click();

		WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));

		String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();

		System.out.println("submitText for Archive:" + submitText);

		Assert.assertTrue(submitText.contains("Archive Successfully"));

		log.info("IMPS Transaction Archived Successfully");
	}

	@Test(dependsOnMethods = { "archiveActionForIMPS" })
	public void unarchiveActionForIMPS() throws InterruptedException, IOException {

		System.out.println("&&&& unarchiveActionForIMPS section ----");
		Thread.sleep(5000);
		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		// click on भारतीय जनता पार्टी for home
		driver.findElement(By.cssSelector("[class='header-title-span']")).click();
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		// click on Archived Transaction
		driver.findElement(By.xpath("(//*[@class='action-text'])[3]")).click();
		ngDriver.waitForAngularRequestsToFinish();

		// click on IMPS tab
		WebElement paymentMode = driver.findElement(By.xpath("(//*[@class='tab-text'])[6]"));
		paymentMode.click();
		ngDriver.waitForAngularRequestsToFinish();
		log.info("Unarchive section");

		Random rand = new Random();
		int randomNumForFilter = rand.nextInt(4) + 1;
		String filterElement = "";

		// by name if 1
		if (randomNumForFilter == 1) {
			filterElement = filterByName;
		}
		// by pan no
		else if (randomNumForFilter == 2) {
			filterElement = filterByPan;
		}
		// by Phone no
		else if (randomNumForFilter == 3) {
			filterElement = filterByPhone;
		}
		// by Instrument No.
		else {
			filterElement = filterByInstrumentNo;
		}

		Thread.sleep(2000);
		driver.findElement(By.xpath("(//*[@formcontrolname='query'])")).sendKeys(filterElement);

		driver.findElement(By.xpath("(//*[@class='ng-arrow-wrapper'])[1]")).click();
		Thread.sleep(2000);

		driver.findElement(By.xpath("//span[contains(text(), '" + filterByState + "')]")).click();

		driver.findElement(By.xpath("//span[contains(text(), 'Search')]")).click();

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		// --------------------------

		WebElement downloadBtn = wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//span[contains(text(),'Download')]"))));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", downloadBtn);
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(4000);

		File folder = new File(System.getProperty("user.dir") + "\\downloadTestFolder");

		Thread.sleep(3000);
		// List the files on that folder
		File[] listOfFiles = folder.listFiles();
		boolean found = false;
		String fileName = null;
		for (File listOfFile : listOfFiles) {
			if (listOfFile.isFile()) {
				fileName = listOfFile.getName();
				System.out.println("fileName " + fileName);
				if (fileName.contains("Archived Transaction List")) {
					found = true;
					log.info("Archived file downloaded: " + fileName);
				}
			}
		}
		Assert.assertTrue(found, "Downloaded document is not found");

		File file = new File(System.getProperty("user.dir") + "\\downloadTestFolder\\" + fileName);

		System.out.println("delete file Absolute path :" + file.getAbsolutePath());

		if (file.delete()) {
			System.out.println("file deleted success");
			log.info("Archived file deleted");
		} else {
			System.out.println("file delete fail");
			log.error("Archive file delete fail");
		}

		Thread.sleep(3000);

		// click on Unarchive from 1st row
		driver.findElement(By.xpath("(//*[@mattooltip='Unarchive Transaction'])[1]")).click();

		// Yes button
		driver.findElement(By.xpath(
				"//button[@class='mat-focus-indicator bg-primary text-white mat-raised-button mat-button-base']"))
				.click();

		WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));

		String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();

		System.out.println("submitText for Unarchive:" + submitText);

		Assert.assertTrue(submitText.contains("Unarchive successfully"));

		log.info("IMPS Transaction Unarchive successfully");
	}

	@Test(dependsOnMethods = { "unarchiveActionForIMPS" })

	public void downloadDonationListTestForIMPS() throws InterruptedException, IOException {
		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(5000);

		System.out.println("&&&& downloadDonationListTestForIMPS section ----");
		ArrayList<String> downloadingFields = new ArrayList<String>();

		ArrayList<String> a = new ArrayList<String>();

		DownloadTotalFormDataDriven dd = new DownloadTotalFormDataDriven();

		ArrayList<String> excel_data_for_download_total_form = dd.getData("IMPS", a);

		// click on भारतीय जनता पार्टी for home
		driver.findElement(By.cssSelector("[class='header-title-span']")).click();
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		driver.findElement(By.cssSelector("[class='count']")).click();
		ngDriver.waitForAngularRequestsToFinish();

		// click on IMPS tab
		WebElement paymentMode = driver.findElement(By.xpath("(//*[@class='tab-text'])[6]"));
		paymentMode.click();
		log.info("download Donation ListTest");

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		Random rand = new Random();
		int randomNumForFilter = rand.nextInt(4) + 1;
		String filterElement = "";

		// by name if 1
		if (randomNumForFilter == 1) {
			filterElement = filterByName;
		}
		// by pan no
		else if (randomNumForFilter == 2) {
			filterElement = filterByPan;
		}
		// by Phone no
		else if (randomNumForFilter == 3) {
			filterElement = filterByPhone;
		}
		// by Instrument No.
		else {
			filterElement = filterByInstrumentNo;
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
			System.out.println("downloadingFields 1 :" + downloadingFields.get(0));
			// System.out.println("downloadingFields 2 :" + downloadingFields.get(1));
			System.out.println("fields size :" + fields.size());

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

	@Test(dependsOnMethods = { "downloadDonationListTestForIMPS" })

	public void actionRequiredForPanCardIMPS() throws InterruptedException, IOException {
		// if pan card was correct in edit then following block will be executed
		if (actionRequiredforPancardApplicable) {

			System.out.println("&&& actionRequiredForPanCardIMPS section ----");

			// click on भारतीय जनता पार्टी for home
			driver.findElement(By.cssSelector("[class='header-title-span']")).click();
			ngDriver.waitForAngularRequestsToFinish();
			Thread.sleep(2000);

			// click on Action Required for Pan card
			driver.findElement(By.xpath("(//*[@class='action-text'])[2]")).click();
			ngDriver.waitForAngularRequestsToFinish();

			// explicit wait
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

			wait.until(ExpectedConditions.visibilityOf(
					driver.findElement(By.xpath("//h2[contains(text(),'Action Required for PanCard')]"))));
			log.info("action Required For PanCard section");

			Random rand = new Random();
			int randomNumForFilter = rand.nextInt(4) + 1;
			String filterElement = "";

			// by name if 1
			if (randomNumForFilter == 1) {
				filterElement = filterByName;
			}
			// by pan no
			else if (randomNumForFilter == 2) {
				filterElement = filterByPan;
			}
			// by Phone no
			else if (randomNumForFilter == 3) {
				filterElement = filterByPhone;
			}
			// by Instrument No.
			else {
				filterElement = filterByInstrumentNo;
			}

			Thread.sleep(3000);
			driver.findElement(By.xpath("(//*[@type='text'])")).sendKeys(filterElement);

			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Search')]")))
					.click();

			ngDriver.waitForAngularRequestsToFinish();
			Thread.sleep(2000);

			int[] countArr = countMatchingActionRequiredForPanCard(driver);

			int allCountNumber = countArr[0];
			int invalidCountNumber = countArr[1];
			int approvedCountNumber = countArr[2];
			int rejectedCountNumber = countArr[3];

			int addOtherThanAll = invalidCountNumber + approvedCountNumber + rejectedCountNumber;
			System.out.println("all count :" + allCountNumber);
			System.out.println("all count after adding :" + addOtherThanAll);

			Assert.assertEquals(allCountNumber, addOtherThanAll, "Count is mismatching");
			log.info("transaction counts are matching before Approved and Rejected");

			// click on invalid tab
			driver.findElement(By.xpath("(//*[@class='tab-text1'])[3]")).click();
			ngDriver.waitForAngularRequestsToFinish();
			Thread.sleep(2000);

			// copy status
			String status = driver.findElement(By
					.xpath("(//*[@class='mat-cell cdk-cell cdk-column-status mat-column-status ng-star-inserted'])[1]"))
					.getAccessibleName();

			System.out.println("status :" + status);
			Assert.assertEquals(status, "Invalid", "Status is not Invalid");

			// click on Action
			driver.findElement(By
					.xpath("(//*[@class='mat-cell cdk-cell cdk-column-action mat-column-action ng-star-inserted'])[1]"))
					.click();
			// randomly generate true for approve and false for reject
			boolean approve = Math.random() < 0.5;

			WebElement remarkInput = driver.findElement(By.tagName("textarea"));
			remarkInput.clear();
			String remarkText = "";
			if (approve) {
				System.out.println(" go for approve");
				remarkText = approveText;
				remarkInput.sendKeys(remarkText);
				driver.findElement(By.className("approve_btn")).click();

				log.info("approve selected");
			} else {
				System.out.println("go for reject");
				remarkText = rejectText;
				remarkInput.sendKeys(remarkText);
				driver.findElement(By.className("reject_btn")).click();

				log.info("reject selected");
			}

			System.out.println("remarkText :" + remarkText);
			WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));

			String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();

			System.out.println("submitText for Unarchive:" + submitText);

			Assert.assertTrue(submitText.contains("Updated Successfully"));
			log.info("Updated Successfully in Pan Card Action");

			ngDriver.waitForAngularRequestsToFinish();
			Thread.sleep(5000);

			if (approve) {
				// click on Approved tab
				driver.findElement(By.xpath("(//*[@class='tab-text1'])[5]")).click();
			} else {
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

			System.out.println("allCountNumberAfter :" + allCountNumberAfter);
			System.out.println("invalidCountNumberAfter :" + invalidCountNumberAfter);
			System.out.println("approvedCountNumberAfter :" + approvedCountNumberAfter);
			System.out.println("rejectedCountNumberAfter :" + rejectedCountNumberAfter);

			// copy status
			String statusAfter = driver.findElement(By
					.xpath("(//*[@class='mat-cell cdk-cell cdk-column-status mat-column-status ng-star-inserted'])[1]"))
					.getAccessibleName();

			// copy Accountant Remark
			String copiedAccountantReamrk = driver.findElement(By.xpath(
					"(//*[@class='mat-cell cdk-cell mat-tooltip-trigger cursor-pointer cdk-column-accountant_remark mat-column-accountant_remark ng-star-inserted'])[1]"))
					.getAccessibleName();

			System.out.println("copiedAccountantReamrk :" + copiedAccountantReamrk);

			if (approve) {
				Assert.assertEquals(approvedCountNumberAfter, approvedCountNumber + 1,
						"Approved count mismatch after approval");

				Assert.assertEquals(statusAfter, "Approved", "Not fount Approved status");
				log.info("Approved count matching");
			} else {
				Assert.assertEquals(rejectedCountNumberAfter, rejectedCountNumber + 1,
						"Rejected count mismatch after rejection");
				Assert.assertEquals(statusAfter, "Rejected", "Not fount Rejected status");
				log.info("Rejected count matching");
			}

			// invalid count will be decreased by 1 after approve or reject action
			Assert.assertEquals(invalidCountNumberAfter, invalidCountNumber - 1,
					"Invalid count mismatch after approval or rejection");

			Assert.assertEquals(copiedAccountantReamrk, remarkText);

			log.info("Invalid count matching");
		}

	}

	@AfterClass
	public void terminate() {
		driver.close();
	}

	private static String pan_letters_category_validation(String category_value, String name_of_proprietorship,
			String copied_name, String[] pan, String proprietorshipChangeTo) {

		String mismatch_error = "";
		String fourth_letter = pan[3];
		String fifth_letter = pan[4];

		fourth_letter = fourth_letter.toUpperCase();
		fifth_letter = fifth_letter.toUpperCase();

		String donor_first_name_letter = copied_name.substring(0, 1);

		if (category_value == "individual" || category_value.equals("individual")) {
			if (proprietorshipChangeTo == "yes" || proprietorshipChangeTo.equals("yes")) {
				String[] proprietorship_name_arr = name_of_proprietorship.split(" ");
				String proprietorship_surname = proprietorship_name_arr[1];

				String proprietorship_surname_1st_letter = proprietorship_surname.substring(0, 1).toUpperCase();

				// check 5th letter of pan with proprietorship_surname
				System.out.println("proprietorship_surname 1st letter :" + proprietorship_surname_1st_letter);
				System.out.println("fifth_letter :" + fifth_letter);

				if (!fifth_letter.equals(proprietorship_surname_1st_letter)) {
					System.out.println("from inside proprietorship_surname ");
					mismatch_error = "Your name does not match your pan card number";
				}
			} else {

				String[] donor_name_arr = copied_name.split(" ");
				String donor_surname = donor_name_arr[1];
				String donor_surname_1st_letter = donor_surname.substring(0, 1);
				System.out.println("donor_surname_1st_letter for individual :" + donor_surname_1st_letter);
				System.out.println("fifth_letter of pan :" + fifth_letter);
				if (!fifth_letter.equals(donor_surname_1st_letter)) {

					System.out.println("seems like it is selected no..");
					mismatch_error = "Your name does not match your pan card number";
				}
			}

			System.out.println("*******4th letter in entered pan $$:" + fourth_letter);

			if (!fourth_letter.equals("P")) {

				System.out.println("*******4th letter is not matching for individual and pan");
				mismatch_error = "Your category selection does not match your pan card number";
			}

		} else if (category_value == "huf" || category_value.equals("huf")) {

			if (!fifth_letter.equals(donor_first_name_letter)) {
				mismatch_error = "Your name does not match your pan card number";
			}
			if (!fourth_letter.equals("H")) {
				mismatch_error = "Your category selection does not match your pan card number";
			}

			System.out.println("4th letter for huf :" + fourth_letter);
		}

		else if (category_value == "partnership" || category_value.equals("partnership")) {

			if (!fifth_letter.equals(donor_first_name_letter)) {
				mismatch_error = "Your name does not match your pan card number";
			}
			if (!fourth_letter.equals("F")) {
				mismatch_error = "Your category selection does not match your pan card number";
			}
			System.out.println("4th letter for partneship inside partnership :" + fourth_letter);
		}

		else if (category_value == "trust" || category_value.equals("trust")) {

			if (!fifth_letter.equals(donor_first_name_letter)) {
				mismatch_error = "Your name does not match your pan card number";
			}

			if (!fourth_letter.equals("T")) {
				mismatch_error = "Your category selection does not match your pan card number";
			}
			System.out.println("4th letter for trust :" + fourth_letter);

		} else if (category_value == "corporation" || category_value.equals("corporation")) {

			if (!fifth_letter.equals(donor_first_name_letter)) {
				mismatch_error = "Your name does not match your pan card number";
			}

			if (!fourth_letter.equals("C")) {
				mismatch_error = "Your category selection does not match your pan card number";
			}
			System.out.println("4th letter for corporation :" + fourth_letter);
		} else if (category_value == "others" || category_value.equals("others")) {

			if (!fifth_letter.equals(donor_first_name_letter)) {
				mismatch_error = "Your name does not match your pan card number";
			}

			if (!fourth_letter.equals("A") && !fourth_letter.equals("B") && !fourth_letter.equals("J")) {

				System.out.println("###fourth_letter### " + fourth_letter);
				mismatch_error = "Your category selection does not match your pan card number";
			}
			System.out.println("4th letter for others :" + fourth_letter);
		}

		return mismatch_error;
	}

	private static String pan_letters_varification(WebDriver driver, IMPS_ModePage impsPage) {

		List<WebElement> elements = impsPage.getPanElements();
		String whole_pan = "";
		String flag_error_txt = "";

		for (int i = 0; i < elements.size(); i++) {
			whole_pan = whole_pan + elements.get(i).getAttribute("value");
			// Check pan letters are converted to UpperCase or not
			Assert.assertEquals(elements.get(i).getAttribute("value").toUpperCase(),
					elements.get(i).getAttribute("value"));

			// check Numeric and Alphabet in pan card
			String str1 = elements.get(i).getAttribute("value");
			if (i > 4 && i < 9) {

				try {
					Integer in = Integer.valueOf(str1);
					System.out.println(in.getClass().getSimpleName());
				} catch (NumberFormatException e) {
					flag_error_txt = "Please enter a valid pan card number";
					System.out.println("There should be digit at position:" + i);
				}

			} else {
				if (str1.matches(".*[0-9].*")) {
					flag_error_txt = "Please enter a valid pan card number";
					System.out.println("Alphabet is required at position:" + i);
				}
			}

		}
		System.out.println("PanCard No :" + whole_pan);

		return flag_error_txt;

	}

	// method for -> amount in words
	private static String convertToIndianCurrency(String amount) {
		BigDecimal bd = new BigDecimal(amount);
		long number = bd.longValue();
		long no = bd.longValue();
		int decimal = (int) (bd.remainder(BigDecimal.ONE).doubleValue() * 100);
		int digits_length = String.valueOf(no).length();
		int i = 0;
		ArrayList<String> str = new ArrayList<>();
		HashMap<Integer, String> words = new HashMap<>();
		words.put(0, "");
		words.put(1, "One");
		words.put(2, "Two");
		words.put(3, "Three");
		words.put(4, "Four");
		words.put(5, "Five");
		words.put(6, "Six");
		words.put(7, "Seven");
		words.put(8, "Eight");
		words.put(9, "Nine");
		words.put(10, "Ten");
		words.put(11, "Eleven");
		words.put(12, "Twelve");
		words.put(13, "Thirteen");
		words.put(14, "Fourteen");
		words.put(15, "Fifteen");
		words.put(16, "Sixteen");
		words.put(17, "Seventeen");
		words.put(18, "Eighteen");
		words.put(19, "Nineteen");
		words.put(20, "Twenty");
		words.put(30, "Thirty");
		words.put(40, "Forty");
		words.put(50, "Fifty");
		words.put(60, "Sixty");
		words.put(70, "Seventy");
		words.put(80, "Eighty");
		words.put(90, "Ninety");
		String digits[] = { "", "Hundred", "Thousand", "Lakh", "Crore" };
		while (i < digits_length) {
			int divider = (i == 2) ? 10 : 100;
			number = no % divider;
			no = no / divider;
			i += divider == 10 ? 1 : 2;
			if (number > 0) {
				int counter = str.size();
				String plural = (counter > 0 && number > 9) ? "" : "";
				String tmp = (number < 21) ? words.get(Integer.valueOf((int) number)) + " " + digits[counter] + plural
						: words.get(Integer.valueOf((int) Math.floor(number / 10) * 10)) + " "
								+ words.get(Integer.valueOf((int) (number % 10))) + " " + digits[counter] + plural;
				str.add(tmp);
			} else {
				str.add("");
			}
		}

		Collections.reverse(str);
		String Rupees = String.join(" ", str).trim();

		String paise = (decimal) > 0
				? " " + words.get(Integer.valueOf((int) (decimal - decimal % 10))) + " "
						+ words.get(Integer.valueOf((int) (decimal % 10)))
				: "";

		if (paise == "") {
			return Rupees + " Rupees Only";
		} else {
			return Rupees + " Rupees And" + paise + " Paise Only";
		}
	}

	// -----------------------Extra methods for indian donation
	// form-----------------------------

	private static void for_yes_no_proprietorship(WebDriver driver, String[] pan_no, String name_of_proprietorship,
			String copied_name, String category, IMPS_ModePage impsPage) {

		for (int i = 0; i < 5; i++) {

			if (i == 0) {
				pan_no[0] = "2";
			} else if (i == 1) {
				pan_no[0] = "F";// correcting 1st pan letter
				pan_no[3] = "S";// wrong 4th pan letter
			} else if (i == 2) {
				pan_no[4] = "z"; // wrong 5th pan letter ,now both 4th & 5th character are wrong
			} else if (i == 3) {
				pan_no[3] = "P"; // correcting 4th pan letter
			} else {

				if (category.equals("individual") && !name_of_proprietorship.equals("")) {
					String[] name_of_proprietorship_arr = name_of_proprietorship.split(" ");
					String proprietorship_surname_1st_letter = name_of_proprietorship_arr[1].substring(0, 1)
							.toUpperCase();

					pan_no[4] = proprietorship_surname_1st_letter; // correcting 5th letter, now every pan characters
																	// are correct
				}
				// No
				else {
					String[] copied_name_arr = copied_name.split(" ");
					String copied_name_surname_1st_letter = copied_name_arr[1].substring(0, 1).toUpperCase();

					pan_no[4] = copied_name_surname_1st_letter; // correcting 5th letter, now every pan characters are
																// correct
				}

			}

			System.out.println("*************** Pan going to fill is ************************:");

			for (int j = 0; j < pan_no.length - 1; j++) {
				System.out.print(pan_no[j]);
			}
			System.out.println();

			fill_pan_number(driver, pan_no, impsPage);

			// last pan input
			WebElement last_pan_input = impsPage.getLastPanInput();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// invalid pan no
			String text_for_invalid_pan = pan_letters_varification(driver, impsPage);

			System.out.println("text_for_invalid_pan :" + text_for_invalid_pan);

			// 4th and 5th letter
			String error_txt_for_4th_and_5th_letter = pan_letters_category_validation(driver, category,
					name_of_proprietorship, copied_name, impsPage);
			System.out.println("error_txt_for_4th_and_5th_letter:" + error_txt_for_4th_and_5th_letter);

			if (text_for_invalid_pan != "") {
				WebElement pan_error_paragraph = impsPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for text_for_invalid_pan pass....");
				Assert.assertEquals(text_for_invalid_pan, pan_error_paragraph.getText());
			} else if (error_txt_for_4th_and_5th_letter != "") {

				WebElement pan_error_paragraph = impsPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for error_txt_for_4th_and_5th_letter pass....");
				Assert.assertEquals(error_txt_for_4th_and_5th_letter, pan_error_paragraph.getText());
			}

			else {
				System.out.println("correct category & pan selection");
			}

		}

	}

	private static void fill_pan_number(WebDriver driver, String[] pan, IMPS_ModePage impsPage) {

		impsPage.get1stPanInput().clear();
		impsPage.get1stPanInput().sendKeys(pan[0]);
		impsPage.get2ndPanInput().sendKeys(pan[1]);
		impsPage.get3rdPanInput().sendKeys(pan[2]);
		impsPage.get4thPanInput().sendKeys(pan[3]);
		impsPage.get5thPanInput().sendKeys(pan[4]);
		impsPage.get6thPanInput().sendKeys(pan[5]);
		impsPage.get7thPanInput().sendKeys(pan[6]);
		impsPage.get8thPanInput().sendKeys(pan[7]);
		impsPage.get9thPanInput().sendKeys(pan[8]);
		impsPage.getLastPanInput().sendKeys(pan[9]);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private static String pan_letters_category_validation(WebDriver driver, String category_value,
			String name_of_proprietorship, String copied_name, IMPS_ModePage impsPage) {

		List<WebElement> pan_elements = impsPage.getPanElements();
		String mismatch_error = "";
		String fourth_letter = pan_elements.get(3).getAttribute("value");
		String fifth_letter = pan_elements.get(4).getAttribute("value");

		String donor_first_name_letter = copied_name.substring(0, 1);

		if (category_value == "individual") {
			if (name_of_proprietorship != "") {
				String[] proprietorship_name_arr = name_of_proprietorship.split(" ");
				String proprietorship_surname = proprietorship_name_arr[1];

				String proprietorship_surname_1st_letter = proprietorship_surname.substring(0, 1).toUpperCase();

				// check 5th letter of pan with proprietorship_surname
				System.out.println("proprietorship_surname 1st letter :" + proprietorship_surname_1st_letter);

				if (!fifth_letter.equals(proprietorship_surname_1st_letter)) {
					System.out.println("from inside proprietorship_surname");
					mismatch_error = "Your name does not match your pan card number";
				}
			} else {

				String[] donor_name_arr = copied_name.split(" ");
				String donor_surname = donor_name_arr[1];
				String donor_surname_1st_letter = donor_surname.substring(0, 1);
				System.out.println("donor_surname_1st_letter for individual :" + donor_surname_1st_letter);
				System.out.println("fifth_letter of pan :" + fifth_letter);
				if (!fifth_letter.equals(donor_surname_1st_letter)) {

					System.out.println("seems like it is selected no..");
					mismatch_error = "Your name does not match your pan card number";
				}
			}

			System.out.println("*******4th letter in entered pan $$:" + fourth_letter);

			if (!fourth_letter.equals("P")) {

				System.out.println("*******4th letter is not matching for individual and pan");
				mismatch_error = "Your category selection does not match your pan card number";
			}

		} else if (category_value == "huf") {

			if (!fifth_letter.equals(donor_first_name_letter)) {
				mismatch_error = "Your name does not match your pan card number";
			}
			if (!fourth_letter.equals("H")) {
				mismatch_error = "Your category selection does not match your pan card number";
			}

			System.out.println("4th letter for huf :" + fourth_letter);
		}

		else if (category_value == "partnership") {

			if (!fifth_letter.equals(donor_first_name_letter)) {
				mismatch_error = "Your name does not match your pan card number";
			}
			if (!fourth_letter.equals("F")) {
				mismatch_error = "Your category selection does not match your pan card number";
			}
			System.out.println("4th letter for partneship inside partnership :" + fourth_letter);
		}

		else if (category_value == "trust") {

			if (!fifth_letter.equals(donor_first_name_letter)) {
				mismatch_error = "Your name does not match your pan card number";
			}

			if (!fourth_letter.equals("T")) {
				mismatch_error = "Your category selection does not match your pan card number";
			}
			System.out.println("4th letter for trust :" + fourth_letter);

		} else if (category_value == "corporation") {

			if (!fifth_letter.equals(donor_first_name_letter)) {
				mismatch_error = "Your name does not match your pan card number";
			}

			if (!fourth_letter.equals("C")) {
				mismatch_error = "Your category selection does not match your pan card number";
			}
			System.out.println("4th letter for corporation :" + fourth_letter);
		} else if (category_value == "others") {

			if (!fifth_letter.equals(donor_first_name_letter)) {
				mismatch_error = "Your name does not match your pan card number";
			}

			if (!fourth_letter.equals("A") && !fourth_letter.equals("B") && !fourth_letter.equals("J")) {

				System.out.println("###fourth_letter### " + fourth_letter);
				mismatch_error = "Your category selection does not match your pan card number";
			}
			System.out.println("4th letter for others :" + fourth_letter);
		}

		return mismatch_error;
	}

	private static void other_than_individual(WebDriver driver, String[] pan_no, String name_of_proprietorship,
			String copied_name, String category, IMPS_ModePage impsPage) {

		String copied_name_1st_letter = copied_name.substring(0, 1);

		for (int i = 0; i < 5; i++) {

			if (i == 0) {
				pan_no[0] = "2";
			} else if (i == 1) {
				pan_no[0] = "F";// correcting 1st pan letter
				pan_no[3] = "S";// wrong 4th pan letter
			} else if (i == 2) {
				// create wrong 5th letter
				if (copied_name_1st_letter.equals("Z")) {
					pan_no[4] = "V";
				} else {
					pan_no[4] = "z"; // wrong 5th pan letter ,now both 4th & 5th character are wrong
				}

			} else if (i == 3) {
				// correcting 4th pan letter

				if (category == "others") {
					pan_no[3] = "A";
				} else if (category == "corporation") {
					pan_no[3] = "C";
				} else if (category == "trust") {
					pan_no[3] = "T";
				} else if (category == "partnership") {
					pan_no[3] = "F";
				} else {
					// category = huf as individual category is separately done
					pan_no[3] = "H";
				}
			} else {
				// correcting 5th letter, now every pan characters are correct
				pan_no[4] = copied_name_1st_letter;

			}

			System.out.println("*************** Pan going to fill is ************************:");

			for (int j = 0; j < pan_no.length - 1; j++) {
				System.out.print(pan_no[j]);
			}
			System.out.println();

			fill_pan_number(driver, pan_no, impsPage);

			// last pan input
			WebElement last_pan_input = impsPage.getLastPanInput();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// invalid pan no
			String text_for_invalid_pan = pan_letters_varification(driver, impsPage);

			System.out.println("text_for_invalid_pan :" + text_for_invalid_pan);

			// 4th and 5th letter
			String error_txt_for_4th_and_5th_letter = pan_letters_category_validation(driver, category,
					name_of_proprietorship, copied_name, impsPage);
			System.out.println("error_txt_for_4th_and_5th_letter:" + error_txt_for_4th_and_5th_letter);

			if (text_for_invalid_pan != "") {

				WebElement pan_error_paragraph = impsPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for text_for_invalid_pan pass....");

				impsPage.uploadWrongPanImage();
				impsPage.getWrongPanRemark().clear();
				impsPage.getWrongPanRemark().sendKeys("text_for_invalid_pan");

				Assert.assertEquals(text_for_invalid_pan, pan_error_paragraph.getText());
			} else if (error_txt_for_4th_and_5th_letter != "") {
				WebElement pan_error_paragraph = impsPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for error_txt_for_4th_and_5th_letter pass....");

				impsPage.uploadWrongPanImage();
				impsPage.getWrongPanRemark().clear();
				impsPage.getWrongPanRemark().sendKeys("error_txt_for_4th_and_5th_letter");

				Assert.assertEquals(error_txt_for_4th_and_5th_letter, pan_error_paragraph.getText());
			}

			else {
				System.out.println("correct category & pan selection");
			}
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
	}

	private static int[] countMatchingActionRequiredForPanCard(WebDriver driver) {
		// get count no for All
		String allCount = driver.findElement(By.xpath("(//*[@class='tab-text1'])[2]")).getText();

		// get count no for Invalid
		String InvalidCount = driver.findElement(By.xpath("(//*[@class='tab-text1'])[4]")).getText();

		// get count no for Approved
		String approvedCount = driver.findElement(By.xpath("(//*[@class='tab-text1'])[6]")).getText();

		// get count no for Rejected
		String rejectedCount = driver.findElement(By.xpath("(//*[@class='tab-text1'])[8]")).getText();

		int allCountNumber = Integer.parseInt(allCount);
		int invalidCountNumber = Integer.parseInt(InvalidCount);
		int approvedCountNumber = Integer.parseInt(approvedCount);
		int rejectedCountNumber = Integer.parseInt(rejectedCount);

		int count[] = { allCountNumber, invalidCountNumber, approvedCountNumber, rejectedCountNumber };

		return count;

	}
}
