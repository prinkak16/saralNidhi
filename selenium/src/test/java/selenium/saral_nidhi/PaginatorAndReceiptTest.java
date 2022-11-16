package selenium.saral_nidhi;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.commons.io.FileUtils;

public class PaginatorAndReceiptTest extends Base {
	public WebDriver driver;
	public NgWebDriver ngDriver;
	public static Logger log = LogManager.getLogger(PaginatorAndReceiptTest.class);
	int numberOfTimesNextClicked = 0;

	@Test
	public void page(ITestContext context) throws IOException, InterruptedException {

		driver = initializeDriver();
		ngDriver = new NgWebDriver((JavascriptExecutor) driver);
		driver.get(url);

		// LocalStorage storage = ((WebStorage) driver).getLocalStorage();
		// new SetLocalStorage(storage, driver, context);
		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		LandingPage lp = new LandingPage(driver);
		lp.login_email().sendKeys("kumar.vikastreasurer@jarvis.consulting");
		lp.login_password().sendKeys("Test@123");

		WebElement sendOTP = wait.until(ExpectedConditions.elementToBeClickable(lp.send_OTP()));
		sendOTP.click();

		WebElement enterOTP = wait.until(ExpectedConditions.visibilityOfElementLocated(lp.enter_otp()));
		enterOTP.sendKeys("227244");

		WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(lp.login_btn()));
		loginButton.click();

		log.info("Login successfully for Pagination test");

		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(2000);

		// click on count
		driver.findElement(By.cssSelector("[class='count']")).click();
		ngDriver.waitForAngularRequestsToFinish();

		Random random = new Random();
		int randomIntForPaymentModeSelection = random.nextInt(8);

		randomIntForPaymentModeSelection = randomIntForPaymentModeSelection + 1;

		randomIntForPaymentModeSelection = 6;

		// click on Payment Mode Tab
		driver.findElement(By.xpath("(//*[@class='tab-text'])[" + randomIntForPaymentModeSelection + "]")).click();
		ngDriver.waitForAngularRequestsToFinish();

		// get record count Text from Payment Mode Tab
		String tabCountText = driver
				.findElement(By.xpath("(//*[@class='tab-text-count'])[" + randomIntForPaymentModeSelection + "]"))
				.getText();

		System.out.println("tabCountText :" + tabCountText);
		String tabCountTextDigit = tabCountText.substring(1, tabCountText.length() - 1);
		int tabCountTextDigitNumber = Integer.parseInt(tabCountTextDigit);

		Thread.sleep(5000);
		JavascriptExecutor js = (JavascriptExecutor) driver;

		WebElement pageElement = driver.findElement(By.className("mat-paginator-range-label"));

		js.executeScript("arguments[0].scrollIntoView();", pageElement);

		// if No of records are > 10 then only class checkPaginator()
		if (tabCountTextDigitNumber > 10) {
			numberOfTimesNextClicked = checkPaginator(1, 10, tabCountTextDigitNumber, 0);
		}

