package com.person.handler;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.lambda.runtime.Context;
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
import org.mockito.internal.stubbing.defaultanswers.ForwardsInvocations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
public class PersonFindAllHandlerTest {

    @Mock
    Context context;

    @InjectMocks
    PersonFindAllHandler handler;

    @Test
    public void testFindAllHandler_WhenNoPerson() throws JsonProcessingException {
        DynamoDBMapper mapper = mock(DynamoDBMapper.class);
        try (MockedStatic<DynamoDBConfig> utilities = Mockito.mockStatic(DynamoDBConfig.class)) {
            List<Person> personList = Collections.emptyList();
            utilities.when(DynamoDBConfig::getMapper).thenReturn(mapper);
            Mockito.when(mapper.scan(any(), any(DynamoDBScanExpression.class)))
                    .thenReturn(mock(PaginatedScanList.class, withSettings().defaultAnswer(new ForwardsInvocations(personList))));
            ApiResponse apiResponse = handler.handleRequest("",context);
            assertNotNull(apiResponse.getBody());
            assertEquals(HttpStatus.SC_OK,apiResponse.getStatusCode());
            assertEquals(new ObjectMapper().writeValueAsString(personList),apiResponse.getBody());

        }
    }

    @Test
    public void testFindAllHandler_WhenPersonAvailable() throws JsonProcessingException {
        DynamoDBMapper mapper = mock(DynamoDBMapper.class);
        try (MockedStatic<DynamoDBConfig> utilities = Mockito.mockStatic(DynamoDBConfig.class)) {
            List<Person> personList = Collections.singletonList(new Person("1","fname","sname","+31(0)6 1111111",null));
            utilities.when(DynamoDBConfig::getMapper).thenReturn(mapper);
            Mockito.when(mapper.scan(any(), any(DynamoDBScanExpression.class)))
                    .thenReturn(mock(PaginatedScanList.class, withSettings().defaultAnswer(new ForwardsInvocations(personList))));
            ApiResponse apiResponse = handler.handleRequest("", context);
            assertNotNull(apiResponse.getBody());
            assertEquals(HttpStatus.SC_OK, apiResponse.getStatusCode());
            assertEquals(new ObjectMapper().writeValueAsString(personList), apiResponse.getBody());

        }
    }

}
