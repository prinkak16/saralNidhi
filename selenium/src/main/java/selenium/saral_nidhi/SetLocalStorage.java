package selenium.saral_nidhi;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.testng.ITestContext;

public class SetLocalStorage {

	public SetLocalStorage(LocalStorage storage, WebDriver driver, ITestContext context) {
		storage = ((WebStorage) driver).getLocalStorage();

		Object systemLanguage = context.getAttribute("systemLanguage");
		Object userCallCenter = context.getAttribute("userCallCenter");
		Object userEmail = context.getAttribute("userEmail");
		Object userPhone = context.getAttribute("userPhone");
		Object userRole = context.getAttribute("userRole");
		Object userName = context.getAttribute("userName");
		Object states = context.getAttribute("states");

		Object permissions = context.getAttribute("permissions");
		Object userCallingRole = context.getAttribute("userCallingRole");
		Object stateDeletionAllowed = context.getAttribute("stateDeletionAllowed");
		Object isTeamLead = context.getAttribute("isTeamLead");
		Object isCallingEnable = context.getAttribute("isCallingEnable");
		Object authToken = context.getAttribute("authToken");
		Object userId = context.getAttribute("userId");
		Object is_state_zone_available = context.getAttribute("is_state_zone_available");
		Object countryState = context.getAttribute("countryState");
		Object manualCallingEnabled = context.getAttribute("manualCallingEnabled");
		Object authStatus = context.getAttribute("authStatus");

		// System.out.println("userPhone fetched :"+userPhone);
		// System.out.println("states fetched :"+states);

		storage.setItem("systemLanguage", (String) systemLanguage);
		storage.setItem("userCallCenter", (String) userCallCenter);
		storage.setItem("userEmail", (String) userEmail);
		storage.setItem("userPhone", (String) userPhone);
		storage.setItem("userRole", (String) userRole);
		storage.setItem("userName", (String) userName);
		storage.setItem("states", (String) states);

		storage.setItem("permissions", (String) permissions);
		storage.setItem("userCallingRole", (String) userCallingRole);
		storage.setItem("stateDeletionAllowed", (String) stateDeletionAllowed);
		storage.setItem("isTeamLead", (String) isTeamLead);
		storage.setItem("isCallingEnable", (String) isCallingEnable);
		storage.setItem("authToken", (String) authToken);
		storage.setItem("userId", (String) userId);

		storage.setItem("is_state_zone_available", (String) is_state_zone_available);
		storage.setItem("countryState", (String) countryState);
		storage.setItem("manualCallingEnabled", (String) manualCallingEnabled);
		storage.setItem("authStatus", (String) authStatus);

		driver.navigate().refresh();
	}
}
