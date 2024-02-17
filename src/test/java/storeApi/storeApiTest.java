package storeApi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class storeApiTest {
    static String URL = "https://petstore.swagger.io";

    /*Так как ресурс petstore.swagger.io открыт для общего использования,
 Удалим все order с id = 5, 6 и 7, чтобы тесты не падали из-за того, что эти
 id были заняты.*/
    @BeforeAll
    public static void beforeAll() {

        Specifications.installSpecifications(Specifications.requestSpec(URL));
        given().expect()
                .statusCode(anyOf(equalTo(200), equalTo(404)))
                .when()
                .delete("/v2/store/order/5");
        given()
                .expect()
                .statusCode(anyOf(equalTo(200), equalTo(404)))
                .when()
                .delete("/v2/store/order/6");
        given()
                .expect()
                .statusCode(anyOf(equalTo(200), equalTo(404)))
                .when()
                .delete("/v2/store/order/7");

        Date shipDate = new Date();
        OrderData order = new OrderData(7, 1212, 2, shipDate, "placed", false);
        given()
                .body(order)
                .expect()
                .statusCode(200)
                .when()
                .post("/v2/store/order");
    }

    //    Удалeние тестовых данных - order с id = 5 и 7
    @AfterAll
    public static void afterAll() {
        given(Specifications.requestSpec(URL), Specifications.responseSpec200())
                .delete("/v2/store/order/5");
        given(Specifications.requestSpec(URL))
                .delete("/v2/store/order/7");
    }

    /*Тест 1:
    Проверить создание order запросом POST /v2/store/order.
    Проверить c помощью запроса GET  /v2/store/order/5,
    что созданный order имеет верные параметры.
    Проверить, что статус-код запроса POST - 200.*/
    @Test
    public void checkOrderCreation() {
        Date currentDate = new Date();
        Date shipDate = new Date(currentDate.getTime() + 86400000);
        OrderData order = new OrderData(5, 1212, 2, shipDate, "placed", false);

        Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec200());
        given()
                .body(order)
                .when()
                .log().all()
                .post("/v2/store/order");


        OrderData newOrder = given()
                .when()
                .get("/v2/store/order/5")
                .then()
                .log().all()
                .extract().body().as(OrderData.class);


        Assertions.assertEquals(5, newOrder.getId());
        Assertions.assertEquals(1212, newOrder.getPetId());
        Assertions.assertEquals(2, newOrder.getQuantity());
        Assertions.assertEquals(shipDate, newOrder.getShipDate());
        Assertions.assertEquals("placed", newOrder.getStatus());
        Assertions.assertFalse(newOrder.isComplete());
    }

    /*Тест 2:
    Проверить, что запрос GET /v2/store/order/{ordertId} с несуществующим id возвращает требуемые поля с ошибкой.
    Проверить, что статус ответа 404.*/
    @Test
    public void checkInvalidOrderId() {
        Specifications.installSpecifications(Specifications.requestSpec(URL), Specifications.responseSpec404());
        NotValidStoreIdResponse NotValidResponse = given()
                .when()
                .get("/v2/store/order/6")
                .then()
                .log().all()
                .extract().body().as(NotValidStoreIdResponse.class);

        Assertions.assertEquals(1, NotValidResponse.getCode());
        Assertions.assertEquals("error", NotValidResponse.getType());
        Assertions.assertEquals("Order not found", NotValidResponse.getMessage());
    }

    /*Тест 3:
    Проверить, что запрос DELETE /v2/store/order/{ordertId} удаляет order.
    Проверить с помощью запроса GET, что удаленный order не найден.
    Проверить, что статус ответа запроса DELETE 200.*/
    @Test
    public void checkOrderDelete() {
        Specifications.installSpecifications(Specifications.requestSpec(URL));
        given()
                .expect()
                .statusCode(200)
                .when()
                .delete("/v2/store/order/7")
                .then()
                .log().all();

        NotValidStoreIdResponse NotValidResponse = given()
                .expect()
                .statusCode(404)
                .when()
                .get("/v2/store/order/7")
                .then()
                .statusCode(404)
                .log().all()
                .extract().body().as(NotValidStoreIdResponse.class);

        Assertions.assertEquals(1, NotValidResponse.getCode());
        Assertions.assertEquals("error", NotValidResponse.getType());
        Assertions.assertEquals("Order not found", NotValidResponse.getMessage());
    }


}
