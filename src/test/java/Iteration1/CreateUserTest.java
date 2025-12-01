package Iteration1;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class CreateUserTest {

    @BeforeAll
    public static void setepRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()));
    }

    @Test
    public void adminCanCreateUserWithCorrectData() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                           "username": "Kate2012",
                           "password": "Kate2000#",
                           "role": "USER"
                        }                                        \s
                       \s""")
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("username", Matchers.equalTo("Kate2000"))
                .body("password", Matchers.not(Matchers.equalTo("Kate2000#")))
                .body("role", Matchers.equalTo("USER"));
    }


    public static Stream<Arguments> userInvalidDate() {
        return Stream.of(
                Arguments.of(" ", "Password33$", "USER", "Username must contain only letters, digits, dashes, underscores, and dots, Username cannot be blank"),
                Arguments.of("sdf", "Pass", "USER", "ERROR")
        );
    }
    @MethodSource("userInvalidDate")
    @ParameterizedTest
    public void adminCanNotCreateUserWithInvalidData(String username, String password, String role) {
        String requestBody = String.format(
                """
                {
                    "username": "%s",
                    "password": "%s",
                    "role": "%s"
                }       
                """, username, password, role);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}
