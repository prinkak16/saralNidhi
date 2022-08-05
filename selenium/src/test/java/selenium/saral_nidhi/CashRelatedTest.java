package selenium.saral_nidhi;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import pageObjects.LandingPage;
import resources.Base;

public class CashRelatedTest extends Base {
	public WebDriver driver;
	public NgWebDriver ngDriver;
	public static Logger log = LogManager.getLogger(CashRelatedTest.class);

	int rowNo = 1;
	String reverseText;

	// ----------------------------- Indian Donation Form
	@Test
	public void basePageNavigation(ITestContext context) throws IOException, InterruptedException {
		driver = initializeDriver();

		ngDriver = new NgWebDriver((JavascriptExecutor) driver);

		driver.get(url);

		 LocalStorage storage = ((WebStorage) driver).getLocalStorage();

		 new SetLocalStorage(storage,driver,context);

		ArrayList<String> a = new ArrayList<String>();

		DataDriven dd = new DataDriven();
		ArrayList<String> excel_data = dd.getData("Cash_ModeTest", a);

		
		Cash_ModePage cashPage = new Cash_ModePage(driver);

		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        /*
        LandingPage lp = new LandingPage(driver);
		lp.login_email().sendKeys(excel_data.get(1));
		lp.login_password().sendKeys(excel_data.get(2));

		WebElement sendOTP = wait.until(ExpectedConditions.elementToBeClickable(lp.send_OTP()));
		sendOTP.click();

		WebElement enterOTP = wait.until(ExpectedConditions.visibilityOfElementLocated(lp.enter_otp()));
		enterOTP.sendKeys(excel_data.get(3));

		WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(lp.login_btn()));
		loginButton.click();
        */
		log.info("Login successfully from Cash_ModeTest");

		WebElement heading1 = wait.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getFirstHeading()));
		Assert.assertEquals(heading1.getText(), "Nidhi Collection");

		cashPage.getIndianDonationForm().click();
		ngDriver.waitForAngularRequestsToFinish();
		WebElement paymentModeOption = wait
				.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getCashPaymentMode()));

		// cash should not be selected
		Assert.assertFalse(cashPage.getCashModeValue().isSelected());

		paymentModeOption.click();
		ngDriver.waitForAngularRequestsToFinish();

		// cash should be selected
		Assert.assertTrue(cashPage.getCashModeValue().isSelected());
		log.info("Cash Mode of Payment selected");
		LocalDate now = LocalDate.now();

		int no_of_back_days = Integer.parseInt(excel_data.get(5));
		LocalDate transactionDate_generate = now.minusDays(no_of_back_days);

		DateTimeFormatter dateFormating = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String transaction_Date = transactionDate_generate.format(dateFormating);

		cashPage.getTransactionDate().sendKeys(transaction_Date);

		String donor_name = excel_data.get(8);
		String donor_phone = excel_data.get(9);
		String donor_email = excel_data.get(10);

		cashPage.getDonorName().sendKeys(donor_name);

		String copied_name = cashPage.getDonorName().getAttribute("value");

		System.out.println("copied_name:" + copied_name);

		// Check name is converted to UpperCase or not
		Assert.assertEquals(donor_name.toUpperCase(), copied_name);
		// type wrong phone no
		cashPage.getDonorPhoneNumber().sendKeys("23222322");

		cashPage.getDonorEmail().click();

		Assert.assertEquals("Please enter a valid phone number", cashPage.getInvalidDonorPhone().getText());

		cashPage.getDonorPhoneNumber().clear();
		cashPage.getDonorPhoneNumber().sendKeys(donor_phone);

		// Enter wrong email
		cashPage.getDonorEmail().sendKeys("abc.com");

		cashPage.getHouse().click();

		Assert.assertEquals("Enter a valid email", cashPage.getInvalidDonorEmail().getText());

		cashPage.getDonorEmail().clear();
		cashPage.getDonorEmail().sendKeys(donor_email);
		cashPage.getHouse().sendKeys(excel_data.get(11));
		cashPage.getLocality().sendKeys(excel_data.get(12));
		cashPage.getPinCode().sendKeys(excel_data.get(13));

		boolean district_bool = wait
				.until(ExpectedConditions.textToBePresentInElementValue(cashPage.getDistrict(), excel_data.get(14)));
		String copied_district = null;

		if (district_bool) {
			copied_district = cashPage.getDistrict().getAttribute("value");
		} else {
			log.error("district name didn't come till 30 sec");
		}

		System.out.println("copied_district :" + copied_district);

		boolean state_bool = wait
				.until(ExpectedConditions.textToBePresentInElement(cashPage.getState(), excel_data.get(15)));

		String copied_state = null;
		if (state_bool) {
			copied_state = cashPage.getState().getText();
		} else {
			log.error("state name didn't come till 30 sec");
		}
		System.out.println("copied_state :" + copied_state);

		Assert.assertEquals(copied_district, excel_data.get(14));
		Assert.assertEquals(copied_state, excel_data.get(15));

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,310)");

		// ------------------------------------------------------------------------------------------------------------------------------

		// String[] arr_category = { "individual", "huf", "partnership", "trust",
		// "corporation", "others" };
		String[] arr_category = { "others" };
		for (String category : arr_category) {

			WebElement categoryElement = cashPage.selectCategory(category);
			categoryElement.click();

			Thread.sleep(1000);

			String[] pan_no = { "F", "O", "O", "P", "K", "3", "1", "3", "4", "k" };

			String name_of_proprietorship = "";
			if (category == "individual") {

				cashPage.getNoProprietorship(categoryElement).click();

				for_yes_no_proprietorship(driver, pan_no, name_of_proprietorship, copied_name, category, cashPage);

				// Now for Yes
				// Thread.sleep(1000);

				System.out.println("-------------------- For Yes-------------------------");

				String proprietorship_txt = cashPage.getProprietorship(categoryElement).getText();
				Assert.assertEquals(proprietorship_txt, "Is it a proprietorship? *");

				WebElement proprietorship_yes = cashPage.getYesProprietorship(categoryElement);
				proprietorship_yes.click();

				// Thread.sleep(1000);

				boolean b1 = cashPage.getYesProprietorshipSelect().isSelected();

				System.out.println("proprietorship_yes is selected or not --->:" + b1);
				Assert.assertTrue(b1);

				WebElement Proprietorship_txt2 = cashPage.getTextAfterYesProprietorship(proprietorship_yes);

				System.out.println(Proprietorship_txt2.getText());
				Assert.assertEquals(Proprietorship_txt2.getText(), "Write the name of the Proprietorship");

				name_of_proprietorship = excel_data.get(16);
				// type Name of proprietorship
				cashPage.getProprietorshipName().sendKeys(name_of_proprietorship);

				for_yes_no_proprietorship(driver, pan_no, name_of_proprietorship, copied_name, category, cashPage);

				// As individual category end so make --> name_of_proprietorship=""
				name_of_proprietorship = "";
			} else if (category == "others") {

				WebElement other_category = wait
						.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getOtherCategory()));

				boolean visible_other_category = other_category.isDisplayed();
				Assert.assertTrue(visible_other_category);
				other_category.sendKeys(excel_data.get(25));

				other_than_individual(driver, pan_no, name_of_proprietorship, copied_name, category, cashPage);
			}

			else {
				other_than_individual(driver, pan_no, name_of_proprietorship, copied_name, category, cashPage);
			}
		}
		// ------------------------------------------------------------------------------------------------------------

		Random rand = new Random();
		int amount = Integer.parseInt(excel_data.get(17));
		int random_int_amount = rand.nextInt(amount);
		String random_int_amount1 = Integer.toString(random_int_amount);

		WebElement amount_input = cashPage.getAmountInput();

		String amount_converted_in_words;
		amount_input.sendKeys(random_int_amount1);
		amount_converted_in_words = convertToIndianCurrency(random_int_amount1);

		// Remove extra whitespace if any
		amount_converted_in_words = amount_converted_in_words.replaceAll("\\s+", " ");

		String amount_in_words_txt = cashPage.getAmountInWords(amount_input).getText();

		System.out.println("amount_converted_in_words **** :" + amount_converted_in_words);
		System.out.println("amount_in_workds_txt **** :" + amount_in_words_txt);

		Assert.assertEquals(amount_converted_in_words, amount_in_words_txt);

		cashPage.getNarationInput().sendKeys(excel_data.get(18));
		WebElement collector_name_input = cashPage.getCollectorName();

		collector_name_input.sendKeys("vk");
		WebElement collecter_phone = cashPage.getCollectorPhone();
		collecter_phone.click();

		wait.until(ExpectedConditions.textToBePresentInElement(cashPage.getWrongCollectorName(collector_name_input),
				"Please enter a valid name"));
		// String collector_name_error =
		// cashPage.getWrongCollectorName(collector_name_input).getText();
		// Assert.assertEquals(collector_name_error, "Please enter a valid name");
		collector_name_input.clear();
		collector_name_input.sendKeys(excel_data.get(19));

		// wrong collector phone no
		collecter_phone.sendKeys("23222322");
		wait.until(ExpectedConditions.textToBePresentInElement(cashPage.getWrongCollectorPhone(collecter_phone),
				"Please enter correct phone number"));
		// String collector_phone_error =
		// cashPage.getWrongCollectorPhone(collecter_phone).getText();
		// Assert.assertEquals(collector_phone_error, "Please enter correct phone
		// number");
		collecter_phone.clear();

		collecter_phone.sendKeys(excel_data.get(20));
		jse.executeScript("window.scrollBy(0,310)");
		// Nature of donation
		String[] donation_nature = { "Voluntary Contribution", "Aajivan Sahyog Nidhi.", "Other" };

		for (String donation : donation_nature) {

			cashPage.getDonationNature(donation).click();
			Thread.sleep(1000);
			System.out.println("nature of donation :--->" + cashPage.getDonationNature2(donation).isSelected());

			Assert.assertTrue(cashPage.getDonationNature2(donation).isSelected());

			if (donation == "Other") {
				cashPage.getOtherNatureOfDonation().sendKeys(excel_data.get(21));
			}

		}

		JavascriptExecutor jse1 = (JavascriptExecutor) driver;
		jse1.executeScript("window.scrollBy(0,410)");
		// party unit
		String[] party_unit_arr = { "CountryState", "Zila", "Mandal" };

		for (String party_unit : party_unit_arr) {

			WebElement party_unit_type = cashPage.getPartyUnit(party_unit);
			party_unit_type.click();
			System.out.println(cashPage.getPartyUnit2(party_unit).isSelected());

			Assert.assertTrue(cashPage.getPartyUnit2(party_unit).isSelected());

			boolean state_exist = cashPage.isElementPresent(driver, "state").isDisplayed();

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
						.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getSelectState()));

				selectState.click();

				WebElement state_unit = wait.until(
						ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenState(state_unit_name)));
				state_unit.click();

			} else if (party_unit == "Zila") {

				zila_exist = cashPage.isElementPresent(driver, "zila").isDisplayed();
				System.out.println("zila_exist after click on zila:" + zila_exist);
				Assert.assertEquals(zila_exist, true);

				// select state
				WebElement selectState = wait
						.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getSelectState()));
				selectState.click();

				WebElement state_from_zila = wait.until(
						ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenState(state_unit_name)));

				try {
					state_from_zila.click();
				} catch (StaleElementReferenceException e) {
					state_from_zila = wait.until(
							ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenState(state_unit_name)));
					state_from_zila.click();
				}

				// select zila
				WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(cashPage.getSelectZila()));
				selectZila.click();

				WebElement zila_unit = wait
						.until(ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenZila(zila_unit_name)));
				zila_unit.click();

			} else {

				zila_exist = cashPage.isElementPresent(driver, "zila").isDisplayed();
				System.out.println("zila_exist after click on zila:" + zila_exist);
				Assert.assertEquals(zila_exist, true);
				// select state
				WebElement selectState = wait
						.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getSelectState()));
				selectState.click();

				WebElement state_from_mandal = wait.until(
						ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenState(state_unit_name)));

				try {
					state_from_mandal.click();
				} catch (StaleElementReferenceException e) {
					state_from_mandal = wait.until(
							ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenState(state_unit_name)));
					state_from_mandal.click();
				}

				// select zila
				WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(cashPage.getSelectZila()));
				selectZila.click();

				WebElement zila_unit = wait
						.until(ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenZila(zila_unit_name)));

				try {
					zila_unit.click();
				} catch (StaleElementReferenceException e) {
					zila_unit = wait.until(
							ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenZila(zila_unit_name)));
					zila_unit.click();
				}

				mandal_exist = cashPage.isElementPresent(driver, "mandal").isDisplayed();
				System.out.println("mandal_exist after click on mandal:" + mandal_exist);
				Assert.assertEquals(mandal_exist, true);

				// select mandal
				WebElement selectMandal = wait
						.until(ExpectedConditions.elementToBeClickable(cashPage.getSelectMandal()));
				selectMandal.click();

				WebElement mandal_unit = wait.until(
						ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenMandal(mandal_unit_name)));
				mandal_unit.click();

			}

		}

		
		
		WebElement clickSubmitBtn = driver.findElement(By.xpath("//button[@color='primary']"));
		clickSubmitBtn.click();

		WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));

		String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();

		System.out.println("submitText :" + submitText);

		if (submitText.contains("You can not donate more than") || submitText.contains("You can only donate")) {
			// send 1 rupee
			System.out.println("inside***********");
			jse.executeScript("scroll(0, -250);");
			amount_input.clear();
			String updatedAmount = Integer.toString(rand.nextInt(10) + 1);
			amount_input.sendKeys(updatedAmount);
			jse.executeScript("scroll(0, 250);");
			Thread.sleep(2000);
		    clickSubmitBtn.click();
		    
		    WebElement submit2 = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));
		    
		    String submitText2 = wait.until(ExpectedConditions.visibilityOf(submit2)).getText();
		    //Assert.assertTrue(submitText2.contains("Record Saved Successfully"));
		    
		    System.out.println("submitText2 1st time:"+submitText2);
		    
		}
		
		else {
			Assert.assertTrue(submitText.contains("Record Saved Successfully"));
		}
		
		

		// success
		log.info("Cash mode Transaction Created Sucessfully");

		ngDriver.waitForAngularRequestsToFinish();
		// ------------------------------------------------------------------------------------------------------------------------

	}

	// ----------------------------- Action Related

	@Test(dependsOnMethods = { "basePageNavigation" })
	public void viewActionForCash(ITestContext context) throws IOException, InterruptedException {

		JavascriptExecutor js = (JavascriptExecutor) driver;

		Cash_ModePage cashPage = new Cash_ModePage(driver);
		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		Thread.sleep(6000);
		log.info("action test");
		// click on भारतीय जनता पार्टी for home
		driver.findElement(By.cssSelector("[class='header-title-span']")).click();

		WebElement heading2 = wait.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getFirstHeading()));
		Assert.assertEquals(heading2.getText(), "Nidhi Collection");
		// WebElement c = cashPage.getCount();

		WebElement c = driver.findElement(By.cssSelector("[class='total-count-div']"));
		WebElement count = wait.until(ExpectedConditions.elementToBeClickable(c));
		js.executeScript("arguments[0].click()", count);

		ngDriver.waitForAngularRequestsToFinish();

		String header = driver.findElement(By.tagName("h2")).getText();
		Assert.assertEquals(header, "Donation List");
		// click on Cash tab
		driver.findElement(By.xpath("(//*[@class='tab-text'])[8]")).click();
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		// clicking on 3 dots for action
		try {
			WebElement row = driver.findElement(
					By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[" + rowNo + "]"));
			WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(row));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", ele);
			System.out.println("clicked successfuly on 3 dots");
		} catch (org.openqa.selenium.StaleElementReferenceException e1) {
			// WebElement row = cashPage.getRow(rowNo);
			WebElement row = driver.findElement(
					By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[" + rowNo + "]"));
			WebElement ele = wait.until(ExpectedConditions.elementToBeClickable(row));
			ele.click();
			System.out.println("exception in clicking 3 dots");
		}

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
		ngDriver.waitForAngularRequestsToFinish();

		String date = cashPage.getTransactionDate().getAttribute("value");
		System.out.println("-------------------- total form count ---------------------");
		System.out.println("date :" + date);

		String donorName = cashPage.getDonorName().getAttribute("value");
		System.out.println("donorName :" + donorName);

		String donor_phone = cashPage.getDonorPhoneNumber().getAttribute("value");
		System.out.println("donor_phone :" + donor_phone);

		String donor_email = cashPage.getDonorEmail().getAttribute("value");
		System.out.println("donor_email :" + donor_email);

		String donor_house = cashPage.getHouse().getAttribute("value");
		System.out.println("donor_house :" + donor_house);

		String locality = cashPage.getLocality().getAttribute("value");
		System.out.println("locality :" + locality);

		String pincode = cashPage.getPinCode().getAttribute("value");
		System.out.println("pincode :" + pincode);

		js.executeScript("window.scrollBy(0,1350)", "");

		// String district = cashPage.getDistrict().getAttribute("value");
		// System.out.println("district :"+district);

		/*
		 * boolean district_bool = wait
		 * .until(ExpectedConditions.textToBePresentInElementValue(cashPage.getDistrict(
		 * ), "Pune")); String copied_district = null;
		 * 
		 * if (district_bool) { copied_district =
		 * cashPage.getDistrict().getAttribute("value"); } else {
		 * log.error("district name didn't come till 50 sec"); }
		 * 
		 * System.out.println(copied_district);
		 * 
		 * String state = cashPage.getState().getText();
		 * System.out.println("state :"+state);
		 * 
		 */

		String[] arr_category = { "individual", "huf", "partnership", "trust", "corporation", "others" };

		for (String category : arr_category) {

			WebElement selected2 = cashPage.selectCategory(category);

			if (selected2.isSelected()) {
				System.out.println("yes " + category + " is selected");
			}
		}

		Thread.sleep(2000);
		String pan1 = cashPage.get1stPanInput().getAttribute("value");
		String pan2 = cashPage.get2ndPanInput().getAttribute("value");
		String pan3 = cashPage.get3rdPanInput().getAttribute("value");
		String pan4 = cashPage.get4thPanInput().getAttribute("value");

		String pan5 = cashPage.get5thPanInput().getAttribute("value");
		String pan6 = cashPage.get6thPanInput().getAttribute("value");
		String pan7 = cashPage.get7thPanInput().getAttribute("value");
		String pan8 = cashPage.get8thPanInput().getAttribute("value");

		String pan9 = cashPage.get9thPanInput().getAttribute("value");
		String pan10 = cashPage.getLastPanInput().getAttribute("value");

		System.out.println("pan :" + pan1 + " " + pan2 + " " + pan3 + " " + pan4 + " " + pan5 + " " + pan6 + " " + pan7
				+ " " + pan8 + " " + pan9 + " " + pan10);

		String amount = cashPage.getAmountInput().getAttribute("value");

		System.out.println("amount :" + amount);

		// String amount_txt = cashPage.getAmountInText().getText();
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

		// click on back icon
		driver.findElement(By.className("back-icon")).click();

		ngDriver.waitForAngularRequestsToFinish();

	}

	@Test(dependsOnMethods = { "viewActionForCash" })

	public void editActionForCash() throws InterruptedException, IOException {

		ArrayList<String> a = new ArrayList<String>();
		ActionReletedDataDriven dd = new ActionReletedDataDriven();
		ArrayList<String> excel_data = dd.getData("cashMode", a);
		Cash_ModePage cashPage = new Cash_ModePage(driver);
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

		System.out.println("Edit section...");

		String random_int_amount1 = "123";
		String categoryChangeTo = excel_data.get(13);
		String proprietorshipChangeTo = excel_data.get(14);
		String name_of_proprietorship = excel_data.get(15);
		String donationNature = excel_data.get(22);
		String party_unit = excel_data.get(24);

		js.executeScript("window.scrollBy(0,750)", "");
		// cashPage.uploadFrontImage();
		// cashPage.uploadBackImage();

		String donor_name = excel_data.get(5);
		String donor_phone = excel_data.get(6);
		String donor_email = excel_data.get(7);

		cashPage.getDonorName().clear();
		cashPage.getDonorName().sendKeys(donor_name);
		String copied_name = cashPage.getDonorName().getAttribute("value");

		System.out.println("copied_name:" + copied_name);
		cashPage.getDonorPhoneNumber().clear();
		cashPage.getDonorPhoneNumber().sendKeys(donor_phone);
		cashPage.getDonorEmail().clear();
		cashPage.getDonorEmail().sendKeys(donor_email);

		cashPage.getHouse().clear();
		cashPage.getHouse().sendKeys(excel_data.get(8));
		cashPage.getLocality().clear();
		cashPage.getLocality().sendKeys(excel_data.get(9));
		cashPage.getPinCode().clear();
		cashPage.getPinCode().sendKeys(excel_data.get(10));

		boolean district_bool = wait
				.until(ExpectedConditions.textToBePresentInElementValue(cashPage.getDistrict(), excel_data.get(11)));
		String copied_district = null;

		if (district_bool) {
			copied_district = cashPage.getDistrict().getAttribute("value");
		} else {
			log.error("district name didn't come till 50 sec");
		}

		System.out.println(copied_district);

		boolean state_bool = wait
				.until(ExpectedConditions.textToBePresentInElement(cashPage.getState(), excel_data.get(12)));

		String copied_state = null;
		if (state_bool) {
			copied_state = cashPage.getState().getText();
			System.out.println("copied_state :" + copied_state);
		} else {
			log.error("state name didn't come till 50 sec");
		}

		// Assert.assertEquals(copied_district, excel_data.get(14));
		// Assert.assertEquals(copied_state, excel_data.get(15));

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,310)");

		WebElement categoryElement = cashPage.selectCategory(categoryChangeTo);
		wait.until(ExpectedConditions.elementToBeClickable(categoryElement)).click();

		if (categoryChangeTo == "individual" || categoryChangeTo.equals("individual")) {
			if (proprietorshipChangeTo == "yes" || proprietorshipChangeTo.equals("yes")) {
				WebElement proprietorship_yes = cashPage.getYesProprietorship(categoryElement);
				proprietorship_yes.click();

				Thread.sleep(1000);

				boolean b1 = cashPage.getYesProprietorshipSelect().isSelected();

				System.out.println("proprietorship_yes is selected or not --->:" + b1);
			

				WebElement Proprietorship_txt2 = cashPage.getTextAfterYesProprietorship(proprietorship_yes);

				System.out.println(Proprietorship_txt2.getText());
				Assert.assertEquals(Proprietorship_txt2.getText(), "Write the name of the Proprietorship");
				// type Name of proprietorship
				cashPage.getProprietorshipName().clear();
				cashPage.getProprietorshipName().sendKeys(name_of_proprietorship);
			} else {
				WebElement proprietorship_no = cashPage.getNoProprietorship(categoryElement);
				proprietorship_no.click();
			}
		} else if (categoryChangeTo == "others" || categoryChangeTo.equals("others")) {
			WebElement other_category = wait
					.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getOtherCategory()));

			boolean visible_other_category = other_category.isDisplayed();
			Assert.assertTrue(visible_other_category);
			other_category.clear();
			other_category.sendKeys(excel_data.get(16));
		}

		// String[] changePanNoTo = { "F", "O", "o", "j", "V", "3", "2", "3", "1", "k"
		// };
		String pan = excel_data.get(17);
		String[] changePanNoTo = pan.split("");

		cashPage.get1stPanInput().clear();
		cashPage.get1stPanInput().sendKeys(changePanNoTo[0]);
		cashPage.get2ndPanInput().sendKeys(changePanNoTo[1]);
		cashPage.get3rdPanInput().sendKeys(changePanNoTo[2]);
		cashPage.get4thPanInput().sendKeys(changePanNoTo[3]);
		cashPage.get5thPanInput().sendKeys(changePanNoTo[4]);
		cashPage.get6thPanInput().sendKeys(changePanNoTo[5]);
		cashPage.get7thPanInput().sendKeys(changePanNoTo[6]);
		cashPage.get8thPanInput().sendKeys(changePanNoTo[7]);
		cashPage.get9thPanInput().sendKeys(changePanNoTo[8]);
		cashPage.getLastPanInput().sendKeys(changePanNoTo[9]);

		// last pan input
		WebElement last_pan_input = cashPage.getLastPanInput();

		// invalid pan no
		String text_for_invalid_pan = pan_letters_varification(driver, cashPage);
		System.out.println("text_for_invalid_pan :" + text_for_invalid_pan);

		// 4th and 5th letter
		String error_txt_for_4th_and_5th_letter = pan_letters_category_validation(categoryChangeTo,
				name_of_proprietorship, copied_name, changePanNoTo, proprietorshipChangeTo);
		System.out.println("error_txt_for_4th_and_5th_letter: " + error_txt_for_4th_and_5th_letter);

		if (text_for_invalid_pan != "") {

			System.out.println("from inside if condition..");

			WebElement pan_error_paragraph = cashPage.getPanErrorParagraph(last_pan_input);
			System.out.println("Assertion for text_for_invalid_pan pass....");

			cashPage.uploadWrongPanImage();
			cashPage.getWrongPanRemark().clear();
			cashPage.getWrongPanRemark().sendKeys("text_for_invalid_pan");

			Assert.assertEquals(text_for_invalid_pan, pan_error_paragraph.getText());
		} else if (error_txt_for_4th_and_5th_letter != "") {

			System.out.println("from inside elseif condition..");
			WebElement pan_error_paragraph = cashPage.getPanErrorParagraph(last_pan_input);
			System.out.println("Assertion for error_txt_for_4th_and_5th_letter pass....");

			cashPage.uploadWrongPanImage();
			cashPage.getWrongPanRemark().clear();
			cashPage.getWrongPanRemark().sendKeys("error_txt_for_4th_and_5th_letter");

			Assert.assertEquals(error_txt_for_4th_and_5th_letter, pan_error_paragraph.getText());
		}

		else {

			System.out.println("from inside else condition..");
			System.out.println("correct category & pan selection");
		}

		Random rand = new Random();
		int amount = Integer.parseInt(excel_data.get(18));
		int random_int_amount = rand.nextInt(amount);
		random_int_amount1 = Integer.toString(random_int_amount);
		WebElement amount_input = cashPage.getAmountInput();

		String amount_converted_in_words;
		amount_input.clear();
		amount_input.sendKeys(random_int_amount1);
		amount_converted_in_words = convertToIndianCurrency(random_int_amount1);

		// Remove extra whitespace if any
		amount_converted_in_words = amount_converted_in_words.replaceAll("\\s+", " ");

		String amount_in_words_txt = cashPage.getAmountInWords(amount_input).getText();

		System.out.println("amount_converted_in_words **** :" + amount_converted_in_words);
		System.out.println("amount_in_workds_txt **** :" + amount_in_words_txt);

		Assert.assertEquals(amount_converted_in_words, amount_in_words_txt);
		cashPage.getNarationInput().clear();
		cashPage.getNarationInput().sendKeys(excel_data.get(19));

		WebElement collector_name_input = cashPage.getCollectorName();

		collector_name_input.clear();
		collector_name_input.sendKeys("vk");
		WebElement collecter_phone = cashPage.getCollectorPhone();
		collecter_phone.click();

		wait.until(ExpectedConditions.textToBePresentInElement(cashPage.getWrongCollectorName(collector_name_input),
				"Please enter a valid name"));
		collector_name_input.clear();
		collector_name_input.sendKeys(excel_data.get(20));

		collecter_phone.clear();
		// wrong collector phone no
		collecter_phone.sendKeys("23222322");
		wait.until(ExpectedConditions.textToBePresentInElement(cashPage.getWrongCollectorPhone(collecter_phone),
				"Please enter correct phone number"));
		collecter_phone.clear();
		collecter_phone.sendKeys(excel_data.get(21));

		jse.executeScript("window.scrollBy(0,430)");

		if (donationNature.equals("Aajivan Sahyog Nidhi")) {
			// add . at the end as Aajivan Sahyog Nidhi. is in value property
			donationNature = donationNature + '.';
		}
		cashPage.getDonationNature(donationNature).click();
		// Assert.assertTrue(cashPage.getDonationNature2(donationNature).isSelected());

		if (donationNature == "Other" || donationNature.equals("Other")) {
			cashPage.getOtherNatureOfDonation().clear();
			cashPage.getOtherNatureOfDonation().sendKeys(excel_data.get(23));
		}

		// party_unit = "Zila";

		if (party_unit == "State" || party_unit.equals("State")) {
			party_unit = "CountryState";
		}

		WebElement party_unit_type = cashPage.getPartyUnit(party_unit);
		party_unit_type.click();
		System.out.println(cashPage.getPartyUnit2(party_unit).isSelected());

		Assert.assertTrue(cashPage.getPartyUnit2(party_unit).isSelected());

		String state_unit_name = excel_data.get(25);

		System.out.println("state_unit_name :" + state_unit_name);
		String zila_unit_name = excel_data.get(26);
		String mandal_unit_name = excel_data.get(27);
		System.out.println("zila_unit_name :" + zila_unit_name);
		System.out.println("mandal_unit_name :" + mandal_unit_name);

		if (party_unit == "CountryState") {
			Thread.sleep(2000);

			WebElement selectState = wait
					.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getSelectState()));

			selectState.click();

			WebElement state_unit = wait
					.until(ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenState(state_unit_name)));
			state_unit.click();

		} else if (party_unit == "Zila" || party_unit.equals("Zila")) {
			Thread.sleep(2000);
			// select state 
			WebElement selectState = wait.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getSelectState()));
			selectState.click();
	
			WebElement state_from_zila = wait
					.until(ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenState(state_unit_name)));

			try {
				state_from_zila.click();
			} catch (StaleElementReferenceException e) {
				state_from_zila = wait.until(
						ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenState(state_unit_name)));
				state_from_zila.click();
			}

			// select zila
			WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(cashPage.getSelectZila()));
			selectZila.click();

			WebElement zila_unit = wait
					.until(ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenZila(zila_unit_name)));
			zila_unit.click();

		} else {

			Thread.sleep(2000);
			// select state
			WebElement selectState = wait
					.until(ExpectedConditions.visibilityOfElementLocated(cashPage.getSelectState()));
			selectState.click();

			WebElement state_from_mandal = wait
					.until(ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenState(state_unit_name)));

			try {
				state_from_mandal.click();
			} catch (StaleElementReferenceException e) {
				state_from_mandal = wait.until(
						ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenState(state_unit_name)));
				state_from_mandal.click();
			}

			// select zila
			WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(cashPage.getSelectZila()));
			selectZila.click();
			
			WebElement zila_unit = wait
					.until(ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenZila(zila_unit_name)));

			try {
				zila_unit.click();
			} catch (StaleElementReferenceException e) {
				zila_unit = wait
						.until(ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenZila(zila_unit_name)));
				zila_unit.click();
			}

			// select mandal
			WebElement selectMandal = wait.until(ExpectedConditions.elementToBeClickable(cashPage.getSelectMandal()));
			selectMandal.click();

			WebElement mandal_unit = wait
					.until(ExpectedConditions.visibilityOfElementLocated(cashPage.selectGivenMandal(mandal_unit_name)));
			mandal_unit.click();

		}
		WebElement clickSubmitBtn = driver.findElement(By.xpath("//button[@color='primary']"));
		clickSubmitBtn.click();

		WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));

		String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();

		System.out.println("submitText Edit section:" + submitText);

		if (submitText.contains("You can not donate more than") || submitText.contains("You can only donate")) {
			System.out.println("inside***********");
			jse.executeScript("scroll(0, -250);");
			amount_input.clear();
			String updatedAmount = Integer.toString(rand.nextInt(10) + 1);
			amount_input.sendKeys(updatedAmount);
			jse.executeScript("scroll(0, 250);");
			Thread.sleep(2000);
		    clickSubmitBtn.click();
		    
		    WebElement submit2 = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));
		    
		    String submitText2 = wait.until(ExpectedConditions.visibilityOf(submit2)).getText();
		   
		    System.out.println("submitText2 Edit section:"+submitText2);
		    
		}
		else {
			Assert.assertTrue(submitText.contains("Record Update Successfully"));	
		}
		
		// setting reverseText
		reverseText = excel_data.get(28);
		ngDriver.waitForAngularRequestsToFinish();
	}

	@Test(dependsOnMethods = { "editActionForCash" })
	public void reversedActionForCash() throws InterruptedException, IOException {

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
		driver.findElement(By.xpath("//*[@formcontrolname='remark']")).sendKeys(reverseText);

		// Submit button
		driver.findElement(By.xpath("//button[contains(text(),'Submit')]")).click();
		 
		  WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));
		  
		  String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();
		  
		  System.out.println("submitText for reversed:"+submitText);
		 
		  Assert.assertTrue(submitText.contains("Updated Successfully"));
		

	}

	@Test(dependsOnMethods = { "reversedActionForCash" })
	public void archiveActionForCash() throws InterruptedException, IOException {
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
		ngDriver.waitForAngularRequestsToFinish();
		// Yes button
		driver.findElement(By
				.xpath("//button[@class='mat-focus-indicator bg-primary text-white mat-raised-button mat-button-base']"))
				.click();
		
		WebElement submit = driver.findElement(By.xpath("(//*[@class='mat-simple-snackbar ng-star-inserted'])"));
		  
		  String submitText = wait.until(ExpectedConditions.visibilityOf(submit)).getText();
		  
		  System.out.println("submitText for Archive:"+submitText);
		 
		  Assert.assertTrue(submitText.contains("Archive Successfully"));
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

	private static String pan_letters_varification(WebDriver driver, Cash_ModePage cashPage) {

		List<WebElement> elements = cashPage.getPanElements();
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
			String copied_name, String category, Cash_ModePage cashPage) {

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

			fill_pan_number(driver, pan_no, cashPage);

			// last pan input
			WebElement last_pan_input = cashPage.getLastPanInput();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// invalid pan no
			String text_for_invalid_pan = pan_letters_varification(driver, cashPage);

			System.out.println("text_for_invalid_pan :" + text_for_invalid_pan);

			// 4th and 5th letter
			String error_txt_for_4th_and_5th_letter = pan_letters_category_validation(driver, category,
					name_of_proprietorship, copied_name, cashPage);
			System.out.println("error_txt_for_4th_and_5th_letter:" + error_txt_for_4th_and_5th_letter);

			if (text_for_invalid_pan != "") {
				WebElement pan_error_paragraph = cashPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for text_for_invalid_pan pass....");
				Assert.assertEquals(text_for_invalid_pan, pan_error_paragraph.getText());
			} else if (error_txt_for_4th_and_5th_letter != "") {

				WebElement pan_error_paragraph = cashPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for error_txt_for_4th_and_5th_letter pass....");
				Assert.assertEquals(error_txt_for_4th_and_5th_letter, pan_error_paragraph.getText());
			}

			else {
				System.out.println("correct category & pan selection");
			}

		}

	}

	private static void fill_pan_number(WebDriver driver, String[] pan, Cash_ModePage cashPage) {

		cashPage.get1stPanInput().clear();
		cashPage.get1stPanInput().sendKeys(pan[0]);
		cashPage.get2ndPanInput().sendKeys(pan[1]);
		cashPage.get3rdPanInput().sendKeys(pan[2]);
		cashPage.get4thPanInput().sendKeys(pan[3]);
		cashPage.get5thPanInput().sendKeys(pan[4]);
		cashPage.get6thPanInput().sendKeys(pan[5]);
		cashPage.get7thPanInput().sendKeys(pan[6]);
		cashPage.get8thPanInput().sendKeys(pan[7]);
		cashPage.get9thPanInput().sendKeys(pan[8]);
		cashPage.getLastPanInput().sendKeys(pan[9]);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private static String pan_letters_category_validation(WebDriver driver, String category_value,
			String name_of_proprietorship, String copied_name, Cash_ModePage cashPage) {

		List<WebElement> pan_elements = cashPage.getPanElements();
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
			String copied_name, String category, Cash_ModePage cashPage) {

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

			fill_pan_number(driver, pan_no, cashPage);

			// last pan input
			WebElement last_pan_input = cashPage.getLastPanInput();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// invalid pan no
			String text_for_invalid_pan = pan_letters_varification(driver, cashPage);

			System.out.println("text_for_invalid_pan :" + text_for_invalid_pan);

			// 4th and 5th letter
			String error_txt_for_4th_and_5th_letter = pan_letters_category_validation(driver, category,
					name_of_proprietorship, copied_name, cashPage);
			System.out.println("error_txt_for_4th_and_5th_letter:" + error_txt_for_4th_and_5th_letter);

			if (text_for_invalid_pan != "") {

				WebElement pan_error_paragraph = cashPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for text_for_invalid_pan pass....");

				cashPage.uploadWrongPanImage();
				cashPage.getWrongPanRemark().clear();
				cashPage.getWrongPanRemark().sendKeys("text_for_invalid_pan");

				Assert.assertEquals(text_for_invalid_pan, pan_error_paragraph.getText());
			} else if (error_txt_for_4th_and_5th_letter != "") {
				WebElement pan_error_paragraph = cashPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for error_txt_for_4th_and_5th_letter pass....");

				cashPage.uploadWrongPanImage();
				cashPage.getWrongPanRemark().clear();
				cashPage.getWrongPanRemark().sendKeys("error_txt_for_4th_and_5th_letter");

				Assert.assertEquals(error_txt_for_4th_and_5th_letter, pan_error_paragraph.getText());
			}

			else {
				System.out.println("correct category & pan selection");
			}
		}
	}

	// ------------------------------------------------------------------------------------------
}
