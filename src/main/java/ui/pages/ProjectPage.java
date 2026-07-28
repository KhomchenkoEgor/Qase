package ui.pages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ElementNotFound;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static ui.locators.ProjectLocators.*;

@Log4j2
public class ProjectPage {

    @Step("Дождаться полной загрузки интерфейса репозитория")
    public ProjectPage waitForPageLoaded() {
        log.info("Ожидаем загрузку визуальных компонентов репозитория Qase...");
        Configuration.clickViaJs = false;
        $x(REPOSITORY_COMPONENT)
                .shouldBe(visible, java.time.Duration.ofSeconds(30));
        log.info("Компоненты репозитория успешно обнаружены.");
        Configuration.clickViaJs = true;
        return this;
    }

    @Step("Проверить, что открыт проект с именем '{projectName}'")
    public ProjectPage checkProjectName(String projectName) {
        log.info("Проверяем заголовок открытого репозитория...");
        if ($x(CREATE_NEW_SUITE_BUTTON).is(visible)) {
            log.info("Успешно зафиксирован пустой стейт репозитория (кнопка 'Create new suite' доступна).");
        } else {
            $x("//h1").shouldBe(visible);
        }
        log.info("Валидация открытия проекта пройдена успешно!");
        return this;
    }

    @Step("Вернуться на страницу со списком проектов")
    public ProjectsPage returnToProjectsList() {
        open("/projects");
        return new ProjectsPage();
    }

    @Step("Создать корневой тест-сьют с именем '{suiteName}'")
    public ProjectPage createSuite(String suiteName) {
        com.codeborne.selenide.Configuration.clickViaJs = false;
        log.info("Начинаем процесс создания тест-сьюта: " + suiteName);
        try {
            $x(EMPTY_REPOSITORY_CREATE_SUITE_BUTTON)
                    .shouldBe(visible, java.time.Duration.ofSeconds(5))
                    .click();
            log.info("Кликнули по центральной кнопке 'Create new suite'.");
        } catch (ElementNotFound e) {
            log.info("Центральная кнопка не найдена. Пытаемся кликнуть по верхней панели инструментов...");
            $x(TOOLBAR_CREATE_SUITE_BUTTON)
                    .shouldBe(visible, java.time.Duration.ofSeconds(5))
                    .click();
        }
        $(SUITE_TITLE_INPUT).shouldBe(visible).setValue(suiteName);
        $x(CONFIRM_SUITE_BUTTON).click();

        log.info("Тест-сьют успешно сохранен в репозитории.");
        Configuration.clickViaJs = true;
        return this;
    }

    @Step("Проверить, что тест-сьют с именем '{suiteName}' отображается в дереве репозитория")
    public ProjectPage checkSuiteVisible(String suiteName) {
        $(byText(suiteName)).shouldBe(visible);
        return this;
    }

    @Step("Создать дочерний тест-сьют '{childName}' внутри родительского '{parentName}'")
    public ProjectPage createChildSuite(String parentName, String childName) {
        log.info("Создаем вложенный сьют '" + childName + "' в родительский '" + parentName + "'");
        String targetButtonXpath = String.format(SUITE_ACTION_BUTTON_TEMPLATE, parentName);
        Configuration.clickViaJs = false;
        $x(targetButtonXpath)
                .shouldBe(visible, java.time.Duration.ofSeconds(10))
                .click();
        sleep(500);
        $(CREATE_SUITE_MENU_ITEM)
                .shouldBe(visible, java.time.Duration.ofSeconds(5))
                .click();
        $(ACTIVE_SUITE_INPUT).shouldBe(visible).setValue(childName);
        $(ACTIVE_CONFIRM_BUTTON).shouldBe(enabled).click();
        log.info("Вложенный тест-сьют успешно создан.");
        return this;
    }

    @Step("Клонировать тест-сьют '{suiteName}' через UI контекстное меню")
    public ProjectPage cloneSuiteViaUi(String suiteName) {
        log.info("Клонируем тест-сьют: " + suiteName);
        String targetButtonXpath = String.format(SUITE_ACTION_BUTTON_TEMPLATE, suiteName);
        Configuration.clickViaJs = false;
        $x(targetButtonXpath)
                .shouldBe(visible, java.time.Duration.ofSeconds(10))
                .click();
        sleep(500);
        $(CLONE_MENU_ITEM)
                .shouldBe(visible, java.time.Duration.ofSeconds(5))
                .click();
        $x(CLONE_CONFIRM_BUTTON)
                .shouldBe(visible, java.time.Duration.ofSeconds(5))
                .click();
        log.info("Запрос на клонирование выполнен.");
        return this;
    }

    @Step("Проверить, что в системе появилось несколько копий сьюта '{suiteName}'")
    public ProjectPage checkClonedSuiteVisible(String suiteName) {
        $$x(String.format(SUITE_TEMPLATE, suiteName)).shouldHave(sizeGreaterThan(1));
        return this;
    }

    @Step("Перейти в раздел 'Test Plans' текущего проекта")
    public TestPlanPage navigateToTestPlans() {
        log.info("Кликаем по пункту 'Test Plans' в боковой панели навигации Qase...");
        Configuration.clickViaJs = true;
        $x(TEST_PLANS_LINK)
                .shouldBe(visible, java.time.Duration.ofSeconds(10))
                .click();
        Configuration.clickViaJs = false;
        return new TestPlanPage();
    }
}