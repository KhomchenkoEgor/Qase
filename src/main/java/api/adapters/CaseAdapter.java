package api.adapters;

import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import api.models.cases.CaseRq;
import api.models.cases.CaseRs;

import static io.restassured.RestAssured.given;

@Log4j2
public class CaseAdapter extends BaseAdapter {

    @Step("API: Создание тест-кейса '{caseRq.title}' в проекте '{projectCode}'")
    public static CaseRs createCase(CaseRq caseRq, String projectCode) {
        return given()
                .spec(spec)
                .pathParam("code", projectCode)
                .body(gson.toJson(caseRq))
                .log().all()
                .when()
                .post("/case/{code}")
                .then()
                .log().all()
                .spec(ok200)
                .extract()
                .as(CaseRs.class);
    }

    @Step("API: Получение данных тест-кейса ID {caseId} из проекта '{projectCode}'")
    public static CaseRs getCase(String projectCode, Integer caseId) {
        return given()
                .spec(spec)
                .pathParams(
                        "code", projectCode,
                        "id", caseId
                )
                .when()
                .log().all()
                .get("/case/{code}/{id}")
                .then()
                .log().all()
                .spec(ok200)
                .extract()
                .as(CaseRs.class);
    }

    @Step("API: Обновление тест-кейса ID {caseId} в проекте '{projectCode}'")
    public static CaseRs updateCase(CaseRq caseRq, String projectCode, Integer caseId) {
        return given()
                .spec(spec)
                .pathParams(
                        "code", projectCode,
                        "id", caseId
                )
                .body(gson.toJson(caseRq))
                .log().all()
                .when()
                .patch("/case/{code}/{id}")
                .then()
                .log().all()
                .spec(ok200)
                .extract()
                .as(CaseRs.class);
    }

    @Step("API: Удаление тест-кейса ID {caseId} из проекта '{projectCode}'")
    public static CaseRs deleteCase(String projectCode, Integer caseId) {
        return given()
                .spec(spec)
                .pathParams(
                        "code", projectCode,
                        "id", caseId
                )
                .when()
                .delete("/case/{code}/{id}")
                .then()
                .log().all()
                .spec(ok200)
                .extract()
                .as(CaseRs.class);
    }
}
