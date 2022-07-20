package resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Base {

	public WebDriver driver;
	public String url;

	public WebDriver initializeDriver() throws IOException {

		Properties prop = new Properties();

		FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"\\src\\main\\java\\resources\\data.properties");

		prop.load(fis);

		//String browserName = prop.getProperty("browser");

		// following is for parameterized browser through maven command
		String browserName = System.getProperty("browser");

		System.out.println("browser name is :"+browserName);
		System.out.println("---------------url value is----------- :"+url);
		url = prop.getProperty("url");

		if (browserName.contains("chrome")) {
			//System.setProperty("webdriver.chrome.driver", "C:\\Users\\PC\\Downloads\\chromedriver.exe");
			//System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\src\\main\\java\\resources\\chromedriver.exe");


		    WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
      System.out.println("--------------inside chrome section---------- :");
			if(browserName.contains("headless")) {
				options.addArguments("--headless");
				options.addArguments("--window-size=1920,1080");

				System.out.println("--------------inside chromeheadless section---------- :");
			}
			driver = new ChromeDriver(options);
		}

		else if (browserName.equals("edge")) {
			//System.setProperty("webdriver.edge.driver", "C:\\Users\\PC\\Downloads\\msedgedriver.exe");
			//System.setProperty("webdriver.edge.driver", System.getProperty("user.dir")+"\\src\\main\\java\\resources\\msedgedriver.exe");

			WebDriverManager.edgedriver().setup();

			//driver = new EdgeDriver();
			EdgeOptions options = new EdgeOptions();

			if(browserName.contains("headless")) {
				options.addArguments("--headless");
				options.addArguments("--window-size=1920,1080");
			}
			driver = new EdgeDriver(options);
		}

		else if (browserName.equals("firefox")) {

		}

		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50));
		driver.manage().window().maximize();

		System.out.println("--------------before return driver value from Base---------- :"+driver);
		return driver;
	}

	public String getScreenShortpath(String testCaseName, WebDriver driver) {
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		String destinationFile = System.getProperty("user.dir") + "\\reports\\" + testCaseName + ".png";
		try {
			FileUtils.copyFile(source, new File(destinationFile));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return destinationFile;
	}
}
