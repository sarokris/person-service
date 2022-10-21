package com.person.handler;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;

import java.util.List;

public class PersonFindAllHandler implements RequestHandler<Object, ApiResponse> {
    @Override
    public ApiResponse handleRequest(Object s, Context context) {
        int statusCode = HttpStatus.SC_OK;
        String responseBody;
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            List<Person> scanResult = DynamoDBConfig.getMapper().scan(Person.class, scanExpression);
            responseBody = new ObjectMapper().writeValueAsString(scanResult);
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            responseBody = e.getMessage();
        }
        return ApiResponse.builder().statusCode(statusCode).body(responseBody).build();
    }
}
