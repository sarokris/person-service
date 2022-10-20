package com.person.handler;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler to process the post request for person creation
 */
public class PersonCreateHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiResponse> {

    public static final String UNABLE_TO_CREATE_PERSON = "Unable to create person";

    @Override
    public ApiResponse handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        int statusCode = HttpStatus.SC_OK;
        String responseBody ;
        ObjectMapper jsonMapper = new ObjectMapper();
        try{
            context.getLogger().log("RequestBody " + input.getBody());
            Person givenPerson = jsonMapper.readValue(input.getBody(),Person.class);
            if(isPersonExists(givenPerson)){
                responseBody = String.format("Person %s %s already available",givenPerson.getFirstname(),givenPerson.getLastname());
            }else{
                DynamoDBConfig.getMapper().save(givenPerson);
                responseBody = jsonMapper.writeValueAsString(givenPerson);
            }
        }catch(Exception e){
            context.getLogger().log(e.getMessage());
            statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            responseBody = UNABLE_TO_CREATE_PERSON;
        }
        return ApiResponse.builder().statusCode(statusCode).body(responseBody).build();
    }

    private boolean isPersonExists(Person givenPerson) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(givenPerson.getFirstname()));
        eav.put(":v2", new AttributeValue().withS(givenPerson.getLastname()));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                //Filter Expression
                .withFilterExpression("firstname =:v1 and lastname=:v2")
                .withExpressionAttributeValues(eav);

        List<Person> existingPersonList = DynamoDBConfig.getMapper().scan(Person.class, scanExpression);
        return existingPersonList != null && !existingPersonList.isEmpty();
    }
}
