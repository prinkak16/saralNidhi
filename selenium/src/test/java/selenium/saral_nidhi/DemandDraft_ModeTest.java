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
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

//import pageObjects.LandingPage;
import pageObjects.DemandDraft_ModePage;
import resources.Base;

public class DemandDraft_ModeTest extends Base {
	public WebDriver driver;
	public static Logger log = LogManager.getLogger(DemandDraft_ModeTest.class);

	@Test
	public void basePageNavigation(ITestContext context) throws IOException, InterruptedException {
		driver = initializeDriver();

		driver.get(url);

		ArrayList<String> a = new ArrayList<String>();

		DataDriven dd = new DataDriven();
		ArrayList<String> excel_data = dd.getData("DemandDraft_ModeTest", a);

		// LandingPage lp = new LandingPage(driver);
		DemandDraft_ModePage demandDraftPage = new DemandDraft_ModePage(driver);

		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		LocalStorage storage = ((WebStorage) driver).getLocalStorage();

		new SetLocalStorage(storage, driver, context);

		/*
		 * lp.login_email().sendKeys(excel_data.get(1));
		 * lp.login_password().sendKeys(excel_data.get(2)); WebElement sendOTP =
		 * wait.until(ExpectedConditions.elementToBeClickable(lp.send_OTP()));
		 * sendOTP.click();
		 * 
		 * WebElement enterOTP =
		 * wait.until(ExpectedConditions.visibilityOfElementLocated(lp.enter_otp()));
		 * enterOTP.sendKeys(excel_data.get(3));
		 * 
		 * WebElement loginButton =
		 * wait.until(ExpectedConditions.elementToBeClickable(lp.login_btn()));
		 * loginButton.click();
		 * 
		 */

		WebElement heading1 = wait
				.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.getFirstHeading()));
		Assert.assertEquals(heading1.getText(), "Nidhi Collection");

		demandDraftPage.getIndianDonationForm().click();

		WebElement paymentModeOption = wait
				.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.getDemandDraftPaymentMode()));

		// Demand Draft should not be selected
		Assert.assertFalse(demandDraftPage.getDemandDraftModeValue().isSelected());

		paymentModeOption.click();

		// Demand Draft should be selected
		Assert.assertTrue(demandDraftPage.getDemandDraftModeValue().isSelected());
		log.info("DemandDraft Mode of Payment selected");
		LocalDate now = LocalDate.now();

		int no_of_back_days = Integer.parseInt(excel_data.get(5));
		LocalDate draftDate_generate = now.minusDays(no_of_back_days);

		DateTimeFormatter dateFormating = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		String transaction_Date = draftDate_generate.format(dateFormating);

		demandDraftPage.getDraftDate().sendKeys(transaction_Date);

		// Wrong draft no
		demandDraftPage.getDraftNumber().sendKeys("12345");

		demandDraftPage.getAccountNumber().click();

		Assert.assertEquals(demandDraftPage.getInvalidDraft().getText(), "Please enter a valid draft number");

		demandDraftPage.getDraftNumber().clear();

		Random random = new Random();
		String random_draft_no = Integer.toString(random.nextInt(900000) + 100000);

		demandDraftPage.getDraftNumber().sendKeys(random_draft_no);

		demandDraftPage.uploadFrontImage();
		demandDraftPage.uploadBackImage();

		demandDraftPage.getAccountNumber().sendKeys(excel_data.get(6));

		demandDraftPage.getIfscCode().sendKeys(excel_data.get(7));

		WebElement bankdetails = wait
				.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.getbankDetails()));

		if (bankdetails.isDisplayed()) {

			System.out.println("Element is Visible");
			bankdetails.click();
		}

		else {

			System.out.println("Element is InVisible");

		}

		String donor_name = excel_data.get(8);
		String donor_phone = excel_data.get(9);
		String donor_email = excel_data.get(10);

		demandDraftPage.getDonorName().sendKeys(donor_name);

		String copied_name = demandDraftPage.getDonorName().getAttribute("value");

		System.out.println("copied_name:" + copied_name);

		// Check name is converted to UpperCase or not
		Assert.assertEquals(donor_name.toUpperCase(), copied_name);
		// type wrong phone no
		demandDraftPage.getDonorPhoneNumber().sendKeys("23222322");

		demandDraftPage.getDonorEmail().click();

		Assert.assertEquals("Please enter a valid phone number", demandDraftPage.getInvalidDonorPhone().getText());

		demandDraftPage.getDonorPhoneNumber().clear();
		demandDraftPage.getDonorPhoneNumber().sendKeys(donor_phone);

		// Enter wrong email
		demandDraftPage.getDonorEmail().sendKeys("abc.com");

		demandDraftPage.getHouse().click();

		Assert.assertEquals("Enter a valid email", demandDraftPage.getInvalidDonorEmail().getText());

		demandDraftPage.getDonorEmail().clear();
		demandDraftPage.getDonorEmail().sendKeys(donor_email);
		demandDraftPage.getHouse().sendKeys(excel_data.get(11));
		demandDraftPage.getLocality().sendKeys(excel_data.get(12));
		demandDraftPage.getPinCode().sendKeys(excel_data.get(13));

		boolean district_bool = wait.until(
				ExpectedConditions.textToBePresentInElementValue(demandDraftPage.getDistrict(), excel_data.get(14)));
		String copied_district = null;

		if (district_bool) {
			copied_district = demandDraftPage.getDistrict().getAttribute("value");
		} else {
			log.error("district name didn't come till 30 sec");
		}

		System.out.println("copied_district :" + copied_district);

		boolean state_bool = wait
				.until(ExpectedConditions.textToBePresentInElement(demandDraftPage.getState(), excel_data.get(15)));

		String copied_state = null;
		if (state_bool) {
			copied_state = demandDraftPage.getState().getText();
		} else {
			log.error("state name didn't come till 30 sec");
		}
		System.out.println("copied_state :" + copied_state);

		Assert.assertEquals(copied_district, excel_data.get(14));
		Assert.assertEquals(copied_state, excel_data.get(15));

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,310)");

		// ------------------------------------------------------------------------------------------------------------------------------

		String[] arr_category = { "individual", "huf", "partnership", "trust", "corporation", "others" };
		for (String category : arr_category) {

			WebElement categoryElement = demandDraftPage.selectCategory(category);
			categoryElement.click();

			Thread.sleep(1000);

			String[] pan_no = { "F", "O", "O", "P", "K", "1", "2", "3", "4", "k" };

			String name_of_proprietorship = "";
			if (category == "individual") {

				demandDraftPage.getNoProprietorship(categoryElement).click();

				for_yes_no_proprietorship(driver, pan_no, name_of_proprietorship, copied_name, category,
						demandDraftPage);

				// Now for Yes
				// Thread.sleep(1000);

				System.out.println("-------------------- For Yes-------------------------");

				String proprietorship_txt = demandDraftPage.getProprietorship(categoryElement).getText();
				Assert.assertEquals(proprietorship_txt, "Is it a proprietorship? *");

				WebElement proprietorship_yes = demandDraftPage.getYesProprietorship(categoryElement);
				proprietorship_yes.click();

				// Thread.sleep(1000);

				boolean b1 = demandDraftPage.getYesProprietorshipSelect().isSelected();

				System.out.println("proprietorship_yes is selected or not --->:" + b1);
				Assert.assertTrue(b1);

				WebElement Proprietorship_txt2 = demandDraftPage.getTextAfterYesProprietorship(proprietorship_yes);

				System.out.println(Proprietorship_txt2.getText());
				Assert.assertEquals(Proprietorship_txt2.getText(), "Write the name of the Proprietorship");

				name_of_proprietorship = excel_data.get(16);
				// type Name of proprietorship
				demandDraftPage.getProprietorshipName().sendKeys(name_of_proprietorship);

				for_yes_no_proprietorship(driver, pan_no, name_of_proprietorship, copied_name, category,
						demandDraftPage);

				// As individual category end so make --> name_of_proprietorship=""
				name_of_proprietorship = "";
			} else if (category == "others") {

				WebElement other_category = wait
						.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.getOtherCategory()));

				boolean visible_other_category = other_category.isDisplayed();
				Assert.assertTrue(visible_other_category);
				other_category.sendKeys("6th vk");

				other_than_individual(driver, pan_no, name_of_proprietorship, copied_name, category, demandDraftPage);
			}

			else {
				other_than_individual(driver, pan_no, name_of_proprietorship, copied_name, category, demandDraftPage);
			}
		}
		// ------------------------------------------------------------------------------------------------------------

		Random rand = new Random();
		int amount = Integer.parseInt(excel_data.get(17));
		int random_int_amount = rand.nextInt(amount);
		String random_int_amount1 = Integer.toString(random_int_amount);

		WebElement amount_input = demandDraftPage.getAmountInput();

		String amount_converted_in_words;
		amount_input.sendKeys(random_int_amount1);
		amount_converted_in_words = convertToIndianCurrency(random_int_amount1);

		// Remove extra whitespace if any
		amount_converted_in_words = amount_converted_in_words.replaceAll("\\s+", " ");

		String amount_in_words_txt = demandDraftPage.getAmountInWords(amount_input).getText();

		System.out.println("amount_converted_in_words **** :" + amount_converted_in_words);
		System.out.println("amount_in_workds_txt **** :" + amount_in_words_txt);

		Assert.assertEquals(amount_converted_in_words, amount_in_words_txt);

		demandDraftPage.getNarationInput().sendKeys(excel_data.get(18));
		WebElement collector_name_input = demandDraftPage.getCollectorName();

		collector_name_input.sendKeys("vk");
		WebElement collecter_phone = demandDraftPage.getCollectorPhone();
		collecter_phone.click();

		 wait.until(ExpectedConditions.textToBePresentInElement(demandDraftPage.getWrongCollectorName(collector_name_input),"Please enter a valid name"));
		//String collector_name_error = demandDraftPage.getWrongCollectorName(collector_name_input).getText();

		//Assert.assertEquals(collector_name_error, "Please enter a valid name");
		collector_name_input.clear();
		collector_name_input.sendKeys(excel_data.get(19));

		// wrong collector phone no
		collecter_phone.sendKeys("23222322");
		wait.until(ExpectedConditions.textToBePresentInElement(demandDraftPage.getWrongCollectorPhone(collecter_phone), "Please enter correct phone number"));
		//String collector_phone_error = demandDraftPage.getWrongCollectorPhone(collecter_phone).getText();
		//Assert.assertEquals(collector_phone_error, "Please enter correct phone number");
		collecter_phone.clear();

		collecter_phone.sendKeys(excel_data.get(20));
		jse.executeScript("window.scrollBy(0,310)");
		// Nature of donation
		String[] donation_nature = { "Voluntary Contribution", "Aajivan Sahyog Nidhi.", "Other" };

		for (String donation : donation_nature) {

			demandDraftPage.getDonationNature(donation).click();
			Thread.sleep(500);
			System.out.println("nature of donation :--->" + demandDraftPage.getDonationNature2(donation).isSelected());

			Assert.assertTrue(demandDraftPage.getDonationNature2(donation).isSelected());

			if (donation == "Other") {
				demandDraftPage.getOtherNatureOfDonation().sendKeys(excel_data.get(21));
			}

		}

		JavascriptExecutor jse1 = (JavascriptExecutor) driver;
		jse1.executeScript("window.scrollBy(0,310)");
		// party unit
		String[] party_unit_arr = { "CountryState", "Zila", "Mandal" };

		for (String party_unit : party_unit_arr) {

			WebElement party_unit_type = demandDraftPage.getPartyUnit(party_unit);
			party_unit_type.click();
			System.out.println(demandDraftPage.getPartyUnit2(party_unit).isSelected());

			Assert.assertTrue(demandDraftPage.getPartyUnit2(party_unit).isSelected());

			boolean state_exist = demandDraftPage.isElementPresent(driver, "state").isDisplayed();

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
						.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.getSelectState()));

				selectState.click();

				WebElement state_unit = wait.until(ExpectedConditions
						.visibilityOfElementLocated(demandDraftPage.selectGivenState(state_unit_name)));
				state_unit.click();

			} else if (party_unit == "Zila") {

				zila_exist = demandDraftPage.isElementPresent(driver, "zila").isDisplayed();
				System.out.println("zila_exist after click on zila:" + zila_exist);
				Assert.assertEquals(zila_exist, true);

				// select state
				WebElement selectState = wait
						.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.getSelectState()));
				selectState.click();

				WebElement state_from_zila = wait.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.selectGivenState(state_unit_name)));
				
				try {
					state_from_zila.click();
				}
				catch (StaleElementReferenceException e) {
					state_from_zila = wait.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.selectGivenState(state_unit_name)));
					state_from_zila.click();
				}
				
				// select zila
				WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(demandDraftPage.getSelectZila()));
				selectZila.click();

				WebElement zila_unit = wait.until(
						ExpectedConditions.visibilityOfElementLocated(demandDraftPage.selectGivenZila(zila_unit_name)));
				zila_unit.click();

			} else {

				zila_exist = demandDraftPage.isElementPresent(driver, "zila").isDisplayed();
				System.out.println("zila_exist after click on zila:" + zila_exist);
				Assert.assertEquals(zila_exist, true);
				// select state
				WebElement selectState = wait
						.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.getSelectState()));
				selectState.click();
				
				WebElement state_from_mandal = wait.until(
						ExpectedConditions.visibilityOfElementLocated(demandDraftPage.selectGivenState(state_unit_name)));
				
				
				try {
					state_from_mandal.click();
				}
				catch (StaleElementReferenceException e) {
					state_from_mandal = wait.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.selectGivenState(state_unit_name)));
					state_from_mandal.click();
				}
				

				// select zila
				WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(demandDraftPage.getSelectZila()));
				selectZila.click();

				WebElement zila_unit = wait.until(
						ExpectedConditions.visibilityOfElementLocated(demandDraftPage.selectGivenZila(zila_unit_name)));
				
				try {
					zila_unit.click();
				}
				catch (StaleElementReferenceException e) {
					zila_unit = wait.until(ExpectedConditions.visibilityOfElementLocated(demandDraftPage.selectGivenZila(zila_unit_name)));
					zila_unit.click();
				}
				
				mandal_exist = demandDraftPage.isElementPresent(driver, "mandal").isDisplayed();
				System.out.println("mandal_exist after click on mandal:" + mandal_exist);
				Assert.assertEquals(mandal_exist, true);

				// select mandal
				WebElement selectMandal = wait
						.until(ExpectedConditions.elementToBeClickable(demandDraftPage.getSelectMandal()));
				selectMandal.click();

				WebElement mandal_unit = wait.until(
						ExpectedConditions.visibilityOfElementLocated(demandDraftPage.selectGivenMandal(mandal_unit_name)));
				mandal_unit.click();

			}

		}
		// success
		log.info("DemandDraft mode Transaction Created Sucessfully");
		// ------------------------------------------------------------------------------------------------------------------------
	}

	@AfterTest
	public void termnate() {
		driver.close();
	}

	private static void other_than_individual(WebDriver driver, String[] pan_no, String name_of_proprietorship,
			String copied_name, String category, DemandDraft_ModePage demandDraftPage) {

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

			fill_pan_number(driver, pan_no, demandDraftPage);

			// last pan input
			WebElement last_pan_input = demandDraftPage.getLastPanInput();

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// invalid pan no
			String text_for_invalid_pan = pan_letters_varification(driver, demandDraftPage);

			System.out.println("text_for_invalid_pan :" + text_for_invalid_pan);

			// 4th and 5th letter
			String error_txt_for_4th_and_5th_letter = pan_letters_category_validation(driver, category,
					name_of_proprietorship, copied_name, demandDraftPage);
			System.out.println("error_txt_for_4th_and_5th_letter:" + error_txt_for_4th_and_5th_letter);

			if (text_for_invalid_pan != "") {

				WebElement pan_error_paragraph = demandDraftPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for text_for_invalid_pan pass....");
				Assert.assertEquals(text_for_invalid_pan, pan_error_paragraph.getText());

				demandDraftPage.uploadWrongPanImage();
				demandDraftPage.getWrongPanRemark().clear();
				demandDraftPage.getWrongPanRemark().sendKeys("text_for_invalid_pan");

			} else if (error_txt_for_4th_and_5th_letter != "") {

				WebElement pan_error_paragraph = demandDraftPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for error_txt_for_4th_and_5th_letter pass....");
				Assert.assertEquals(error_txt_for_4th_and_5th_letter, pan_error_paragraph.getText());

				demandDraftPage.uploadWrongPanImage();
				demandDraftPage.getWrongPanRemark().clear();
				demandDraftPage.getWrongPanRemark().sendKeys("error_txt_for_4th_and_5th_letter");
			}

			else {
				System.out.println("correct category & pan selection");
			}
		}
	}

	private static void for_yes_no_proprietorship(WebDriver driver, String[] pan_no, String name_of_proprietorship,
			String copied_name, String category, DemandDraft_ModePage demandDraftPage) {

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

			fill_pan_number(driver, pan_no, demandDraftPage);

			// last pan input
			WebElement last_pan_input = demandDraftPage.getLastPanInput();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// invalid pan no
			String text_for_invalid_pan = pan_letters_varification(driver, demandDraftPage);

			System.out.println("text_for_invalid_pan :" + text_for_invalid_pan);

			// 4th and 5th letter
			String error_txt_for_4th_and_5th_letter = pan_letters_category_validation(driver, category,
					name_of_proprietorship, copied_name, demandDraftPage);
			System.out.println("error_txt_for_4th_and_5th_letter:" + error_txt_for_4th_and_5th_letter);

			if (text_for_invalid_pan != "") {
				WebElement pan_error_paragraph = demandDraftPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for text_for_invalid_pan pass....");
				Assert.assertEquals(text_for_invalid_pan, pan_error_paragraph.getText());
			} else if (error_txt_for_4th_and_5th_letter != "") {

				WebElement pan_error_paragraph = demandDraftPage.getPanErrorParagraph(last_pan_input);
				System.out.println("Assertion for error_txt_for_4th_and_5th_letter pass....");
				Assert.assertEquals(error_txt_for_4th_and_5th_letter, pan_error_paragraph.getText());
			}

			else {
				System.out.println("correct category & pan selection");
			}

		}

	}

	private static void fill_pan_number(WebDriver driver, String[] pan, DemandDraft_ModePage demandDraftPage) {

		demandDraftPage.get1stPanInput().clear();
		demandDraftPage.get1stPanInput().sendKeys(pan[0]);
		demandDraftPage.get2ndPanInput().sendKeys(pan[1]);
		demandDraftPage.get3rdPanInput().sendKeys(pan[2]);
		demandDraftPage.get4thPanInput().sendKeys(pan[3]);
		demandDraftPage.get5thPanInput().sendKeys(pan[4]);
		demandDraftPage.get6thPanInput().sendKeys(pan[5]);
		demandDraftPage.get7thPanInput().sendKeys(pan[6]);
		demandDraftPage.get8thPanInput().sendKeys(pan[7]);
		demandDraftPage.get9thPanInput().sendKeys(pan[8]);
		demandDraftPage.getLastPanInput().sendKeys(pan[9]);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private static String pan_letters_category_validation(WebDriver driver, String category_value,
			String name_of_proprietorship, String copied_name, DemandDraft_ModePage demandDraftPage) {

		List<WebElement> pan_elements = demandDraftPage.getPanElements();
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

	private static String pan_letters_varification(WebDriver driver, DemandDraft_ModePage demandDraftPage) {

		List<WebElement> elements = demandDraftPage.getPanElements();
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

}