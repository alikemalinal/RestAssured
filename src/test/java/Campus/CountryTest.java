package Campus;

import Campus.Model.Country;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class CountryTest {
    private Cookies cookies;

    @BeforeClass
    public void loginCampus() {

        baseURI = "https://demo.mersys.io/";

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "richfield.edu");
        credentials.put("password", "Richfield2020!");
        credentials.put("rememberMe", "true");

        cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(credentials)

                        .when()
                        .post("auth/login")

                        .then()
                        //.log().cookies()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
        ;
    }

    String countryID;
    String countryName;
    String countryCode;

    @Test
    public void createCountry() {

        countryName = getRandomName();
        countryCode = getRandomCode();

        Country country = new Country();
        country.setName(countryName); // generateCountryName
        country.setCode(countryCode); // generateCountryCode

        countryID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(country)

                        .when()
                        .post("school-service/api/countries")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")
        ;

    }


    @Test(dependsOnMethods = "createCountry")
    public void createCountryNegative() {

        //"message": "The Country with Name \"France 375\" already exists.",

        Country country = new Country();
        country.setName(countryName);
        country.setCode(countryCode);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .post("school-service/api/countries")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The Country with Name \"" + countryName + "\" already exists."))
        ;
    }

    @Test(dependsOnMethods = "createCountry")
    public void updateCountry() {

        //"message": "The Country with Name \"France 375\" already exists.",
        countryName = getRandomName();

        Country country = new Country();
        country.setId(countryID);
        country.setName(countryName);
        country.setCode(countryCode);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .put("school-service/api/countries")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(countryName))
        ;
    }

    @Test(dependsOnMethods = "updateCountry")
    public void deleteCountryById() {

        given()
                .cookies(cookies)
                .pathParam("countryID", countryID)

                .when()
                .delete("school-service/api/countries/{countryID}")

                .then()
                .log().body()
                .statusCode(208)
        ;
    }

    @Test(dependsOnMethods = "deleteCountryById")
    public void deleteCountryByIdNegative() {

        given()
                .cookies(cookies)
                .pathParam("countryID", countryID)
                .log().uri()
                .when()
                .delete("school-service/api/countries/{countryID}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    @Test(dependsOnMethods = "deleteCountryById")
    public void updateCountryNegative() {

        countryName = getRandomName();

        Country country = new Country();
        country.setId(countryID);
        country.setName(countryName);
        country.setCode(countryCode);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .put("school-service/api/countries")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("Country not found"))
        ;
    }

    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase();
    }

    public String getRandomCode() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }
}