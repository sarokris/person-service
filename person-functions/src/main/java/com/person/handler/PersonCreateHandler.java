package com.person.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;

/**
 * Handler to process the post request for person creation
 */
public class PersonCreateHandler implements RequestHandler<Person,Object> {

    @Override
    public Object handleRequest(Person input, Context context) {
        try{
            DynamoDBConfig.getMapper().save(input);
        }catch(Exception e){
            context.getLogger().log(e.getMessage());
            return "Unable to create person";
        }
        return input;
    }
}
