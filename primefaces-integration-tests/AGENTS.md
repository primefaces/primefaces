# primefaces-integration-tests

Guidance for Coding Agents working inside the `primefaces-integration-tests` module.
Read the root [`AGENTS.md`](../AGENTS.md) first for repository-wide conventions, and
[`primefaces-selenium/AGENTS.md`](../primefaces-selenium/AGENTS.md) for the test
**framework** this module builds on. This file focuses on **how to add a test and keep
component coverage healthy**.

## Purpose

This module is the **browser-based regression suite** for the PrimeFaces component
library. Each test boots a real Faces application (embedded Tomcat + Weld + Mojarra or
MyFaces), drives a page with Selenium, and asserts component behavior, rendered markup,
widget configuration, and the absence of JavaScript errors. It is the primary guard
against regressions in real component rendering and client-side behavior — unit tests in
`primefaces` cover server-side logic only.

It consumes `primefaces-selenium` (the reusable framework) as a `test`-scoped dependency.
This module supplies the **deployment** (`TomcatDeploymentAdapter`), the **views and
beans** under test, and the **tests** themselves.

## The test triad

Almost every test is three coordinated artifacts that share a numbered name
(`<Component><NNN>`). To exercise the `p:dataTable` "basic + paginator" scenario:

| Role | Location | Example |
|---|---|---|
| **View** (`.xhtml`) | `src/main/webapp/<component>/` | `datatable/dataTable001.xhtml` |
| **Backing bean** (CDI) | `src/main/java/.../integrationtests/<component>/` | `DataTable001.java` (`@Named @ViewScoped`) |
| **Test** (JUnit 5) | `src/test/java/.../integrationtests/<component>/` | `DataTable001Test.java` |

The view's `getLocation()` / `goTo()` path is **relative to the webapp root** and matches
the file under `src/main/webapp` (e.g. `datatable/dataTable001.xhtml`). The bean is wired
into the view by EL name (`#{dataTable001...}`). Backing beans use Lombok `@Data` and a
`@PostConstruct init()` to seed data, often via a shared `*Service` (e.g.
`ProgrammingLanguageService`).

> Not every test needs a bean — purely static markup scenarios can use a view alone, and
> a bean can back several numbered views. But keep the `NNN` number aligned across the
> three files so the triad is greppable.

## Numbering & naming conventions

- **`<Component><NNN>`** where `NNN` is zero-padded and increments per scenario:
  `DataTable001`, `DataTable002`, … A higher number is a *new scenario*, not a fix to an
  existing one — add a new triad rather than overloading an existing view.
- Optional suffix describing the angle: `DataTable005PagingTest`, `DataTable007LazyTest`,
  `DataTable007CellTest`.
- Test classes end in `Test` and are **package-private** (`class DataTable001Test`).
- Test **methods** carry `@Order(n)` and `@DisplayName("Component: human description")`.
  Reference the GitHub issue in the display name or a comment when a test pins a specific
  bug, e.g. `"DataTable: GitHub #7193 global filter..."`.
- Group reusable setup/data helpers in an `Abstract<Component>Test` per component package
  (see `AbstractDataTableTest`), and cross-component table helpers in the root
  `org.primefaces.integrationtests.AbstractTableTest`.

## Anatomy of a test

Tests extend `AbstractPrimePageTest` (from the framework). Two equally valid styles exist
in the codebase — match the surrounding package:

**1. Page-object style** (preferred for new tests; declarative locators, auto-navigation):

```java
class Rating001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Rating: set value and cancel value using AJAX")
    void ajax(Page page) {              // page param is injected AND navigated to automatically
        Rating rating = page.ratingAjax;
        rating.setValue(4);
        assertEquals(4L, rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:ratingAjax") Rating ratingAjax;
        @FindBy(id = "form:msgs")       Messages messages;

        @Override
        public String getLocation() {
            return "rating/rating001.xhtml";   // relative to webapp root
        }
    }
}
```

