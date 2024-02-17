package userApi;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) //так как тесты зависимые, задаем четкий порядок выполенния
public class userApiTest {
    static String URL = "https://petstore.swagger.io";


    //Удаление тестовых данных
    @AfterAll
    public static void afterAll(){
        given(Specifications.requestSpec(URL))
                .delete("/v2/user/Jhon23")
                .then()
                .log().all();
        given(Specifications.requestSpec(URL), Specifications.responseSpec404())
                .delete("/v2/user/Mary1234")
                .then()
                .log().all();
    }



    /*
    Тест 1:
    Проверить создание 2-х user запросом POST /v2/user/createWithArray.
    Проверить c помощью запроса GET  /v2/user/{userName},
    что созданные user имеют верные параметры.
    Проверить, что статус-код запроса POST - 200.
     */
    @Order(1)
    @Test
    public void checkCreateWhithArray() {
        Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec200());
        UserData mary = new UserData(23, "Mary1234", "Mary", "Watson",
                "mary@gmail.com", "qwe1234qwe", "123123123", 0);
        UserData jhon = new UserData(24, "Jhon23", "Jhon", "Green", "jhon@gmail.com", "qaz901qaz",
                "234234234", 1);

        ArrayList<UserData> users = new ArrayList<>();
        users.add(mary);
        users.add(jhon);

        AnswerBody answerBody = given()
                .body(users)
                .when()
                .post("/v2/user/createWithArray")
                .then()
                .log().all()
                .extract().body().as(AnswerBody.class);

        UserData maryResponse = given()
                .when()
                .get("/v2/user/Mary1234")
                .then()
                .log().all()
                .extract().body().as(UserData.class);


        UserData jhonResponse = given()
                .when()
                .get("/v2/user/Jhon23")
                .then()
                .log().all()
                .extract().body().as(UserData.class);

        Assertions.assertEquals("ok", answerBody.getMessage());
        Assertions.assertEquals(mary.getId(), maryResponse.getId());
        Assertions.assertEquals(mary.getUsername(), maryResponse.getUsername());
        Assertions.assertEquals(mary.getFirstName(), maryResponse.getFirstName());
        Assertions.assertEquals(mary.getLastName(), maryResponse.getLastName());
        Assertions.assertEquals(jhon.getUsername(), jhonResponse.getUsername());
        Assertions.assertEquals(jhon.getFirstName(), jhonResponse.getFirstName());
        Assertions.assertEquals(jhon.getLastName(), jhonResponse.getLastName());
    }

    /*
    Тест 2:
    Проверить удаление user запросом DELETE /v2/user/{userName}.
    Проверить c помощью запроса GET  /v2/user/{userName},
    что удаленный user не найден.
    Проверить, что статус-код запроса DELETE - 200.
     */
    @Order(2)
    @Test
    public void checkUserDelete() {
        Specifications.installSpecifications(Specifications.requestSpec(URL));
        AnswerBody answerDeleteBody = given()
                .when()
                .delete("/v2/user/Mary1234")
                .then()
                .statusCode(200)
                .log().all()
                .extract().body().as(AnswerBody.class);

        AnswerBody answerGetBody = given(storeApi.Specifications.requestSpec(URL), storeApi.Specifications.responseSpec404())
                .get("/v2/user/Mary1234")
                .then()
                .statusCode(404)
                .log().all()
                .extract().body().as(AnswerBody.class);

        Assertions.assertEquals("Mary1234", answerDeleteBody.getMessage());
        Assertions.assertEquals("User not found", answerGetBody.getMessage());
        Assertions.assertEquals("error", answerGetBody.getType());

    }

    /*
    Тест 3:
    Проверить изменение user запросом PUT /v2/user/{userName}.
    Проверить c помощью запроса GET  /v2/user/{userName},
    что измененный user имеет верный параметры.
    Проверить, что статус-код запроса PUT - 200.
     */
    @Order(3)
    @Test
    public void checkUserChange() {
       Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec200());
        //создадим user Jhon с измененными email и status
       UserData changedJhon = new UserData(24, "Jhon23", "Jhon", "Green", "jhon1111@gmail.com", "qaz901qaz",
                "234234234", 2);

        AnswerBody answerBody = given()
                .body(changedJhon)
                .when()
                .put("/v2/user/Jhon23")
                .then()
                .log().all()
                .extract().body().as(AnswerBody.class);

        UserData JhonResponse = given()
                .when()
                .get("/v2/user/Jhon23")
                .then()
                .log().all()
                .extract().body().as(UserData.class);

        Assertions.assertEquals(changedJhon.getId(), JhonResponse.getId());
        Assertions.assertEquals(changedJhon.getUsername(), JhonResponse.getUsername());
        Assertions.assertEquals(changedJhon.getFirstName(), JhonResponse.getFirstName());
        Assertions.assertEquals(changedJhon.getLastName(), JhonResponse.getLastName());
        Assertions.assertEquals(changedJhon.getEmail(), JhonResponse.getEmail());
        Assertions.assertEquals(changedJhon.getUserStatus(), JhonResponse.getUserStatus());
    }
}
