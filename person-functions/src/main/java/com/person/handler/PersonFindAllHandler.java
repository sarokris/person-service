package com.person.handler;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class PersonFindAllHandler implements RequestHandler<String, ApiResponse> {
    @Override
    public ApiResponse handleRequest(String s, Context context) {
        ApiResponse apiResponse;
        List<Person> result = new ArrayList<>();
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            List<Person> scanResult = DynamoDBConfig.getMapper().scan(Person.class, scanExpression);
            result.addAll(scanResult);
            apiResponse = ApiResponse.builder().statusCode(HttpStatus.SC_OK).body(result).build();
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            apiResponse = ApiResponse.builder().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(e.getMessage()).build();
        }
        return apiResponse;
    }
}
