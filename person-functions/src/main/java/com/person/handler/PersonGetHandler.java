package com.person.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.person.AppConstants.KEY_ID;

public class PersonGetHandler implements RequestStreamHandler {

    public static final String PERSON_NOT_FOUND = "Person not found in the DB";
    public static final String UNABLE_TO_FETCH_PERSON = "Unable to fetch person from DB";
    public static final String KEY_PATHPARAMETERS = "pathParameters";

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        ApiResponse response = new ApiResponse();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        JSONParser parser = new JSONParser();
        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            JSONObject reqObj =(JSONObject) parser.parse(reader);
            if(reqObj.get(KEY_PATHPARAMETERS)!=null){
                JSONObject pps = (JSONObject)reqObj.get(KEY_PATHPARAMETERS);
                if(pps.get(KEY_ID)!=null){
                    String personId = (String)pps.get(KEY_ID);
                    Person resultObj = DynamoDBConfig.getMapper().load(Person.class,personId);
                    int statusCode ;
                    String responseBody;
                    if(resultObj == null ){
                        statusCode = HttpStatus.SC_NOT_FOUND;
                        responseBody = PERSON_NOT_FOUND;
                    }else{
                        statusCode = HttpStatus.SC_OK;
                        responseBody = jsonMapper.writeValueAsString(resultObj);
                    }
                    response = ApiResponse.builder().statusCode(statusCode).body(responseBody).build();
                }
            }

        }catch (Exception e){
            response = ApiResponse.builder().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(UNABLE_TO_FETCH_PERSON).build();
        }finally {
            writer.write(jsonMapper.writeValueAsString(response));
            reader.close();
            writer.close();
        }

    }
}
