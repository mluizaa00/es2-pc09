package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage {

    private final Page page;

    public final Locator usernameInput;
    public final Locator passwordInput;
    public final Locator loginButton;
    public final Locator errorAlert;
    public final Locator requiredMessages;

    public LoginPage(Page page) {
        this.page = page;
        usernameInput = page.getByPlaceholder("Username");
        passwordInput = page.getByPlaceholder("Password");
        loginButton = page.locator("button[type='submit']");
        errorAlert = page.locator(".oxd-alert-content-text");
        requiredMessages = page.locator(".oxd-input-field-error-message");
    }

    public void open() {
        page.navigate("auth/login");
    }

    public void login(String username, String password) {
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
    }
}
