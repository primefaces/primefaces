# AGENTS.md / CLAUDE.md

This file provides guidance to Coding Agents like Claude Code (claude.ai/code) or Codex when working with code in this repository.

`CLAUDE.md` is a symlink to `AGENTS.md` (both at the repo root and in the modules below), so both toolchains read the same text — always edit `AGENTS.md`.

## Project Overview

PrimeFaces is a JSF/Faces component library for Java web applications. It is a multi-module Maven project combining Java backend components and TypeScript/JavaScript frontend code. Current version is `17.0.0-SNAPSHOT` (`org.primefaces:primefaces-parent`).

## Module Structure

| Module | Purpose |
|---|---|
| `primefaces` | Core library — Java Faces components + TypeScript/CSS frontend, incl. the design token themes (`src/main/frontend/themes`, see its README) |
| `primefaces-cdk` | Component Development Kit — annotation processor and Maven plugin for code generation |
| `primefaces-showcase` | Demo WAR application |
| `primefaces-selenium` | Reusable Selenium test framework (base classes for component testing) |
| `primefaces-integration-tests` | Selenium-based browser integration tests |
| `primefaces-cli` | Command-line migration helpers (PrimeFlex, Tailwind, grid CSS), picocli-based |
| `primefaces-coverage` | Code coverage aggregation (JaCoCo) |

See [`primefaces-selenium/AGENTS.md`](primefaces-selenium/AGENTS.md) and [`primefaces-integration-tests/AGENTS.md`](primefaces-integration-tests/AGENTS.md) for details on how to write tests.

Non-module directories: `conf/` (Checkstyle configs, MIT license header, formatter/minify config), `docs/` (versioned user documentation site, current: `docs/17_0_0`).

## Instructions for Agents

### Goal

Help with this repository by making the smallest correct change that solves the
requested task.

### Defaults

- Stay within the requested scope
- Prefer the simplest working fix over broad rewrites
- Match existing code style and project structure
- Do not edit unrelated files just because they could be improved
- Ask before destructive or irreversible actions
- Do not claim tests passed unless you actually ran them
- Do not invent requirements, endpoints, configs, or results

### Working style

- Restate the task briefly before major edits
- Surface assumptions when they affect behavior
- Show what changed and why
- Keep diffs easy to review
- Stop and ask if the task expands into a refactor or redesign

### Uncertainty

- Flag low-confidence conclusions explicitly
- Prefer "I need to verify X" over guessing
- When there are multiple reasonable paths, present the trade-off

### Memory

- TODO / not defined yet

