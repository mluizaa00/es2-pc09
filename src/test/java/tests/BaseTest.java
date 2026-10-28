package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/";
    static final String USERNAME = "Admin";
    static final String PASSWORD = "admin123";

    static Playwright playwright;
    static Browser browser;

    BrowserContext context;
    Page page;

    @BeforeAll
    static void startBrowser() {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
        PlaywrightAssertions.setDefaultAssertionTimeout(30000);
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void openPage() {
        context = browser.newContext(new Browser.NewContextOptions().setBaseURL(BASE_URL));
        page = context.newPage();
        page.setDefaultTimeout(30000);
    }

    @AfterEach
    void closePage() {
        context.close();
    }
}
