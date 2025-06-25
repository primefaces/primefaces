[![Maven](https://img.shields.io/maven-central/v/org.primefaces/primefaces.svg)](https://repo.maven.apache.org/maven2/org/primefaces/primefaces-integration-tests/)
[![Javadocs](http://javadoc.io/badge/org.primefaces/primefaces-selenium.svg)](http://javadoc.io/doc/org.primefaces/primefaces-integration-tests)

# primefaces-integration-tests

To provide an integration and regression test suite for PrimeFaces, based on our own [PrimeFaces Selenium](https://github.com/primefaces/primefaces/tree/master/primefaces-selenium)

It utilizes Tomcat Embedded, Weld, RestEasy and different Faces implementations.

## Build & Run

- Build the project: `mvn clean package`
- When running an integration test the profile `integration-tests` has to be activated and a Jakarta Faces implementation has to be selected.
- Run integration tests with the _verify_ phase and profiles activated: `mvn verify -Pintegration-tests,parallel-execution,mojarra-4.0`
- Run a single test with `mvn verify -Pintegration-tests,parallel-execution,mojarra-4.0 -Dit.test=DatePicker001Test`

## Run as web application
- `mvn clean jetty:run -P"mojarra-4.0"`
- Now you can open it on 'http://localhost:8080/integrationtests/'

## Profiles

#### Browsers

  - `firefox`
  - `chrome` (default)
  - `safari`

Keep in mind there are - as of january 2021 - following limitations for Safari webdriver:
- headless-mode is not available (https://github.com/SeleniumHQ/selenium/issues/5985)
- no parallel test-execution because Safari does not allow for concurrent sessions (https://github.com/SeleniumHQ/selenium/issues/2172)
- Safari is only available on MacOS (and iOS / iPadOS)

#### Jakarta Faces Implementations

  - `mojarra-4.0` - Mojarra 4.0 implementation
  - `myfaces-4.0` - MyFaces 4.0 implementation
  - `mojarra-4.1` - Mojarra 4.1 implementation
  - `myfaces-4.1` - MyFaces 4.1 implementation

#### Misc

  - `headless` - headless browser
  - `client-state-saving` - ClientSide instead of ServerSide Faces state saving
  - `csp` - enabled Content Security Policy
  - `uploader-native` - FileUpload configured to use the native servlet implementation
  - `uploader-commons` - FileUpload configured to use the Apache commons-fileupload
  - `theme-nova` - Nova theme
  - `theme-saga` - Saga theme
