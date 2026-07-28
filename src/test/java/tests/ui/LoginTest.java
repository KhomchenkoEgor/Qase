package tests.ui;

import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import utils.QwenDataGenerator;

@Log4j2
@Epic("Qase UI Application")
@Feature("Авторизация")
@Story("Вход в личный кабинет")
@Owner("Khomchenko E.S.")
public class LoginTest extends BaseTest {

    @Test(
            testName = "Успешный вход в Qase.io",
            description = "Проверка успешного SPA-перехода на дашборд проектов после ввода валидных кредов",
            groups = {"smoke", "regression"})
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink("QASE-UI-01")
    public void checkSuccessfulLogin() {
        log.info("Тест UI: Попытка успешной авторизации валидным юзером");
        loginPage.openPage()
                .login(user, password)
                .isPageOpened();
    }

    @Test(
            testName = "Авторизация с ошибочным паролем",
            description = "Проверка вывода глобального алерта безопасности при неверном пароле",
            groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("QASE-UI-02")
    public void checkLoginWithWrongPassword() {
        log.info("Тест UI: Проверка триггера глобальной ошибки безопасности");
        String wrongPassword = "Wrong_Password_" + QwenDataGenerator.generateText(5);
        loginPage.openPage()
                .loginWithInvalidCredentials(user, wrongPassword)
                .checkGlobalError("These credentials do not match our records.");
    }

    @Test(
            testName = "Авторизация с битым email-форматом",
            description = "Проверка работы JS-валидатора регулярных выражений на поле Email",
            groups = {"regression"})
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("QASE-UI-03")
    public void checkLoginWithInvalidEmailFormat() {
        log.info("Тест UI: Проверка фронтенд-валидации формата строки email");
        String randomEmail = QwenDataGenerator.generateEmail();
        loginPage.openPage()
                .loginWithInvalidCredentials(randomEmail, password)
                .checkFieldError("does not match format email");
    }
}
