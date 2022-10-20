package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class PersonInfraApp {

    public static final String ACCOUNT_ID = "662065144489";
    public static final String REGION_AP_SOUTH_1 = "ap-south-1";
    public static final String PERSON_INFRA_STACK_ID = "PersonInfraStack";

    public static void main(final String[] args) {
        App app = new App();

        new PersonInfraStack(app, PERSON_INFRA_STACK_ID, StackProps.builder()
                /*
                .env(Environment.builder()
                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                        .region(System.getenv("CDK_DEFAULT_REGION"))
                        .build())
                */

                // Uncomment the next block if you know exactly what Account and Region you
                // want to deploy the stack to.
                .env(Environment.builder()
                        .account(ACCOUNT_ID)
                        .region(REGION_AP_SOUTH_1)
                        .build())
                .build());

        app.synth();
    }
}

