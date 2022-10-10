import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    @Test
    public void test() {

        given()
                // Hazirlik islemlerini yapacagiz. (token, send, body, parametreler)
                .when()
                // link'i ve methodu veriyoruz.
                .then()
        // assertion ve verileri ele alma kismi (extract)
        ;
    }

    @Test
    public void statusCodeTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body() // log.All() butun respond'u gosterir
                .statusCode(200) // status control
        ;

    }

    @Test
    public void contentTypeTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body() // log.All() butun respond'u gosterir
                .statusCode(200) // status control
                .contentType(ContentType.JSON) // hatali durum kontrolunu yaptik
        ;

    }

    @Test
    public void checkStateInResponseBody() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("country", equalTo("United States")) // body.country == United States
                .statusCode(200)

        ;

        /*
            body.country -> body("country")
            body.'post code' -> body("post code")
            body.'country abbreviation' -> body("country abbreviation")
            body.places[0].'place name' -> body("body.places[0].'place name')
            body.places[0].state
         */

//        {
//            "post code": "90210",
//                "country": "United States",
//                "country abbreviation": "US",
//                "places": [
//            {
//                "place name": "Beverly Hills",
//                    "longitude": "-118.4065",
//                    "state": "California",
//                    "state abbreviation": "CA",
//                    "latitude": "34.0901"
//            }
//    ]
//        }


    }

    @Test
    public void bodyJsonPathTest2() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places[0].state", equalTo("California")) // birebir esit mi?
                .statusCode(200)

        ;

    }

    @Test
    public void bodyJsonPathTest3() {

        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body()
                .body("places.'place name'", hasItem("Çaputçu Köyü")) // place.state -> get all state elements as list
                .statusCode(200)    // bir index verilmezse dizinin butun elemanlarinda arar.

        ;

    }

    @Test
    public void bodyArrayHasSizeTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places", hasSize(1)) // verilen pathdeki listin size kontrolu
                .statusCode(200)

        ;

    }

    @Test
    public void combiningTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()
                .body("places", hasSize(1))
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                .statusCode(200)

        ;

    }

    @Test
    public void pathParamTest() {

        given()
                .pathParams("Country", "us")
                .pathParams("ZipCode", 90210)
                .log().uri()

                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipCode}")

                .then()
                .log().body()

                .statusCode(200)

        ;

    }

    @Test
    public void pathParamTest2() {

        // 90210'dan 90250'ye kadar test sonuclarinda place size'nin hepsinde 1 geldigini test ediniz

        for (int i = 90210; i <= 90213; i++) {
            given()
                    .pathParams("Country", "us")
                    .pathParams("ZipCode", i)
                    .log().uri()

                    .when()
                    .get("http://api.zippopotam.us/{Country}/{ZipCode}")

                    .then()
                    .log().body()
                    .body("places", hasSize(1))
                    .statusCode(200)

            ;
        }
    }

    @Test
    public void queryParamTest() {

//        https://gorest.co.in/public/v1/users?page=1

        given()
                .param("page", 1)
                .log().uri()

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(1))
                .statusCode(200)

        ;
    }

    @Test
    public void queryParamTest2() {

//        https://gorest.co.in/public/v1/users?page=1

        for (int pageNo = 1; pageNo <= 10; pageNo++) {
            given()
                    .param("page", pageNo)
                    .log().uri() // request linki

                    .when()
                    .get("https://gorest.co.in/public/v1/users")

                    .then()
                    .log().body()
                    .body("meta.pagination.page", equalTo(pageNo))
                    .statusCode(200)

            ;
        }

    }

    @Test
    public void requestResponseSpecification() {

//        https://gorest.co.in/public/v1/users?page=1

        given()
                .param("page", 1)
                .spec(reqSpec)

                .when()
                .get("/users") // url'nin basinda http yoksa basUri'deki degeri otomatik olarak alir

                .then()
                .body("meta.pagination.page", equalTo(1))
                .spec(responseSpecs)

        ;
    }

    @Test
    public void extractingJsonPath() {

        String placeName = // return degerine gore String veya integer olarak tanimla.
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        // .log().body()
                        .statusCode(200)
                        .extract().path("places[0].'place name'")
                // extract methodu ve given ile baslayan satir bir deger dondurur hale geldi
                // extract en son da olmali
                ;

        System.out.println("placeName: " + placeName);

    }

    @Test
    public void extractingJsonPathInt() {

        int limit = // return degerine gore String veya integer olarak tanimla.
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        // .log().body()
                        .statusCode(200)
                        .extract().path("meta.pagination.limit");

        Assert.assertEquals(limit, 10, "result");

        System.out.println("limit: " + limit);

    }

    @Test
    public void extractingJsonPathInt2() {

        int id =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        // .log().body()
                        .statusCode(200)
                        .extract().path("data[2].id");

        System.out.println("id: " + id);


    }

    @Test
    public void extractingJsonPathIntList() {

        List<Integer> listId =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        // .log().body()
                        .statusCode(200)
                        .extract().path("data.id");

        System.out.println("listId: " + listId);
        Assert.assertTrue(listId.contains(3045));

    }

    @Test
    public void extractingJsonPathStringList() {

        List<String> names =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        // .log().body()
                        .statusCode(200)
                        .extract().path("data.name");

        System.out.println("names: " + names);
        Assert.assertTrue(names.contains("Datta Achari"));


    }

    @Test
    public void extractingJsonPathResponseAll() {

        Response body =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        // .log().body()
                        .statusCode(200)
                        .extract().response(); // butun body alindi

        List<Integer> listId = body.path("data.id");
        List<String> names = body.path("data.name");
        int limit = body.path("meta.pagination.limit");

        System.out.println("listId = " + listId);
        System.out.println("names = " + names);
        System.out.println("limit = " + limit);

    }

    @Test
    public void extractingJsonPojo() {

        Location location =
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .extract().as(Location.class);

        System.out.println("location = " + location);

        System.out.println("location.getCountry() = " + location.getCountry());
        System.out.println("location.getPlaces().get(0).getPlacename() = " +
                location.getPlaces().get(0).getPlacename());

    }

    @Test
    public void extractingJsonPojoTask1() {

        Location location =
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .extract().as(Location.class);

        System.out.println("location = " + location);

        System.out.println("location.getCountry() = " + location.getCountry());
        System.out.println("location.getPlaces().get(0).getPlacename() = " +
                location.getPlaces().get(0).getPlacename());

    }




    private RequestSpecification reqSpec;
    private ResponseSpecification responseSpecs;

    @BeforeClass
    void Setup() {

        baseURI = "https://gorest.co.in/public/v1"; // RestAssured kendi static degiskeni tanimli deger ataniyor.

        reqSpec = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setAccept(ContentType.JSON)
                .build();

        responseSpecs = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();
    }
}


