[![Maven](https://img.shields.io/maven-central/v/org.primefaces/primefaces.svg)](https://repo.maven.apache.org/maven2/org/primefaces/primefaces-selenium/)
[![Javadocs](http://javadoc.io/badge/org.primefaces/primefaces-selenium.svg)](http://javadoc.io/doc/org.primefaces/primefaces-selenium)

# primefaces-selenium

PrimeFaces testing support based on JUnit5, Selenium and the concept of page objects / fragments. 
It also supports JUnit5 parallel test execution to speed up tests.

PrimeFaces-Selenium provides a hook-in to either startup a local server, use a remote adress and to instantiate the WebDriver.

This is the successor of primefaces-arquillian and heavily inspired by Arquillian Graphene.

## Configuration

PrimeFaces-Selenium requires a `/primefaces-selenium/config.properties` to set a `PrimeSeleniumAdapter`.
A sample implementation, which starts a local TomEE, can be found here: [TomEE Adapter](https://github.com/primefaces-extensions/primefaces-integration-tests/blob/master/src/test/java/org/primefaces/extensions/integrationtests/PrimeFacesSeleniumTomEEAdapter.java) and [FireFox TomEE Adapter](https://github.com/primefaces-extensions/primefaces-integration-tests/blob/master/src/test/java/org/primefaces/extensions/integrationtests/PrimeFacesSeleniumTomEEFirefoxAdapterImpl.java)

Properties:
|       property name      |   type  | default |                 description                 |
|:------------------------:|:-------:|---------|:-------------------------------------------:|
|          adapter         | org.primefaces.extensions.selenium.spi.PrimeSeleniumAdapter    |         | Adapter/Hook-In implementation class |
|        guiTimeout        |   int   | 2       |       GUI timeout for waits in seconds      |
|        ajaxTimeout       |   int   | 10      |      AJAX timeout for guards in seconds     |
|        httpTimeout       |   int   | 10      |      HTTP timeout for guards in seconds     |
|    documentLoadTimeout   |   int   | 15      |       Document load timeout in seconds      |
|    disableAnimations     | boolean | true    | If animations should be disabled for tests  |

### Status

Currently, only the following components are implemented (partially):

#### HTML

- Link

#### JSF / PrimeFaces

- AccordionPanel
- AutoComplete
- Calendar
- CascadeSelect
- Chips
- CommandButton
- CommandLink
- ConfirmDialog
- ConfirmPopup
- ~~DataList~~ (use DataView)
- DataTable
- DataView
- DatePicker
- Dialog
- FileUpload  
- InputMask
- InputNumber
- ~~InputSwitch~~ (use ToggleSwitch)
- InputText
- InputTextarea
- Messages
- OutputLabel
- OverlayPanel
- Panel
- Password
- Rating
- Schedule
- SelectBooleanCheckbox
- SelectBooleanButton
- SelectManyCheckbox
- SelectManyMenu  
- SelectOneButton
- SelectOneMenu
- SelectOneRadio
- Slider
- Spinner
- TabView
- TextEditor
- Timeline
- ToggleSwitch
- Tree 
- TreeTable
- TriStateCheckbox

### Usage

Example view:

```java
import org.openqa.selenium.support.FindBy;
import org.primefaces.extensions.selenium.AbstractPrimePage;
import org.primefaces.extensions.selenium.component.InputText;
import org.primefaces.extensions.selenium.component.SelectOneMenu;

public class IndexPage extends AbstractPrimePage {

    @FindBy(id = "form:manufacturer")
    private SelectOneMenu manufacturer;

    @FindBy(id = "form:car")
    private InputText car;

    public SelectOneMenu getManufacturer() {
        return manufacturer;
    }

    public InputText getCar() {
        return car;
    }

    @Override
    public String getLocation() {
        return "index.xhtml";
    }
}
```

Example test:

```java
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.primefaces.extensions.selenium.AbstractPrimePageTest;

public class IndexPageTest extends AbstractPrimePageTest {

    @Inject
    private AnotherPage another;

    @Test
    public void myFirstTest(IndexPage index) throws InterruptedException {
        // right page?
        Assertions.assertTrue(index.isAt());
        assertNotDisplayed(index.getCar());

        // just to follow the browser with a human eye for the showcase :D - not need in your real tests
        Thread.sleep(2000);

        // select manufacturer
        assertDisplayed(index.getManufacturer());
        index.getManufacturer().select("BMW");
        Assertions.assertTrue(index.getManufacturer().isSelected("BMW"));

        // just to follow the browser with a human eye for the showcase :D - not need in your real tests
        Thread.sleep(2000);

        // type car
        assertDisplayed(index.getCar());
        index.getCar().setValue("E30 M3");

        // just to follow the browser with a human eye for the showcase :D - not need in your real tests
        Thread.sleep(2000);

        another.goTo();
        
        ...
    }
}
```

Creating component without annotations:

```java
InputText input = PrimeSelenium.createFragment(InputText.class, By.id("test"));
```

### Build & Run

- Build by source `mvn clean install`