Declaring an `AbstractPrimePage` subclass as a **test-method parameter** makes the
framework navigate to its `getLocation()` before the test runs. `@FindBy` fields (incl.
component wrappers like `Rating`, `DataTable`) are auto-populated.

**2. `goTo` + `createFragment` style** (handy for parameterized views):

```java
@ParameterizedTest
@MethodSource("provideXhtmls")          // runs the same assertions across view variants
void basicAndPaginator(String xhtml) {
    goTo(xhtml);
    DataTable dataTable = PrimeSelenium.createFragment(DataTable.class, By.id("form:datatable"));
    ...
}
```

### Conventions every test should follow

- **Arrange / Act / Assert** comment blocks — used consistently across the suite.
- **Guard every AJAX/navigation interaction.** Use the component wrapper methods (they
  guard internally) or wrap raw clicks with `PrimeSelenium.guardAjax(...)`. Never assert
  immediately after an un-guarded click — the request may still be in flight. See the
  framework's guard mechanism in [`primefaces-selenium/CLAUDE.md`](../primefaces-selenium/CLAUDE.md).
- **Assert the widget configuration** via a private `assertConfiguration(JSONObject cfg)`
  that reads `getWidgetConfiguration()` and calls `assertNoJavascriptErrors()`. This
  catches client-side JS errors that wouldn't otherwise fail the test.
- Prefer **component wrapper APIs** (`dataTable.sort("Name")`, `rating.setValue(4)`) over
  hand-rolled `WebElement` clicks; only drop to `WebElement`/`Actions` for behavior the
  wrapper doesn't expose (e.g. meta-click multisort in `DataTable003Test`).
- Tests in a class **share one instance** (`@TestInstance(PER_CLASS)` from the base) and
  run in `@Order`. Don't rely on cross-method state beyond what `@Order` guarantees; each
  method re-navigates.

## Adding a new test — checklist

1. **Pick the component package** under `src/main/webapp`, `src/main/java/.../integrationtests`,
   and `src/test/java/.../integrationtests`. If the component has no package yet, create
   all three (this is the common case for closing a coverage gap — see below).
2. **Create the view** `src/main/webapp/<component>/<component><NNN>.xhtml`. Use a single
   `<h:form id="form">`, give the component a stable `id` and `widgetVar`, and include
   `p:messages`/`p:commandButton`s the test needs to drive/observe behavior. Copy an
   existing view in the package as a template (license header + namespaces matter).
3. **Create the backing bean** (if the scenario needs server state)
   `src/main/java/.../integrationtests/<component>/<Component><NNN>.java`: `@Named
   @ViewScoped`, `implements Serializable`, Lombok `@Data`, seed data in
   `@PostConstruct init()`. Reuse an existing `*Service`/model where possible.
4. **Create the test** `src/test/java/.../integrationtests/<component>/<Component><NNN>Test.java`
   extending `AbstractPrimePageTest` (or a component `Abstract...Test`). Follow the
   anatomy above.
5. **Run it locally** (see below) and confirm `assertNoJavascriptErrors()` passes.
6. **MIT license header** is required on every `.java` (and is enforced) — copy it from a
   sibling file. Checkstyle (160-col, 4-space indent) applies to test sources too
   (`conf/checkstyle-tests.xml`).

## Running tests

From the repo root (profiles, not bare `mvn test` — surefire/failsafe are skipped by
default and only the `integration-tests` profile wires Failsafe up):

```bash
# Full suite, headless Chrome, Mojarra 4.0, parallel
mvn clean verify -f primefaces-integration-tests/pom.xml \
    -Pintegration-tests,parallel-execution,headless,chrome,mojarra-4.0

# Single test class (omit parallel/headless to watch the browser)
mvn verify -f primefaces-integration-tests/pom.xml \
    -Pintegration-tests,mojarra-4.0 -Dit.test=DataTable001Test

# Single method
... -Dit.test=DataTable001Test#basicAndPaginator

# Live app for manual debugging (Jetty), then open http://localhost:8080/integrationtests/
mvn clean jetty:run -Pmojarra-4.0 -f primefaces-integration-tests/pom.xml
```

### Profile axes (compose them)

