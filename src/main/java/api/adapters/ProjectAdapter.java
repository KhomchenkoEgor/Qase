package api.adapters;

import lombok.extern.log4j.Log4j2;
import api.models.project.ProjectRq;
import api.models.project.ProjectRs;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Log4j2
public class ProjectAdapter extends BaseAdapter{

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

    public static void deleteProject(String code){
        given()
                .spec(spec)
                .pathParams("code", code)
                .log().all()
                .when()
                .delete("/project/{code}")
                .then()
                .log().all()
                .spec(ok200);
    }
}
