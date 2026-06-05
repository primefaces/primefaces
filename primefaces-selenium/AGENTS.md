# primefaces-selenium

Guidance for Coding Agents working inside the `primefaces-selenium` module. Read the
root [`AGENTS.md`](../AGENTS.md) first for repository-wide conventions; this file
adds the detail specific to the Selenium test framework.

## Purpose

`primefaces-selenium` is a **reusable test framework**, not a test suite. It provides
the base classes, page-object/fragment infrastructure, AJAX/HTTP synchronization, and
ready-made component wrappers that real test suites (notably `primefaces-integration-tests`)
build on. It is published to Maven Central as a `test`-scoped dependency for downstream
PrimeFaces users who want to write Selenium tests against their own apps.

It is heavily inspired by Arquillian Graphene and supports JUnit 5 parallel execution.

## Module Layout

This is an aggregator (`pom` packaging) with two published JARs:

| Submodule | Artifact | Contents                                                                                                                                                                                              |
|---|---|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `primefaces-selenium-core` | `primefaces-selenium-core` | Framework engine: JUnit 5 extensions, page/fragment factories, WebDriver lifecycle, AJAX/HTTP guards, config, SPI hooks. Depends only on Selenium + JUnit + byte-buddy + org.json + webdrivermanager. |
| `primefaces-selenium-components` | `primefaces-selenium-components` | Component wrappers (e.g. `DataTable`, `DatePicker`, `SelectOneMenu`, …) under `org.primefaces.selenium.component`. Depends on `-core`.                                                                |

`-core` knows nothing about specific components; `-components` knows nothing about the
deployment/test runner. Keep that dependency direction intact.

## Key Concepts

### Page objects and fragments
- **`AbstractPrimePage`** — a page object. Subclass it, implement `getLocation()` (a path
  relative to the base URL), optionally override `getBaseLocation()`. `@FindBy` fields are
  auto-populated.
- **`AbstractPrimePageFragment`** — base for anything bound to a `WebElement` (it implements
  `WebElement` + `WrapsElement`). `getRoot()` lazily resolves the underlying element via its
  `ElementLocator`.
- **`AbstractComponent extends AbstractPrimePageFragment`** — base for all PrimeFaces component
  wrappers; adds widget helpers (`getWidgetByIdScript()`, `getWidgetConfiguration()` → `JSONObject`,
  `isAjaxified(event)`, `destroy()`). `AbstractInputComponent`, `AbstractTable`,
  `AbstractPageableData`, etc. specialize further.

### Injection / test lifecycle (`AbstractPrimePageTest`)
Tests extend `AbstractPrimePageTest`, which wires four JUnit 5 extensions:
- `BootstrapExtension` — once per JVM, starts the `DeploymentAdapter` (embedded container) and
  registers a shutdown hook.
- `WebDriverExtension` — creates the WebDriver `@BeforeAll`, quits it `@AfterAll`.
- `PageInjectionExtension` — resolves `AbstractPrimePage` **test-method parameters** (auto-navigates
  to them via `goTo`) and injects `@Inject`-annotated page **fields** (`javax.inject` or
  `jakarta.inject`, not navigated).
- `ScreenshotOnFailureExtension` — on failure, writes a PNG + `.txt` context to `screenshotDirectory`.

Class defaults: `@TestInstance(PER_CLASS)` and `@TestMethodOrder(OrderAnnotation.class)` — so use
`@Order(n)` and remember tests within a class share an instance.

### The AJAX/HTTP guard mechanism (the heart of the framework)
PrimeFaces tests must wait for AJAX/navigation to settle deterministically. The mechanism:
1. `onload.js` (in `-core` resources) is injected into the page (`OnloadScripts`) and installs a
   `window.pfselenium` object that monkey-patches `XMLHttpRequest.send`, `HTMLFormElement.submit`,
   and the PrimeFaces validation hook to track in-flight state (`xhr`, `submitting`, `navigating`,
   `validationFailed`).
2. **`PrimeSelenium.guardAjax(target)` / `guardHttp(target)`** wrap an element (or widget script) in
   a byte-buddy proxy (`Guard`). After the wrapped call, they block until the page is quiescent
   (`document.readyState === 'complete'`, `jQuery.active == 0`, `PrimeFaces.ajax.Queue` empty, no
   animation, `pfselenium` flags cleared) or the configured timeout fires.
3. `PrimeExpectedConditions` provides the reusable `ExpectedCondition` predicates these waits use.

**Rule:** any interaction that triggers a server round-trip must be guarded. Component wrappers do
this internally (see `CommandButton.click()` — it inspects `data-pfconfirmcommand`, AJAX behavior, and
`type=submit` to choose `guardAjax` vs `guardHttp` vs no guard, and exposes `clickUnguarded()` for
the `ajax="false"` download case). When you add a wrapper, follow the same detect-then-guard pattern
rather than sprinkling `Thread.sleep`.

### WebDriver management
- `WebDriverProvider` holds the driver in a **`ThreadLocal`** (enabling parallel execution) and
  decorates it with an `EventFiringDecorator` so onload scripts are re-injected after every
  navigation (`OnloadScriptsEventListener`) and optional scroll-into-view-before-click behavior.
- `DefaultWebDriverAdapter` builds Chrome/Firefox/Safari drivers, resolving the binary via
  `webdrivermanager` (Boni Garcia) unless a `webdriver.<x>.driver` system property is set. Headless
  uses a 1920×1080 window; Safari has no headless and gets various timing workarounds.

