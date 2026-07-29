package tests.ui;

import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import api.models.project.ProjectRq;
import api.models.suite.SuiteRq;
import org.testng.annotations.Test;
import utils.AiDataGenerator;

@Log4j2
@Epic("Qase UI Application")
@Feature("Тест-сьюты")
@Story("Дерево тест-сьютов и контекстные меню")
@Owner("Khomchenko E.S.")
public class SuiteUiTest extends BaseTest {

    @Test(
            testName = "Конструирование и клонирование дерева сьютов",
            description = "Проверка создания вложенных папок и их размножения через контекстные меню UI",
            groups = {"regression"})
    @Description("Тест проверяет стабильность работы тяжелых анимаций и контекстных меню в дереве репозитория без использования жестких sleep")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("QASE-UI-05")
    public void checkNestedSuiteAndCloningLifecycle() {
        log.info("Тест UI: Управление вложенными структурами папок в браузере");
        ProjectRq projectData = AiDataGenerator.generateProjectData();
        SuiteRq parentSuiteData = AiDataGenerator.generateSuiteData();
        SuiteRq childSuiteData = AiDataGenerator.generateSuiteData();
        projectCode = projectData.getCode();

        loginPage.openPage()
                .login(user, password)
                .isPageOpened()
                .createProject(projectData.getTitle(), projectData.getCode())
                .createSuite(parentSuiteData.getTitle())
                .checkSuiteVisible(parentSuiteData.getTitle())
                .createChildSuite(parentSuiteData.getTitle(), childSuiteData.getTitle())
                .checkSuiteVisible(childSuiteData.getTitle())
                .cloneSuiteViaUi(parentSuiteData.getTitle())
                .checkClonedSuiteVisible(parentSuiteData.getTitle())
                .returnToProjectsList()
                .deleteProject(projectData.getTitle());
    }
}