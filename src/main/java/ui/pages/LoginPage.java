package ui.pages;

import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.shadowCss;
import static com.codeborne.selenide.Selenide.*;
import static ui.dict.Elements.SIGN_IN;
import static ui.locators.LoginLocators.*;


@Log4j2
public class LoginPage {

    @Step("Открыть страницу авторизации")
    public LoginPage openPage() {
        log.info("UI: Открытие веб-страницы авторизации Qase.io");
        clearBrowserCookies();
        open("/login");
        log.info("UI: Текущий URL после открытия: {}",
                webdriver().driver().url());
        $(LOGIN)
                .shouldBe(visible, Duration.ofSeconds(20));
        return this;
    }

    private void acceptCookiesIfPresent() {
        log.info("UI: Проверка cookie баннера");
        try {
            $(shadowCss("button", "#usercentrics-cmp-ui"))
                    .shouldBe(visible, Duration.ofSeconds(5))
                    .click();
            log.info("UI: Cookie приняты");
        } catch (Exception e) {
            log.info("UI: Cookie баннер отсутствует или уже закрыт");
        }
    }

    @Step("Выполнить вход в систему пользователем: '{user}'")
    public ProjectsPage login(String user, String password) {
        log.info("UI: Начало авторизации под пользователем: {}", user);
        acceptCookiesIfPresent();
        log.info("UI: Заполнение поля Email");
        $(LOGIN).setValue(user);
        log.info("UI: Заполнение поля Password");
        $(PASSWORD).setValue(password);
        log.info("UI: Клик по кнопке '{}'", SIGN_IN);
        $(byText(SIGN_IN)).click();
        log.info("После клика URL: {}", webdriver().driver().url());
        log.info("UI: Ожидание индикатора успешной загрузки страницы проектов: '{}'", "a[href='/projects']");
        $(PROJECTS)
                .shouldBe(visible, Duration.ofSeconds(40));
        return new ProjectsPage();
    }

    @Step("Выполнить попытку входа с невалидными данными пользователем: '{user}'")
    public LoginPage loginWithInvalidCredentials(String user, String password) {
        log.info("UI: Попытка невалидной авторизации под пользователем: {}", user);
        log.info("UI: Ввод Email: {} и Password", user);
        $(LOGIN).setValue(user);
        $(PASSWORD).setValue(password);
        log.info("UI: Отправка формы авторизации");
        $(byText(SIGN_IN)).click();

        return this;
    }

    @Step("Проверить отображение глобальной ошибки безопасности '{expectedError}'")
    public LoginPage checkGlobalError(String expectedError) {
        log.info("UI: Ожидание и проверка текста глобального алерта ошибки: {}", expectedError);
        $(GLOBAL_ERROR_ALERT)
                .shouldBe(visible, java.time.Duration.ofSeconds(5))
                .shouldHave(text(expectedError));
        return this;
    }

    @Step("Проверить ошибку валидации формата поля: '{expectedError}'")
    public LoginPage checkFieldError(String expectedErrorPass) {
        log.info("UI: Ожидание и проверка текста локальной ошибки валидации инпута: {}", expectedErrorPass);
        $(FIELD_ERROR_MESSAGE)
                .shouldBe(visible, java.time.Duration.ofSeconds(5))
                .shouldHave(partialText(expectedErrorPass));
        return this;
    }
}