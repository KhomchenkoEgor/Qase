package tests.api;

import api.adapters.ProjectAdapter;
import api.models.project.ProjectRq;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.QwenDataGenerator;

import static org.testng.Assert.assertTrue;

@Log4j2
public abstract class BaseApiTest {

    protected String projectCode;

    @BeforeMethod(alwaysRun = true)
    public void createProjectBeforeTest() {
        log.info("API Предусловие: Создание изолированного проекта");
        ProjectRq projectRq = QwenDataGenerator.generateProjectData();
        projectCode = projectRq.getCode();
        var response = ProjectAdapter.createProject(projectRq);
        assertTrue(
                response.getStatus(),
                "Не удалось создать тестовый проект!"
        );
        log.info("Создан тестовый проект: {}", projectCode);
    }

    @AfterMethod(alwaysRun = true)
    public void deleteProjectAfterTest() {
        if (projectCode != null) {
            log.info("API Постусловие: Удаление проекта {}",projectCode
            );
            ProjectAdapter.deleteProject(projectCode);
            projectCode = null;
        }
    }
}