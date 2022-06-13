package ru.yandex.praktikum.user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.client.UserApiClient;
import ru.yandex.praktikum.model.UserLogin;
import ru.yandex.praktikum.model.UserRegister;
import ru.yandex.praktikum.model.UserUpdateInfo;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class UserUpdateInfoNoAuthorizationTest {

    private UserApiClient userApiClient;
    private UserRegister userRegister;
    private String accessTokenRegister;
    private String accessToken;


    private String userName;
    private String userEmail;
    private String userPassword;

    public UserUpdateInfoNoAuthorizationTest(String userName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    @Before
    public void setUp() {
//      Создание пользователя
        userApiClient = new UserApiClient();
        userRegister = new UserRegister("RD_UserName_1", "test-data_rd@yandex.ru", "password");

        ValidatableResponse userCreateResponse = userApiClient.register(new UserRegister(userRegister.getName(), userRegister.getEmail(), userRegister.getPassword()));
        accessTokenRegister = userCreateResponse.extract().path("accessToken");

//      Авторизация пользователя в системе
        ValidatableResponse loginResponse = userApiClient.login(new UserLogin(userRegister.getEmail(), userRegister.getPassword()),accessTokenRegister);
        accessToken = loginResponse.extract().path("accessToken").toString();
    }

    @After
    public void tearDown() {
//      удаляем пользователя
        userApiClient.delete(accessToken);
    }


    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{

                {"RD_UserName_new","test-data_rd@yandex.ru","password"},
                {"RD_UserName_1","new_test-data_rd@yandex.ru","password"},
                {"RD_UserName_1","test-data_rd@yandex.ru","password_new"}
          };
    }

    @Test
    @Order(10)
    @DisplayName("Тест на проверку возможности изменения данных пользователя без авторизации") // имя теста
    @Description("Проверяется возможность изменения данных пользователя без авторизации,также проверяется статус и тело ответа") // описание теста
    public void userWithAuthorizationUpdateName() {
//      Изменение данных имени авторизированного пользователя
        ValidatableResponse userUpdateResponse = userApiClient.userUpdate (new UserUpdateInfo(userName, userEmail, userPassword),null);

//      Получаем данные из ответа сервиса api/auth/user для проверки
        int statusCode = userUpdateResponse.extract().statusCode();
        Boolean isRegistrationOk = userUpdateResponse.extract().path("success");
        String errorMessage = userUpdateResponse.extract().path("message");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 401", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("User is not updated", isRegistrationOk, is(false));
        assertThat("User is updated", errorMessage, equalTo("You should be authorised"));
    }
}
