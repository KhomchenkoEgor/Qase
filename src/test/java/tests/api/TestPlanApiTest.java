package tests.api;

import api.adapters.CaseAdapter;
import api.adapters.PlanAdapter;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import api.models.cases.CaseRq;
import api.models.plan.PlanRq;
import api.models.plan.PlanRs;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.AiDataGenerator;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Log4j2
@Epic("Qase API Engine")
@Feature("Тест-планы")
@Story("Релизные тест-планы и привязка тест-кейсов")
@Owner("Khomchenko E.S.")
public class TestPlanApiTest extends BaseApiTest {

    private Integer caseId;

    @BeforeMethod(alwaysRun = true)
    public void prepareCase() {
        log.info("API Предусловие: Создание базового кейса");
        CaseRq caseRq = AiDataGenerator.generateTestCaseData();
        var caseRs = CaseAdapter.createCase(caseRq,projectCode);
        caseId = caseRs.getResult().getId();
    }

    @Test(
            testName = "Жизненный цикл Релизного Тест-Плана",
            description = "Проверка сборки тест-плана из массива примитивов (ID кейсов) через API",
            groups = {"regression"})
    @Description("Тест валидирует привязку сгенерированных кейсов к релизному тест-плану и проверяет парсинг массивов в jsonPath ответа")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("QASE-API-04")
    public void checkTestPlanCrudLifecycle() {
        log.info("Тест: Создание релизного тест-плана для проекта: {}", projectCode);
        PlanRq planRq = AiDataGenerator.generatePlanData(List.of(caseId));
        PlanRs planRs = PlanAdapter.createPlan(planRq, projectCode);
        assertTrue(planRs.getStatus(), "Не удалось создать тест-план через API!");
        int planId = planRs.getResult().getId();
        var getPlanResponse = PlanAdapter.getPlan(projectCode, planId);
        assertEquals(getPlanResponse.jsonPath().getString("result.title"), planRq.getTitle(),
                "Название тест-плана не совпадает со сгенерированным ИИ!");
        assertEquals(getPlanResponse.jsonPath().getList("result.cases.case_id").get(0), caseId,
                "ID тест-кейса внутри плана сохранился некорректно!");
        PlanAdapter.deletePlan(projectCode, planId);
        log.info("План ID {} успешно деинсталлирован.", planId);
    }
}