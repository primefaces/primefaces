# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PrimeFaces is a JSF/Faces component library for Java web applications. It is a multi-module Maven project combining Java backend components and TypeScript/JavaScript frontend code. Current version is `16.0.0-SNAPSHOT` (`org.primefaces:primefaces-parent`).

## Module Structure

| Module | Purpose |
|---|---|
| `primefaces` | Core library — Java Faces components + TypeScript/CSS frontend |
| `primefaces-themes` | SASS theme compilation (Nova, Saga, etc.) via dart-sass |
| `primefaces-cdk` | Component Development Kit — annotation processor and Maven plugin for code generation |
| `primefaces-showcase` | Demo WAR application |
| `primefaces-selenium` | Reusable Selenium test framework (base classes for component testing) |
| `primefaces-integration-tests` | Selenium-based browser integration tests |
| `primefaces-cli` | Command-line utilities |
| `primefaces-coverage` | Code coverage aggregation (JaCoCo) |

## Build Commands

```bash
# Standard build (runs tests)
mvn clean install

# Fast build — skips tests, javadoc, checkstyle
mvn clean install -Pquick

# Parallel + minified production build
mvn clean install -T1C -Pminify

# Build only the core library module
mvn clean install -f primefaces/pom.xml
```

## Testing

**Unit tests** (JUnit 5 + Mockito, located in `primefaces/src/test/java`):
```bash
mvn clean test -f primefaces/pom.xml
```

**Integration tests** (Selenium + Embedded Tomcat + Weld):
```bash
# Run all integration tests (headless Chrome, Mojarra 4.0)
mvn clean verify -f primefaces-integration-tests/pom.xml -Pintegration-tests,parallel-execution,headless,chrome,mojarra-4.0

# Run a single integration test class
mvn verify -f primefaces-integration-tests/pom.xml -Pintegration-tests,parallel-execution,mojarra-4.0 -Dit.test=DatePicker001Test

# Run integration tests as a live web app for manual debugging
mvn clean jetty:run -Pmojarra-4.0 -f primefaces-integration-tests/pom.xml
# Then open http://localhost:8080/integrationtests/
```

**Integration test profiles:**
- Faces impl: `mojarra-4.0`, `mojarra-4.1`, `myfaces-4.0`, `myfaces-4.1`
- Browser: `chrome`, `firefox`, `safari`
- Options: `headless`, `csp` (Content Security Policy), `client-state-saving`
- Screenshots on failure are written to `/tmp/pf_it/` (override with `SCREENSHOT_DIRECTORY`)

## Linting & Code Style

```bash
# Run Checkstyle validation (bound to the validate phase)
mvn validate
```

- Rules: `conf/checkstyle.xml` (main), `conf/checkstyle-tests.xml` (tests)
- Max line length: 160 characters; indentation: 4 spaces (no tabs)
- MIT license header required in all source files (`conf/header.txt`), enforced by `license-maven-plugin`
- No compilation warnings allowed (`showWarnings`/`showDeprecation` are on); all source must be in English
- `biome.json` configures Biome for the JS/TS sources

## Frontend (TypeScript/JavaScript)

**Location:** `primefaces/src/main/frontend/`

**Toolchain:** Yarn 4 + ESBuild + TypeScript — Node.js and Yarn are downloaded automatically by `frontend-maven-plugin` during the Maven build (Node v22, Yarn v1.22).

```bash
# From primefaces/src/main/frontend/
yarn install
yarn run build:bundle   # ESBuild bundle
yarn tsc -b             # TypeScript type check only
```

**Architecture:**
- Source is organized under `packages/` — one TypeScript package per output `.js`/`.css` file bundled into the JAR.
- TypeScript project references manage inter-package dependencies.
- Packages share state via `window` globals (intentional pattern for JSF resource loading).
- ESBuild plugins rewrite CSS image references to JSF EL resource expressions.
- Set `NODE_ENV=production` for minified output; default is `development`.

Key frontend dependencies: jQuery 4, jQuery UI 1.14, Chart.js 4, FullCalendar 5, Moment.js, Quill 2.

## Code Generation (CDK)

The `primefaces-cdk` annotation processor runs at compile time to generate `*Impl` classes and taglib XMLs from annotations on component classes (annotations live in `primefaces-cdk/primefaces-cdk-api`):
- `@FacesComponentBase`, `@FacesBehaviorBase` — mark base classes
- `@Property`, `@Facet` — declare component attributes and facets

Do not edit generated `*Impl` files directly; edit the annotated base class instead. Note that `maven-compiler-plugin` runs with `<proc>full</proc>` (annotation processing on).

## Architecture Notes

- **Java target:** 11 (source and bytecode); project builds under Java 11–25.
- **Faces compatibility:** Jakarta Faces 4.0 / 4.1 (Mojarra and MyFaces both supported).
- The main library JAR bundles all frontend resources; no separate CDN step is needed for development.
- The showcase WAR (`primefaces-showcase`) is the canonical example of component usage.
- Release/publishing is handled via `jreleaser-maven-plugin` (Maven Central / Sonatype); the `release` profile stages to `target/staging-deploy`.

## CI

- **Build CI:** `.github/workflows/build.yml` (Continuous Integration) — triggered on push/PR. Runs `mvn clean install -T1C -Pminify` over a Java 11 + 25 matrix, **and** an integration-test matrix (Java 11/17/25 across `mojarra-4.0/4.1` and `myfaces-4.0/4.1`, headless Chrome, theme-saga, CSP). SonarCloud runs on `master` with Java 25.
- **Nightly integration tests:** `.github/workflows/nightly.yml` — daily at 06:00 UTC, broader matrix over Java versions, Faces impls, browsers, and theme combinations.
