package com.person.infra;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class PersonInfraApp {

    public static final String PERSON_INFRA_STACK_ID = "PersonInfraStack";

    public static void main(final String[] args) {
        App app = new App();

        new PersonInfraStack(app, PERSON_INFRA_STACK_ID, StackProps.builder()
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())

                .build());

        app.synth();
    }
}

