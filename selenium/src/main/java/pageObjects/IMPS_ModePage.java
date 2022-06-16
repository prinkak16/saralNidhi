package pageObjects;

import static org.openqa.selenium.support.locators.RelativeLocator.with;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class IMPS_ModePage {

	public WebDriver driver;

	public IMPS_ModePage(WebDriver driver) {
		this.driver = driver;

	}

	
	// -------------------------------------------------------

	By heading1 = By.className("heading");

	By indianDonationForm = By.xpath("//*[contains(text(), 'Indian Donation Form')]");
	By imps_mode_value = By.cssSelector("*[value='5']");
	By paymentMode = By.xpath("//span[contains(text(), 'IMPS')]");

	By transactionDate = By.cssSelector("[formcontrolname='date_of_transaction']");
	By utrNumber = By.cssSelector("[formcontrolname='utr_number']");
	By accountNumber = By.cssSelector("[formcontrolname='account_number']");
	By invalid_utr = By.cssSelector("[role='alert']");
	By ifscCode = By.cssSelector("[formcontrolname='ifsc_code']");
	By bankDetails = By.cssSelector("[class='bank-details']");

	By donorName = By.cssSelector("[formcontrolname='name']");
	By donorPhoneNumber = By.cssSelector("[formcontrolname='phone']");
	By donorEmail = By.cssSelector("[formcontrolname='email']");
	By invalidDonorPhone = By.cssSelector("[role='alert']");
	By invalidDonorEmail = By.cssSelector("[role='alert']");

	By house = By.cssSelector("[formcontrolname='house']");
	By locality = By.cssSelector("[formcontrolname='locality']");
	By pincode = By.cssSelector("[formcontrolname='pincode']");
	By district = By.cssSelector("[formcontrolname='district']");
	By state = By.className("mat-select-min-line");

	By no_proprietorship = By.xpath("//span[contains(text(), 'No')]");
	By proprietorship = By.className("single-label");
	By yes_proprietorship = By.tagName("label");
	By yes_proprietorship_select = By.xpath("//input[@value='true']");
	By textAfterYesProprietorship = By.tagName("mat-label");

	By proprietorshipName = By.cssSelector("[formcontrolname='proprietorship_name']");
	By otherCategory = By.cssSelector("[formcontrolname='other_category']");

	By firstPanInput = By.cssSelector("input[id*='otp_0_']");
	By secondPanInput = By.cssSelector("input[id*='otp_1_']");
	By thirdPanInput = By.cssSelector("input[id*='otp_2_']");
	By fourthPanInput = By.cssSelector("input[id*='otp_3_']");
	By fifthPanInput = By.cssSelector("input[id*='otp_4_']");
	By sixthPanInput = By.cssSelector("input[id*='otp_5_']");
	By seventhPanInput = By.cssSelector("input[id*='otp_6_']");
	By eightthPanInput = By.cssSelector("input[id*='otp_7_']");
	By ninethPanInput = By.cssSelector("input[id*='otp_8_']");
	By lastPanInput = By.cssSelector("input[id*='otp_9_']");
	By pan_error_paragraph = By.cssSelector("[role='alert']");
	By panElements = By.cssSelector("input[class*='otp-input']");
	
	By wrongPanRemark = By.xpath("//input[@placeholder='Remarks if any']");
	
	By amount_input = By.cssSelector("[formcontrolname='amount']");
	By amount_in_words = By.tagName("p");
	By narationInput = By.cssSelector("[formcontrolname='narration']");
	By collectorName = By.cssSelector("[formcontrolname='collector_name']");
	By collectorPhone = By.cssSelector("[formcontrolname='collector_phone']");
	By wrongCollectorName = By.cssSelector("[role='alert']");
	By wrongCollectorPhone = By.cssSelector("[role='alert']");
	By otherNatureOfDonation = By.cssSelector("[formcontrolname='other_nature_of_donation']");

	By selectState = By.xpath("//*[contains(text(), 'Select state')]");
	By selectZila = By.xpath("//*[contains(text(), 'Select zila')]");
	By selectMandal = By.xpath("//*[contains(text(), 'Select mandal')]");

	public By getFirstHeading() {

		return heading1;
	}

	public WebElement getIndianDonationForm() {

		return driver.findElement(indianDonationForm);
	}

	public WebElement getIMPSModeValue() {
		return driver.findElement(imps_mode_value);
	}

	public By getIMPSPaymentMode() {
		return paymentMode;
	}

	public WebElement getTransactionDate() {
		return driver.findElement(transactionDate);
	}

	public WebElement getUTRNumber() {
		return driver.findElement(utrNumber);
	}

	
	public void uploadWrongPanImage() {
		WebElement upload_file3 = driver.findElement(By.xpath("//input[@type='file']"));
		upload_file3.sendKeys(System.getProperty("user.dir") + "\\src\\main\\java\\resources\\upload_image2.jpg");
	}

	public WebElement getWrongPanRemark() {
		return driver.findElement(wrongPanRemark);
	}

	public WebElement getAccountNumber() {
		return driver.findElement(accountNumber);
	}

	public WebElement getInvalidUTR() {
		return driver.findElement(with(invalid_utr).above(accountNumber));
	}

	public WebElement getIfscCode() {
		return driver.findElement(ifscCode);
	}

	public By getbankDetails() {
		return bankDetails;
	}

	public WebElement getDonorName() {
		return driver.findElement(donorName);
	}

	public WebElement getDonorPhoneNumber() {
		return driver.findElement(donorPhoneNumber);
	}

	public WebElement getDonorEmail() {
		return driver.findElement(donorEmail);
	}

	public WebElement getInvalidDonorPhone() {
		return driver.findElement(with(invalidDonorPhone).toLeftOf(donorEmail));
	}

	public WebElement getInvalidDonorEmail() {
		return driver.findElement(with(invalidDonorEmail).below(donorEmail));
	}

	public WebElement getHouse() {
		return driver.findElement(house);
	}

	public WebElement getLocality() {
		return driver.findElement(locality);
	}

	public WebElement getPinCode() {
		return driver.findElement(pincode);
	}

	public WebElement getDistrict() {
		return driver.findElement(district);
	}

	public WebElement getState() {
		return driver.findElement(with(state).toRightOf(district));
	}

	public WebElement selectCategory(String category) {
		return driver.findElement(By.cssSelector("*[value='" + category + "']"));
	}

	public WebElement getNoProprietorship(WebElement cat) {
		return driver.findElement(with(no_proprietorship).below(cat));
	}

	public WebElement getProprietorship(WebElement categoryElement) {
		return driver.findElement(with(proprietorship).below(categoryElement));

	}

	public WebElement getYesProprietorship(WebElement categoryElement) {
		return driver.findElement(with(yes_proprietorship).below(categoryElement));
	}

	public WebElement getYesProprietorshipSelect() {
		return driver.findElement(yes_proprietorship_select);
	}

	public WebElement getTextAfterYesProprietorship(WebElement proprietorship_yes) {
		return driver.findElement(with(textAfterYesProprietorship).below(proprietorship_yes));
	}

	public WebElement getProprietorshipName() {
		return driver.findElement(proprietorshipName);
	}

	public By getOtherCategory() {
		return otherCategory;
	}

	// --------------------------------------------------------------
	public WebElement get1stPanInput() {
		return driver.findElement(firstPanInput);
	}

	public WebElement get2ndPanInput() {
		return driver.findElement(secondPanInput);
	}

	public WebElement get3rdPanInput() {
		return driver.findElement(thirdPanInput);
	}

	public WebElement get4thPanInput() {
		return driver.findElement(fourthPanInput);
	}

	public WebElement get5thPanInput() {
		return driver.findElement(fifthPanInput);
	}

	public WebElement get6thPanInput() {
		return driver.findElement(sixthPanInput);
	}

	public WebElement get7thPanInput() {
		return driver.findElement(seventhPanInput);
	}

	public WebElement get8thPanInput() {
		return driver.findElement(eightthPanInput);
	}

	public WebElement get9thPanInput() {
		return driver.findElement(ninethPanInput);
	}

	public WebElement getLastPanInput() {
		return driver.findElement(lastPanInput);
	}

	// --------------------------------------------------------------

	public WebElement getPanErrorParagraph(WebElement last_pan_input) {
		return driver.findElement(with(pan_error_paragraph).toRightOf(last_pan_input));
	}

	public List<WebElement> getPanElements() {
		return driver.findElements(panElements);
	}

	public WebElement getAmountInput() {
		return driver.findElement(amount_input);
	}

	public WebElement getAmountInWords(WebElement amount_input) {
		return driver.findElement(with(amount_in_words).toRightOf(amount_input));
	}

	public WebElement getNarationInput() {
		return driver.findElement(narationInput);
	}

	public WebElement getCollectorName() {
		return driver.findElement(collectorName);
	}

	public WebElement getCollectorPhone() {
		return driver.findElement(collectorPhone);
	}

	public WebElement getWrongCollectorName(WebElement collector_name_input) {
		return driver.findElement(with(wrongCollectorName).below(collector_name_input));
	}

	public WebElement getWrongCollectorPhone(WebElement collecter_phone) {
		return driver.findElement(with(wrongCollectorPhone).below(collecter_phone));
	}

	public WebElement getDonationNature(String donation) {
		return driver.findElement(By.cssSelector("*[value='" + donation + "']"));
	}

	public WebElement getDonationNature2(String donation) {
		return driver.findElement(By.xpath("//input[@value='" + donation + "']"));
	}

	public WebElement getOtherNatureOfDonation() {
		return driver.findElement(otherNatureOfDonation);
	}

	public WebElement getPartyUnit(String party_unit) {
		return driver.findElement(By.cssSelector("*[value='" + party_unit + "']"));
	}

	public WebElement getPartyUnit2(String party_unit) {
		return driver.findElement(By.xpath("//input[@value='" + party_unit + "']"));
	}

	public By getSelectState() {
		return selectState;
	}

	public By getSelectZila() {
		return selectZila;
	}

	public By getSelectMandal() {
		return selectMandal;
	}

	public WebElement isElementPresent(WebDriver driver, String partyUnitName) {

		if (partyUnitName == "state") {
			return driver.findElement(selectState);
		} else if (partyUnitName == "zila") {
			return driver.findElement(selectZila);
		} else {
			return driver.findElement(selectMandal);
		}

	}

	public By selectGivenState(String stateName) {

		return By.xpath("//div/span[contains(text(), '" + stateName + "')]");
	}

	public By selectGivenZila(String zilaName) {

		return By.xpath("//div/span[contains(text(), '" + zilaName + "')]");
	}

	public By selectGivenMandal(String mandalName) {

		return By.xpath("//div/span[contains(text(), '" + mandalName + "')]");
	}

}