- **Faces impl** (exactly one): `mojarra-4.0`, `mojarra-4.1`, `myfaces-4.0`, `myfaces-4.1`.
  Swapping these changes both the dependency and a filtered `facesListener` in `web.xml`.
- **Browser** (one): `chrome`, `firefox`, `safari` (`safari` forces serial execution).
- **Toggles**: `headless`, `parallel-execution`, `csp` (sets `primefaces.CSP=true`),
  `client-state-saving` (client state saving), `theme-saga` / `theme-nova`.

### How a run actually works

- `maven-failsafe-plugin` runs `*Test` classes in the integration-test phase.
- `BootstrapExtension` starts `TomcatDeploymentAdapter` once per JVM: it boots embedded
  Tomcat against the exploded WAR in `target/primefaces-integration-tests/`, on a random
  port, and pre-warms the Faces impl.
- `WebDriverExtension` opens/quits the browser per test class.
- Parallel mode runs **classes concurrently, methods serially** (`same_thread`), sized by
  `JUnit5Selenium4Strategy` (= CPU count). Keep tests within a class independent of other
  classes' state, but ordered methods within a class are fine.
- Config (timeouts, headless, screenshot dir) comes from
  `src/test/resources-filtered/primefaces-selenium/config.properties` (Maven-filtered).
- On failure, `ScreenshotOnFailureExtension` writes a PNG + context `.txt` to
  `screenshotDirectory` (defaults to `/tmp/pf_it/`, override via `SCREENSHOT_DIRECTORY`).

### `web.xml` is shared

All views run under one `src/main/webapp/WEB-INF/web.xml` (Maven-filtered). It enables
CSV (`CLIENT_SIDE_VALIDATION=true`), multi-view state, CSP/theme/state-saving via filtered
properties, and routes uncaught exceptions to `/error.xhtml`. A new view inherits all of
this — you don't configure per-view context-params; toggle behavior via profiles instead.

## Coverage: keeping it good

This is the whole point of the module, so think in terms of **which components and which
behaviors** are exercised, not just line count.

- First, check the existing coverage of a component. Pick components which are not covered yet.
  Or find relevant usage-scenarios of components that are not covered yet.
- **To close a "no coverage" gap**: confirm there's a matching component wrapper in
  `primefaces-selenium-components` (`org.primefaces.selenium.component`). If the wrapper is
  missing, it must be added there first (that's a framework change — see the selenium
  module). Then create the triad starting at `<Component>001`.
- **To deepen "shallow" coverage**: add new `<Component><NNN>` triads for distinct
  scenarios (AJAX vs non-AJAX, converters, disabled/readonly, keyboard interaction,
  validation, multi-instance, state restoration after update) rather than piling
  assertions into one view.
- **Good coverage for a component generally exercises**: initial render + widget config,
  the primary interaction (select/sort/filter/toggle/upload…), AJAX events firing the
  expected listeners (assert via `p:messages`), **state surviving an AJAX update**
  (a recurring bug class — see the "must not be lost after update" assertions in
  `DataTable001Test`), and `assertNoJavascriptErrors()`.
- **CI runs the suite across the Faces-impl matrix and with CSP** (see root `CLAUDE.md` →
  CI). Write tests that pass on both Mojarra and MyFaces and under CSP: don't rely on
  inline event handlers that CSP blocks, and don't assert impl-specific markup/IDs.

## Gotchas

- Don't add `mvn test` expecting tests to run — surefire is skipped; use the
  `integration-tests` profile + Failsafe (`*Test` naming, `-Dit.test=`).
- A view that 500s shows up as `/error.xhtml`; check the failure screenshot/context and
  the bean's EL wiring.
- Component IDs in assertions are namespaced by the form (`form:datatable`,
  `form:datatable:globalFilter`) — keep view IDs stable, tests bind to them by `@FindBy`
  /`By.id`.
- `safari` and `client-state-saving`/`csp` runs are the usual sources of
  env-specific flakiness; if a test only fails there, suspect CSP-blocked inline JS or
  state-saving timing before assuming a product bug.
