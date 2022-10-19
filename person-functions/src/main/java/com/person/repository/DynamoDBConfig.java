package com.person.repository;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDBConfig {

    public static final String SERVICE_ENDPOINT = "";
    public static final String REGION = "";
    public static final String ACCESS_KEY = "";
    public static final String SECRET_KEY = "";

    public static DynamoDBMapper getMapper() {
        return new DynamoDBMapper(amazonDynamoDBConfig());
    }

    private static AmazonDynamoDB amazonDynamoDBConfig() {
        return AmazonDynamoDBClientBuilder.defaultClient();
//        return AmazonDynamoDBClientBuilder.standard()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(SERVICE_ENDPOINT, REGION))
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY))).build();
    }
}