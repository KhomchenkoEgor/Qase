package api.adapters;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import api.models.plan.PlanRq;
import api.models.plan.PlanRs;
import static io.restassured.RestAssured.given;

@Log4j2
public class PlanAdapter extends BaseAdapter {

    @Step("API: Создание тест-плана '{planRq.title}' в проекте '{projectCode}'")
    public static PlanRs createPlan(PlanRq planRq, String projectCode) {
        return given()
                .spec(spec)
                .pathParam("code", projectCode)
                .body(gson.toJson(planRq))
                .log().all()
                .when()
                .post("/plan/{code}")
                .then()
                .log().all()
                .spec(ok200)
                .extract()
                .as(PlanRs.class);
    }

    @Step("API: Получение данных тест-плана ID {planId} из проекта '{projectCode}'")
    public static Response getPlan(String projectCode, Integer planId) {
        return given()
                .spec(spec)
                .pathParams(
                        "code", projectCode,
                        "id", planId
                )
                .log().all()
                .when()
                .get("/plan/{code}/{id}")
                .then()
                .log().all()
                .spec(ok200)
                .extract()
                .response();
    }

    @Step("API: Удаление тест-плана ID {planId} из проекта '{projectCode}'")
    public static Response deletePlan(String projectCode, Integer planId) {
        return given()
                .spec(spec)
                .pathParams(
                        "code", projectCode,
                        "id", planId
                )
                .log().all()
                .when()
                .delete("/plan/{code}/{id}")
                .then()
                .log().all()
                .spec(ok200)
                .extract()
                .response();
    }
}