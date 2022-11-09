package in.reqres._test;


import in.reqres.registration.Registr;
import in.reqres.spec.Specification;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestNoPojo {
    private static final String URL = "https://reqres.in/";
    @Test
    public void checkAvatarIdTest(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpec(200));
        Response response = given()
                .when()
                .get("api/users?page=2")
                .then()//.log().all()
                .body("page", equalTo(2))
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        List<Integer> ids = jsonPath.get("data.id");
        List<String> emais = jsonPath.get("data.email");
        List<String> avatars = jsonPath.get("data.avatar");

        for (int i = 0; i < avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(ids.get(i).toString()));
        }
        Assert.assertTrue(emais.stream().allMatch(x->x.endsWith("reqres.in")));
    }

    @Test
    public void successRegTest(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpec(200));
        Map<String,String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
//                .body("id", equalTo(4))
//                .body("token", equalTo("QpwL5tke4Pnpja7X4"))
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.get("id");
        String token = jsonPath.get("token");
        Assert.assertEquals(4,id);
        Assert.assertEquals("QpwL5tke4Pnpja7X4", token);
    }

    @Test
    public void unSuccessReg(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpec(400));
        Map<String,String> user = new HashMap<>();
        user.put("email", "sydney@fife");
        Response response =  given()
                .body(new Registr("sydney@fife",""))
                .when()
                .post("api/register")
                .then().log().all()
//                .body("error", equalTo("Missing password"))
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        String error = jsonPath.get("error");
        Assert.assertEquals("Missing password", error);
    }
}
