package ru.yandex.praktikum.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import ru.yandex.praktikum.client.UserApiClient;
import ru.yandex.praktikum.data.UserGeneration;
import ru.yandex.praktikum.model.UserLogin;
import ru.yandex.praktikum.model.UserRegister;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserLoginTest {
    private UserApiClient userApiClient;
    private UserRegister userRegister;
    private UserGeneration userGeneration;
    private String accessTokenRegister;
    private String accessToken;


    @Before
    public void setUp() {
//      Создание пользователя
        userApiClient = new UserApiClient();
        userGeneration = new UserGeneration();
        userRegister = new UserRegister(userGeneration.userName(),userGeneration.userEmail(),userGeneration.userPassword());

        ValidatableResponse userCreateResponse = userApiClient.register(new UserRegister(userRegister.getName(), userRegister.getEmail(), userRegister.getPassword()));
        accessTokenRegister = userCreateResponse.extract().path("accessToken");

    }

    @After
    public void tearDown() {
//      удаляем пользователя
        userApiClient.delete(accessToken);
    }

    @Test
    @Order(4)
    @DisplayName("Тест на проверку возможности логина пользователя") // имя теста
    @Description("Проверяется возможность логина пользователя,также проверяется статус и тело ответа") // описание теста
    public void userLoginCorrectParameters() {
//      Логин пользователя
        ValidatableResponse loginResponse = userApiClient.login(new UserLogin(userRegister.getEmail(), userRegister.getPassword()),accessTokenRegister);

//      Получаем данные из ответа сервиса /api/auth/login для проверки
        int statusCode = loginResponse.extract().statusCode();
        Boolean isLoginOk = loginResponse.extract().path("success");
        accessToken = loginResponse.extract().path("accessToken").toString();

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 200", statusCode, equalTo(SC_OK));
        assertThat("User is not login", isLoginOk, is(true));

    }

    @Test
    @Order(5)
    @DisplayName("Тест на проверку возможности логина пользователя c некорректным почтовым адресом (email)") // имя теста
    @Description("Проверяется возможность логина пользователя c некорректным почтовым адресом (email), а также проверка статуса и тела ответа") // описание теста
    public void userLoginIncorrectEmail() {
//      Формируем некорректный email
        String userIncorrectEmail = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5)+ ".ru";

//      Логин пользователя
        ValidatableResponse loginResponse = userApiClient.login(new UserLogin( userIncorrectEmail, userRegister.getPassword()),accessTokenRegister);

//      Получаем данные из ответа сервиса /api/auth/login для проверки
        int statusCode = loginResponse.extract().statusCode();
        String errorMessage =  loginResponse.extract().path("message");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 401", statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
        assertThat("Wrong message body", errorMessage,equalTo("email or password are incorrect"));
    }

    @Test
    @Order(6)
    @DisplayName("Тест на проверку возможности логина пользователя c некорректным паролем") // имя теста
    @Description("Проверяется возможность логина пользователя c некорректным c некорректным паролем а также проверка статуса и тела ответа") // описание теста
    public void userLoginIncorrectPassword() {
//      Формируем некорректный пароль
        String userIncorrectPassword = RandomStringUtils.randomAlphabetic(10) ;

//      Логин пользователя
        ValidatableResponse loginResponse = userApiClient.login(new UserLogin( userRegister.getEmail(), userIncorrectPassword ), accessTokenRegister);

//      Получаем данные из ответа сервиса /api/auth/login для проверки
        int statusCode = loginResponse.extract().statusCode();
        String errorMessage =  loginResponse.extract().path("message");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 401", statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
        assertThat("Wrong message body", errorMessage,equalTo("email or password are incorrect"));
    }
}
