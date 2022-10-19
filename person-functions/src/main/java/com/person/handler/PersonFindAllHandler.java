package com.person.handler;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;

import java.util.ArrayList;
import java.util.List;

public class PersonFindAllHandler implements RequestHandler<String,List<Person>> {
    @Override
    public List<Person> handleRequest(String s, Context context) {
        List<Person> result = new ArrayList<>();
        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            List<Person> scanResult = DynamoDBConfig.getMapper().scan(Person.class, scanExpression);
            result.addAll(scanResult);
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
        }
        return result;
    }
}
