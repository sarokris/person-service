package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class PersonInfraStack extends Stack {
    public PersonInfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public PersonInfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here
        Function.Builder.create(this,"person-lambda")
                .runtime(Runtime.JAVA_8)
                .handler("com.person.handler.PersonGetHandler")
                .memorySize(512)
                .timeout(Duration.seconds(20))
                .functionName("person-create-lambda")
                .code(Code.fromAsset("../assets/person-functions.jar"))
                .build();

    }
}
