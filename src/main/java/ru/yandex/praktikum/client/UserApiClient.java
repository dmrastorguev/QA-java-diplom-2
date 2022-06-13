package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.model.UserLogin;
import ru.yandex.praktikum.model.UserLogout;
import ru.yandex.praktikum.model.UserRegister;
import ru.yandex.praktikum.model.UserUpdateInfo;

import static io.restassured.RestAssured.given;

public class UserApiClient extends BaseHttpClient {

  private static final String USER_PATH = "auth/";

    @Step("Send POST request to /api/auth/register : {userRegister}")
    public  ValidatableResponse  register (UserRegister userRegister) {
        return given()
                .spec(baseSpec())
                .body(userRegister)
                .when()
                .post(USER_PATH + "register")
                .then()
                .log().ifError();
    }


    @Step("Send POST request to /api/auth/login  : {userLogin}")
    public ValidatableResponse login (UserLogin userLogin,String accessToken) {
        return given()
                .header("Authorization", "Bearer" + accessToken)
                .spec(baseSpec())
                .body(userLogin)
                .when()
                .post(USER_PATH + "login")
                .then()
                .log().ifError();
    }


    @Step("Send DELETE request to /api/auth/user : {userDelete}")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .header(  "Authorization", "Bearer" + accessToken)
                .spec(baseSpec())
                .when()
                .delete(USER_PATH+ "user")
                .then()
                .log().all()
                .log().ifError();
    }


    @Step("Send PATCH request to api/auth/user : {userUpdateInfo}")
    public ValidatableResponse userUpdate (UserUpdateInfo userUpdateInfo,String accessToken) {
        return given()
                .header(  "Authorization", "Bearer" + accessToken)
                .spec(baseSpec())
                .body(userUpdateInfo)
                .when()
                .patch(USER_PATH + "user")
                .then()
                .log().all()
                .log().ifError();
    }

    @Step("Send POST request to /api/auth/logout : {userLogout}")
    public ValidatableResponse userLogout(UserLogout userLogout) {
        return given()
                .spec(baseSpec())
                .body(userLogout)
                .when()
                .post(USER_PATH + "logout")
                .then()
                .log().all()
                .log().ifError();
    }


}
