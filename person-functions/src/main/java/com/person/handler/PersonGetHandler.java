package com.person.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;

public class PersonGetHandler implements RequestHandler<String, ApiResponse> {
    @Override
    public ApiResponse handleRequest(String id, Context context) {
        ApiResponse response;
        try {
           Person resultObj = DynamoDBConfig.getMapper().load(Person.class,id);
           if(resultObj == null ){
               response = ApiResponse.builder().statusCode(HttpStatus.SC_NOT_FOUND).body("Person not found in the DB").build();
           }else{
               response = ApiResponse.builder().statusCode(HttpStatus.SC_OK).body(resultObj).build();
           }

        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            response = ApiResponse.builder().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Unable to fetch person from DB").build();
        }
        return response;
    }
}
