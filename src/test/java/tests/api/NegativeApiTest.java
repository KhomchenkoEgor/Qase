package tests.api;

import api.adapters.ProjectAdapter;
import api.models.error.AuthErrorRs;
import api.models.error.ErrorRs;
import api.models.project.ProjectRq;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import static api.adapters.BaseAdapter.expectStatus;
import static api.adapters.BaseAdapter.notFound404;
import static api.adapters.BaseAdapter.spec;
import static api.adapters.BaseAdapter.specWithToken;
import static api.adapters.BaseAdapter.unauthorized401;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;


@Log4j2
@Epic("Qase API Engine")
@Feature("Негативные проверки")
@Story("Обработка ошибок: авторизация, валидация тела, отсутствующий ресурс")
@Owner("Khomchenko E.S.")
public class NegativeApiTest extends BaseApiTest{

    private static final int VALIDATION_ERROR_CODE = 400;
    private static final String NON_EXISTENT_PROJECT_CODE = "ZZZZZQ";

    @Test(
            testName = "Отказ в создании проекта с невалидным токеном",
            description = "Проверка, что API отклоняет запрос неавторизованного клиента и не создаёт проект",
            groups = {"smoke", "regression"})
    @Description("Негативный сценарий уровня авторизации. Запрос уходит с заведомо испорченным "
            + "значением заголовка Token: проверяется код 401 и признак status=false в теле ответа")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("QASE-API-05")
    public void checkCreateProjectIsRejectedWithInvalidToken() {
        log.info("Тест: попытка создать проект с невалидным токеном авторизации");
        ProjectRq rq = ProjectRq.builder()
                .title("Unauthorized Attempt")
                .code("UNAUTH")
                .description("Проект не должен быть создан: токен невалиден")
                .access("all")
                .build();

        Response response = ProjectAdapter.createProjectRaw(rq, specWithToken("invalid_token_value"));

        response.prettyPrint();
        response.then().spec(unauthorized401);
        AuthErrorRs error = response.as(AuthErrorRs.class);

        assertNotNull(error.getError(),
                "Ответ 401 должен содержать описание ошибки!");

        assertEquals(error.getError(),
                "Unauthenticated.",
                "Текст ошибки авторизации отличается");
    }

    @Test(
            testName = "Отказ в создании проекта с невалидным телом запроса",
            description = "Проверка серверной валидации обязательных полей: пустой заголовок и код неверного формата",
            groups = {"regression"})
    @Description("Негативный сценарий уровня валидации. Тело нарушает сразу два ограничения: "
            + "title пустой, а code содержит кириллицу, дефисы и превышает допустимую длину. "
            + "Проверяется, что валидация выполняется на бэкенде, а не только в UI-форме")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("QASE-API-06")
    public void checkCreateProjectIsRejectedWithInvalidPayload() {
        log.info("Тест: попытка создать проект с нарушением ограничений обязательных полей");
        ProjectRq invalidRq = ProjectRq.builder()
                .title("")
                .code("НЕ-ВАЛИДНЫЙ-КОД-2026")
                .description("Проект не должен быть создан: нарушены ограничения полей")
                .access("all")
                .build();

        Response response = ProjectAdapter.createProjectRaw(invalidRq, spec);

        response.then().spec(expectStatus(VALIDATION_ERROR_CODE));
        ErrorRs error = response.as(ErrorRs.class);
        assertEquals(error.getStatus(), Boolean.FALSE,
                "При невалидном теле запроса поле status в ответе должно быть false!");
        assertFalse(error.getErrorMessage() == null || error.getErrorMessage().isBlank(),
                "Ответ на невалидное тело должен содержать непустое errorMessage!");
        log.info("Бэкенд отклонил невалидное тело. Причина: {}. Поля с ошибками: {}",
                error.getErrorMessage(), response.jsonPath().getList("errorFields"));
    }

    @Test(
            testName = "Запрос несуществующего проекта",
            description = "Проверка, что чтение отсутствующего ресурса возвращает 404, а не пустой успешный ответ",
            groups = {"regression"})
    @Description("Негативный сценарий уровня ресурса. Запрашивается проект с кодом корректного "
            + "формата, которого нет в рабочем пространстве: проверяется, что API отличает "
            + "отсутствие сущности от успешного ответа с пустым result")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("QASE-API-07")
    public void checkGetNonExistentProjectReturnsNotFound() {
        log.info("Тест: запрос проекта с несуществующим кодом {}", NON_EXISTENT_PROJECT_CODE);

        Response response = ProjectAdapter.getProjectRaw(NON_EXISTENT_PROJECT_CODE);

        response.then().spec(notFound404);
        ErrorRs error = response.as(ErrorRs.class);
        assertEquals(error.getStatus(), Boolean.FALSE,
                "При запросе отсутствующего проекта поле status должно быть false!");
        assertNotNull(error.getErrorMessage(),
                "Ответ 404 должен содержать текстовое описание причины!");
        log.info("API корректно сообщил об отсутствии ресурса: {}", error.getErrorMessage());
    }
}
