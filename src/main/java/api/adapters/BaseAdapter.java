package api.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import utils.PropertyReader;

public class BaseAdapter {

    protected static final String BASE_URL = "https://api.qase.io";
    protected static final String BASE_PATH = "/v1";
    private static final String TOKEN_HEADER = "Token";
    public static final String TOKEN = requireToken();

    static {
        RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().blacklistHeader(TOKEN_HEADER));
    }

    public static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    public static RequestSpecification spec =
            specWithToken(TOKEN);

    public static RequestSpecification specWithToken(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath(BASE_PATH)
                .setContentType(ContentType.JSON)
                .addHeader(TOKEN_HEADER, token)
                .addFilter(new AllureRestAssured())
                .build();
    }

    public static ResponseSpecification expectStatus(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .build();
    }

    public static ResponseSpecification ok200 =
            expectStatus(200);

    public static ResponseSpecification badRequest400 =
            expectStatus(400);

    public static ResponseSpecification unauthorized401 =
            expectStatus(401);

    public static ResponseSpecification notFound404 =
            expectStatus(404);

    private static String requireToken() {
        String token = System.getProperty("token");

        if (token == null || token.isBlank()) {
            token = PropertyReader.getProperty("token");
        }

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "Не найден API-токен Qase"
            );
        }
        return token;
    }
}