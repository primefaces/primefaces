[![Maven](https://img.shields.io/maven-central/v/org.primefaces/primefaces.svg)](https://repo.maven.apache.org/maven2/org/primefaces/primefaces-integration-tests/)
[![Javadocs](http://javadoc.io/badge/org.primefaces/primefaces-selenium.svg)](http://javadoc.io/doc/org.primefaces/primefaces-integration-tests)

# primefaces-integration-tests

To provide an integration and regression test suite for PrimeFaces, based on our own [PrimeFaces Selenium](https://github.com/primefaces/primefaces/tree/master/primefaces-selenium)

It utilizes Tomcat Embedded, OpenWebBeans, RestEasy and different JSF implementations.

## Build & Run

- Build the project: `mvn clean package`
- When running an integration test the profile `integration-tests` has to be activated and a JSF implementation has to be selected.
- Run integration tests with the _verify_ phase and profiles activated: `mvn verify -Pintegration-tests,mojarra-2.3`
- Run a single test with `mvn verify -Pintegration-tests,mojarra-2.3 -Dit.test=DatePicker001Test`

## Profiles

#### Browsers

  - `firefox` (default)
  - `chrome`
  - `safari`

Keep in mind there are - as of january 2021 - following limitations for Safari webdriver:
- headless-mode ist not available (https://github.com/SeleniumHQ/selenium/issues/5985)
- no parallel test-execution because Safari does not allow for concurrent sessions (https://github.com/SeleniumHQ/selenium/issues/2172)
- Safari is only available on MacOS (and iOS / iPadOS)

#### JSF Implementations

  - `mojarra-2.3` - Mojarra 2.3 JSF implementation
  - `myfaces-2.3` - MyFaces 2.3 JSF implementation
  - `myfaces-next-2.3` - MyFaces 2.3 next JSF implementation

#### Misc

  - `headless` - headless browser
  - `client-state-saving` - ClientSide instead of ServerSide JSF state saving
  - `csp` - enabled Content Security Policy
  - `uploader-native` - FileUpload configured to use the native servlet implementation
  - `uploader-commons` - FileUpload configured to use the Apache commons-fileupload
  - `theme-nova` - Nova theme
  - `theme-saga` - Saga theme
