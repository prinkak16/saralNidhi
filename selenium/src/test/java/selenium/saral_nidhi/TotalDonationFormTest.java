package selenium.saral_nidhi;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import pageObjects.Cheque_ModePage;
import pageObjects.LandingPage;
import resources.Base;

public class TotalDonationFormTest extends Base {
	public WebDriver driver;
	public static Logger log = LogManager.getLogger(RoughTest.class);

	@Test
	public void basePageNavigation(ITestContext context) throws IOException, InterruptedException {
		driver = initializeDriver();

		driver.get(url);

		//LocalStorage storage = ((WebStorage) driver).getLocalStorage();

		//new SetLocalStorage(storage, driver, context);
		ArrayList<String> a = new ArrayList<String>();

		DataDriven dd = new DataDriven();
		ArrayList<String> excel_data = dd.getData("Cheque_ModeTest", a);

		 LandingPage lp = new LandingPage(driver);
		Cheque_ModePage chequePage = new Cheque_ModePage(driver);
		// explicit wait
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

		
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
		 
		log.info("Login successfully in Cheque_ModeTest");

		WebElement heading1 = wait.until(ExpectedConditions.visibilityOfElementLocated(chequePage.getFirstHeading()));
		Assert.assertEquals(heading1.getText(), "Nidhi Collection");

		chequePage.getIndianDonationForm().click();

		WebElement paymentModeOption = wait
				.until(ExpectedConditions.visibilityOfElementLocated(chequePage.getChequePaymentMode()));

		// cheque should not be selected
		Assert.assertFalse(chequePage.getChequeModeValue().isSelected());

		paymentModeOption.click();
		
		log.info("action test");
		driver.findElement(By.cssSelector("[class='header-title-span']")).click();
		
		//Cheque_ModePage chequePage = new Cheque_ModePage(driver);
		
		WebElement heading2 = wait.until(ExpectedConditions.visibilityOfElementLocated(chequePage.getFirstHeading()));
		Assert.assertEquals(heading2.getText(), "Nidhi Collection");
		WebElement c = driver.findElement(By.cssSelector("[class='total-count-div']"));
		WebElement count = wait.until(ExpectedConditions.elementToBeClickable(c));
		count.click();
		
		String header = driver.findElement(By.className("header-text")).getText();
		Assert.assertEquals(header,"Donation List");
		
		// click on cheque tab
			WebElement paymentMode = driver.findElement(By.xpath("(//*[@class='tab-text'])[2]"));
			paymentMode.click();
						
		JavascriptExecutor js = (JavascriptExecutor) driver;
		//String[] actionArr = {"View","Edit","Reversed","Archive"};
		String[] actionArr = {"View","Edit"};
		
		for(String action : actionArr) {
			
			List<WebElement> list=driver.findElements(By.className("mat-menu-trigger"));
			
			System.out.println("list :"+list.size());
			WebElement row = (WebElement) list.get(0);
			
				try {
					Thread.sleep(1500);
					((JavascriptExecutor)driver).executeScript("arguments[0].click();",row);
					//row.click();
					Thread.sleep(1500);
					
				}
				catch (StaleElementReferenceException e) {
					System.out.println("*** inside catch ***");
					row = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//*[@class='mat-icon notranslate material-icons mat-icon-no-color'])[1]")));
					//row.click();
					((JavascriptExecutor)driver).executeScript("arguments[0].click();",row);
				}	
			//}
			Thread.sleep(1500);
			//WebElement actionElement = wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//span[contains(text(),'"+action+"')]"))));
			System.out.println("before locating action :"+action);
			
			List<WebElement> actionItem = driver.findElements(By.className("mat-menu-item")); 
			
			System.out.println("actionItem :"+actionItem.size());
			WebElement actionElement;
			if(action == "View") {
				actionElement = (WebElement) actionItem.get(0);	
			}
			else if (action == "Edit") {
				actionElement = (WebElement) actionItem.get(1);
			}
			else {
				actionElement = (WebElement) actionItem.get(2);
			}
			 
			System.out.println("Before clicking :"+action);

			
			//for(int i=0;i<4;i++) {
				//System.out.println("Attempt for actionElemnt :"+i);
				try {
					System.out.println("try block...");
					//((JavascriptExecutor)driver).executeScript("arguments[0].click();",actionElement);
					WebElement el= wait.until(ExpectedConditions.elementToBeClickable(actionElement));
					el.click();
				}
				catch (StaleElementReferenceException e) {
					System.out.println("catch block...");
					actionElement = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//span[contains(text(),'"+action+"')]"))));
					//row.click();
					((JavascriptExecutor)driver).executeScript("arguments[0].click();",actionElement);
				}	
			//}
			
			
			if (action=="View") {
				System.out.println("for clicking View");
				Thread.sleep(2000);
				Boolean b = driver.findElement(By.cssSelector("*[value='1']")).isSelected();
				Boolean ca = driver.findElement(By.xpath("//input[@value='1']")).isSelected();
				
				System.out.println("cheque is selected :"+b);
				System.out.println("cheque is selected2 :"+ca);
				
				String date = driver.findElement(By.xpath("//*[@formcontrolname='date_of_cheque']")).getAttribute("value");
				System.out.println("-------------------- total form count ---------------------");
				System.out.println("date :"+date);
				String chequeNo = driver.findElement(By.xpath("//*[@formcontrolname='cheque_number']")).getAttribute("value");
				System.out.println("chequeNo :"+chequeNo);
				String ifsc_code = driver.findElement(By.xpath("//*[@formcontrolname='ifsc_code']")).getAttribute("value");
				System.out.println("ifsc_code :"+ifsc_code);
				String bank_name = driver.findElement(By.xpath("//*[@formcontrolname='bank_name']")).getAttribute("value");
				System.out.println("bank_name :"+bank_name);
				String branch_name = driver.findElement(By.xpath("//*[@formcontrolname='branch_name']")).getAttribute("value");
				System.out.println("branch_name :"+branch_name);
				String branch_address = driver.findElement(By.xpath("//*[@formcontrolname='branch_address']")).getAttribute("value");
				System.out.println("branch_address :"+branch_address);
			
				String donorName = driver.findElement(By.xpath("//*[@formcontrolname='name']")).getAttribute("value");
				System.out.println("donorName :"+donorName);
				
				String donor_phone = driver.findElement(By.xpath("//*[@formcontrolname='phone']")).getAttribute("value");
				System.out.println("donor_phone :"+donor_phone);
				
				String donor_email = driver.findElement(By.xpath("//*[@formcontrolname='email']")).getAttribute("value");
				System.out.println("donor_email :"+donor_email);
				
				String donor_house = driver.findElement(By.xpath("//*[@formcontrolname='house']")).getAttribute("value");
				System.out.println("donor_house :"+donor_house);
				
                String locality = chequePage.getLocality().getAttribute("value");
                System.out.println("locality :"+locality);
                
                String pincode = chequePage.getPinCode().getAttribute("value");
                System.out.println("pincode :"+pincode);
                
                js.executeScript("window.scrollBy(0,1350)", "");
                
                //String district = chequePage.getDistrict().getAttribute("value");
                //System.out.println("district :"+district);
        		boolean district_bool = wait
        				.until(ExpectedConditions.textToBePresentInElementValue(chequePage.getDistrict(), "Pune"));
        		String copied_district = null;

        		if (district_bool) {
        			copied_district = chequePage.getDistrict().getAttribute("value");
        		} else {
        			log.error("district name didn't come till 30 sec");
        		}

        		System.out.println(copied_district);
                
                String state = chequePage.getState().getText();
                System.out.println("state :"+state);
                
                String[] arr_category = { "individual", "huf", "partnership", "trust", "corporation", "others" };
               
                
                for(String category:arr_category) {
                	//Thread.sleep(2000);
           
                	WebElement selected2 = driver.findElement(By.xpath("(//input[@value='"+category+"'])"));
                             	
                	if(selected2.isSelected()) {
                		System.out.println("yes "+category+" is selected");
                	}
                }
                
                Thread.sleep(2000);
    			String pan1 = chequePage.get1stPanInput().getAttribute("value");
    			String pan2 = chequePage.get2ndPanInput().getAttribute("value");
    			String pan3 = chequePage.get3rdPanInput().getAttribute("value");
    			String pan4 = chequePage.get4thPanInput().getAttribute("value");
    			
    			String pan5 = chequePage.get5thPanInput().getAttribute("value");
    			String pan6 = chequePage.get6thPanInput().getAttribute("value");
    			String pan7 = chequePage.get7thPanInput().getAttribute("value");
    			String pan8 = chequePage.get8thPanInput().getAttribute("value");
    			
    			String pan9 = chequePage.get9thPanInput().getAttribute("value");
    			String pan10 = chequePage.getLastPanInput().getAttribute("value");
    			
    			System.out.println("pan :"+pan1 +" "+pan2+" "+pan3+" "+pan4+" "+pan5+" "+pan6+" "+pan7+" "+pan8+" "+pan9+" "+pan10);
    			
    			String amount = driver.findElement(By.xpath("//*[@formcontrolname='amount']")).getAttribute("value");
    			
    			System.out.println("amount :"+amount);
    			
    			String amount_txt = driver.findElement(By.className("amount-in-text")).getText();
    			
    			System.out.println("amount_txt :"+amount_txt);
    			
    			String narration = driver.findElement(By.xpath("//*[@formcontrolname='narration']")).getAttribute("value");
    			
    			System.out.println("narration :"+narration);
    			
                String collector_name = driver.findElement(By.xpath("//*[@formcontrolname='collector_name']")).getAttribute("value");
    			
    			System.out.println("collector_name :"+collector_name); 
    			
    			String collector_phone = driver.findElement(By.xpath("//*[@formcontrolname='collector_phone']")).getAttribute("value");
    			
    			System.out.println("collector_phone :"+collector_phone); 
    			
    			
    			js.executeScript("window.scrollBy(0,750)", "");
    			
    			String[] arr_donation_nature = { "Voluntary Contribution", "Aajivan Sahyog Nidhi.", "Other" };
             
                for(String donationType:arr_donation_nature) {
                	//Thread.sleep(2000);
           
                	WebElement selected3 = driver.findElement(By.xpath("(//input[@value='"+donationType+"'])"));
                             	
                	if(selected3.isSelected()) {
                		System.out.println("yes "+donationType+" is selected");
                		
                		if(donationType=="Other") {
                    		String other_nature_of_donation = driver.findElement(By.xpath("//*[@formcontrolname='other_nature_of_donation']")).getAttribute("value");
                			
                			System.out.println("other_nature_of_donation :"+other_nature_of_donation);
                    	}
                	}
                }
                
                String[] arr_partyUnit = { "CountryState", "Zila", "Mandal" };
                
                for(String partyUnit:arr_partyUnit) {
                	//Thread.sleep(2000);
           
                	WebElement selected4 = driver.findElement(By.xpath("(//input[@value='"+partyUnit+"'])"));
                             	
                	if(selected4.isSelected()) {
                		System.out.println("yes "+partyUnit+" is selected");
                	}
                }
                
                //boolean bState = isDisplayedElement(driver.findElement(By.xpath("(//*[@class='ng-value-label ng-star-inserted'])[2]")));
                boolean bState = isDisplayedElement(driver,By.xpath("(//*[@class='ng-value-label ng-star-inserted'])[2]"));
                System.out.println("bState :"+bState);
                boolean bZila = isDisplayedElement(driver,By.xpath("(//*[@class='ng-value-label ng-star-inserted'])[3]"));
                System.out.println("bZila :"+bZila);
                boolean bMandal = isDisplayedElement(driver,By.xpath("(//*[@class='ng-value-label ng-star-inserted'])[4]"));
            
                System.out.println("bMandal :"+bMandal);
                
                String stateName = "";
                String zilaName = "";
                String mandalName = "";
                
                if(bState) {
                	stateName = driver.findElement(By.xpath("(//*[@class='ng-value-label ng-star-inserted'])[2]")).getText();
                }
                
                if(bZila) {
                	zilaName = driver.findElement(By.xpath("(//*[@class='ng-value-label ng-star-inserted'])[3]")).getText();
                }
                
                if(bMandal) {
                   mandalName = driver.findElement(By.xpath("(//*[@class='ng-value-label ng-star-inserted'])[4]")).getText();
                }
                System.out.println("stateName :"+stateName);
                System.out.println("zilaName :"+zilaName);
                System.out.println("mandalName :"+mandalName);
                
                // click on back icon
                driver.findElement(By.className("back-icon")).click(); 
                
			}
			else if(action=="Reversed") {
				driver.findElement(By.tagName("textarea")).sendKeys("aa");
			}
			else if(action=="Edit") {
				System.out.println("Edit section...");
				js.executeScript("window.scrollBy(0,750)", "");
				//chequePage.uploadFrontImage();
				//chequePage.uploadBackImage();
				
				chequePage.getAccountNumber().clear();
				chequePage.getAccountNumber().sendKeys(excel_data.get(6));

				chequePage.getIfscCode().clear();
				chequePage.getIfscCode().sendKeys(excel_data.get(7));
				
				chequePage.getAccountNumber().clear();
				chequePage.getAccountNumber().sendKeys(excel_data.get(6));
				
				chequePage.getIfscCode().clear();
				chequePage.getIfscCode().sendKeys(excel_data.get(7));
				
				WebElement bankdetails = wait.until(ExpectedConditions.visibilityOfElementLocated(chequePage.getbankDetails()));

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
				
				chequePage.getDonorName().clear();
				chequePage.getDonorName().sendKeys(donor_name);
				String copied_name = chequePage.getDonorName().getAttribute("value");

				System.out.println("copied_name:" + copied_name);
				chequePage.getDonorPhoneNumber().clear();
				chequePage.getDonorPhoneNumber().sendKeys(donor_phone);
				chequePage.getDonorEmail().clear();
				chequePage.getDonorEmail().sendKeys(donor_email);
				
				chequePage.getHouse().clear();
				chequePage.getHouse().sendKeys(excel_data.get(11));
				chequePage.getLocality().clear();
				chequePage.getLocality().sendKeys(excel_data.get(12));
				chequePage.getPinCode().clear();
				chequePage.getPinCode().sendKeys(excel_data.get(13));
				
				boolean district_bool = wait
						.until(ExpectedConditions.textToBePresentInElementValue(chequePage.getDistrict(), excel_data.get(14)));
				String copied_district = null;

				if (district_bool) {
					copied_district = chequePage.getDistrict().getAttribute("value");
				} else {
					log.error("district name didn't come till 30 sec");
				}

				System.out.println(copied_district);

				boolean state_bool = wait
						.until(ExpectedConditions.textToBePresentInElement(chequePage.getState(), excel_data.get(15)));

				String copied_state = null;
				if (state_bool) {
					copied_state = chequePage.getState().getText();
					System.out.println("copied_state :" + copied_state);
				} else {
					log.error("state name didn't come till 30 sec");
				}

				//Assert.assertEquals(copied_district, excel_data.get(14));
				//Assert.assertEquals(copied_state, excel_data.get(15));
				
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("window.scrollBy(0,310)");
				
				String categoryChangeTo = "others";
				String proprietorshipChangeTo = "yes";
				String name_of_proprietorship ="abcd lte";
				
				WebElement categoryElement = chequePage.selectCategory(categoryChangeTo);
	            wait.until(ExpectedConditions.elementToBeClickable(categoryElement)).click();
	            
	            if(categoryChangeTo == "individual") {
	            	if(proprietorshipChangeTo == "yes") {
	            		WebElement proprietorship_yes = chequePage.getYesProprietorship(categoryElement);
	    				proprietorship_yes.click();

	    				Thread.sleep(1000);

	    				boolean b1 = chequePage.getYesProprietorshipSelect().isSelected();

	    				System.out.println("proprietorship_yes is selected or not --->:" + b1);
	    				Assert.assertTrue(b1);

	    				WebElement Proprietorship_txt2 = chequePage.getTextAfterYesProprietorship(proprietorship_yes);

	    				System.out.println(Proprietorship_txt2.getText());
	    				Assert.assertEquals(Proprietorship_txt2.getText(), "Write the name of the Proprietorship");
	    				// type Name of proprietorship
	    				chequePage.getProprietorshipName().sendKeys(name_of_proprietorship);
	            	}
	            	else {
	            		WebElement proprietorship_no = chequePage.getNoProprietorship(categoryElement);
	    				proprietorship_no.click();
	    				boolean b2 = proprietorship_no.isSelected();
	    				Assert.assertTrue(b2);
	            	}
	            }
	            else if (categoryChangeTo == "others") {
	            	WebElement other_category = wait
							.until(ExpectedConditions.visibilityOfElementLocated(chequePage.getOtherCategory()));

					boolean visible_other_category = other_category.isDisplayed();
					Assert.assertTrue(visible_other_category);
					other_category.sendKeys("others updating");
				}
	            
	            String[] changePanNoTo = { "F", "O", "o", "j", "V", "3", "2", "3", "1", "k" };
	            
	            chequePage.get1stPanInput().clear();
	            chequePage.get1stPanInput().sendKeys(changePanNoTo[0]);
	            chequePage.get2ndPanInput().sendKeys(changePanNoTo[1]);
	    		chequePage.get3rdPanInput().sendKeys(changePanNoTo[2]);
	    		chequePage.get4thPanInput().sendKeys(changePanNoTo[3]);
	    		chequePage.get5thPanInput().sendKeys(changePanNoTo[4]);
	    		chequePage.get6thPanInput().sendKeys(changePanNoTo[5]);
	    		chequePage.get7thPanInput().sendKeys(changePanNoTo[6]);
	    		chequePage.get8thPanInput().sendKeys(changePanNoTo[7]);
	    		chequePage.get9thPanInput().sendKeys(changePanNoTo[8]);
	    		chequePage.getLastPanInput().sendKeys(changePanNoTo[9]);
	    		
	    		// last pan input
				WebElement last_pan_input = chequePage.getLastPanInput();
	    		
	    		// invalid pan no
				String text_for_invalid_pan = pan_letters_varification(driver, chequePage);
				System.out.println("text_for_invalid_pan :" + text_for_invalid_pan);
				
				// 4th and 5th letter
				String error_txt_for_4th_and_5th_letter = pan_letters_category_validation(categoryChangeTo,name_of_proprietorship, copied_name, changePanNoTo,proprietorshipChangeTo);
				System.out.println("error_txt_for_4th_and_5th_letter: " + error_txt_for_4th_and_5th_letter);
				
				if (text_for_invalid_pan != "") {
					
					System.out.println("from inside if condition..");

					WebElement pan_error_paragraph = chequePage.getPanErrorParagraph(last_pan_input);
					System.out.println("Assertion for text_for_invalid_pan pass....");
					
					chequePage.uploadWrongPanImage();
					chequePage.getWrongPanRemark().clear();
					chequePage.getWrongPanRemark().sendKeys("text_for_invalid_pan");
					
					Assert.assertEquals(text_for_invalid_pan, pan_error_paragraph.getText());
				} else if (error_txt_for_4th_and_5th_letter != "") {
					
					System.out.println("from inside elseif condition..");
					WebElement pan_error_paragraph = chequePage.getPanErrorParagraph(last_pan_input);
					System.out.println("Assertion for error_txt_for_4th_and_5th_letter pass....");
					
					chequePage.uploadWrongPanImage();
					chequePage.getWrongPanRemark().clear();
					chequePage.getWrongPanRemark().sendKeys("error_txt_for_4th_and_5th_letter");
					
					Assert.assertEquals(error_txt_for_4th_and_5th_letter, pan_error_paragraph.getText());
				}

				else {
					
					System.out.println("from inside else condition..");
					System.out.println("correct category & pan selection");
				}
				
				Random rand = new Random();
				int amount = Integer.parseInt(excel_data.get(17));
				int random_int_amount = rand.nextInt(amount);
				String random_int_amount1 = Integer.toString(random_int_amount);

				WebElement amount_input = chequePage.getAmountInput();

				String amount_converted_in_words;
				amount_input.clear();
				amount_input.sendKeys(random_int_amount1);
				amount_converted_in_words = convertToIndianCurrency(random_int_amount1);

				// Remove extra whitespace if any
				amount_converted_in_words = amount_converted_in_words.replaceAll("\\s+", " ");

				String amount_in_words_txt = chequePage.getAmountInWords(amount_input).getText();

				System.out.println("amount_converted_in_words **** :" + amount_converted_in_words);
				System.out.println("amount_in_workds_txt **** :" + amount_in_words_txt);

				Assert.assertEquals(amount_converted_in_words, amount_in_words_txt);
				chequePage.getNarationInput().clear();
				chequePage.getNarationInput().sendKeys(excel_data.get(18));
				
				WebElement collector_name_input = chequePage.getCollectorName();
                
				collector_name_input.clear();
				collector_name_input.sendKeys("vk");
				WebElement collecter_phone = chequePage.getCollectorPhone();
				collecter_phone.click();

				 wait.until(ExpectedConditions.textToBePresentInElement(chequePage.getWrongCollectorName(collector_name_input),"Please enter a valid name"));
				collector_name_input.clear();
				collector_name_input.sendKeys(excel_data.get(19));

				collecter_phone.clear();
				// wrong collector phone no
				collecter_phone.sendKeys("23222322");
				wait.until(ExpectedConditions.textToBePresentInElement(chequePage.getWrongCollectorPhone(collecter_phone), "Please enter correct phone number"));
				collecter_phone.clear();
				collecter_phone.sendKeys(excel_data.get(20));
				
				jse.executeScript("window.scrollBy(0,310)");
				
				String donationNature ="Other";
				chequePage.getDonationNature(donationNature).click();
				Assert.assertTrue(chequePage.getDonationNature2(donationNature).isSelected());
				
				if (donationNature == "Other") {
					chequePage.getOtherNatureOfDonation().clear();
					chequePage.getOtherNatureOfDonation().sendKeys(excel_data.get(21));
				}
				
				String party_unit = "Mandal";
				
				if(party_unit=="State") {
					party_unit="CountryState";
				}
				

					WebElement party_unit_type = chequePage.getPartyUnit(party_unit);
					party_unit_type.click();
					System.out.println(chequePage.getPartyUnit2(party_unit).isSelected());

					Assert.assertTrue(chequePage.getPartyUnit2(party_unit).isSelected());

					//boolean state_exist = chequePage.isElementPresent(driver, "state").isDisplayed();

					//System.out.println("state_exist :" + state_exist);
					//Assert.assertEquals(state_exist, true);
					
					String state_unit_name = excel_data.get(22);

					System.out.println("state_unit_name :" + state_unit_name);
					String zila_unit_name = excel_data.get(23);
					String mandal_unit_name = excel_data.get(24);
					System.out.println("zila_unit_name :" + zila_unit_name);
					System.out.println("mandal_unit_name :" + mandal_unit_name);

					if (party_unit == "CountryState") {
						Thread.sleep(2000);

						WebElement selectState = wait
								.until(ExpectedConditions.visibilityOfElementLocated(chequePage.getSelectState()));

						selectState.click();

						WebElement state_unit = wait.until(
								ExpectedConditions.visibilityOfElementLocated(chequePage.selectGivenState(state_unit_name)));
						state_unit.click();

					} else if (party_unit == "Zila") {
						// select state
						WebElement selectState = wait
								.until(ExpectedConditions.visibilityOfElementLocated(chequePage.getSelectState()));
						selectState.click();

						WebElement state_from_zila = wait.until(ExpectedConditions.visibilityOfElementLocated(chequePage.selectGivenState(state_unit_name)));
						
						try {
							state_from_zila.click();
						}
						catch (StaleElementReferenceException e) {
							state_from_zila = wait.until(ExpectedConditions.visibilityOfElementLocated(chequePage.selectGivenState(state_unit_name)));
							state_from_zila.click();
						}
						
						// select zila
						WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(chequePage.getSelectZila()));
						selectZila.click();

						WebElement zila_unit = wait.until(
								ExpectedConditions.visibilityOfElementLocated(chequePage.selectGivenZila(zila_unit_name)));
						zila_unit.click();

					} else {

						// select state
						WebElement selectState = wait
								.until(ExpectedConditions.visibilityOfElementLocated(chequePage.getSelectState()));
						selectState.click();
						
						WebElement state_from_mandal = wait.until(
								ExpectedConditions.visibilityOfElementLocated(chequePage.selectGivenState(state_unit_name)));
					
						try {
							state_from_mandal.click();
						}
						catch (StaleElementReferenceException e) {
							state_from_mandal = wait.until(ExpectedConditions.visibilityOfElementLocated(chequePage.selectGivenState(state_unit_name)));
							state_from_mandal.click();
						}
						
						// select zila
						WebElement selectZila = wait.until(ExpectedConditions.elementToBeClickable(chequePage.getSelectZila()));
						selectZila.click();

						WebElement zila_unit = wait.until(
								ExpectedConditions.visibilityOfElementLocated(chequePage.selectGivenZila(zila_unit_name)));
						
						try {
							zila_unit.click();
						}
						catch (StaleElementReferenceException e) {
							zila_unit = wait.until(ExpectedConditions.visibilityOfElementLocated(chequePage.selectGivenZila(zila_unit_name)));
							zila_unit.click();
						}
						
						// select mandal
						WebElement selectMandal = wait
								.until(ExpectedConditions.elementToBeClickable(chequePage.getSelectMandal()));
						selectMandal.click();

						WebElement mandal_unit = wait.until(
								ExpectedConditions.visibilityOfElementLocated(chequePage.selectGivenMandal(mandal_unit_name)));
						mandal_unit.click();

					}
				 
				 driver.findElement(By.xpath("//button[@color='primary']")).click();
				 
				
				 //WebElement updateText = driver.findElement(By.xpath("//span[contains(text(),'Record Update successfully')]"));
				 
				 String updateText = driver.findElement(By.xpath("//span[contains(text(),'Record Update successfully')]")).getText();
				 
				 System.out.println("updateText :"+updateText);
	    		
			}
			else {
				System.out.println("else part running...");
			}
			
		}
	}
	
	private static String pan_letters_category_validation(String category_value,
			String name_of_proprietorship, String copied_name, String[] pan, String proprietorshipChangeTo) {

		String mismatch_error = "";
		String fourth_letter = pan[3];
		String fifth_letter = pan[4];
		
		fourth_letter=fourth_letter.toUpperCase();
        fifth_letter=fifth_letter.toUpperCase();
        
		String donor_first_name_letter = copied_name.substring(0, 1);

		if (category_value == "individual") {
			  if(proprietorshipChangeTo == "yes") {
				String[] proprietorship_name_arr = name_of_proprietorship.split(" ");
				String proprietorship_surname = proprietorship_name_arr[1];

				String proprietorship_surname_1st_letter = proprietorship_surname.substring(0, 1).toUpperCase();

				// check 5th letter of pan with proprietorship_surname
				System.out.println("proprietorship_surname 1st letter :" + proprietorship_surname_1st_letter);
				System.out.println("fifth_letter :"+fifth_letter);

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

	private static String pan_letters_varification(WebDriver driver, Cheque_ModePage chequePage) {

		List<WebElement> elements = chequePage.getPanElements();
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
	
	private static boolean isDisplayedElement(WebDriver driver , By element) {
        try {
        	WebElement ele = driver.findElement(element);
            return ele.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

}
