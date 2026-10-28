package tests;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.TimeoutError;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pages.LoginPage;
import pages.VacanciesPage;
import pages.VacancyFormPage;

public class VacancyTest extends BaseTest {

    VacanciesPage vacanciesPage;
    VacancyFormPage formPage;
    List<String> vacanciesToDelete = new ArrayList<>();

    @BeforeEach
    void setUp() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.login(USERNAME, PASSWORD);
        assertThat(page).hasURL(Pattern.compile("dashboard/index"));

        vacanciesPage = new VacanciesPage(page);
        formPage = new VacancyFormPage(page);
        vacanciesToDelete.clear();
    }

    @AfterEach
    void deleteCreatedVacancies() {
        for (String name : vacanciesToDelete) {
            vacanciesPage.open();
            try {
                vacanciesPage.row(name).waitFor(new Locator.WaitForOptions().setTimeout(10000));
            } catch (TimeoutError e) {
                continue;
            }
            vacanciesPage.clickDelete(name);
            vacanciesPage.confirmDelete();
            assertThat(vacanciesPage.toastMessage).hasText("Successfully Deleted");
        }
    }

    String newVacancyName() {
        return "QA Vacancy " + System.currentTimeMillis();
    }

    String createVacancy(String name, boolean active) {
        formPage.open();
        formPage.fillName(name);
        formPage.selectFirstJobTitle();
        formPage.selectFirstHiringManager();
        if (!active) {
            formPage.turnOffActive();
        }
        formPage.save();
        assertThat(page).hasURL(Pattern.compile("addJobVacancy/\\d+"));
        vacanciesToDelete.add(name);
        return name;
    }

    @Test
    @DisplayName("VAC-ADD-01 add vacancy with all fields")
    void addVacancyWithAllFields() {
        String name = newVacancyName();

        vacanciesPage.open();
        vacanciesPage.clickAdd();
        formPage.fillName(name);
        String jobTitle = formPage.selectFirstJobTitle();
        formPage.fillDescription("Vacancy created by automated test");
        formPage.selectFirstHiringManager();
        formPage.fillPositions("3");
        formPage.save();
        vacanciesToDelete.add(name);

        assertThat(page).hasURL(Pattern.compile("addJobVacancy/\\d+"));
        assertThat(formPage.title).hasText("Edit Vacancy");
        assertThat(formPage.nameInput).hasValue(name);
        assertThat(formPage.descriptionInput).hasValue("Vacancy created by automated test");
        assertThat(formPage.positionsInput).hasValue("3");

        vacanciesPage.open();
        vacanciesPage.searchByName(name);
        assertThat(vacanciesPage.row(name)).containsText(jobTitle);
        assertThat(vacanciesPage.row(name)).containsText("Active");
    }

    @Test
    @DisplayName("VAC-ADD-02 add vacancy with required fields only")
    void addVacancyWithRequiredFieldsOnly() {
        String name = createVacancy(newVacancyName(), true);

        vacanciesPage.open();
        assertThat(vacanciesPage.row(name)).containsText("Active");
    }

    @Test
    @DisplayName("VAC-ADD-03 required fields")
    void requiredFields() {
        formPage.open();
        formPage.save();

        assertThat(formPage.errorFor("Vacancy Name")).hasText("Required");
        assertThat(formPage.errorFor("Job Title")).hasText("Required");
        assertThat(formPage.errorFor("Hiring Manager")).hasText("Required");
    }

    @Test
    @DisplayName("VAC-ADD-04 duplicate vacancy name")
    void duplicateVacancyName() {
        String name = createVacancy(newVacancyName(), true);

        formPage.open();
        formPage.fillName(name);
        assertThat(formPage.errorFor("Vacancy Name")).hasText("Already exists");
        formPage.save();

        assertThat(formPage.errorFor("Vacancy Name")).hasText("Already exists");
        assertThat(page).hasURL(Pattern.compile("addJobVacancy$"));
    }

    @ParameterizedTest(name = "VAC-ADD-05 number of positions out of range ({0})")
    @ValueSource(strings = {"0", "100"})
    void positionsOutOfRange(String positions) {
        formPage.open();
        formPage.fillPositions(positions);

        assertThat(formPage.errorFor("Number of Positions")).hasText("Should be a number between 1-99");
    }

    @ParameterizedTest(name = "VAC-ADD-06 number of positions in the limit ({0})")
    @ValueSource(strings = {"1", "99"})
    void positionsInTheLimit(String positions) {
        formPage.open();
        formPage.fillPositions("abc");
        assertThat(formPage.errorFor("Number of Positions")).isVisible();

        formPage.fillPositions(positions);

        assertThat(formPage.errorFor("Number of Positions")).isHidden();
    }

    @Test
    @DisplayName("VAC-ADD-07 number of positions with letters")
    void positionsWithLetters() {
        formPage.open();
        formPage.fillPositions("abc");

        assertThat(formPage.errorFor("Number of Positions")).hasText("Should be a numeric value");
    }

    @Test
    @DisplayName("VAC-ADD-08 hiring manager that does not exist")
    void hiringManagerThatDoesNotExist() {
        formPage.open();
        formPage.fillName(newVacancyName());
        formPage.selectFirstJobTitle();
        formPage.typeHiringManager("Nobody Withthisname");
        assertThat(formPage.hiringManagerSuggestions()).hasText("No Records Found");
        formPage.save();

        assertThat(formPage.errorFor("Hiring Manager")).hasText("Invalid");
    }

    @Test
    @DisplayName("VAC-ADD-09 cancel add vacancy")
    void cancelAddVacancy() {
        String name = newVacancyName();

        formPage.open();
        formPage.fillName(name);
        formPage.selectFirstJobTitle();
        formPage.cancel();

        assertThat(page).hasURL(Pattern.compile("viewJobVacancy"));
        assertThat(vacanciesPage.recordsFound).isVisible();
        assertThat(vacanciesPage.row(name)).hasCount(0);
    }

    @Test
    @DisplayName("VAC-EDT-01 edit vacancy")
    void editVacancy() {
        String name = createVacancy(newVacancyName(), true);
        String newName = name + " Edited";

        vacanciesPage.open();
        vacanciesPage.searchByName(name);
        vacanciesPage.edit(name);
        assertThat(formPage.nameInput).hasValue(name);
        formPage.fillName(newName);
        formPage.fillDescription("Edited description");
        formPage.fillPositions("5");
        formPage.turnOffActive();
        formPage.save();

        assertThat(formPage.toastMessage).hasText("Successfully Saved");
        vacanciesToDelete.remove(name);
        vacanciesToDelete.add(newName);

        page.reload();
        assertThat(formPage.nameInput).hasValue(newName);
        assertThat(formPage.descriptionInput).hasValue("Edited description");
        assertThat(formPage.positionsInput).hasValue("5");

        vacanciesPage.open();
        assertThat(vacanciesPage.row(newName)).containsText("Closed");
    }

    @Test
    @DisplayName("VAC-EDT-02 edit to a name that already exists")
    void editToExistingName() {
        String firstName = createVacancy(newVacancyName(), true);
        String secondName = createVacancy(newVacancyName(), true);

        vacanciesPage.open();
        vacanciesPage.searchByName(secondName);
        vacanciesPage.edit(secondName);
        assertThat(formPage.nameInput).hasValue(secondName);
        formPage.fillName(firstName);
        assertThat(formPage.errorFor("Vacancy Name")).hasText("Already exists");
        formPage.save();

        assertThat(formPage.errorFor("Vacancy Name")).hasText("Already exists");
        vacanciesPage.open();
        assertThat(vacanciesPage.row(secondName)).hasCount(1);
    }

    @Test
    @DisplayName("VAC-EDT-03 edit with empty name")
    void editWithEmptyName() {
        String name = createVacancy(newVacancyName(), true);

        assertThat(formPage.nameInput).hasValue(name);
        formPage.fillName("");
        formPage.save();

        assertThat(formPage.errorFor("Vacancy Name")).hasText("Required");
    }

    @Test
    @DisplayName("VAC-EDT-04 cancel edit")
    void cancelEdit() {
        String name = createVacancy(newVacancyName(), true);

        assertThat(formPage.nameInput).hasValue(name);
        formPage.fillName(name + " Cancelled");
        formPage.cancel();

        assertThat(page).hasURL(Pattern.compile("viewJobVacancy"));
        assertThat(vacanciesPage.recordsFound).isVisible();
        assertThat(vacanciesPage.row(name)).hasCount(1);
        assertThat(vacanciesPage.row(name + " Cancelled")).hasCount(0);
    }

    @Test
    @DisplayName("VAC-SRC-01 search by vacancy name")
    void searchByVacancyName() {
        String name = createVacancy(newVacancyName(), true);

        vacanciesPage.open();
        vacanciesPage.searchByName(name);

        assertThat(vacanciesPage.recordsFound).hasText("(1) Record Found");
        assertThat(vacanciesPage.row(name)).isVisible();
    }

    @Test
    @DisplayName("VAC-SRC-02 search by job title")
    void searchByJobTitle() {
        String name = newVacancyName();
        formPage.open();
        formPage.fillName(name);
        String jobTitle = formPage.selectFirstJobTitle();
        formPage.selectFirstHiringManager();
        formPage.save();
        vacanciesToDelete.add(name);
        assertThat(page).hasURL(Pattern.compile("addJobVacancy/\\d+"));

        vacanciesPage.open();
        vacanciesPage.filterBy("Job Title", jobTitle);
        vacanciesPage.search();

        assertThat(vacanciesPage.row(name)).isVisible();
        assertThat(vacanciesPage.row(name)).containsText(jobTitle);
    }

    @Test
    @DisplayName("VAC-SRC-03 search by status")
    void searchByStatus() {
        String name = createVacancy(newVacancyName(), false);

        vacanciesPage.open();
        vacanciesPage.filterBy("Status", "Closed");
        vacanciesPage.search();

        assertThat(vacanciesPage.row(name)).isVisible();
        assertThat(vacanciesPage.row(name)).containsText("Closed");
    }

    @Test
    @DisplayName("VAC-SRC-04 search with no results")
    void searchWithNoResults() {
        String name = createVacancy(newVacancyName(), true);

        vacanciesPage.open();
        vacanciesPage.filterBy("Vacancy", name);
        vacanciesPage.filterBy("Status", "Closed");
        vacanciesPage.search();

        assertThat(vacanciesPage.recordsFound).hasText("No Records Found");
    }

    @Test
    @DisplayName("VAC-SRC-05 reset filters")
    void resetFilters() {
        String name = createVacancy(newVacancyName(), true);

        vacanciesPage.open();
        vacanciesPage.filterBy("Vacancy", name);
        vacanciesPage.filterBy("Status", "Active");
        vacanciesPage.search();
        assertThat(vacanciesPage.recordsFound).hasText("(1) Record Found");

        vacanciesPage.reset();

        assertThat(vacanciesPage.filterValue("Vacancy")).hasText("-- Select --");
        assertThat(vacanciesPage.filterValue("Status")).hasText("-- Select --");
        assertThat(vacanciesPage.recordsFound).not().hasText("(1) Record Found");
    }

    @Test
    @DisplayName("VAC-DEL-01 delete vacancy")
    void deleteVacancy() {
        String name = createVacancy(newVacancyName(), true);

        vacanciesPage.open();
        vacanciesPage.clickDelete(name);
        vacanciesPage.confirmDelete();

        assertThat(vacanciesPage.toastMessage).hasText("Successfully Deleted");
        assertThat(vacanciesPage.row(name)).hasCount(0);
    }

    @Test
    @DisplayName("VAC-DEL-02 cancel delete")
    void cancelDelete() {
        String name = createVacancy(newVacancyName(), true);

        vacanciesPage.open();
        vacanciesPage.clickDelete(name);
        assertThat(vacanciesPage.deleteDialog()).isVisible();
        vacanciesPage.cancelDelete();

        assertThat(vacanciesPage.deleteDialog()).isHidden();
        assertThat(vacanciesPage.row(name)).isVisible();
    }
}
