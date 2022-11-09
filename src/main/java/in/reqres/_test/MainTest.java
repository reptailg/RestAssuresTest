package in.reqres._test;


import in.reqres.data.Colors;
import in.reqres.registration.Registr;
import in.reqres.registration.SuccesReg;
import in.reqres.registration.UnSuccessReg;
import in.reqres.user.*;
import in.reqres.spec.Specification;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

public class MainTest {
    private static final String URL = "https://reqres.in/";

    @Test
    public void checkAvatarIdTest(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpec(200));
        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then()//.log().all()
                .extract().body().jsonPath().getList("data",UserData.class);

        users.forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));

        Assert.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

        List<String> avatars = users.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> ids = users.stream().map(x->x.getId().toString()).collect(Collectors.toList());

        for(int i = 0; i < avatars.size(); i++){
             Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }

    @Test
    public void successRegTest(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpec(200));
        SuccesReg succesReg = given()
                .body(new Registr("eve.holt@reqres.in","pistol"))
                .when()
                .post("api/register")
                .then()//.log().all()
                .extract().as(SuccesReg.class);
        Assert.assertEquals(4, succesReg.getId());
        Assert.assertEquals("QpwL5tke4Pnpja7X4", succesReg.getToken());
    }

    @Test
    public void unSuccessReg(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpec(400));
        UnSuccessReg unSuccessReg = given()
                .body(new Registr("sydney@fife",""))
                .when()
                .post("api/register")
                .then()//.log().all()
                .extract().as(UnSuccessReg.class);
        Assert.assertEquals("Missing password", unSuccessReg.getError());
    }

    @Test
    public void sortedYearsTest(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpec(200));
        List<Colors> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", Colors.class);
        List<Integer> years = colors.stream().map(Colors::getYear).collect(Collectors.toList());
        List<Integer> sortedYears = years.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(sortedYears, years);
    }

    @Test
    public void deletePersonTest(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpec(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void timeTest(){
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpec(200));
        UserTimeResponse response = given()
                .body(new UserTime("morpheus", "zion resident"))
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String currentTime = Clock.systemUTC().instant().toString().replaceAll("(.{8})$","");
        Assert.assertEquals(currentTime, response.getUpdatedAt().replaceAll("(.{5})$",""));

    }
}
