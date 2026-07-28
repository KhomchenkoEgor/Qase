package api.adapters;

import utils.PropertyReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;


public class BaseAdapter {

    protected static final String BASE_URL = "https://api.qase.io";
    public static final String TOKEN =
            System.getProperty("token", PropertyReader.getProperty("token"));

    public static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    public static RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setBasePath("/v1")
            .setContentType(ContentType.JSON)
            .addHeader("Token", TOKEN)
            .build();

    public static ResponseSpecification ok200 = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .build();
}
