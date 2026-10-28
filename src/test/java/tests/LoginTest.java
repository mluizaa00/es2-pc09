package tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.DashboardPage;
import pages.LoginPage;

public class LoginTest extends BaseTest {

    LoginPage loginPage;
    DashboardPage dashboardPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page);
        dashboardPage = new DashboardPage(page);
        loginPage.open();
    }

    @Test
    @DisplayName("LOG-01 login with valid credentials")
    void loginWithValidCredentials() {
        loginPage.login(USERNAME, PASSWORD);

        assertThat(page).hasURL(Pattern.compile("dashboard/index"));
        assertThat(dashboardPage.title).hasText("Dashboard");
    }

    @Test
    @DisplayName("LOG-02 login with wrong password")
    void loginWithWrongPassword() {
        loginPage.login(USERNAME, "wrongpassword");

        assertThat(loginPage.errorAlert).hasText("Invalid credentials");
        assertThat(page).hasURL(Pattern.compile("auth/login"));
    }

    @Test
    @DisplayName("LOG-03 login with unknown username")
    void loginWithUnknownUsername() {
        loginPage.login("unknownuser", PASSWORD);

        assertThat(loginPage.errorAlert).hasText("Invalid credentials");
    }

    @Test
    @DisplayName("LOG-04 login with empty fields")
    void loginWithEmptyFields() {
        loginPage.login("", "");

        assertThat(loginPage.requiredMessages).hasText(new String[] {"Required", "Required"});
    }

    @Test
    @DisplayName("LOG-05 password is case sensitive")
    void passwordIsCaseSensitive() {
        loginPage.login(USERNAME, PASSWORD.toUpperCase());

        assertThat(loginPage.errorAlert).hasText("Invalid credentials");
    }

    @Test
    @DisplayName("LOG-06 logout")
    void logout() {
        loginPage.login(USERNAME, PASSWORD);
        assertThat(dashboardPage.title).hasText("Dashboard");

        dashboardPage.logout();

        assertThat(page).hasURL(Pattern.compile("auth/login"));
    }

    @Test
    @DisplayName("LOG-07 protected page without login goes to login page")
    void protectedPageWithoutLogin() {
        page.navigate("recruitment/viewJobVacancy");

        assertThat(page).hasURL(Pattern.compile("auth/login"));
    }
}
