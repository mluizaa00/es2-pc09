package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

public class VacanciesPage {

    private final Page page;

    public final Locator recordsFound;
    public final Locator toastMessage;

    public VacanciesPage(Page page) {
        this.page = page;
        recordsFound = page.locator(".orangehrm-horizontal-padding .oxd-text--span");
        toastMessage = page.locator(".oxd-text--toast-message");
    }

    public void open() {
        page.navigate("recruitment/viewJobVacancy");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    public void clickAdd() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add")).click();
    }

    public void filterBy(String filter, String option) {
        filter(filter).locator(".oxd-select-text").click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(option).setExact(true))
                .first()
                .click();
    }

    public Locator filterValue(String filter) {
        return filter(filter).locator(".oxd-select-text-input");
    }

    public void search() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
    }

    public void reset() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Reset")).click();
    }

    public void searchByName(String vacancyName) {
        filterBy("Vacancy", vacancyName);
        search();
    }

    public Locator row(String vacancyName) {
        return page.locator(".oxd-table-card")
                .filter(new Locator.FilterOptions()
                        .setHas(page.getByText(vacancyName, new Page.GetByTextOptions().setExact(true))));
    }

    public void edit(String vacancyName) {
        row(vacancyName).locator(".bi-pencil-fill").click();
    }

    public void clickDelete(String vacancyName) {
        row(vacancyName).locator(".bi-trash").click();
    }

    public void confirmDelete() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, Delete")).click();
    }

    public void cancelDelete() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("No, Cancel")).click();
    }

    public Locator deleteDialog() {
        return page.locator(".orangehrm-dialog-popup");
    }

    private Locator filter(String label) {
        return page.locator(".oxd-table-filter .oxd-input-group")
                .filter(new Locator.FilterOptions().setHas(page.locator("label", new Page.LocatorOptions().setHasText(label))));
    }
}
