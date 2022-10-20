package com.person.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;

import java.util.Map;

import static com.person.AppConstants.KEY_ID;
import static com.person.AppConstants.STRING_SUCCESS;

public class PersonGetHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiResponse>{

    public static final String PERSON_NOT_FOUND = "Person not found in the DB";
    public static final String UNABLE_TO_FETCH_PERSON = "Unable to fetch person from DB";
//

    @Override
    public ApiResponse handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        ApiResponse response;
        ObjectMapper jsonMapper = new ObjectMapper();
        int statusCode = HttpStatus.SC_OK;
        String responseBody = STRING_SUCCESS;
        try {
            Map<String, String> pathParams =  event.getPathParameters();
            if(pathParams.containsKey(KEY_ID)){
                String personId = pathParams.get(KEY_ID);
                Person resultObj = DynamoDBConfig.getMapper().load(Person.class,personId);
                if(resultObj == null ){
                    statusCode = HttpStatus.SC_NOT_FOUND;
                    responseBody = PERSON_NOT_FOUND;
                }else{
                    statusCode = HttpStatus.SC_OK;
                    responseBody = jsonMapper.writeValueAsString(resultObj);
                }
            }

        }catch (Exception e){
            context.getLogger().log(UNABLE_TO_FETCH_PERSON + e.getMessage());
            statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            responseBody = UNABLE_TO_FETCH_PERSON;
        }
        response = ApiResponse.builder().statusCode(statusCode).body(responseBody).build();
        return response;
    }
}
