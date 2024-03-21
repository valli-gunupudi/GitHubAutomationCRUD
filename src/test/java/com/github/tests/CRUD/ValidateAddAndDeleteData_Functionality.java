package com.github.tests.CRUD;

import com.github.base.APIHelper;
import com.github.base.BaseTest;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import com.github.requestPOJO.CreateRepositoryReq;
import com.github.requestPOJO.UpdateRepoRequest;
import com.github.responsePOJO.CreateErrorRepoResponse;
import com.github.responsePOJO.CreateRepositoryRes;
import com.github.responsePOJO.Errors;
import com.github.responsePOJO.GetErrorRepoResponse;
import com.github.responsePOJO.GetRepoResponse;
import com.github.responsePOJO.UpdateRepoResponse;

import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.github.utils.EnvironmentDetails;
import com.github.utils.JsonSchemaValidate;
import java.util.List;

public class ValidateAddAndDeleteData_Functionality extends BaseTest {
    APIHelper apiHelper;
    String name, description, homepage;
    boolean privateValue;
    String id="";

    @BeforeClass
    public void beforeClass() {
        apiHelper = new APIHelper();        
    }

    @Test(priority = 0, description = "validate get data object")
    public void validateGetSingleRepo() {
        Response data = apiHelper.getSingleRepo(EnvironmentDetails.getProperty("OWNER"), EnvironmentDetails.getProperty("REPO"));
        data.then().statusCode(200);
        data.prettyPrint(); 
        Assert.assertEquals(data.header("content-Type"), "application/json; charset=utf-8", "header type not matching");
        
        GetRepoResponse getRepoResponse= data.as(GetRepoResponse.class);
        Assert.assertEquals(getRepoResponse.getFull_name(), "valli-gunupudi/Feb2024Blessed", "Get data functionality is not working as expected, full_name is not matching");
        Assert.assertEquals(getRepoResponse.getDefault_branch(), "main", "Get data functionality is not working as expected, default_branch is not matching");
        System.out.println("statusCode:"+data.statusCode());
        System.out.println("Full_name:"+getRepoResponse.getFull_name());
        System.out.println("Default_branch:"+getRepoResponse.getDefault_branch());
        System.out.println("Header content-Type"+data.getHeader("content-Type"));
        JsonSchemaValidate.validateSchema(data.asPrettyString(), "GetRepoResponseSchema.json");
    }
    
    @Test(priority = 1, description = "validate get data object", dependsOnMethods = "validateGetSingleRepo")
    public void validateGetNonExistSingleRepo() {
        Response data = apiHelper.getSingleRepo(EnvironmentDetails.getProperty("OWNER"), EnvironmentDetails.getProperty("NONREPO"));
        data.then().statusCode(404);
        //data.prettyPrint();
        GetErrorRepoResponse getErrorRepoResponse= data.as(GetErrorRepoResponse.class);
        Assert.assertEquals(data.getStatusCode(), HttpStatus.SC_NOT_FOUND, "Create functionality is not working as expected.");
        Assert.assertEquals(getErrorRepoResponse.getMessage(), "Not Found", "Not throwing error");
        System.out.println("statusCode:"+data.statusCode());
        System.out.println("Message:"+getErrorRepoResponse.getMessage());
    }

    @Test(priority = 2, description = "validate get data object", dependsOnMethods = "validateGetNonExistSingleRepo")
    public void validateGetAllRepos() {
        Response data = apiHelper.getAllRepos(EnvironmentDetails.getProperty("OWNER"));
        data.then().statusCode(200);
        data.prettyPrint();
        //List<GetRepoResponse> getRepoResponseList = data.getBody().as(new TypeRef<List<GetRepoResponse.class>>() {
        //});        
        GetRepoResponse[] getRepoResponseList= data.as(GetRepoResponse[].class);
        
        System.out.println("statusCode:"+data.statusCode());
        System.out.println("total no. of repos "+getRepoResponseList.length);
        for (GetRepoResponse dataResponse : getRepoResponseList) {
        	//data.then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("ExpectedJsonSchema/GetAllRepoResponseSchema.json"));
        	JsonSchemaValidate.validateSchemaInClassPath(data, "ExpectedJsonSchema/GetAllRepoResponseSchema.json");         
        	if (!dataResponse.isPrivate()) 
                System.out.println(dataResponse.getFull_name()+" "+dataResponse.isPrivate());
        }
        Assert.assertEquals(data.header("content-Type"), "application/json; charset=utf-8", "header type not matching");
        System.out.println("Header content-Type"+data.getHeader("content-Type"));
    }
    
