package com.person.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDBConfig {

    public static DynamoDBMapper getMapper() {
        return new DynamoDBMapper(amazonDynamoDBConfig());
    }

    private static AmazonDynamoDB amazonDynamoDBConfig() {
        return AmazonDynamoDBClientBuilder.defaultClient();
    }
}