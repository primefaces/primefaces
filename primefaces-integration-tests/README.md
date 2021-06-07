[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Actions Status](https://github.com/primefaces-extensions/primefaces-integration-tests/workflows/Java%20CI/badge.svg)](https://github.com/primefaces-extensions/primefaces-integration-tests/actions)
[![Stackoverflow](https://img.shields.io/badge/StackOverflow-primefaces-chocolate.svg)](https://stackoverflow.com/questions/tagged/primefaces-extensions)
[![Discord Chat](https://img.shields.io/discord/591914197219016707.svg?color=7289da&label=chat&logo=discord&style=flat-square)](https://discord.gg/gzKFYnpmCY)


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

- Build by source: `mvn clean package`
- Run "integration tests" with the _verify_ phase: `mvn verify`
- Run a single test with `mvn verify -Dit.test=org.primefaces.extensions.integrationtests.datepicker.DatePicker001Test`

### Firefox (default)

The default configuration of the project runs tests with a visible Firefox browser. This requires geckodriver in order to allow selenium to control the browser.
This is a binary file that can be downloaded via the link above, and can be placed in the base directory of the project where it will be accessed by selenium
for running the tests. The default location for the binary is the base directory of the project with an assumed file name of `geckodriver`
for Linux/MacOS and `geckodriver.exe` for Windows, but a non-default path can be specified via a system property:

    mvn verify -Dwebdriver.firefoxDriverBinary=$HOME/.selenium/drivers/firefox/geckodriver

The tests will be run by default in a visible browser UI, but can be run in headless mode by activating the headless-mode maven profile:

    mvn verify -Pheadless-mode

### Chrome

Google Chrome may also be used to run the tests by activating the "chrome" maven profile. This can be done on the commandline with the -P maven parameter:

    mvn verify -Pchrome

Using Chrome to run the tests requires the "chromedriver" binary which can be downloaded via the link above, and can be placed in the base directory of the
project. Selenium will access the binary from the base directory with an assumed file name of `chromedriver` for Linux/MacOS and `geckodriver.exe` for Windows,
but a non-default path can be specified via a system property:

      mvn verify -Dwebdriver.chromeDriverBinary=$HOME/.selenium/drivers/chrome/chromedriver -Pchrome

When using Chrome, the headless-mode maven profile may be activated to run chrome in headless mode:

     mvn verify -Pchrome,headless-mode

### Safari

Safari may also be used to run the tests by activating the "safari" maven profile. This can be done on the commandline with the -P maven parameter:

    mvn verify -Psafari

Keep in mind there are - as of january 2021 - following limitations for Safari webdriver:

- headless-mode ist not available (https://github.com/SeleniumHQ/selenium/issues/5985)
- no parallel test-execution because Safari does not allow for concurrent sessions (https://github.com/SeleniumHQ/selenium/issues/2172)
- Safari is only available on MacOS (and iOS / iPadOS)