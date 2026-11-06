# AGENTS.md / CLAUDE.md

This file provides guidance to Coding Agents like Claude Code (claude.ai/code) or Codex when working with code in this
repository (branch `13.X`, maintenance of PrimeFaces 13.0.x).

`CLAUDE.md` is a symlink to `AGENTS.md`, so both toolchains read the same text — always edit `AGENTS.md`.

## Project Overview

PrimeFaces 13.0.x is a JSF component library (`org.primefaces:primefaces-parent`, version in the root `pom.xml`). It is a
maintenance branch: fixes are usually backports from `master`, keep them minimal and keep public API (protected renderer
methods included) binary compatible.

| Module | Purpose |
|---|---|
| `primefaces` | Core library — Java components + JavaScript/CSS resources |
| `primefaces-showcase` | Demo WAR application |
| `primefaces-selenium` | Reusable Selenium test framework |
| `primefaces-integration-tests` | Selenium integration tests, JSF 2.x (`javax`) |
| `primefaces-integration-tests-jakarta` | The same tests for Jakarta Faces 4.0 |

## Differences to master

- `javax.faces` (JSF 2.2 / 2.3); the Jakarta jar is built from the same sources as the `jakarta` classifier.
- Java source/target 1.8, CI builds with Java 11. Builds with JDK 11–25 (JDK 25 needs Lombok 1.18.40+, otherwise
  javac fails with `ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN`).
- No frontend build: widgets are plain JavaScript in `primefaces/src/main/resources/META-INF/resources/primefaces/<component>/`.
- Classic `ui-*` CSS classes and themes (no design tokens, no `p-*` classes).
- No CDK: components are `*Base` + component classes written by hand.

## Build Commands

```bash
mvn clean install                                   # standard build (runs tests)
mvn clean install -Pquick                           # fast build (the profile only exists in primefaces/pom.xml)
mvn -o install -DskipTests -f primefaces/pom.xml    # only the core library
```

## Testing

```bash
# Unit tests
mvn clean test -f primefaces/pom.xml

# Single integration test class (javax, Mojarra 2.3)
mvn verify -f primefaces-integration-tests/pom.xml -Pintegration-tests,headless,chrome,mojarra-2.3 -Dit.test=Panel001Test
```

- Integration test Faces profiles: `mojarra-2.2`, `mojarra-2.3`, `myfaces-2.2`, `myfaces-2.3`, `myfaces-next-2.3`
  (`primefaces-integration-tests-jakarta`: `mojarra-4.0`, `myfaces-4.0`); `mojarra-4.0` does not exist in the javax module.
- Other profiles: `parallel-execution`, `headless`, `chrome`, `firefox`, `safari`, `csp`, `client-state-saving`,
  `theme-nova`, `theme-saga` (CI: `theme-saga,csp`).
- Integration tests use the installed PrimeFaces jar: run the core install above after changes in `primefaces/`.

**Showcase** (manual testing):
- Start from the module directory (Jetty 9 resolves `web-development.xml` relative to the working directory):
  `cd primefaces-showcase && mvn -B clean jetty:run -Pnon-ee,mojarra-2.3`, URL `http://localhost:8080/showcase/`.
- Port 8080 is shared with the master showcase and the integration tests' `jetty:run`; only one of them can run.
- Changes in `primefaces/` need the core install and a showcase restart.

## Code Style

- Checkstyle (`mvn validate`): `conf/checkstyle.xml`; max line length 160, 4 spaces, MIT license header in all sources.
- All sources in English; match the existing style of the file.
