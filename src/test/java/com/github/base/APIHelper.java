package com.github.base;

//import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import com.github.requestPOJO.CreateRepositoryReq;
import com.github.requestPOJO.UpdateRepoRequest;
import com.github.utils.EnvironmentDetails;
import org.testng.Assert;

import java.util.HashMap;

@Slf4j
public class APIHelper {
    RequestSpecification reqSpec;
    String token = EnvironmentDetails.getProperty("token");
   
    

    public APIHelper() {
        RestAssured.baseURI = EnvironmentDetails.getProperty("baseURL");
        reqSpec = RestAssured.given();       
    }

    public Response getSingleRepo(String OWNER,String REPO) {
        reqSpec = RestAssured.given();
        reqSpec.headers("Authorization","Bearer "+token);
        Response response = null;
        try {
            response = reqSpec.get("/repos/"+OWNER+"/"+REPO);
            response.then().log().all();
        } catch (Exception e) {
            Assert.fail("Get data is failing due to :: " + e.getMessage());
        }
        return response;
    }
    
    public Response getAllRepos(String OWNER) {
        reqSpec = RestAssured.given();
        reqSpec.headers("Authorization","Bearer "+token);
        Response response = null;
        try {
            response = reqSpec.get("users/"+OWNER+"/repos");
            response.then().log().all();
        } catch (Exception e) {
            Assert.fail("Get data is failing due to :: " + e.getMessage());
        }
        return response;
    }

    public Response createRepo(CreateRepositoryReq createRepoRequest) {
        reqSpec = RestAssured.given();
        Response response = null;
        try {
            log.info("Creating below data :: " + new ObjectMapper().writeValueAsString(createRepoRequest));
            reqSpec.headers("Authorization","Bearer "+token);
            reqSpec.body(new ObjectMapper().writeValueAsString(createRepoRequest)); //Serializing createRepo Request POJO classes to byte stream
            response = reqSpec.post("/user/repos");
            response.then().log().all();
        } catch (Exception e) {
            Assert.fail("Add data functionality is failing due to :: " + e.getMessage());
        }
        return response;
    }

    public Response updateRepo(UpdateRepoRequest updateRepoRequest, String OWNER, String REPO) {
        reqSpec = RestAssured.given();
        reqSpec.headers("Authorization","Bearer "+token);
        Response response = null;
        try {
            reqSpec.body(new ObjectMapper().writeValueAsString(updateRepoRequest)); //Serializing updateData Request POJO classes to byte stream
            response = reqSpec.patch("/repos/"+OWNER+"/"+REPO);
            response.then().log().all();
        } catch (Exception e) {
            Assert.fail("Update data functionality is failing due to :: " + e.getMessage());
        }
        return response;
    }

    public Response deleteRepo(String OWNER,String REPO) {
        reqSpec = RestAssured.given();
        reqSpec.headers("Authorization","Bearer "+token);
        Response response = null;
        try {
            response = reqSpec.delete("/repos/"+OWNER+"/"+REPO);
            response.then().log().all();
        } catch (Exception e) {
            Assert.fail("Delete data functionality is failing due to :: " + e.getMessage());
        }
        return response;
    }

    public HashMap<String, String> getHeaders(boolean forLogin) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        if (!forLogin) {
            headers.put("token", token);
        }
        return headers;
    }

}
