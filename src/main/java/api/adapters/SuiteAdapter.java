package api.adapters;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import api.models.suite.SuiteRq;
import api.models.suite.SuiteRs;
import static io.restassured.RestAssured.given;

@Log4j2
public class SuiteAdapter extends BaseAdapter {

    @Step("API: Создание тест-сьюта '{suiteRq.title}' в проекте '{projectCode}'")
    public static SuiteRs createSuite(SuiteRq suiteRq, String projectCode) {
        return given()
                .spec(spec)
                .pathParam("code", projectCode)
                .body(gson.toJson(suiteRq))
                .log().all()
                .when()
                .post("/suite/{code}")
                .then()
                .log().all()
                .spec(ok200)
                .extract()
                .as(SuiteRs.class);
    }

    @Step("API: Получение структуры тест-сьюта ID {suiteId} из проекта '{projectCode}'")
    public static Response getSuite(String projectCode, Integer suiteId) {
        return given()
                .spec(spec)
                .pathParams(
                        "code", projectCode,
                        "id", suiteId
                )
                .log().all()
                .when()
                .get("/suite/{code}/{id}")
                .then()
                .log().all()
                .spec(ok200)
                .extract()
                .response();
    }

    @Step("API: Удаление тест-сьюта ID {suiteId} из проекта '{projectCode}'")
    public static SuiteRs deleteSuite(String projectCode, Integer suiteId) {
        return given()
                .spec(spec)
                .pathParams(
                        "code", projectCode,
                        "id", suiteId
                )
                .log().all()
                .when()
                .delete("/suite/{code}/{id}")
                .then()
                .log().all()
                .spec(ok200)
                .extract()
                .as(SuiteRs.class);
    }
}
