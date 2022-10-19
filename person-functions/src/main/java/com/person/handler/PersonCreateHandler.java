package com.person.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpOptions;

/**
 * Handler to process the post request for person creation
 */
public class PersonCreateHandler implements RequestHandler<Person, ApiResponse> {

    @Override
    public ApiResponse handleRequest(Person input, Context context) {
        ApiResponse apiResponse;
        try{
            DynamoDBConfig.getMapper().save(input);
            apiResponse = ApiResponse.builder().statusCode(HttpStatus.SC_OK).body(input).build();
        }catch(Exception e){
            context.getLogger().log(e.getMessage());
            apiResponse = ApiResponse.builder()
                    .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body("Unable to create person").build();
        }
        return apiResponse;
    }
}
