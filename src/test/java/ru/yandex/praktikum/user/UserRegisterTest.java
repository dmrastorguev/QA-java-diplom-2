package ru.yandex.praktikum.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import ru.yandex.praktikum.client.UserApiClient;
import ru.yandex.praktikum.data.UserGeneration;
import ru.yandex.praktikum.model.UserRegister;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRegisterTest {

    private UserApiClient userApiClient;
    private UserRegister userRegister;
    private UserGeneration userGeneration;
    private String accessToken;


    @Before
    public void setUp() {
        userApiClient = new UserApiClient();
        userGeneration = new UserGeneration();
        userRegister = new UserRegister(userGeneration.userName(),userGeneration.userEmail(),userGeneration.userPassword());
    }

    @After
    public void tearDown() {
//       удаляем созданного пользователя
        userApiClient.delete(accessToken);
    }

    @Test
    @Order(1)
    @DisplayName("Тест на проверку создание пользователя") // имя теста
    @Description("Тест на проверку создание пользователя с корректными параметрами") // описание теста
    public void userRegisterWithCorrectParameter() {
//      Создание пользователя
        ValidatableResponse userRegisterResponse = userApiClient.register(new UserRegister(userRegister.getName(), userRegister.getEmail(), userRegister.getPassword()));
        System.out.println(userRegisterResponse.extract().response().getBody().print());

//      Получаем данные из ответа сервиса /api/auth/register для проверки
        int statusCode = userRegisterResponse.extract().statusCode();
        Boolean isRegistrationOk = userRegisterResponse.extract().path("success");
        accessToken = userRegisterResponse.extract().path("accessToken").toString();

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 200", statusCode, equalTo(SC_OK));
        assertThat("Courier is not created", isRegistrationOk, is(true));
    }

    @Test
    @Order(2)
    @DisplayName("Тест на проверку создание дубликата пользователя")
    @Description("Тест проверяет, что нельзя создать двух одинаковых пользователей")
    public void userRegisterDuplicateRecord() {
//      Создание пользователя
        ValidatableResponse userRegisterResponse = userApiClient.register(new UserRegister(userRegister.getName(), userRegister.getEmail(), userRegister.getPassword()));
        accessToken = userRegisterResponse.extract().path("accessToken").toString();

//      Создание дубликата пользователя
        ValidatableResponse userRegisterResponseDuplicate = userApiClient.register(new UserRegister(userRegister.getName(), userRegister.getEmail(), userRegister.getPassword()));
        System.out.println(userRegisterResponseDuplicate.extract().response().getBody().print());

//      Получаем данные из ответа сервиса /api/auth/register для проверки
        int statusCode = userRegisterResponseDuplicate.extract().statusCode();
        Boolean isRegistrationOk = userRegisterResponseDuplicate.extract().path("success");
        String errorMessage = userRegisterResponseDuplicate.extract().path("message");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 403", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User is not created", isRegistrationOk, is(false));
        assertThat("User is created", errorMessage, equalTo("User already exists"));
    }
}








