package com.person.handler;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.person.model.ApiResponse;
import com.person.model.Person;
import com.person.repository.DynamoDBConfig;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.defaultanswers.ForwardsInvocations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static com.person.handler.PersonCreateHandler.UNABLE_TO_CREATE_PERSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
public class PersonCreateHandlerTest {

    @Mock
    APIGatewayProxyRequestEvent event;

    @Mock
    Context context;

    @InjectMocks
    PersonCreateHandler createHandler;

    @BeforeEach
    public void setUp(){
        LambdaLogger logger = mock(LambdaLogger.class);
        when(context.getLogger()).thenReturn(logger);
        doNothing().when(logger).log(anyString());
    }



    @Test
    public void testCreatFailure_InvalidInput()  {
        when(event.getBody()).thenReturn("{\"name\" : \"test\"}" );
        ApiResponse apiResponse = createHandler.handleRequest(event,context);
        assertNotNull(apiResponse.getBody());
        assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR,apiResponse.getStatusCode());
        assertEquals(UNABLE_TO_CREATE_PERSON,apiResponse.getBody());
    }

    @Test
    public void testCreatFailure_personExists() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("person_valid.json").toURI());

        String bodyStr = new String(Files.readAllBytes(path));
        Person givenPerson = new ObjectMapper().readValue(bodyStr,Person.class);

        DynamoDBMapper mapper = mock(DynamoDBMapper.class);
        try (MockedStatic<DynamoDBConfig> utilities = Mockito.mockStatic(DynamoDBConfig.class)) {
            List<Person> personList = Collections.singletonList(givenPerson);
            utilities.when(DynamoDBConfig::getMapper).thenReturn(mapper);
            when(event.getBody()).thenReturn(bodyStr);
            Mockito.when(mapper.scan(any(), any(DynamoDBScanExpression.class)))
                    .thenReturn(mock(PaginatedScanList.class, withSettings().defaultAnswer(new ForwardsInvocations(personList))));
            ApiResponse apiResponse = createHandler.handleRequest(event,context);

            assertNotNull(apiResponse.getBody());
            assertEquals(HttpStatus.SC_OK,apiResponse.getStatusCode());

            String expectedRespons = String.format("Person %s %s already available",givenPerson.getFirstname(),givenPerson.getLastname());
            assertEquals(expectedRespons,apiResponse.getBody());
        }
    }

    @Test
    public void testCreatSuccess() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("person_valid.json").toURI());

        String bodyStr = new String(Files.readAllBytes(path));
        Person givenPerson = new ObjectMapper().readValue(bodyStr,Person.class);

        DynamoDBMapper mapper = mock(DynamoDBMapper.class);
        try (MockedStatic<DynamoDBConfig> utilities = Mockito.mockStatic(DynamoDBConfig.class)) {
            utilities.when(DynamoDBConfig::getMapper).thenReturn(mapper);
            when(event.getBody()).thenReturn(bodyStr);
            doNothing().when(mapper).save(any(Person.class));
            ApiResponse apiResponse = createHandler.handleRequest(event,context);

            assertNotNull(apiResponse.getBody());
            assertEquals(HttpStatus.SC_OK,apiResponse.getStatusCode());
            assertEquals(new ObjectMapper().writeValueAsString(givenPerson),apiResponse.getBody());
        }
    }


}
