package selenium.saral_nidhi;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import resources.Base;
import resources.ExtentReporterNG;

public class Listeners extends Base implements ITestListener {

	ExtentTest test;
	ExtentReports extent = ExtentReporterNG.getReportObject();

	ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();

	public void onTestStart(ITestResult result) {
		test = extent.createTest(result.getInstance().getClass().getSimpleName());
		extentTest.set(test);
	}

	public void onTestSuccess(ITestResult result) {
		extentTest.get().log(Status.PASS, result.getName()+" Test Passed");
	}

	public void onTestFailure(ITestResult result) {
		extentTest.get().fail(result.getThrowable());
		WebDriver driver = null;
		
		System.out.println("The name of the testcase failed is :"+result.getName());
		
		String testMethodName = result.getName();
		

		try {
			driver = (WebDriver) result.getTestClass().getRealClass().getDeclaredField("driver")
					.get(result.getInstance());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		extentTest.get().addScreenCaptureFromPath(getScreenShortpath(testMethodName, driver), testMethodName);
	
		
		
		//---------------- Earlier ---------------------
		/*
		System.out.println("The name of the testcase failed is :"+result.getName());

		 //String testMethodName = result.getMethod().getMethodName();
		String testMethodName = result.getInstance().getClass().getSimpleName();
		
		System.out.println("The testMethodName of the testcase failed is :"+testMethodName);

		try {
			driver = (WebDriver) result.getTestClass().getRealClass().getDeclaredField("driver")
					.get(result.getInstance());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		extentTest.get().addScreenCaptureFromPath(getScreenShortpath(testMethodName, driver), testMethodName);
       */
		//----------------- end-----------------------------------------------------------------------------
	}

	public void onTestSkipped(ITestResult result) {
		
		extentTest.get().log(Status.SKIP, result.getName()+" Test skipped");
		// ---------- Earlier skip code ------
		/*
		System.out.println("The name of the testcase Skipped is :"+result.getName());
		System.out.println("The testMethodName of the testcase Skipped is :"+result.getInstance().getClass().getSimpleName());
		
		extentTest.get().log(Status.SKIP, "Test skipped");
		 */
		
		// ---------- Earlier skip code end------
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub
		ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
	}

	public void onTestFailedWithTimeout(ITestResult result) {
		// TODO Auto-generated method stub
		ITestListener.super.onTestFailedWithTimeout(result);
	}

	public void onStart(ITestContext context) {
		// TODO Auto-generated method stub
		ITestListener.super.onStart(context);
	}

	public void onFinish(ITestContext context) {
		extent.flush();
	}

}
