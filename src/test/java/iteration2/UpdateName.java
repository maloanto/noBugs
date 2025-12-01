package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.UserModel;
import models.UserResponseModel;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.UserGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UpdateName {

    private String adminToken;
    private UserModel user;
    private String userToken;
    private UserResponseModel userRequest;

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
    }


    @Test
    public void userCanChangeNameWithValidDate(){
        String newName = "Jonn Smith";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", newName);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(newName));
    }

    @Test
    public void userCanChangeNameToSingleLetterNameAndSurname(){
        String newName = "J S";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", newName);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(newName));
    }

    @Test
    public void userCanChangeNameToLongNameAndSurname(){
        String newName = "Jfsdffdsfdsfsdfsdf Passdfsdfsdfdsfds";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", newName);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(newName));
    }

    @Test
    public void userCanNotChangeNameWithRussianCharacters(){
        String newName = "Гарри Поттер";

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newName)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(null));
    }

    @Test
    public void userCanNotChangeNameToSingleWordName(){
        String newName = "Jonn";

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newName)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(null));
    }

    @Test
    public void userCanNotChangeNameToANameContainingNumbers(){
        String newName = "Jonn Smith1234";

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newName)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(null));
    }


    @Test
    public void userCanNotChangeNameToANameContainingSpecialCharacters(){
        String newName = "Jonn Smith#";

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(newName)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        given()
                .header("Authorization", userToken)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(null));
    }
}
