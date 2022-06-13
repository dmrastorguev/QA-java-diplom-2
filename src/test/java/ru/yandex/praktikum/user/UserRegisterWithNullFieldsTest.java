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
import ru.yandex.praktikum.data.UserGeneration;
import ru.yandex.praktikum.model.UserRegister;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class UserRegisterWithNullFieldsTest {

    private UserApiClient userApiClient;
    private static UserGeneration userGeneration;

    private String userName;
    private String userEmail;
    private String userPassword;
    private String accessToken;
    private static String e;
    private static String p;
    private static String n;

    public UserRegisterWithNullFieldsTest(String userName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    @Before
    public void setUp() {
        userApiClient = new UserApiClient();
        userGeneration = new UserGeneration();
        
    }

    @After
    public void tearDown() {
//       удаляем созданного пользователя
        userApiClient.delete(accessToken);
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{

                {"","testmail1@t.ru","password1"},
                {"TestName1","","password1"},
                {"","","password1"},
                {"TestName1","testmail1@t.ru",""},
                {"TestName1","",""},
                {"","testmail1@t.ru",""},
                {"","",""}
        };
    }

    @Test
    @Order(3)
    @DisplayName("Тест на невозможность создания пользователя без одного из обязательных полей")
    @Description("Тест проверяет, что в одном из обязательных полей отсутствуют значения,то запрос возвращает ошибку.")
    public void courierCreateWithNullFieldsTest() throws NullPointerException {
        ValidatableResponse userRegisterResponseDuplicate = userApiClient.register(new UserRegister(userName, userEmail, userPassword));

//      В случае, если пользователь будет создан без одного из обязательных полей, добавлено получение accessToken для его удаления
//      Если пользователь не создается без одного из обязательных полей, то получение accessToken приводит к образованию NullPointerException добавлен блок (try - catch)
        try {
            accessToken = userRegisterResponseDuplicate.extract().path("accessToken").toString();
        }
        catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("Exception was processed. Program continues");
            System.out.println("Шалость не удалась. Для успешной регистрации не хватает данных пользователя");
        }

//      Получаем данные из ответа сервиса /api/auth/register для проверки
        int statusCode = userRegisterResponseDuplicate.extract().statusCode();
        Boolean isRegistrationOk = userRegisterResponseDuplicate.extract().path("success");
        String errorMessage = userRegisterResponseDuplicate.extract().path("message");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 403", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User is not created", isRegistrationOk, is(false));
        assertThat("User is created", errorMessage, equalTo("Email, password and name are required fields"));

    }
}