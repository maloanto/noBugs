package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.DepositRequestModel;
import models.UserModel;
import models.UserResponseModel;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.UserGenerator;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class CreateDeposit {
    private String adminToken;
    private UserModel user;
    private String userToken;
    private UserResponseModel userRequest;
    private int userAccId;

    @BeforeAll
    public static void setapRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));
    }

    @BeforeEach
    public void setup() {
        adminToken = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        
                        {
                           "username": "admin",
                           "password": "admin"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().header("Authorization");

        user = UserGenerator.generateRandomUser();
        userRequest = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization",adminToken)
                .body(user)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .as(UserResponseModel.class);

        userToken = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");

        userAccId = given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .body().jsonPath().getInt("id");
    }




    @Test
    public void userCanTopUpTheirAccountBy0_01() {
        DepositRequestModel deposit = new DepositRequestModel(userAccId, 0.01);
        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.01f));
    }

    @Test
    public void userCanTopUpTheirAccountBy4_999() {
        DepositRequestModel deposit = new DepositRequestModel(userAccId, 4.999);
        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(4.999f));
    }

    @Test
    public void userCanTopUpTheirAccountBy5_000() {
        DepositRequestModel deposit = new DepositRequestModel(userAccId, 5.000);
        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(5.000f));
    }


    @Test
    public void userCanNotTopUpTheirAccountByNegativeValue() {
        DepositRequestModel deposit = new DepositRequestModel(userAccId, -0.01);
        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(containsString("Deposit amount must be at least 0.01"));

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.0f));
    }


    @Test
    public void userCanNotTopUpTheirAccountBy0() {
        DepositRequestModel deposit = new DepositRequestModel(userAccId, 0);
        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(containsString("Deposit amount must be at least 0.01"));

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.0f));
    }

    @Test
    public void userCanNotTopUpTheirAccountBy5001() {
        DepositRequestModel deposit = new DepositRequestModel(userAccId, 6.001);
        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(containsString("Deposit amount must be at least 0.01"));

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.0f));
    }

    @Test
    public void userCanNotTopUpNonExistentAccount() {
        DepositRequestModel deposit = new DepositRequestModel(10001, 2.0);
        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body(containsString("Unauthorized access to account"));

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.0f));
    }
}
