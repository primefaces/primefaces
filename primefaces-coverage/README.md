# PrimeFaces Code Coverage

This module only comes for internal use within PrimeFaces team. It is used to generate code coverage reports for PrimeFaces.

## How to use

Run the following command to generate code coverage reports for PrimeFaces:
```
mvn verify -Pintegration-tests,parallel-execution,headless,chrome,theme-saga,csp,mojarra-4.0 -f ../pom.xml
```

The generated report will be available at `primefaces-coverage/target/site/jacoco-aggregate/index.html`.

It´s planed to feed the generated report into Sonar Cloud.