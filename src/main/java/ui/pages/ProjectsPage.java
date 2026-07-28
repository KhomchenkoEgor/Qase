package ui.pages;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.open;
import static ui.dict.Elements.CREATE_NEW_PROJECT_BUTTON;
import static ui.dict.Elements.CREATE_PROJECT_BUTTON;
import static ui.locators.ProjectsLocators.*;


@Log4j2
public class ProjectsPage {

    @Step("Открыть страницу со списком проектов")
    public ProjectsPage isPageOpened() {
        log.info("UI: Переход на страницу списка репозиториев через прямой URL /projects");
        open("/projects");
        return this;
    }

    @Step("Создать новый проект. Имя: '{projectName}', Код: '{projectCode}'")
    public ProjectPage createProject(String projectName, String projectCode) {
        log.info("UI: Старт создания проекта. Name: {}, Code: {}", projectName, projectCode);
        Configuration.clickViaJs = false;
        log.info("UI: Клик по кнопке инициализации '{}'", CREATE_NEW_PROJECT_BUTTON);
        $(byText(CREATE_NEW_PROJECT_BUTTON)).shouldBe(visible).click();
        log.info("UI: Ввод названия проекта");
        $(PROJECT_NAME).shouldBe(visible).setValue(projectName);
        $(PROJECT_CODE).click();
        actions().keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).sendKeys(Keys.BACK_SPACE).perform();
        $(PROJECT_CODE).setValue(projectCode);
        log.info("UI: Отправка формы создания проекта кнопкой '{}'", CREATE_PROJECT_BUTTON);
        $(byText(CREATE_PROJECT_BUTTON)).click();
        Configuration.clickViaJs = true;
        return new ProjectPage().waitForPageLoaded();
    }

    @Step("Удалить проект с именем: '{project}'")
    public ProjectsPage deleteProject(String project) {
        log.info("UI: Инициализация удаления проекта: {}", project);
        log.info("UI: Поиск строки таблицы для проекта '{}' и вызов Action-меню", project);
        $(byText(project))
                .ancestor(TABLE_ROW)
                .find(ACTION_MENU)
                .click();
        $(REMOVE_BUTTON).click();
        log.info("UI: Подтверждение удаления в модальном окне безопасности");
        $x(DELETE_PROJECT_BUTTON).click();
        log.info("UI: Проект '{}' успешно удален.", project);
        return this;
    }

    @Step("Перейти на страницу проекта: '{projectName}'")
    public ProjectPage openPage(String projectName) {
        log.info("Кликаем по проекту в таблице для безопасного SPA-перехода: {}", projectName);
        $(byText(projectName))
                .shouldBe(visible)
                .click();
        return new ProjectPage().waitForPageLoaded();
    }
}