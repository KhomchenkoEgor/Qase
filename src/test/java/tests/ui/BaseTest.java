package tests.ui;

import api.adapters.ProjectAdapter;
import com.codeborne.selenide.Configuration;
import io.qameta.allure.Step;
import io.qameta.allure.testng.AllureTestNg;
import listeners.TestListener;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.*;
import ui.pages.LoginPage;
import ui.pages.ProjectsPage;
import utils.PropertyReader;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

@Log4j2
@Listeners({AllureTestNg.class, TestListener.class})
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseTest {

    LoginPage loginPage;
    ProjectsPage projectsPage;
    String projectCode;
    boolean projectCreated = false;

    String user = System.getProperty("user", PropertyReader.getProperty("user"));
    String password = System.getProperty("password", PropertyReader.getProperty("password"));

    @BeforeMethod(alwaysRun = true, description = "Настройка драйвера")
    @Parameters({"browser"})
    @Step("Инициализация браузера {browser} и подготовка страниц")
    public void setUp(@Optional("chrome") String browser) {
        Configuration.baseUrl = "https://app.qase.io";
        Configuration.timeout = 30000;
        Configuration.clickViaJs = true;
        Configuration.browserSize = "1920x1080";
        Configuration.headless = true;
        Configuration.browser = browser;

        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions options = new ChromeOptions();
            Map<String, Object> chromePrefs = new HashMap<>();
            chromePrefs.put("credentials_enable_service", false);
            chromePrefs.put("profile.password_manager_enabled", false);
            options.setExperimentalOption("prefs", chromePrefs);
            options.addArguments(
                    "--headless",
                    "--incognito",
                    "--disable-notifications",
                    "--disable-popup-blocking",
                    "--disable-infobars"
            );
            Configuration.browserCapabilities = options;
        } else if (browser.equalsIgnoreCase("edge")) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--headless");
            Configuration.browserCapabilities = options;
        } else if (browser.equalsIgnoreCase("firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            options.setBinary("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
            options.addArguments("--headless");
            Configuration.browserCapabilities = options;
        }

        loginPage = new LoginPage();
        projectsPage = new ProjectsPage();
    }

    @AfterMethod(alwaysRun = true, description = "API очистка тестовых данных и закрытие браузера")
    @Step("Удаление созданного проекта через API и закрытие браузера")
    public void tearDown() {

        try {
            if (projectCreated && projectCode != null) {
                log.info("UI Гибридное Постусловие: API-зачистка проекта: {}", projectCode);
                ProjectAdapter.deleteProject(projectCode);
                log.info("Проект {} успешно удален через API", projectCode);
            }
        } catch (Exception e) {
            log.error("Ошибка при API удалении проекта {}", projectCode, e);
        } finally {
            projectCode = null;
            projectCreated = false;

            if (hasWebDriverStarted()) {
                log.info("Закрытие браузера");
                closeWebDriver();
            }
        }
    }
}
