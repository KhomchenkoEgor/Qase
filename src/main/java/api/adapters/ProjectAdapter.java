package api.adapters;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import api.models.project.ProjectRq;
import api.models.project.ProjectRs;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@Log4j2
public class ProjectAdapter extends BaseAdapter{

    @Step("API: Создание проекта '{rq.title}' с кодом '{rq.code}'")
    public static ProjectRs createProject(ProjectRq rq) {
        return given()
                .spec(spec)
                .body(rq)
                .log().all()
                .when()
                .post("/project")
                .then()
                .log().all()
                .body(matchesJsonSchemaInClasspath("schemas/create_project_schema.json"))
                .spec(ok200)
                .extract()
                .as(ProjectRs.class);
    }

    @Step("API: Удаление проекта с кодом '{code}'")
    public static void deleteProject(String code){
        given()
                .spec(spec)
                .pathParams("code", code)
                .log().all()
                .when()
                .delete("/project/{code}")
                .then()
                .log().all()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Step("API: Запрос на создание проекта '{rq.code}' без проверки успешности")
    public static Response createProjectRaw(ProjectRq rq, RequestSpecification requestSpec) {
        return given()
                .spec(requestSpec)
                .body(gson.toJson(rq))
                .log().all()
                .when()
                .post("/project")
                .then()
                .log().all()
                .extract()
                .response();
    }

    @Step("API: Запрос проекта с кодом '{code}' без проверки успешности")
    public static Response getProjectRaw(String code) {
        return given()
                .spec(spec)
                .pathParams("code", code)
                .log().all()
                .when()
                .get("/project/{code}")
                .then()
                .log().all()
                .extract()
                .response();
    }
}
