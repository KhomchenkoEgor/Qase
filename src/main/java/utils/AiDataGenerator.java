package utils;

import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import api.models.cases.CaseRq;
import api.models.plan.PlanRq;
import api.models.project.ProjectRq;
import api.models.suite.SuiteRq;


import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AiDataGenerator {

    private static final String MISTRAL_API_URL = "https://api.mistral.ai/v1";
    private static final String MODEL_NAME = "mistral-medium-latest";
    private static final String API_KEY = System.getProperty("mistral_api_key", PropertyReader.getProperty("mistral_api_key"));
    private static final Gson GSON = new Gson();

    private static final RequestSpecification mistralSpec = new RequestSpecBuilder()
            .setBaseUri(MISTRAL_API_URL)
            .setBasePath("/chat/completions")
            .setContentType(ContentType.JSON)
            .addHeader("Authorization", "Bearer " + API_KEY)
            .build();

    private static String generateJsonViaLlm(String userPrompt) {
        String systemPrompt = "You are a professional QA automation data generator. " +
                "Output ONLY a valid raw JSON object matching the requested schema. " +
                "Do not include any explanations, markdown code blocks (```json), or comments.";

        if (API_KEY == null || API_KEY.isEmpty()) {
            return getFallbackJson(userPrompt);
        }
        Map<String, Object> requestBody = Map.of(
                "model", MODEL_NAME,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.6
        );

        try {
            return given()
                    .spec(mistralSpec)
                    .body(GSON.toJson(requestBody))
                    .when()
                    .post()
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getString("choices[0].message.content");
        } catch (Exception e) {

            return getFallbackJson(userPrompt);
        }
    }

    public static ProjectRq generateProjectData() {
        String prompt = """
                Generate a JSON for creating a project in Qase.io.
                Schema criteria:
                - title: Unique IT project name in English (e.g., 'Aura Fintech Hub', 'Nova Stream Core') + random 3-digit number.
                - code: Random uppercase string STRICTLY between 3 and 5 characters long, only letters A-Z (e.g., 'NEXUS', 'MTRX').
                - description: A short description of this testing repository in Russian language.
                - access: strictly "all"
                - group: strictly null
                """;

        String jsonResponse = "";
        try {
            jsonResponse = generateJsonViaLlm(prompt);
            ProjectRq dto = GSON.fromJson(jsonResponse, ProjectRq.class);
            dto.setAccess("all");
            dto.setGroup(null);
            return dto;
        } catch (Exception e) {
            System.err.println("Ошибка парсинга данных проекта. Применен фолбэк.");
            long rnd = (long) (Math.random() * 1000);
            return ProjectRq.builder()
                    .title("Fallback Project " + rnd)
                    .code("FALLB")
                    .description("Резервные данные проекта")
                    .access("all")
                    .group(null)
                    .build();
        }
    }

    public static SuiteRq generateSuiteData() {
        String prompt = """
                Generate a JSON for a test suite (folder) inside a repository.
                Schema criteria:
                - title: Name of a functional module in an e-commerce or bank application in English (e.g., 'Shopping Cart Operations', 'Biometric Authorization').
                - description: Short description of covered test cases in Russian.
                """;
        return GSON.fromJson(generateJsonViaLlm(prompt), SuiteRq.class);
    }

    public static CaseRq generateTestCaseData() {
        String prompt = """
                Generate a JSON for a high-quality QA test case.
                **CRITICAL**: preconditions and postconditions MUST be **single strings**, NOT arrays!
                Use semicolons (;) to separate multiple conditions.
                Schema criteria:
                - title: Meaningful test case title in Russian language.
                - description: Detailed test scenario objective in Russian.
                - preconditions: **Single string** with initial test conditions in Russian, separated by semicolons.
                - postconditions: **Single string** with cleanup or expected global state in Russian, separated by semicolons.
                - severity: integer (1 for Blocker, 2 for Critical, 3 for Major).
                - priority: integer (1 for High, 2 for Medium, 3 for Low).
                - status: integer (strictly 1).
                - steps: Array of 2 realistic test steps. Each step must contain fields:
                  * action: user interaction text in Russian.
                  * expected_result: verification outcome text in Russian.
                """;
        return GSON.fromJson(generateJsonViaLlm(prompt), CaseRq.class);
    }

    public static PlanRq generatePlanData(List<Integer> caseIds) {
        String prompt = String.format("""
                Generate a JSON object for a release test plan.
                CRITICAL: Every field must be a primitive type. No nested objects allowed!
                Schema criteria:
                - title: Name of the test plan (e.g., 'Regression Sprint 12') strictly as a single string.
                - description: Objectives of this testing cycle strictly as a single text string in Russian.
                - cases: This array MUST be exactly this list of integers: %s
                """, caseIds.toString());

        try {
            String jsonResponse = generateJsonViaLlm(prompt);
            if (jsonResponse.contains("Fallback Plan Release") || !jsonResponse.contains("cases")) {
                throw new RuntimeException("Требуется локальный Java-фолбэк с передачей реальных ID кейсов");
            }
            return GSON.fromJson(jsonResponse, PlanRq.class);
        } catch (Exception e) {
            System.err.println("Применяется надежный Java-фолбэк для PlanRq с привязкой кейсов из теста.");
            long localRnd = (long) (Math.random() * 1000);
            return PlanRq.builder()
                    .title("Fallback Plan Release " + localRnd)
                    .description("Регрессионный тест-план. Сгенерировано автоматически (Локальный фолбэк).")
                    .cases(caseIds)
                    .build();
        }
    }

    public static String generatePlanTitleViaLlm() {
        String prompt = "Generate a single realistic QA Test Plan title for a web application release " +
                "(e.g., 'Sprint 14 Regression', 'Payment Gateway Hotfix'). " +
                "Output ONLY a raw text string, no JSON, no quotes, no formatting.";

        String response = generateJsonViaLlm(prompt);
        return response.replaceAll("\"", "").replaceAll("\\{", "").replaceAll("\\}", "").trim();
    }

    private static String getFallbackJson(String prompt) {
        long rnd = (long) (Math.random() * 1000);
        if (prompt.contains("code")) {
            return String.format("{\"title\":\"Fallback Proj %d\",\"code\":\"FBK%d\",\"description\":\"Резервные данные\"}", rnd, rnd);
        } else if (prompt.contains("suite")) {
            return "{\"title\":\"Fallback Suite Core\",\"description\":\"Резервная папка\"}";
        } else if (prompt.contains("steps")) {
            return "{\"title\":\"Fallback Case Request\",\"severity\":2,\"priority\":2,\"status\":1}";
        } else if (prompt.contains("cases")) {
            return "{\"title\":\"Fallback Plan Release\",\"description\":\"Резервный план\",\"cases\":[1]}";
        } else {
            return "{\"title\":\"Fallback Plan Release\",\"description\":\"Резервный план\"}";
        }
    }

    public static String generateInvalidEmail() {
        String prompt = "Generate a single INVALID email address for testing purposes. " +
                "The email MUST NOT contain '@' symbol or have a valid domain format. " +
                "Examples: 'invalid-email', 'test@', '@domain.com', 'plainstring'. " +
                "Output ONLY a raw text string, no markdown, no quotes, no formatting.";

        try {
            String response = generateJsonViaLlm(prompt);
            return response.replaceAll("\"", "").replaceAll("\\{", "").replaceAll("\\}", "").trim();
        } catch (Exception e) {
            return "invalid-email-" + (int) (Math.random() * 10000);
        }
    }

    public static String generateText(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
