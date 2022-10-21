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
import software.amazon.awscdk.services.lambda.HttpMethod;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;

public class PersonInfraStack extends Stack {

    //DynamoDB properties
    public static final String DYNAMO_TABLE_NAME = "Persons";
    public static final String DYNAMO_PRIMARY_KEY = "id";

    //Handler names
    public static final String CREATE_FUN_HANDLER = "com.person.handler.PersonCreateHandler";
    public static final String GET_FUN_HANDLER = "com.person.handler.PersonGetHandler";
    public static final String FIND_ALL_FUN_HANDLER = "com.person.handler.PersonFindAllHandler";

    //Lambda function names
    public static final String FUNCTION_CREATE_PERSON = "CreatePerson";
    public static final String FUNCTION_GET_PERSON = "GetPerson";
    public static final String FUNCTION_FIND_ALL_PERSON = "FindAllPerson";
    public static final String STRING_TABLE_NAME = "TABLE_NAME";
    public static final String STRING_PRIMARY_KEY = "PRIMARY_KEY";

    public PersonInfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public PersonInfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Table dynamoTable = constructDynamoTable();
        constructLambdaFunctionsApis(dynamoTable);
    }

    private void constructLambdaFunctionsApis(Table dynamoTable) {

        Map<String,String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put(STRING_TABLE_NAME,dynamoTable.getTableName());
        lambdaEnvMap.put(STRING_PRIMARY_KEY, DYNAMO_PRIMARY_KEY);

        Map<String,String> funMetadataMap = new HashMap<>();
        funMetadataMap.put(FUNCTION_CREATE_PERSON,CREATE_FUN_HANDLER);
        funMetadataMap.put(FUNCTION_GET_PERSON,GET_FUN_HANDLER);
        funMetadataMap.put(FUNCTION_FIND_ALL_PERSON,FIND_ALL_FUN_HANDLER);

        Map<String,Function> lamdaFunMap = generateLambdaFunctions(funMetadataMap,lambdaEnvMap);

        lamdaFunMap.forEach((k,v) -> dynamoTable.grantReadWriteData(v));


        RestApiProps restApiProps = RestApiProps.builder()
                .restApiName("Person Service")
                .binaryMediaTypes(singletonList(ContentType.APPLICATION_JSON.getMimeType()))
                .build();

        RestApi restApi = new RestApi(this,"personApi",restApiProps);

        //configuring the requestmapping
        IResource personResource = restApi.getRoot().addResource("person");

        //Lambda integration
        Integration createIntegration = new LambdaIntegration(lamdaFunMap.get(FUNCTION_CREATE_PERSON));
        personResource.addMethod(HttpMethod.POST.name(),createIntegration);

        //findById adding path param placeholder
        IResource pathParamResource = personResource.addResource("{id}");
        Integration getPersonIntgrtn = new LambdaIntegration(lamdaFunMap.get(FUNCTION_GET_PERSON));
        pathParamResource.addMethod(HttpMethod.GET.name(),getPersonIntgrtn);

        Integration findAllPersonIntgrtn = new LambdaIntegration(lamdaFunMap.get(FUNCTION_FIND_ALL_PERSON));
        personResource.addMethod(HttpMethod.GET.name(),findAllPersonIntgrtn);
    }

    private Map<String, Function> generateLambdaFunctions(Map<String, String> funMetadataMap, Map<String, String> lambdaEnvMap) {
        Map<String,Function> lamdaFunMap = new HashMap<>();
        funMetadataMap.forEach((functionName,handlerName) ->
            lamdaFunMap.put(functionName, new Function(this, functionName
                    ,getLambdaFunProps(functionName, handlerName,lambdaEnvMap)))
        );
        return lamdaFunMap;
    }


    private Table constructDynamoTable() {

        Attribute partitionKey = Attribute.builder()
                .name(DYNAMO_PRIMARY_KEY)
                .type(AttributeType.STRING)
                .build();

        TableProps tableProps = TableProps.builder()
                .tableName(DYNAMO_TABLE_NAME)
                .partitionKey(partitionKey)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

       return new Table(this, DYNAMO_TABLE_NAME,tableProps);

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