[//]: # (some ideas here )

[//]: # (- Read `DECISIONS.md` before making architectural changes)

[//]: # (- Read `KNOWN_ISSUES.md` before debugging recurring failures)

[//]: # (- Update `SESSION.md` with current status when work stops mid-task)

[//]: # (- Treat pinned project facts as constraints, not suggestions)

### Guardrails

- Never hide uncertainty behind polished wording
- Never change public contracts without calling it out
- Never mix requested work with opportunistic cleanup unless asked

## Build Commands

```bash
# Standard build (runs tests)
mvn clean install

# Fast build — skips tests, javadoc/sources, checkstyle, license and VDL steps
# (`quick` applies to all modules, primefaces/pom.xml adds its own skips; `minify` only affects that module)
mvn clean install -Pquick

# Parallel + minified production build
mvn clean install -T1C -Pminify

# Build only the core library module
mvn clean install -f primefaces/pom.xml
```

## Testing

**Unit tests** (JUnit Jupiter 6.x + Mockito 5, located in `primefaces/src/test/java`):
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
# Then open http://localhost:8080/ (a directory listing, e.g. http://localhost:8080/datatable/dataTable001.xhtml)
```

**Showcase** (manual testing):
- Start: `mvn -B -f primefaces-showcase/pom.xml clean jetty:run -Pnon-ee,mojarra-4.0`, URL `http://localhost:8080/`
  (same port as the integration tests' `jetty:run`, so only one of them can run).
- It is often already running: check `curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/index.xhtml` (200/302 =
  up; the integration tests have no `index.xhtml`, so they answer 404) before starting it, never stop a running one. Poll every 2s for at most 2 minutes, then read the jetty log instead of waiting.
- XHTML in `src/main/webapp` is served from source (just reload); Java classes and the PrimeFaces jar need a restart (no
  automatic redeployment). Changes in `primefaces/` need `mvn -B -o install -Pquick -f primefaces/pom.xml` first.

**Integration test profiles:**
- Faces impl: `mojarra-4.0`, `mojarra-4.1`, `myfaces-4.0`, `myfaces-4.1`
- Browser: `chrome`, `firefox`, `safari` (`safari` forces serial execution and excludes tests tagged `SafariExclude`)
- Options: `headless`, `parallel-execution`, `csp` (Content Security Policy), `client-state-saving`
- Theme: `theme-aura` (default, CI), `theme-lara`, `theme-nora`, `theme-metal`, `theme-glass`
- Screenshots on failure are only written when `screenshotDirectory` is configured; CI sets `SCREENSHOT_DIRECTORY=/tmp/pf_it/`

## Linting & Code Style

```bash
# Run Checkstyle validation (bound to the validate phase)
mvn validate
```

- Rules: `conf/checkstyle.xml` (main), `conf/checkstyle-tests.xml` (tests)
- Max line length: 160 characters; indentation: 4 spaces (no tabs)
- MIT license header required in all source files (`conf/header.txt`), enforced by `license-maven-plugin`
- Don't introduce compilation warnings (`showWarnings`/`showDeprecation` are on, but the build does not fail on them); all sources must be in English

## Frontend (TypeScript/JavaScript)

**Location:** `primefaces/src/main/frontend/`

**Toolchain:** Yarn 4 (Plug'n'Play, checked in as `.yarn/releases/yarn-4.9.2.cjs`) + ESBuild + TypeScript — Node.js and Yarn are downloaded automatically by `frontend-maven-plugin` during the Maven build (pinned in `primefaces/pom.xml`: Node v22.21.1, Yarn v1.22.22, which hands over to Yarn 4 via `yarnPath` in `.yarnrc.yml`).

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
- CSS: design tokens only (`var(--p-...)`, no literal colors), in cascade layers; the themes (token values per preset)
  are in `themes/` and built to `primefaces-<theme>/theme.css`. Conventions and structure: `themes/README.md`;
  `yarn run build:themes:check` enforces them (also part of `build:bundle`).
- A widget in its own package (e.g. `packages/knob/knob`) keeps its CSS there (`index.css` -> `<package>/<name>.css`, added
  as `@ResourceDependency` of the component), only the widgets of `components.js` go into `components.css`.
- Minified output unless `NODE_ENV=development`: the Maven build passes `development` by default and `production` with
  `-Pminify`; running `yarn run build:bundle` directly without `NODE_ENV` builds minified.

Key frontend dependencies: jQuery 4 (no jQuery UI: drag and drop, sorting and resizing are PrimeFaces' own pointer event interactions `PrimeFaces.interactions` in `packages/core/src/interaction`; widgets call them directly, there are no jQuery plugins `$.fn.draggable`, ... anymore), Chart.js 4, FullCalendar 5, Moment.js, Quill 2.

## Code Generation (CDK)

The `primefaces-cdk` annotation processor runs at compile time to generate `*Impl` classes from annotations on component classes; the taglib XML is generated afterwards from the compiled classes by the CDK Maven plugin (goal `generate-taglib`, phase `process-classes`). The annotations live in `primefaces-cdk/primefaces-cdk-api`:
- `@FacesComponentBase`, `@FacesBehaviorBase`, `@FacesConverterBase`, `@FacesValidatorBase` — mark base classes
- `@Property`, `@Facet` — declare component attributes and facets

Do not edit generated `*Impl` files directly; edit the annotated base class instead. Note that `maven-compiler-plugin` runs with `<proc>full</proc>` (annotation processing on).

## Architecture Notes

- **Java target:** 17 (source and bytecode); CI builds and tests on Java 17, 21 and 25.
- **Faces compatibility:** Jakarta Faces 4.0 / 4.1 (Mojarra and MyFaces both supported).
- The main library JAR bundles all frontend resources; no separate CDN step is needed for development.
- The showcase WAR (`primefaces-showcase`) is the canonical example of component usage.
- Release/publishing is handled via `jreleaser-maven-plugin` (Maven Central / Sonatype); the `release` profile stages to `target/staging-deploy`.

## CI

- **Build CI:** `.github/workflows/build.yml` (push/PR) — builds on Java 17 and 25, then runs the integration tests headless in Chrome with `theme-aura,csp` with Mojarra and MyFaces: 4.0 on Java 17, 4.1 on Java 21, both on Java 25
- **Nightly integration tests:** `.github/workflows/nightly.yml` (daily at 06:00 UTC, broader matrix over Java versions, Faces impls, browsers, and theme combinations), plus `nightly-safari.yml` and `nightly-file.upload.yml`
- **Publishing:** `publish-snapshot.yml`, `publish-vdldoc.yml`, `deploy-showcase.yml`, `release.yml`
