package com.myorg;

import org.apache.http.entity.ContentType;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PersonInfraStack extends Stack {
    public static final String DYNAMO_TABLE_NAME = "Persons";
    public static final String DYNAMO_PRIMARY_KEY = "id";

    public PersonInfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public PersonInfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Attribute partitionKey = Attribute.builder()
                .name(DYNAMO_PRIMARY_KEY)
                .type(AttributeType.STRING)
                .build();

        TableProps tableProps = TableProps.builder()
                .tableName(DYNAMO_TABLE_NAME)
                .partitionKey(partitionKey)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        Table dynamoTable = new Table(this, DYNAMO_TABLE_NAME,tableProps);

        Map<String,String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put("TABLE_NAME",dynamoTable.getTableName());
        lambdaEnvMap.put("PRIMARY_KEY", DYNAMO_PRIMARY_KEY);

        String createFunHandlerName = "com.person.handler.PersonCreateHandler";
        String getFunHandlerName = "com.person.handler.PersonGetHandler";
        String findAllFunHandlerName = "com.person.handler.PersonFindAllHandler";

        Function createPersonFun = new Function(this,"CreatePerson"
                ,getLambdaFunProps("CreatePerson",createFunHandlerName,lambdaEnvMap));
        Function getPersonFun = new Function(this,"GetPerson"
                ,getLambdaFunProps("GetPerson",getFunHandlerName,lambdaEnvMap));
        Function findAllPersonFun = new Function(this,"FindAllPerson"
                ,getLambdaFunProps("FindAllPerson",findAllFunHandlerName,lambdaEnvMap));

        dynamoTable.grantReadWriteData(createPersonFun);
        dynamoTable.grantReadWriteData(getPersonFun);
        dynamoTable.grantReadWriteData(findAllPersonFun);

        RestApiProps restApiProps = RestApiProps.builder()
                .restApiName("Person Service")
                .binaryMediaTypes(Arrays.asList(ContentType.APPLICATION_JSON.getMimeType()))
                .build();
        RestApi restApi = new RestApi(this,"personApi",restApiProps);

        //configuring the requestmapping
        IResource personResource = restApi.getRoot().addResource("person");

        //Lambda integration
        Integration createIntegration = new LambdaIntegration(createPersonFun);
        personResource.addMethod("POST",createIntegration);

        //findById adding path param placeholder
        IResource pathParamResource = personResource.addResource("{id}");
        Integration getPersonIntgrtn = new LambdaIntegration(getPersonFun);
        pathParamResource.addMethod("GET",getPersonIntgrtn);

        Integration findAllPersonIntgrtn = new LambdaIntegration(findAllPersonFun);
        personResource.addMethod("GET",findAllPersonIntgrtn);


    }

    private FunctionProps getLambdaFunProps(String functionName, String handlerName,Map<String, String> lambdaEnvMap) {
        return FunctionProps.builder()
                .code(Code.fromAsset("../assets/person-functions.jar"))
                .runtime(Runtime.JAVA_8)
                .handler(handlerName)
                .environment(lambdaEnvMap)
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .functionName(functionName)
                .build();
    }
}
