package petApi;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class petApiTest {
    static String URL = "https://petstore.swagger.io";


    /*
    - Так как ресурс petstore.swagger.io открыт для общего использования,
    Удалим все order с id = 1212, 1214 и 1215, чтобы тесты не падали из-за того, что эти
    id были заняты.
    - Создадим тестового Pet.
    */
    @BeforeAll
    public static void beforeAll() {
        Specifications.installSpecifications(Specifications.requestSpec(URL));
        PetData catBettyData = CreatePet.createInitialPet(1212L, "Betty");
        PetData catLilyData = CreatePet.createInitialPet(1213L, "Lily");
        given()
                .expect()
                .statusCode(anyOf(equalTo(200), equalTo(404)))
                .when()
                .delete("/v2/pet/1212");

        given()
                .expect()
                .statusCode(anyOf(equalTo(200), equalTo(404)))
                .when()
                .delete("/v2/pet/1213");

        given()
                .expect()
                .statusCode(anyOf(equalTo(200), equalTo(404)))
                .when()
                .delete("/v2/pet/1214");

        given()
                .expect()
                .statusCode(anyOf(equalTo(200), equalTo(404)))
                .when()
                .delete("/v2/pet/1215");

        given()
                .body(catBettyData)
                .expect()
                .statusCode(200)
                .when()
                .post("/v2/pet");

        given()
                .body(catLilyData)
                .expect()
                .statusCode(200)
                .when()
                .post("/v2/pet");
    }

    //Удалим тестовых Pets
    @AfterAll
    public static void afterAll() {
        Specifications.installSpecifications(Specifications.requestSpec(URL));
        given()
                .delete("/v2/pet/1212");
        given()
                .delete("/v2/pet/1213");
        given()
                .delete("/v2/pet/1214");
        given()
                .delete("/v2/pet/1215");
    }

    /*Тест 1:
    Проверить, что данные питомцев успешно создаются методом POST /v2/pet.
    В массиве, полученном в ответе на запрос GET v2/pet/findByStatus?status=available
    должны содержаться данные и питомцах с id = 1214 и id = 1213.
    Проверить, что статусы ответа запросов POST - 200.
     */
    @Test
    public void checkPetsCreated() {
        Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec200());
        PetData catFruData = CreatePet.createInitialPet(1214L, "Fru");
        PetData catDinData = CreatePet.createInitialPet(1215L, "Din");

        given()
                .body(catFruData).post("/v2/pet")
                .then()
                .log().all();
        given()
                .body(catDinData).post("/v2/pet")
                .then()
                .log().all();

        List<PetData> list = given()
                .when()
                .get("v2/pet/findByStatus?status=available")
                .then()
                .log().all()
                .extract().body().jsonPath().<PetData>getList(".", PetData.class);

        Assertions.assertTrue(list.stream().map(PetData::getId).collect(Collectors.toList()).containsAll(List.of(1214L, 1215L)));
    }

    /*Тест 2:
    Проверить, что запрос GET /v2/pet/{petId} возвращает требуемые поля.
    Проверить, что статус ответа 200.
     */
    @Test
    public void checkFindPetById() {
        Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec200());
        PetData petData = given()
                .when()
                .get(URL + "/v2/pet/1212")
                .then().log().all()
                .extract().body().as(PetData.class);

        Assertions.assertEquals(1212L, petData.getId());
        Assertions.assertEquals("Betty", petData.getName());
        Assertions.assertEquals("available", petData.getStatus());
        Assertions.assertEquals("cats", petData.getCategory().getName());
    }

    /*Тест 3:
    Проверить, что запрос GET /v2/pet/{petId} с несуществующим id возвращает требуемые поля.
    Проверить, что статус ответа 404.
     */
    @Test
    public void checkNotValidId() {
        Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec404());
        NotValidIdResponse response = given()
                .when()
                .get(URL + "/v2/pet/1216")
                .then().log().all()
                .extract().body().as(NotValidIdResponse.class);

        Assertions.assertEquals(1, response.getCode());
        Assertions.assertEquals("error", response.getType());
        Assertions.assertEquals("Pet not found", response.getMessage());
    }

    /*Тест 4:
    Проверить, что запрос PUT /v2/pet изменяет поля конкрентного питомца -
    проверяем, что в запросе GET по id измененного питомца возвращаются
    валидные данные.
    Проверить, что статус ответа запроса PUT 200.
     */
    @Test
    public void checkPetDataChanged() {
        Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec200());
        PetData catTimData = CreatePet.createBettyWhithChangedNameStatus(1213L, "Tim", "sold");

        given()
                .body(catTimData)
                .when()
                .put(URL + "/v2/pet")
                .then().log().all();

        PetData changedPetData = given()
                .when()
                .get(URL + "/v2/pet/1213")
                .then().log().all()
                .extract().body().as(PetData.class);

        Assertions.assertEquals(1213L, changedPetData.getId());
        Assertions.assertEquals("Tim", changedPetData.getName());
        Assertions.assertEquals("sold", changedPetData.getStatus());
       // в асерте на проверку category допущена ошибка, чтобы тест упал
        Assertions.assertEquals("cat", changedPetData.getCategory().getName());
    }
}