package com.person.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.person.model.Person;

/**
 * Handler to process the post request for person creation
 */
public class PersonCreateHandler implements RequestHandler<Person,Person> {

    @Override
    public Person handleRequest(Person person, Context context) {
        return new Person("Saravananand","Krishnamani","Den Haag");
    }
}
