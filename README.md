# rspace-client-java
The project contains Java code that demonstrates how to make calls to RSpace API.

Check the [code examples](https://github.com/rspace-os/rspace-client-java/tree/master/API/src/test/java/com/researchspace/api/client/examples),
or the [implementation code and model](https://github.com/rspace-os/rspace-client-java/tree/master/API/src/main/java/com/researchspace/api/client).

The examples are written as JUnit tests, and can be run without any extra setup. By default they'll connect to a test account at [RSpace Community server](https://community.researchspace.com/public/apiDocs). 

To run the unit tests from the command line:

    > cd API
    > ./gradlew clean test

To run the examples for a different user, or different RSpace server, update the [config.properties](https://github.com/rspace-os/rspace-client-java/blob/master/API/config.properties) file. To enable API access for a particular user you need to 'Regenerate API key' at your RSpace profile page.

Minimum version of RSpace required: 1.41

Minimum version of Java required: 1.8

Minimum version of Gradle required: 2.12 (running ./gradlew should install a valid version)
