package com.person.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;

/**
 * Handler to process the post request for person creation
 */
public class PersonCreateHandler implements RequestHandler<Person, ApiResponse> {

    public static final String UNABLE_TO_CREATE_PERSON = "Unable to create person";

    @Override
    public ApiResponse handleRequest(Person input, Context context) {
        int statusCode = HttpStatus.SC_OK;
        String responseBody ;
        ObjectMapper jsonMapper = new ObjectMapper();
        try{
            DynamoDBConfig.getMapper().save(input);
            responseBody = jsonMapper.writeValueAsString(input);
        }catch(Exception e){
            context.getLogger().log(e.getMessage());
            statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            responseBody = UNABLE_TO_CREATE_PERSON;
        }
        return ApiResponse.builder().statusCode(statusCode).body(responseBody).build();
    }
}
