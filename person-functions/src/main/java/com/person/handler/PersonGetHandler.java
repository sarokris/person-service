package com.person.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.Map;

public class PersonGetHandler implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        ApiResponse response = new ApiResponse();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        JSONParser parser = new JSONParser();
        try {
            JSONObject reqObj =(JSONObject) parser.parse(reader);
            if(reqObj.get("pathParameters")!=null){
                JSONObject pps = (JSONObject)reqObj.get("pathParameters");
                if(pps.get("id")!=null){
                    String personId = (String)pps.get("id");
                    Person resultObj = DynamoDBConfig.getMapper().load(Person.class,personId);
                    if(resultObj == null ){
                        response = ApiResponse.builder().statusCode(HttpStatus.SC_NOT_FOUND).body("Person not found in the DB").build();
                    }else{
                        response = ApiResponse.builder().statusCode(HttpStatus.SC_OK).body(resultObj).build();
                    }
                }
            }

        }catch (Exception e){
            response = ApiResponse.builder().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body("Unable to fetch person from DB").build();
        }finally {
            writer.write(response.toString());
            reader.close();
            writer.close();
        }

    }
}
