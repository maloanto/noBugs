package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.DepositRequestModel;
import models.TransferModel;
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

public class CreateTransfer {
    private String adminToken;
    private static UserModel user1;
    private static UserModel user2;
    ;
    private static String user1Token;
    private static String user2Token;
    private static UserResponseModel userRequest1;
    private static UserResponseModel userRequest2;
    private static int user1AccId;
    private static int user1Acc2Id;
    private static int user2AccId;


    @BeforeAll
    public static void setepRestAssured() {
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

        user1 = UserGenerator.generateRandomUser();
        user2 = UserGenerator.generateRandomUser();

        userRequest1 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", adminToken)
                .body(user1)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .as(UserResponseModel.class);

        userRequest2 = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", adminToken)
                .body(user2)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .as(UserResponseModel.class);

        user1Token = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user1)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");

        user2Token = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user2)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");

        System.out.println(user1Token);
        System.out.println(user2Token);

        user1AccId = given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .body().jsonPath().getInt("id");

        user2AccId = given()
                .header("Authorization", user2Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .body().jsonPath().getInt("id");
    }

    @Test
    public void userCanTransferBy0_01() {
        DepositRequestModel deposit = new DepositRequestModel(user1AccId, 0.03);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        TransferModel transfer = new TransferModel(user1AccId, user2AccId, 0.01);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transfer)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.02f));

        given()
                .header("Authorization", user2Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.01f));
    }

    @Test
    public void userCanTransferBy10_000() {
        DepositRequestModel deposit = new DepositRequestModel(user1AccId, 5000.00);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        TransferModel transfer = new TransferModel(user1AccId, user2AccId, 10000.00);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transfer)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.00f));

        given()
                .header("Authorization", user2Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(10000.00f));
    }

    @Test
    public void userCanTransferBy9_999() {
        DepositRequestModel deposit = new DepositRequestModel(user1AccId, 5000.00);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        TransferModel transfer = new TransferModel(user1AccId, user2AccId, 9999.99);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transfer)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.01f));

        given()
                .header("Authorization", user2Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(9999.99f));
    }

    @Test
    public void userCanTransferBy2_000() {
        DepositRequestModel deposit = new DepositRequestModel(user1AccId, 5000.00);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        TransferModel transfer = new TransferModel(user1AccId, user2AccId, 2000.00);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transfer)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(3000.00f));

        given()
                .header("Authorization", user2Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(2000.00f));
    }


    @Test
    public void userCanTransfer2000ToTheirAccount() {
        DepositRequestModel deposit = new DepositRequestModel(user1AccId, 2000.00);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        user1Acc2Id = given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .body().jsonPath().getInt("id");



        TransferModel transfer = new TransferModel(user1AccId, user1Acc2Id, 2000.00);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transfer)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("find { it.id == " + user1AccId + " }.balance", equalTo(0.0f))
                .body("find { it.id == " + user1Acc2Id + " }.balance", equalTo(2000.0f));

    }

    @Test
    public void userCannotTransferMoreThan10_000() {
        DepositRequestModel deposit = new DepositRequestModel(user1AccId, 5000.00);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(deposit)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .statusCode(HttpStatus.SC_OK);

        TransferModel transfer = new TransferModel(user1AccId, user2AccId, 10000.01);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transfer)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(containsString("Transfer amount cannot exceed 10000"));

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(15000.00f));

        given()
                .header("Authorization", user2Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.0f));
    }




    @Test
    public void userCannotTransferMoreThanTheirAccount() {
        TransferModel transfer = new TransferModel(user1AccId, user2AccId, 0.01);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transfer)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(containsString("insufficient funds or invalid accounts"));

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.0f));
    }

    @Test
    public void userCannotTransferAnAccountTthatDoesNotExist() {
        TransferModel transfer = new TransferModel(user1AccId, 111111, 0.01);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transfer)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(containsString("insufficient funds or invalid accounts"));

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.0f));
    }

    @Test
    public void userCannotTransferANegativeAmount() {
        TransferModel transfer = new TransferModel(user1AccId, user2AccId, -0.01);
        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transfer)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(containsString("Transfer amount must be at least 0.01"));

        given()
                .header("Authorization", user1Token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("[0].balance", equalTo(0.0f));
    }


    
}
