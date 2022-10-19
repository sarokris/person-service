package com.person.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.person.model.Person;

public class PersonGetHandler implements RequestHandler<String,Person> {
    @Override
    public Person handleRequest(String s, Context context) {
        return new Person("Saravananand","Krishnamani","Den Haag");
    }
}
