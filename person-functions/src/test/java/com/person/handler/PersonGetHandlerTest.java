package com.person.handler;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static com.person.handler.PersonGetHandler.PERSON_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonGetHandlerTest {

    @Mock
    APIGatewayProxyRequestEvent event;

    @Mock
    Context context;

    @InjectMocks
    PersonGetHandler getHandlerTest;

    @Test
    public void handleRequestWhenNoPersonFound()  {
        DynamoDBMapper mapper = mock(DynamoDBMapper.class);
        try (MockedStatic<DynamoDBConfig> utilities = Mockito.mockStatic(DynamoDBConfig.class)) {
            utilities.when(DynamoDBConfig::getMapper).thenReturn(mapper);
            when(mapper.load(eq(Person.class), anyString()))
                    .thenReturn(null);
            Map<String, String> pathParams = new HashMap<>();
            pathParams.put("id","1");
            when(event.getPathParameters()).thenReturn(pathParams);
            ApiResponse apiResponse = getHandlerTest.handleRequest(event,context);
            assertNotNull(apiResponse.getBody());
            assertEquals(HttpStatus.SC_NOT_FOUND,apiResponse.getStatusCode());
            assertEquals(PERSON_NOT_FOUND,apiResponse.getBody());

        }
    }

    @Test
    public void handleRequestWhenPersonFound() throws JsonProcessingException {
        DynamoDBMapper mapper = mock(DynamoDBMapper.class);
        try (MockedStatic<DynamoDBConfig> utilities = Mockito.mockStatic(DynamoDBConfig.class)) {
            Person person = new Person("1","fname","sname","+31(0)6 1111111",null);
            utilities.when(DynamoDBConfig::getMapper).thenReturn(mapper);
            when(mapper.load(eq(Person.class), anyString()))
                    .thenReturn(person);
            Map<String, String> pathParams = new HashMap<>();
            pathParams.put("id","1");
            when(event.getPathParameters()).thenReturn(pathParams);
            ApiResponse apiResponse = getHandlerTest.handleRequest(event,context);
            assertNotNull(apiResponse.getBody());
            assertEquals(HttpStatus.SC_OK,apiResponse.getStatusCode());
            assertEquals(new ObjectMapper().writeValueAsString(person), apiResponse.getBody());

        }
    }
}
