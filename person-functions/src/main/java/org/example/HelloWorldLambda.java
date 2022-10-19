package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Lambda functions for Person service!
 *
 */
public class HelloWorldLambda implements RequestHandler<String,String>
{
    @Override
    public String handleRequest(String s, Context context) {
        return "Hello, Welcome to the world of Lambda serverless architecture " + s;
    }
}
