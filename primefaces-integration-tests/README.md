PrimeFaces Integration Tests
----------------------------

To provide an integration and regression test suite for PrimeFaces.

## Prerequisites

This project uses [selenium webdriver](https://www.selenium.dev/) for web browser automation. Currently, a native installation
of [Firefox](https://firefox.com/) and/or [Chrome](https://www.google.com/chrome/) and/or Safari is required. Additionally, the selenium webdriver corresponding
to the browser(s) must also be installed. These can be downloaded from the following locations:

- [firefox (gecko) webdriver](https://github.com/mozilla/geckodriver)
- [chrome webdriver](https://chromedriver.chromium.org/)

Safari already comes with webdriver OOTB - look for more information into https://developer.apple.com/documentation/webkit/testing_with_webdriver_in_safari

## Build & Run

- Build the project: `mvn clean package`
- When running an integration test the profile `integration-tests` has to be activated and a JSF implementation has to be selected.
  This project provides the following profiles to select a JSF implementation
  - `mojarra-2.3.x` current Mojarra 2.3 JSF implementation
  - `myfaces-2.3.x` current MyFaces 2.3 JSF implementation
- Run integration tests with the _verify_ phase and profiles activated: `mvn verify -Pintegration-tests,mojarra-2.3.x`
- Run a single test with `mvn verify -Pintegration-tests,mojarra-2.3.x -Dit.test=org.primefaces.integrationtests.datepicker.DatePicker001Test`

### Firefox (default)

The default configuration of the project runs tests with a visible Firefox browser. Firefox may also be selected explicitly by activating the `firefox` maven profile.
Using Firefox requires "geckodriver" in order to allow selenium to control the browser, a binary file that can be downloaded via the link above, and should be placed
in a folder containing WebDriver’s binaries. Please add the folder containing WebDriver’s binaries to your system’s path. Selenium will then be able to locate the
additional binaries without requiring any further configuration.

 `mvn verify -Pintegration-tests,mojarra-2.3.x,firefox`

The tests will be run by default in a visible browser UI, but can be run in headless mode by activating the `headless` maven profile:

 `mvn verify -Pintegration-tests,mojarra-2.3.x,firefox,headless`

### Chrome

Google Chrome may also be used to run the tests by activating the `chrome` maven profile.
Using Chrome requires "chromedriver" in order to allow selenium to control the browser, a binary file that can be downloaded via the link above, and should be placed
in a folder containing WebDriver’s binaries. Please add the folder containing WebDriver’s binaries to your system’s path. Selenium will then be able to locate the
additional binaries without requiring any further configuration.

 `mvn verify -Pintegration-tests,mojarra-2.3.x,chrome`

The tests will be run by default in a visible browser UI, but can be run in headless mode by activating the `headless` maven profile:

 `mvn verify -Pintegration-tests,mojarra-2.3.x,chrome,headless`

### Safari

Safari may also be used to run the tests by activating the `safari` maven profile.

 `mvn verify -Pintegration-tests,mojarra-2.3.x,safari`

Keep in mind there are - as of january 2021 - following limitations for Safari webdriver:

- headless-mode ist not available (https://github.com/SeleniumHQ/selenium/issues/5985)
- no parallel test-execution because Safari does not allow for concurrent sessions (https://github.com/SeleniumHQ/selenium/issues/2172)
- Safari is only available on MacOS (and iOS / iPadOS)

### More configuration

This project provides some more profiles to select a theme or to enable CSP. Please see profiles in `pom.xml` for details.
