package ui.locators;

public final class ProjectLocators {

    private ProjectLocators() {
    }

    public static final String SUITE_TITLE_INPUT =
            "#title";
    public static final String CONFIRM_SUITE_BUTTON =
            "//span[text()='Create']/ancestor::button";
    public static final String ACTIVE_SUITE_INPUT =
            "form.NWLa0T #title";
    public static final String ACTIVE_CONFIRM_BUTTON =
            "form.NWLa0T button[type='submit']";
    public static final String REPOSITORY_COMPONENT =
            "//span[text()='Repository'] | //button[contains(., 'Manual test')]";
    public static final String CREATE_NEW_SUITE_BUTTON =
            "//button[contains(., 'Create new suite')]";
    public static final String SUITE_ACTION_BUTTON_TEMPLATE =
            "//button[contains(@aria-label, 'suite %s actions')]";
    public static final String CREATE_SUITE_MENU_ITEM =
            "[data-key='create_suite']";
    public static final String CLONE_MENU_ITEM =
            "[data-key='clone']";
    public static final String CLONE_CONFIRM_BUTTON =
            "//span[text()='Clone']/ancestor::button | //button[contains(., 'Clone')]";
    public static final String TEST_PLANS_LINK =
            "//aside//span[text()='Test Plans'] | //aside//a[contains(@href, '/plans')]";
    public static final String SUITE_TEMPLATE =
            "//span[text()='%s']";
    public static final String TOOLBAR_CREATE_SUITE_BUTTON =
            "//button[contains(., 'Suite') or contains(., 'сьют')] | //*[text()='+ Suite]";
    public static final String EMPTY_REPOSITORY_CREATE_SUITE_BUTTON =
            "//button[contains(., 'Create new suite')] | //a[contains(., 'Create new suite')]";
}