    @Test(priority = 3, description = "validate get data object", dependsOnMethods = "validateGetAllRepos")
    public void validateCreateRepofunctionality() {
    	name = "NPI-Testing";
    	description = "This is your first repo!";
    	homepage = "https://github.com";
    	privateValue = true;    	
    	
    	CreateRepositoryReq createRepoRequest = CreateRepositoryReq.builder().name(name).description(description).homepage(homepage).Private(privateValue).build();
        Response data = apiHelper.createRepo(createRepoRequest);
        data.then().statusCode(201);
        data.jsonPath().prettyPrint();
        
        CreateRepositoryRes createRepoResponse  = data.as(CreateRepositoryRes.class);
        Assert.assertEquals(data.getStatusCode(), HttpStatus.SC_CREATED, "Create functionality is not working as expected.");
        Assert.assertEquals(createRepoResponse.getName(), "NPI-Testing", "create data functionality is not working as expected, name is not matching");;
        Assert.assertEquals(createRepoResponse.getOwner().getLogin(),"valli-gunupudi","not matching");
        Assert.assertEquals(createRepoResponse.getOwner().getType(),"User","not matching");
        System.out.println("statusCode:"+data.statusCode());
        System.out.println("name:"+createRepoResponse.getName());
        System.out.println("login:"+createRepoResponse.getOwner().getLogin());
        System.out.println("type:"+createRepoResponse.getOwner().getType());
    }
    
    @Test(priority = 4, description = "validate get data object", dependsOnMethods = "validateCreateRepofunctionality")
    public void validateExistingRepoErrorfunctionality() {
    	name = "APi-World-Touring";
    	description = "This is your first repo!";
    	homepage = "https://github.com";
    	privateValue = false;    	
    	
    	CreateRepositoryReq createRepoRequest = CreateRepositoryReq.builder().name(name).description(description).homepage(homepage).Private(privateValue).build();
        Response data = apiHelper.createRepo(createRepoRequest);
        data.then().statusCode(422);
        data.jsonPath().prettyPrint();
        CreateErrorRepoResponse errorRepoResponse  = data.as(CreateErrorRepoResponse.class);
        Errors[] errors = errorRepoResponse.getErrors();
        Assert.assertEquals(data.getStatusCode(), HttpStatus.SC_UNPROCESSABLE_ENTITY, "Create functionality is not working as expected.");
        Assert.assertEquals(errors[0].getMessage(), "name already exists on this account", "Create functionality is not working as expected.");
        System.out.println("statusCode:"+data.statusCode());
        System.out.println("errors/message="+errors[0].getMessage());
    }
    
    @Test(priority = 5, description = "validate get data object",dependsOnMethods = "validateExistingRepoErrorfunctionality")
    public void validateUpdateRepofunctionality() {    	
    	name = "APi-World-Tour";
    	description = "Api world tour repositry!";
    	privateValue = false; 
    	
    	UpdateRepoRequest updateRepoRequest = UpdateRepoRequest.builder().name(name).description(description).Private(privateValue).build();
        Response data = apiHelper.updateRepo(updateRepoRequest,EnvironmentDetails.getProperty("OWNER"), "APi-World-Touring");
        data.then().statusCode(200);
        data.jsonPath().prettyPrint();
        
        UpdateRepoResponse updRepoResponse  = data.as(UpdateRepoResponse.class);
        Assert.assertEquals(data.getStatusCode(), HttpStatus.SC_OK, "Update functionality is not working as expected.");
        Assert.assertEquals(updRepoResponse.getName(), "APi-World-Tour", "Update data functionality is not working as expected, name is not matching");;
    }
    
    @Test(priority = 6, description = "delete data functionality", dependsOnMethods = "validateUpdateRepofunctionality")
    public void validateDeleteRepoFunctionality() {
        Response data = apiHelper.deleteRepo(EnvironmentDetails.getProperty("OWNER"), "Kesting-Repo");
        data.then().statusCode(204);
        data.prettyPrint(); 
        System.out.println("statusCode:"+data.statusCode());
    }

    @Test(priority = 7, description = "validate deleted data in the get data object", dependsOnMethods = "validateDeleteRepoFunctionality")
    public void validateDeletedRepoErrorfunctionality() {
        Response data = apiHelper.deleteRepo(EnvironmentDetails.getProperty("OWNER"), "Kesting-Repo");
        data.then().statusCode(404);
        data.jsonPath().prettyPrint();
        GetErrorRepoResponse errorRepoResponse  = data.as(GetErrorRepoResponse.class);
        Assert.assertEquals(data.getStatusCode(), HttpStatus.SC_NOT_FOUND, "Create functionality is not working as expected.");
        Assert.assertEquals(errorRepoResponse.getMessage(), "Not Found", "Not throwing error");
        System.out.println("statusCode:"+data.statusCode());
        System.out.println("errors/message="+errorRepoResponse.getMessage());
    }
}
