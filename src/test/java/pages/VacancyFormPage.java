package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

public class VacancyFormPage {

    private final Page page;

    public final Locator title;
    public final Locator nameInput;
    public final Locator descriptionInput;
    public final Locator hiringManagerInput;
    public final Locator positionsInput;
    public final Locator activeSwitch;
    public final Locator toastMessage;

    public VacancyFormPage(Page page) {
        this.page = page;
        title = page.locator(".orangehrm-card-container h6").first();
        nameInput = field("Vacancy Name").locator("input");
        descriptionInput = page.getByPlaceholder("Type description here");
        hiringManagerInput = page.getByPlaceholder("Type for hints...");
        positionsInput = field("Number of Positions").locator("input");
        activeSwitch = page.locator(".oxd-switch-input").first();
        toastMessage = page.locator(".oxd-text--toast-message");
    }

    public void open() {
        page.navigate("recruitment/addJobVacancy");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    public void fillName(String name) {
        nameInput.fill(name);
    }

    public String selectFirstJobTitle() {
        field("Job Title").locator(".oxd-select-text").click();
        Locator option = page.getByRole(AriaRole.OPTION).nth(1);
        String jobTitle = option.innerText();
        option.click();
        return jobTitle;
    }

    public void selectJobTitle(String jobTitle) {
        field("Job Title").locator(".oxd-select-text").click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(jobTitle).setExact(true))
                .first()
                .click();
    }

    public void fillDescription(String description) {
        descriptionInput.fill(description);
    }

    public void selectFirstHiringManager() {
        hiringManagerInput.fill("a");
        page.locator(".oxd-autocomplete-option")
                .filter(new Locator.FilterOptions().setHasNotText("Searching"))
                .first()
                .click();
    }

    public void typeHiringManager(String text) {
        hiringManagerInput.fill(text);
    }

    public Locator hiringManagerSuggestions() {
        return page.locator(".oxd-autocomplete-dropdown");
    }

    public void fillPositions(String positions) {
        positionsInput.fill(positions);
    }

    public void turnOffActive() {
        activeSwitch.click();
    }

    public void save() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save")).click();
    }

    public void cancel() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel")).click();
    }

    public Locator errorFor(String label) {
        return field(label).locator(".oxd-input-field-error-message");
    }

    private Locator field(String label) {
        return page.locator("form .oxd-input-group")
                .filter(new Locator.FilterOptions().setHas(page.locator("label", new Page.LocatorOptions().setHasText(label))));
    }
}
