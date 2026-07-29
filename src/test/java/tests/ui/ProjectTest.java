package tests.ui;

import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import api.models.project.ProjectRq;
import org.testng.annotations.Test;
import utils.AiDataGenerator;

@Log4j2
@Epic("Qase UI Application")
@Feature("Проекты")
@Story("Создание и удаление репозиториев через интерфейс")
@Owner("Khomchenko E.S.")
public class ProjectTest extends BaseTest {

    @Test(
            testName = "Создание проекта через веб-интерфейс",
            description = "Сквозная проверка создания, валидации имени и полного удаления проекта кликами в UI",
            groups = {"smoke", "regression"})
    @Description("ИИ генерирует креативное IT-название проекта (Aura Fintech Hub и т.д.), тест создает его в React-интерфейсе и подчищает через контекстное меню")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("QASE-UI-04")
    public void checkCreateProject() {
        log.info("Тест UI: Старт сквозного сценария управления репозиторием");
        ProjectRq projectData = AiDataGenerator.generateProjectData();
        String projectName = projectData.getTitle();
        projectCode = projectData.getCode();
        log.info("Для UI сессии выбраны ИИ-данные. Имя: [{}], Код: [{}]", projectName, projectCode);
        loginPage.openPage()
                .login(user, password)
                .isPageOpened()
                .createProject(projectName, projectCode)
                .checkProjectName(projectName)
                .returnToProjectsList()
                .deleteProject(projectName);
    }
}