		System.out.println(" numberOfTimesNextClicked after execution :" + numberOfTimesNextClicked);

	}

	@Test(dependsOnMethods = { "page" })
	public void receiptDownload() throws InterruptedException, IOException {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(110));

		// click on First Page button of pagination
		driver.findElement(By.xpath("//*[@aria-label='First page']")).click();
		ngDriver.waitForAngularRequestsToFinish();

		// check if receipt is generated on current page
		List<WebElement> generatedReceiptElement = driver.findElements(By.xpath("//*[@mattooltip='Download Receipt']"));

		if (generatedReceiptElement.size() > 0) {

			File folder1 = new File(System.getProperty("user.dir") + "\\downloadTestFolder");

			FileUtils.cleanDirectory(folder1);

			// click on 1st receipt button to download
			generatedReceiptElement.get(1).click();

			WebElement currentRowActionButton = driver
					.findElement(with(By.tagName("button")).toLeftOf(generatedReceiptElement.get(1)));

			currentRowActionButton.click();
			WebElement viewActionElement = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'View')]")));

			viewActionElement.click();
			ngDriver.waitForAngularRequestsToFinish();
			Thread.sleep(2000);
			WebElement donorNameElement = driver.findElement(By.xpath("//*[@formcontrolname='name']"));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", donorNameElement);
			Thread.sleep(500);

			String coppiedDonorName = donorNameElement.getAttribute("value");

			// Replacing "space " with "_" in coppiedDonorName
			coppiedDonorName = coppiedDonorName.replace(" ", "_");
			System.out.println("coppiedDonorName :" + coppiedDonorName);
			Thread.sleep(5000);
			File folder = new File(System.getProperty("user.dir") + "\\downloadTestFolder");

			// List the files on that folder
			File[] listOfFiles = folder.listFiles();
			boolean found = false;
			String fileName = null;
			for (File listOfFile : listOfFiles) {
				if (listOfFile.isFile()) {
					fileName = listOfFile.getName();
					System.out.println("downloaded fileName is :" + fileName);

					if (fileName.contains(coppiedDonorName)) {
						found = true;
						log.info("downloaded file :" + fileName);
						System.out.println("###################### fileName is found.............." + fileName);
					}
				}
			}

			Assert.assertTrue(found, "Downloaded document is not found");

			File file = new File(System.getProperty("user.dir") + "\\downloadTestFolder\\" + fileName);
			System.out.println("delete file Absolute path :" + file.getAbsolutePath());

			if (file.delete()) {
				System.out.println("file deleted success");
				log.info(fileName + " file deleted success");
			} else {
				System.out.println("file delete fail");
				log.error(fileName + " file delete fail");
			}

		} else {
			System.out.println("************ receipt is not found at the current page **************");
		}
	}

	private int checkPaginator(int startingRange, int endingRange, int total, int numberOfTimesNextClicked)
			throws InterruptedException {

		if (endingRange >= total) {
			return numberOfTimesNextClicked;
		}

		// get the text for pagination
		String paginatorText = driver.findElement(By.className("mat-paginator-range-label")).getText();

		System.out.println("paginatorText :" + paginatorText);

		String[] paginatorTextArr = paginatorText.split("of");

		String paginatorTextBefore_of = paginatorTextArr[0];

		System.out.println("paginatorTextBefore_of :" + paginatorTextBefore_of);

		String totalEnteredRecord = paginatorTextArr[1];
		System.out.println("totalEnteredRecord :" + totalEnteredRecord);

		String[] paginatorRangeArr = paginatorTextArr[0].split(" – ");

		System.out.println("paginatorRangeArr[0] :" + paginatorRangeArr[0]);

		int coppiedTotalRecord = Integer.parseInt(totalEnteredRecord.trim());
		System.out.println("coppiedTotalRecord :" + coppiedTotalRecord);

		int coppiedStartRange = Integer.parseInt(paginatorRangeArr[0].trim());
		System.out.println("coppiedStartRange :" + coppiedStartRange);

		int coppiedEndRange = Integer.parseInt(paginatorRangeArr[1].trim());
		System.out.println("coppiedEndRange :" + coppiedEndRange);

		startingRange = coppiedStartRange + 10;

		// If (coppiedTotalRecord - coppiedEndRange) < 10 then add that number else add
		// 10

		if ((coppiedTotalRecord - endingRange) < 10) {
			endingRange = endingRange + (coppiedTotalRecord - endingRange);
		} else {
			endingRange = coppiedEndRange + 10;
		}
		total = coppiedTotalRecord;

		// click on next page icon of pagination
		driver.findElement(By.xpath("//*[@aria-label='Next page']")).click();
		ngDriver.waitForAngularRequestsToFinish();
		Thread.sleep(5000);
		return checkPaginator(startingRange, endingRange, total, numberOfTimesNextClicked + 1);

	}
}

//----------------------- previous-----------------------//

