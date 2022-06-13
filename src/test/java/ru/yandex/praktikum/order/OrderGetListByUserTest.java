package ru.yandex.praktikum.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.yandex.praktikum.client.OrderApiClient;
import ru.yandex.praktikum.client.UserApiClient;
import ru.yandex.praktikum.data.UserGeneration;
import ru.yandex.praktikum.model.OrderCreate;
import ru.yandex.praktikum.model.UserLogin;
import ru.yandex.praktikum.model.UserRegister;
import java.util.ArrayList;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class OrderGetListByUserTest {

    private UserApiClient userApiClient;
    private OrderApiClient orderApiClient;
    private UserRegister userRegister;
    private UserGeneration userGeneration;
    private String accessTokenRegister;
    private String accessToken;
    private OrderCreate orderCreate;
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
        myIngredientsList = new ArrayList<String>();
        myIngredientsList.add(idIngredientsList.get(0));
        myIngredientsList.add(idIngredientsList.get(1));
        myIngredientsList.add(idIngredientsList.get(3));

//      Создаем заказ
        orderCreate = new OrderCreate(myIngredientsList);
        orderApiClient.create(new OrderCreate(orderCreate.getIngredients()), accessToken);
    }

    @After
    public void tearDown() {
//      удаляем пользователя
        userApiClient.delete(accessToken);
    }

    @Test
    @Order(16)
    @DisplayName("Тест на проверку возможности получения списка заказов для авторизированного пользователя") // имя теста
    @Description("Проверяется возможность получения списка заказов для авторизированного пользователя,также проверяется статус и тело ответа") // описание теста
    public void orderGetListByUserWithAuthorization() {
//      Отправляем запрос на получение списка заказов для авторизированного пользователя
        ValidatableResponse orderGetListByUserResponse = orderApiClient.ordersListByUser(accessToken);

//      Получаем данные из ответа сервиса /api/orders для проверки
        int statusCode = orderGetListByUserResponse.extract().statusCode();
        Boolean isOrderGetListOk = orderGetListByUserResponse.extract().path("success");
        ArrayList<Integer>  orderNumbers = orderGetListByUserResponse.extract().path("orders.number");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 200", statusCode, equalTo(SC_OK));
        assertThat("OrderList is not get", isOrderGetListOk , is(true));
        assertThat("OrderList is empty", orderNumbers.size(), is(not(0)));
    }

    @Test
    @Order(17)
    @DisplayName("Тест на проверку возможности получения списка заказов для не авторизированного пользователя") // имя теста
    @Description("Проверяется возможность получения списка заказов для не авторизированного пользователя,также проверяется статус и тело ответа") // описание теста
    public void orderGetListByUserWithoutAuthorization() {
//      Отправляем запрос на получение списка заказов для авторизированного пользователя
        ValidatableResponse orderGetListByUserResponse = orderApiClient.ordersListByUser(null);

//      Получаем данные из ответа сервиса /api/orders для проверки
        int statusCode = orderGetListByUserResponse.extract().statusCode();
        Boolean isOrderGetListOk = orderGetListByUserResponse.extract().path("success");

//      Проверяем, что ожидаемый результат совпадает с действительным
        assertThat("Status code couldn't be 401", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat("OrderList is get", isOrderGetListOk , is(false));
    }

}
