package ui.pages;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static ui.locators.TestPlanLocators.*;

@Log4j2
public class TestPlanPage {

    @Step("Создать тест-план с названием '{planTitle}' и привязать к нему сгенерированные кейсы")
    public TestPlanPage createTestPlan(String planTitle) {
        Configuration.clickViaJs = false;
        log.info("Инициализируем открытие формы создания тест-плана...");

        $x(INITIATE_CREATE_PLAN_BUTTON).shouldBe(visible, java.time.Duration.ofSeconds(10)).click();
        $(PLAN_TITLE_INPUT).shouldBe(visible, java.time.Duration.ofSeconds(5)).setValue(planTitle);
        $(ADD_CASES_BUTTON).shouldBe(visible).click();
        sleep(500);
        $x(FIRST_CASE_CHECKBOX).shouldBe(visible, java.time.Duration.ofSeconds(5)).click();
        $x(DONE_BUTTON).shouldBe(visible, java.time.Duration.ofSeconds(5)).click();
        $(SAVE_PLAN_BUTTON).shouldBe(enabled).click();
        log.info("Тест-план успешно сохранен.");
        return this;
    }

    @Step("Проверить, что тест-план с названием '{planTitle}' успешно добавлен на страницу")
    public TestPlanPage checkPlanVisible(String planTitle) {
        $x(String.format(PLAN_TITLE_TEMPLATE, planTitle, planTitle))
                .shouldBe(visible, java.time.Duration.ofSeconds(5));
        return this;
    }

    @Step("Вернуться к списку проектов")
    public ProjectsPage returnToProjects() {
        open("/projects");
        return new ProjectsPage();
    }
}