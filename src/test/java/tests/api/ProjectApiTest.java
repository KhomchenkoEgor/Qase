package tests.api;

import api.adapters.ProjectAdapter;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import api.models.project.ProjectRq;
import api.models.project.ProjectRs;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import utils.AiDataGenerator;

import static api.adapters.ProjectAdapter.createProject;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Log4j2
@Epic("Qase API Engine")
@Feature("Проекты")
@Story("Управление жизненным циклом репозитория")
@Owner("Khomchenko E.S.")
public class ProjectApiTest {

    private final ThreadLocal<String> projectCode = new ThreadLocal<>();

    @Test(
            testName = "Создание проекта через API",
            description = "Проверка успешного создания нового репозитория проекта с помощью ИИ данных",
            groups = {"smoke", "regression"})
    @Description("Тест запрашивает уникальные параметры (имя, код) у локальной LLM Qwen, отправляет POST запрос и валидирует ответ бэкенда")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("QASE-API-01")
    public void checkCreateProject() {
        log.info("Тест: Запуск сценария генерации и создания проекта через API");
        ProjectRq rq = AiDataGenerator.generateProjectData();
        projectCode.set(rq.getCode());
        log.info("ИИ сгенерировал уникальный код проекта для этой сессии: {}", projectCode.get());
        ProjectRs rs = createProject(rq);
        assertTrue(rs.getStatus(), "Бэкенд отказал в создании проекта!");
        assertEquals(rs.getResult().getCode(), projectCode.get(), "Код в ответе бэкенда не совпадает со сгенерированным ИИ!");
    }

    @AfterMethod(alwaysRun = true)
    public void deleteProject() {
        if (projectCode.get() != null) {
            log.info("API Очистка: Каскадное удаление временного проекта с кодом: {}", projectCode.get());
            ProjectAdapter.deleteProject(projectCode.get());
            projectCode.remove();
        }
    }
}
