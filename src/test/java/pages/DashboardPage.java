package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class DashboardPage {

    private final Page page;

    public final Locator title;

    public DashboardPage(Page page) {
        this.page = page;
        title = page.locator(".oxd-topbar-header-breadcrumb h6");
    }

    public void logout() {
        page.locator(".oxd-userdropdown-tab").click();
        page.getByText("Logout").click();
    }
}