/*
 * package selenium.saral_nidhi;
 * 
 * import java.io.File; import java.io.IOException; import java.time.Duration;
 * import java.util.ArrayList; import java.util.List; import java.util.Random;
 * import org.apache.logging.log4j.LogManager; import
 * org.apache.logging.log4j.Logger; import org.openqa.selenium.By; import
 * org.openqa.selenium.JavascriptExecutor; import org.openqa.selenium.Keys;
 * import org.openqa.selenium.WebDriver; import org.openqa.selenium.WebElement;
 * import org.openqa.selenium.support.ui.ExpectedConditions; import
 * org.openqa.selenium.support.ui.WebDriverWait; import org.testng.Assert;
 * import org.openqa.selenium.html5.LocalStorage; import
 * org.openqa.selenium.html5.WebStorage; import org.testng.ITestContext; import
 * org.testng.annotations.AfterClass; import org.testng.annotations.Test; import
 * com.paulhammant.ngwebdriver.NgWebDriver; import pageObjects.LandingPage;
 * import resources.Base; import static
 * org.openqa.selenium.support.locators.RelativeLocator.with;
 * 
 * public class RoughTest extends Base { public WebDriver driver; public
 * NgWebDriver ngDriver; public static Logger log =
 * LogManager.getLogger(RoughTest.class); int numberOfTimesNextClicked = 0;
 * 
 * @Test public void page(ITestContext context) throws IOException,
 * InterruptedException {
 * 
 * driver = initializeDriver(); ngDriver = new NgWebDriver((JavascriptExecutor)
 * driver); driver.get(url);
 * 
 * // LocalStorage storage = ((WebStorage) driver).getLocalStorage(); // new
 * SetLocalStorage(storage, driver, context); // explicit wait WebDriverWait
 * wait = new WebDriverWait(driver, Duration.ofSeconds(50));
 * 
 * LandingPage lp = new LandingPage(driver);
 * lp.login_email().sendKeys("kumar.vikastreasurer@jarvis.consulting");
 * lp.login_password().sendKeys("Test@123");
 * 
 * WebElement sendOTP =
 * wait.until(ExpectedConditions.elementToBeClickable(lp.send_OTP()));
 * sendOTP.click();
 * 
 * WebElement enterOTP =
 * wait.until(ExpectedConditions.visibilityOfElementLocated(lp.enter_otp()));
 * enterOTP.sendKeys("227244");
 * 
 * WebElement loginButton =
 * wait.until(ExpectedConditions.elementToBeClickable(lp.login_btn()));
 * loginButton.click();
 * 
 * log.info("Login successfully for Pagination test");
 * 
 * ngDriver.waitForAngularRequestsToFinish(); Thread.sleep(2000);
 * 
 * // click on count
 * driver.findElement(By.cssSelector("[class='count']")).click();
 * ngDriver.waitForAngularRequestsToFinish();
 * 
 * Random random = new Random(); int randomIntForPaymentModeSelection =
 * random.nextInt(8);
 * 
 * randomIntForPaymentModeSelection = randomIntForPaymentModeSelection + 1;
 * 
 * randomIntForPaymentModeSelection = 6;
 * 
 * // click on Payment Mode Tab
 * driver.findElement(By.xpath("(//*[@class='tab-text'])[" +
 * randomIntForPaymentModeSelection + "]")).click();
 * ngDriver.waitForAngularRequestsToFinish();
 * 
 * // get record count Text from Payment Mode Tab String tabCountText = driver
 * .findElement(By.xpath("(//*[@class='tab-text-count'])[" +
 * randomIntForPaymentModeSelection + "]")) .getText();
 * 
 * System.out.println("tabCountText :" + tabCountText); String tabCountTextDigit
 * = tabCountText.substring(1, tabCountText.length() - 1); int
 * tabCountTextDigitNumber = Integer.parseInt(tabCountTextDigit);
 * 
 * Thread.sleep(5000); JavascriptExecutor js = (JavascriptExecutor) driver;
 * 
 * WebElement pageElement =
 * driver.findElement(By.className("mat-paginator-range-label"));
 * 
 * js.executeScript("arguments[0].scrollIntoView();", pageElement);
 * 
 * // if No of records are > 10 then only class checkPaginator() if
 * (tabCountTextDigitNumber > 10) { numberOfTimesNextClicked = checkPaginator(1,
 * 10, tabCountTextDigitNumber, 0); }
 * 
 * System.out.println(" numberOfTimesNextClicked after execution :" +
 * numberOfTimesNextClicked);
 * 
 * }
 * 
 * @Test(dependsOnMethods = { "page" }) public void receiptDownload() throws
 * InterruptedException, IOException {
 * 
 * WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(110));
 * 
 * // click on First Page button of pagination
 * driver.findElement(By.xpath("//*[@aria-label='First page']")).click();
 * ngDriver.waitForAngularRequestsToFinish();
 * 
 * // check if receipt is generated on current page List<WebElement>
 * generatedReceiptElement =
 * driver.findElements(By.xpath("//*[@mattooltip='Download Receipt']"));
 * 
 * if (generatedReceiptElement.size() > 0) { // click on 1st receipt button to
 * download generatedReceiptElement.get(0).click();
 * 
 * WebElement currentRowActionButton =
 * driver.findElement(with(By.tagName("button")).toLeftOf(
 * generatedReceiptElement.get(0)));
 * 
 * currentRowActionButton.click(); WebElement viewActionElement = wait
 * .until(ExpectedConditions.elementToBeClickable(By.xpath(
 * "//span[contains(text(),'View')]")));
 * 
 * viewActionElement.click(); ngDriver.waitForAngularRequestsToFinish();
 * Thread.sleep(2000); WebElement donorNameElement =
 * driver.findElement(By.xpath("//*[@formcontrolname='name']"));
 * ((JavascriptExecutor)
 * driver).executeScript("arguments[0].scrollIntoView(true);",
 * donorNameElement); Thread.sleep(500);
 * 
 * String coppiedDonorName = donorNameElement.getAttribute("value");
 * 
 * //Replacing "space " with "_" in coppiedDonorName
 * coppiedDonorName=coppiedDonorName.replace(" ", "_");
 * System.out.println("coppiedDonorName :"+coppiedDonorName);
 * Thread.sleep(5000); File folder = new File(System.getProperty("user.dir") +
 * "\\downloadTestFolder");
 * 
 * // List the files on that folder File[] listOfFiles = folder.listFiles();
 * boolean found = false; String fileName = null; for (File listOfFile :
 * listOfFiles) { if (listOfFile.isFile()) { fileName = listOfFile.getName();
 * System.out.println("downloaded fileName is :" + fileName);
 * 
 * //found = true;
 * 
 * if (fileName.contains("coppiedDonorName")) { found = true;
 * log.info("downloaded file :" + fileName);
 * System.out.println("###################### fileName is found.............." +
 * fileName); }
 * 
 * 
 * } }
 * 
 * Assert.assertTrue(found, "Downloaded document is not found");
 * 
 * File file = new File(System.getProperty("user.dir") +
 * "\\downloadTestFolder\\" + fileName);
 * System.out.println("delete file Absolute path :" + file.getAbsolutePath());
 * 
 * if (file.delete()) { System.out.println("file deleted success");
 * log.info(fileName + " file deleted success"); } else {
 * System.out.println("file delete fail"); log.error(fileName +
 * " file delete fail"); }
 * 
 * } else { System.out.
 * println("************ receipt is not found at the current page **************"
 * ); } }
 * 
 * private int checkPaginator(int startingRange, int endingRange, int total, int
 * numberOfTimesNextClicked) throws InterruptedException {
 * 
 * if (endingRange >= total) { return numberOfTimesNextClicked; }
 * 
 * // get the text for pagination String paginatorText =
 * driver.findElement(By.className("mat-paginator-range-label")).getText();
 * 
 * System.out.println("paginatorText :" + paginatorText);
 * 
 * String[] paginatorTextArr = paginatorText.split("of");
 * 
 * String paginatorTextBefore_of = paginatorTextArr[0];
 * 
 * System.out.println("paginatorTextBefore_of :" + paginatorTextBefore_of);
 * 
 * String totalEnteredRecord = paginatorTextArr[1];
 * System.out.println("totalEnteredRecord :" + totalEnteredRecord);
 * 
 * String[] paginatorRangeArr = paginatorTextArr[0].split(" – ");
 * 
 * System.out.println("paginatorRangeArr[0] :" + paginatorRangeArr[0]);
 * 
 * int coppiedTotalRecord = Integer.parseInt(totalEnteredRecord.trim());
 * System.out.println("coppiedTotalRecord :" + coppiedTotalRecord);
 * 
 * int coppiedStartRange = Integer.parseInt(paginatorRangeArr[0].trim());
 * System.out.println("coppiedStartRange :" + coppiedStartRange);
 * 
 * int coppiedEndRange = Integer.parseInt(paginatorRangeArr[1].trim());
 * System.out.println("coppiedEndRange :" + coppiedEndRange);
 * 
 * startingRange = coppiedStartRange + 10;
 * 
 * // If (coppiedTotalRecord - coppiedEndRange) < 10 then add that number else
 * add // 10
 * 
 * if ((coppiedTotalRecord - endingRange) < 10) { endingRange = endingRange +
 * (coppiedTotalRecord - endingRange); } else { endingRange = coppiedEndRange +
 * 10; } total = coppiedTotalRecord;
 * 
 * // click on next page icon of pagination
 * driver.findElement(By.xpath("//*[@aria-label='Next page']")).click();
 * ngDriver.waitForAngularRequestsToFinish(); Thread.sleep(5000); return
 * checkPaginator(startingRange, endingRange, total, numberOfTimesNextClicked +
 * 1);
 * 
 * } }
 */
