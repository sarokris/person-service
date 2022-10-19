package com.person.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;

public class PersonGetHandler implements RequestHandler<String,Object> {
    @Override
    public Object handleRequest(String s, Context context) {
        Person resultObj;
        try {
            resultObj = DynamoDBConfig.getMapper().load(Person.class,s);
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return "Unable to fetch person from DB";
        }
        return resultObj == null ? "Person not found in the DB" : resultObj;
    }
}
