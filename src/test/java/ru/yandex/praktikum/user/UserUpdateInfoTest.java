package ru.yandex.praktikum.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import ru.yandex.praktikum.client.UserApiClient;
import ru.yandex.praktikum.data.UserGeneration;
import ru.yandex.praktikum.model.UserLogin;
import ru.yandex.praktikum.model.UserLogout;
import ru.yandex.praktikum.model.UserRegister;
import ru.yandex.praktikum.model.UserUpdateInfo;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserUpdateInfoTest {

    private UserApiClient userApiClient;
    private UserRegister userRegister;
    private UserGeneration userGeneration;
    private String accessTokenRegister;
    private String accessToken;
    private String refreshToken;


    @Before
    public void setUp() {
//      Создание пользователя
        userApiClient = new UserApiClient();
        userGeneration = new UserGeneration();
        userRegister = new UserRegister(userGeneration.userName(),userGeneration.userEmail(),userGeneration.userPassword());

        ValidatableResponse userCreateResponse = userApiClient.register(new UserRegister(userRegister.getName(), userRegister.getEmail(), userRegister.getPassword()));
        accessTokenRegister = userCreateResponse.extract().path("accessToken");

//      Авторизация пользователя в системе
        ValidatableResponse loginResponse = userApiClient.login(new UserLogin(userRegister.getEmail(), userRegister.getPassword()),accessTokenRegister);
        accessToken = loginResponse.extract().path("accessToken").toString();
        refreshToken = loginResponse.extract().path("refreshToken").toString();
    }

    @After
    public void tearDown() {
//      удаляем пользователя
        userApiClient.delete(accessToken);
    }

    @Test
    @Order(7)
    @DisplayName("Тест на проверку возможности изменения данных почтового адреса(email) авторизированного пользователя") // имя теста
    @Description("Проверяется возможность изменения данных почтового адреса(email) авторизированного пользователя,также проверяется статус и тело ответа") // описание теста
    public void userWithAuthorizationUpdateEmail() {

//      Изменение данных почтового адреса(email) авторизированного пользователя
        ValidatableResponse userUpdateResponse = userApiClient.userUpdate (new UserUpdateInfo(userRegister.getName(), "test700777new@yandex.ru", userRegister.getPassword()),accessToken);

//      Получаем данные из ответа сервиса api/auth/user для проверки
        int statusCode = userUpdateResponse.extract().statusCode();
        Boolean isUpdateOk = userUpdateResponse.extract().path("success");
        String userName = userUpdateResponse.extract().path("user.email");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 200", statusCode, equalTo(SC_OK));
        assertThat("User is not updated", isUpdateOk, is(true));
        assertThat("UserName is not change", userName, equalTo("test700777new@yandex.ru"));
    }


    @Test
    @Order(8)
    @DisplayName("Тест на проверку возможности изменения данных имени авторизированного пользователя") // имя теста
    @Description("Проверяется возможность изменения данных имени авторизированного пользователя,также проверяется статус и тело ответа") // описание теста
    public void userWithAuthorizationUpdateName() {

//      Изменение данных имени авторизированного пользователя
        ValidatableResponse userUpdateResponse = userApiClient.userUpdate (new UserUpdateInfo("rda_UserName_new", userRegister.getEmail(), userRegister.getPassword()),accessToken);

//      Получаем данные из ответа сервиса api/auth/user для проверки
        int statusCode = userUpdateResponse.extract().statusCode();
        Boolean isUpdateOk = userUpdateResponse.extract().path("success");
        String userName = userUpdateResponse.extract().path("user.name");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 200", statusCode, equalTo(SC_OK));
        assertThat("User is not updated", isUpdateOk, is(true));
        assertThat("UserName is not change", userName, equalTo("rda_UserName_new"));
    }





    @Test
    @Order(9)
    @DisplayName("Тест на проверку возможности изменения данных пароля авторизированного пользователя") // имя теста
    @Description("Проверяется возможность изменения данных пароля авторизированного пользователя,также проверяется статус и тело ответа") // описание теста
    public void userWithAuthorizationUpdatePassword() {

//      Изменение данных пароля авторизированного пользователя
        ValidatableResponse userUpdateResponse = userApiClient.userUpdate (new UserUpdateInfo(userRegister.getName(), userRegister.getEmail(), "new_password1"),accessToken);

//      Получаем данные из ответа сервиса api/auth/user для проверки
        int statusCode = userUpdateResponse.extract().statusCode();
        Boolean isUpdateOk = userUpdateResponse.extract().path("success");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 200", statusCode, equalTo(SC_OK));
        assertThat("User is not updated", isUpdateOk, is(true));

//      Необходимо выйти из системы
        userApiClient.userLogout(new UserLogout(refreshToken));

//      Проверка, что авторизация в систему происходит с новым значением пароля
        ValidatableResponse loginResponse = userApiClient.login(new UserLogin(userRegister.getEmail(), "new_password1"),accessToken);
        accessToken = loginResponse.extract().path("accessToken").toString();

        int statusCodeLogin = loginResponse.extract().statusCode();
        Boolean isLoginOk = loginResponse.extract().path("success");

        assertThat("Status code couldn't be 200", statusCodeLogin, equalTo(SC_OK));
        assertThat("User is not login", isLoginOk, is(true));

    }
}
