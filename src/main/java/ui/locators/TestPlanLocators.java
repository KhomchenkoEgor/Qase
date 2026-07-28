package ui.locators;

import static ui.locators.ProjectLocators.SUITE_ACTION_BUTTON_TEMPLATE;

public final class TestPlanLocators {

    private TestPlanLocators() {
    }

    public static final String INITIATE_CREATE_PLAN_BUTTON = "//a[contains(text(),'Create plan')]";
    public static final String PLAN_TITLE_INPUT = "form.form-create-plan input[name='title']";
    public static final String ADD_CASES_BUTTON = "#edit-plan-add-cases-button";
    public static final String SAVE_PLAN_BUTTON = "#save-plan";
    public static final String FIRST_CASE_CHECKBOX =
            "//div[contains(@class,'modal') or contains(@class,'drawer') or @role='dialog']" +
                    "//input[@type='checkbox']/ancestor::label | //input[@type='checkbox']/ancestor::label";
    public static final String DONE_BUTTON = "//span[text()='Done']/ancestor::button";
    public static final String PLAN_TITLE_TEMPLATE =
            "//a[contains(text(), '%s')] | //*[contains(text(), '%s')]";
}
