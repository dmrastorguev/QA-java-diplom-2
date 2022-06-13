package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.model.OrderCreate;

import static io.restassured.RestAssured.given;

public class OrderApiClient extends BaseHttpClient {

    private static final String USER_PATH = "orders/";
    private static final String USER_PATH_INGR = "ingredients";

    @Step("Send POST request to /api/orders : {orderCreate}")
    public ValidatableResponse create (OrderCreate orderCreate, String accessToken) {
        return given()
                .header("Authorization", "Bearer" + accessToken)
                .spec(baseSpec())
                .body(orderCreate)
                .when()
                .post(USER_PATH)
                .then()
                .log().all()
                .log().ifError();
    }

    @Step("Send POST request to /api/orders : {ordersListByUser}")
    public ValidatableResponse ordersListByUser (String accessToken) {
        return given()
                .header("Authorization", "Bearer" + accessToken)
                .when()
                .get(USER_PATH)
                .then()
                .log().all()
                .log().ifError();
    }

    @Step("Send GET request to /api/ingredients : {getIngredients}")
    public ValidatableResponse getIngredients () {
        return given()
                .spec(baseSpec())
                .when()
                .get(USER_PATH_INGR)
                .then()
                .log().all()
                .log().ifError();
    }

}
