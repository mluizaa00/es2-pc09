# Test scenarios - OrangeHRM

Site: https://opensource-demo.orangehrmlive.com/

Features: Login and Recruitment > Vacancies (add, edit, search and delete).

## How the scenarios were chosen

The scenarios are black box tests, based only on what we see on the screen. We used:

- **Equivalence partitioning**: valid and invalid data (right and wrong password, filled and empty fields).
- **Boundary values**: Number of Positions accepts 1 to 99, so we tested 0, 1, 99 and 100.
- **Decision table**: combinations of username/password and of search filters.
- **State transition**: logged in and logged out, vacancy Active and Closed, vacancy created and deleted.
- **Error guessing**: duplicate name, hiring manager that does not exist, password in uppercase.

About the automation:

- Web E2E tests are slow, so we only automated the most important flows (high and medium priority).
- Each test is independent: it creates the vacancies it needs and deletes them at the end.
- We used the Page Object pattern: each screen has a class with its elements and actions, and the tests only call these methods.
- We used Playwright locators by text, placeholder and role, and Playwright assertions that wait automatically (no fixed sleeps).

## Login

| ID | Scenario | Expected result | Priority | Automated |
| --- | --- | --- | --- | --- |
| LOG-01 | Login with valid credentials (Admin / admin123) | Goes to the Dashboard | High | Yes |
| LOG-02 | Login with wrong password | Message "Invalid credentials" | High | Yes |
| LOG-03 | Login with unknown username | Message "Invalid credentials" | High | Yes |
| LOG-04 | Login with empty fields | "Required" on username and password | High | Yes |
| LOG-05 | Password in uppercase (ADMIN123) | Message "Invalid credentials" | Medium | Yes |
| LOG-06 | Logout | Goes back to the login page | High | Yes |
| LOG-07 | Open a page without login | Redirects to the login page | High | Yes |
| LOG-08 | "Forgot your password?" link | Opens the Reset Password page | Low | No |
| LOG-09 | Many wrong passwords in a row | Account is blocked | Low | No, it could block the demo user |

## Vacancies - Add

| ID | Scenario | Expected result | Priority | Automated |
| --- | --- | --- | --- | --- |
| VAC-ADD-01 | Add vacancy with all fields | Opens "Edit Vacancy" with the saved data and the vacancy shows in the list | High | Yes |
| VAC-ADD-02 | Add vacancy with only the required fields | Vacancy shows in the list as Active | High | Yes |
| VAC-ADD-03 | Save with empty form | "Required" on Vacancy Name, Job Title and Hiring Manager | High | Yes |
| VAC-ADD-04 | Name that already exists | "Already exists" and the vacancy is not saved | High | Yes |
| VAC-ADD-05 | Number of positions 0 and 100 | "Should be a number between 1-99" | Medium | Yes |
| VAC-ADD-06 | Number of positions 1 and 99 | No error | Medium | Yes |
| VAC-ADD-07 | Number of positions with letters | "Should be a numeric value" | Medium | Yes |
| VAC-ADD-08 | Hiring manager that does not exist | "No Records Found" in the suggestions and "Invalid" when saving | Medium | Yes |
| VAC-ADD-09 | Cancel | Goes back to the list and nothing is created | Medium | Yes |
| VAC-ADD-10 | Published vacancy appears on the public jobs page | Vacancy listed on the RSS/web page | Low | No |

## Vacancies - Edit

| ID | Scenario | Expected result | Priority | Automated |
| --- | --- | --- | --- | --- |
| VAC-EDT-01 | Edit name, description, positions and set as inactive | "Successfully Saved", data kept after reload and status Closed in the list | High | Yes |
| VAC-EDT-02 | Change name to one that already exists | "Already exists" and nothing changes | Medium | Yes |
| VAC-EDT-03 | Clear the name and save | "Required" | Medium | Yes |
| VAC-EDT-04 | Cancel the edit | Goes back to the list with the old name | Medium | Yes |
| VAC-EDT-05 | Add an attachment | File shows in Attachments | Low | No |

## Vacancies - Search

| ID | Scenario | Expected result | Priority | Automated |
| --- | --- | --- | --- | --- |
| VAC-SRC-01 | Search by vacancy name | "(1) Record Found" with the vacancy | High | Yes |
| VAC-SRC-02 | Search by job title | The vacancy shows with that job title | High | Yes |
| VAC-SRC-03 | Search by status Closed | The inactive vacancy shows as Closed | Medium | Yes |
| VAC-SRC-04 | Filters with no result | "No Records Found" | Medium | Yes |
| VAC-SRC-05 | Reset | Filters go back to "-- Select --" | Medium | Yes |
| VAC-SRC-06 | Search by hiring manager | Only vacancies of that manager | Medium | No |
| VAC-SRC-07 | Sort the columns | List sorted | Low | No |

## Vacancies - Delete

| ID | Scenario | Expected result | Priority | Automated |
| --- | --- | --- | --- | --- |
| VAC-DEL-01 | Delete a vacancy | "Successfully Deleted" and it is not in the list anymore | High | Yes |
| VAC-DEL-02 | Cancel the delete | The vacancy is still in the list | High | Yes |
| VAC-DEL-03 | Delete more than one vacancy at once | All selected vacancies are deleted | Medium | No |

## Bug found

The site only checks if the vacancy name already exists after you type it (there is a small delay). If you click Save very fast, before the "Already exists" message appears, the vacancy is saved with a repeated name. The API also accepts a repeated name. Because of this, the duplicate name tests wait for the message to appear before clicking Save.