### `@FindByParentPartialId`
An alternative to `@FindBy` for fragment fields: locates a child by `parentId + value` (or by
`name`). Use `searchFromRoot=true` when the component is relocated in the DOM (`appendTo="..."`).

## Configuration

Driven by `config.properties` on the test classpath at `/primefaces-selenium/config.properties`
(read once by the singleton `ConfigProvider`). Every property can be overridden by a matching JVM
system property or environment variable, and values may use `${ENV_VAR}` placeholder syntax.

You **must** provide either `deployment.baseUrl` (remote app) or `deployment.adapter` (a
`DeploymentAdapter` implementation that starts/stops a local container) — otherwise `ConfigProvider`
throws at startup.

| Property | Default | Purpose |
|---|---|---|
| `deployment.baseUrl` | — | base URL for a remote app |
| `deployment.adapter` | — | `DeploymentAdapter` class to start/stop a local container |
| `webdriver.adapter` | `DefaultWebDriverAdapter` | custom `WebDriverAdapter` to build the driver yourself |
| `webdriver.browser` | — | `chrome` / `firefox` / `safari` |
| `webdriver.headless` | `false` | run headless |
| `webdriver.version` | newest | pin a driver version |
| `webdriver.logLevel` | `WARNING` | JUL level passed to the browser logs |
| `timeout.gui` | `2` | GUI wait (`waitGui`), seconds |
| `timeout.ajax` | `10` | AJAX guard, seconds |
| `timeout.http` | `10` | HTTP guard, seconds |
| `timeout.documentLoad` | `15` | document-load wait, seconds |
| `timeout.fileUpload` | `20` | file-upload wait, seconds |
| `disableAnimations` | `true` | inject CSS/jQuery to kill animations (stabilizes tests) |
| `scrollElementIntoView` | — | scroll-before-click `scrollIntoView` option (boolean/object) |
| `screenshotDirectory` | — | where failure screenshots/text go |
| `onloadScripts.adapter` | — | `OnloadScriptsAdapter` to register extra onload scripts |

A real example lives at
`primefaces-integration-tests/src/test/resources-filtered/primefaces-selenium/config.properties`
(uses `TomcatDeploymentAdapter` and `${...}` placeholders filtered by Maven).

## SPI / extension points

Implement these to plug the framework into an environment (all in `org.primefaces.selenium.spi`):
- `DeploymentAdapter` — `startup()` / `getBaseUrl()` / `shutdown()`. Canonical example:
  `primefaces-integration-tests` `TomcatDeploymentAdapter`.
- `WebDriverAdapter` — `initialize(config)` + `createWebDriver()` to fully control driver creation.
- `OnloadScriptsAdapter` — append custom JS to the onload bundle.

## Writing / changing a component wrapper

1. Put it in `org.primefaces.selenium.component` (HTML-only wrappers go under `.../component/html`).
   Extend the closest base (`AbstractComponent`, `AbstractInputComponent`, `AbstractTable`, …).
2. Wrappers are **abstract** classes — they are instantiated as byte-buddy proxies by the factories,
   never with `new`. Constructors must be the default no-arg (or `WebElement`-arg for `Select`-style).
3. Guard every server round-trip (see `CommandButton`). Detect AJAX via `isAjaxified(event)` /
   `ComponentUtils.hasAjaxBehavior(...)`; read widget config via `getWidgetConfiguration()`.
4. Prefer `ComponentUtils.sendKeys(input, value)` for typing — it sends one char at a time on Chrome
   to avoid dropped keystrokes.
5. Update the component list in [`README.md`](README.md) if you add a new public component.

## Usage pattern (consumer side)

```java
// Page object
public class IndexPage extends AbstractPrimePage {
    @FindBy(id = "form:manufacturer") private SelectOneMenu manufacturer;
    @Override public String getLocation() { return "index.xhtml"; }
}

// Test — page is injected and auto-navigated by PageInjectionExtension
class IndexPageTest extends AbstractPrimePageTest {
    @Test
    void select(IndexPage index) {
        index.getManufacturer().select("BMW"); // wrapper guards AJAX internally
        assertTrue(index.getManufacturer().isSelected("BMW"));
    }
}

// Ad-hoc fragment without a @FindBy field
InputText input = PrimeSelenium.createFragment(InputText.class, By.id("test"));
```

See `primefaces-integration-tests` (e.g. `DatePicker001Test`) for the canonical `static class Page
extends AbstractPrimePage` + `@Order`ed methods pattern.

## Building & gotchas

- Build: `mvn clean install -f primefaces-selenium/pom.xml` (or `-Pquick` to skip tests/checkstyle).
- This module is a framework — it has **no executable tests of its own**; you exercise changes by
  running `primefaces-integration-tests` against it (see the root `CLAUDE.md` integration-test
  commands).
- Checkstyle/license rules from the root apply: 160-char lines, 4-space indent, MIT header, English.
- `byte-buddy` proxying drives the page/fragment/guard magic. If you hit "Could not proxy class …
  missing constructor", the cause is almost always a non-default constructor on a wrapper/page.
- Don't introduce `Thread.sleep` for synchronization — use the guards / `PrimeExpectedConditions` /
  `PrimeSelenium.waitGui()`. `PrimeSelenium.wait(ms)` exists only for genuine unavoidable waits
  (e.g. Safari timing hacks).
