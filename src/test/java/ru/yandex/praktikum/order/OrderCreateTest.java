package ru.yandex.praktikum.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import ru.yandex.praktikum.client.OrderApiClient;
import ru.yandex.praktikum.client.UserApiClient;
import ru.yandex.praktikum.data.UserGeneration;
import ru.yandex.praktikum.model.OrderCreate;
import ru.yandex.praktikum.model.UserLogin;
import ru.yandex.praktikum.model.UserRegister;

import java.util.ArrayList;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderCreateTest {

    private UserApiClient userApiClient;
    private OrderApiClient orderApiClient;
    private UserRegister userRegister;
    private UserGeneration userGeneration;
    private String accessTokenRegister;
    private String accessToken;
    private  OrderCreate orderCreate;
    private ArrayList<String> myIngredientsList;


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

//      Получение списка всех ингредиентов и подготовка данных для создания заказа
        orderApiClient = new OrderApiClient();
        ValidatableResponse orderIngredientList = orderApiClient.getIngredients();
        ArrayList<String> idIngredientsList = orderIngredientList.extract().path("data._id");
//      Подготовка набора данных для создания заказа
        myIngredientsList = new ArrayList<String>();
        myIngredientsList.add(idIngredientsList.get(0));
        myIngredientsList.add(idIngredientsList.get(1));
        myIngredientsList.add(idIngredientsList.get(3));
    }

    @After
    public void tearDown() {
//      удаляем пользователя
        userApiClient.delete(accessToken);
    }

    @Test
    @Order(11)
    @DisplayName("Тест на проверку возможности создания заказа для авторизированного пользователя") // имя теста
    @Description("Проверяется возможность создания заказа для авторизированного пользователя,также проверяется статус и тело ответа") // описание теста
    public void orderCreateWithAuthorization() {

//      Создаем заказ
        orderCreate = new OrderCreate(myIngredientsList);
        ValidatableResponse orderCreateResponse = orderApiClient.create(new OrderCreate(orderCreate.getIngredients()), accessToken);

//      Получаем данные из ответа сервиса /api/orders для проверки
        int statusCode = orderCreateResponse.extract().statusCode();
        Boolean isOderCreateOk = orderCreateResponse.extract().path("success");
        Integer orderPrice = orderCreateResponse.extract().path("order.price");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 200", statusCode, equalTo(SC_OK));
        assertThat("Order is not created", isOderCreateOk, is(true));
        assertThat("OrderPrice is 0 or null", orderPrice, is(not(0)));
    }

    @Test
    @Order(12)
    @DisplayName("Тест на проверку возможности создания заказа без ингредиентов для авторизированного пользователя") // имя теста
    @Description("Проверяется возможность заказа без ингредиентов для авторизированного пользователя,также проверяется статус и тело ответа") // описание теста
    public void orderCreateWithAuthorizationNoIngredients()  {

//      Создаем заказ
        orderCreate = new OrderCreate(null);
        ValidatableResponse orderCreateResponse = orderApiClient.create(new OrderCreate(orderCreate.getIngredients()), accessToken);

//      Получаем данные из ответа сервиса /api/orders для проверки
        int statusCode = orderCreateResponse.extract().statusCode();
        Boolean isOderCreateOk = orderCreateResponse.extract().path("success");
        String errorMessage = orderCreateResponse.extract().path("message");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 400", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Order is created", isOderCreateOk, is(false));
        assertThat("Order ingredients was added", errorMessage, equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Order(13)
    @DisplayName("Тест на проверку возможности создания заказа без ингредиентов для не авторизированного пользователя") // имя теста
    @Description("Проверяется возможность создания заказа без ингредиентов для не авторизированного пользователя,также проверяется статус и тело ответа") // описание теста
    public void orderCreateWithoutAuthorizationNoIngredients()  {

//      Создаем заказ
        orderCreate = new OrderCreate(null);
        ValidatableResponse orderCreateResponse = orderApiClient.create(new OrderCreate(orderCreate.getIngredients()), null);

//      Получаем данные из ответа сервиса /api/orders для проверки
        int statusCode = orderCreateResponse.extract().statusCode();
        Boolean isOderCreateOk = orderCreateResponse.extract().path("success");
        String errorMessage = orderCreateResponse.extract().path("message");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 400", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat("Order is created", isOderCreateOk, is(false));
        assertThat("Order ingredients was added", errorMessage, equalTo("Ingredient ids must be provided"));
    }


    @Test
    @Order(14)
    @DisplayName("Тест на проверку возможности создания заказа для не авторизированного пользователя") // имя теста
    @Description("Проверяется возможность создания заказа для не авторизированного пользователя,также проверяется статус и тело ответа") // описание теста
    public void orderCreateWithoutAuthorization()  {

//      Создаем заказ
        orderCreate = new OrderCreate(myIngredientsList);
        ValidatableResponse orderCreateResponse = orderApiClient.create(new OrderCreate(orderCreate.getIngredients()), null);

//      Получаем данные из ответа сервиса /api/orders для проверки
        int statusCode = orderCreateResponse.extract().statusCode();
        Boolean isOderCreateOk = orderCreateResponse.extract().path("success");
        Integer oderNumber = orderCreateResponse.extract().path("oder.number");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 200", statusCode, equalTo(SC_OK));
        assertThat("Order is not created", isOderCreateOk, is(true));
        assertThat("Order Number is null", oderNumber, is(not(0)));
    }

    @Test
    @Order(15)
    @DisplayName("Тест на проверку возможности создания заказа для авторизированного пользователя") // имя теста
    @Description("Проверяется возможность создания заказа для авторизированного пользователя,также проверяется статус и тело ответа") // описание теста
    public void orderCreateWithAuthorizationWrongIngredients() throws Exception{

//      Подготавливаем данные для заказа - ингредиенты с неправильным хешем
        ArrayList<String> myIngredientsListWithWrongIngredients = new ArrayList<String>();
        myIngredientsListWithWrongIngredients.add("61c0c5a71d1f82001bdaaa703");

//      Создаем заказ
        orderCreate = new OrderCreate(myIngredientsListWithWrongIngredients);
        ValidatableResponse orderCreateResponse = orderApiClient.create(new OrderCreate(orderCreate.getIngredients()), accessToken);

//      Получаем данные из ответа сервиса /api/orders для проверки
        int statusCode = orderCreateResponse.extract().statusCode();

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 500", statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
    }
}
