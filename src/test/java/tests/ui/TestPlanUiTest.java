package tests.ui;

import api.adapters.CaseAdapter;
import api.adapters.ProjectAdapter;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import api.models.cases.CaseRq;

import api.models.project.ProjectRq;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.QwenDataGenerator;

@Log4j2
@Epic("Qase UI Application")
@Feature("Тест-планы")
@Story("Сборка планов тестирования через UI")
@Owner("Khomchenko E.S.")
public class TestPlanUiTest extends BaseTest {

    private String projectName;

    @BeforeMethod(alwaysRun = true)
    public void prepareDataViaApi() {
        log.info("UI Гибридное Предусловие: Скоростной накат данных через API адаптеры");
        ProjectRq projectRq = QwenDataGenerator.generateProjectData();
        projectCode = projectRq.getCode();
        projectName = projectRq.getTitle();
        ProjectAdapter.createProject(projectRq);
        projectCreated = true;
        CaseRq caseRq = QwenDataGenerator.generateTestCaseData();
        CaseAdapter.createCase(caseRq, projectCode);
    }

    @Test(
            testName = "Сборка Тест-Плана из модального окна",
            description = "Проверка создания тест-плана, открытия дровера выбора кейсов и клика по чекбоксам",
            groups = {"regression"})
    @Description("Гибридный тест. Быстро готовит репозиторий через бэкэнд, а затем кликами в UI собирает релизный план на базе ИИ-заголовка")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("QASE-UI-06")
    public void checkCreateTestPlanViaUi() {
        log.info("Тест UI: Открытие репозитория [{}] и переход в конструктор планов", projectName);
        String planTitle = QwenDataGenerator.generatePlanTitleViaLlm();

        loginPage.openPage()
                .login(user, password)
                .isPageOpened();

        projectsPage.openPage(projectName)
                .navigateToTestPlans()
                .createTestPlan(planTitle)
                .checkPlanVisible(planTitle)
                .returnToProjects();
    }
}