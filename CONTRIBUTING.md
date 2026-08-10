# Contributing to PrimeFaces

Thank you for your interest in contributing to PrimeFaces!

PrimeFaces is a community-driven, MIT-licensed open source project. Contributions of all kinds are welcome, whether you're fixing a bug, improving documentation, adding tests, enhancing accessibility, improving performance, or implementing a new feature.

Please take a few minutes to review these guidelines before opening an issue or pull request.

## Reporting Bugs

Before opening a new issue, please search the existing issues to determine whether it has already been reported.

When reporting a bug, please include as much information as possible:

* PrimeFaces version
* Jakarta Faces implementation and version (Mojarra or MyFaces)
* Application server
* Java version
* Browser (if applicable)
* A minimal reproducer, preferably based on the PrimeFaces Showcase or a small GitHub repository
* Stack traces, screenshots, or recordings where appropriate

Clear reproduction steps significantly improve the chances of a quick resolution.

## Feature Requests

Feature requests are always welcome.

Please describe:

* The problem you're trying to solve.
* Why the feature would benefit the community.
* Any proposed API or implementation ideas (optional).

For larger features or API changes, we encourage opening an issue first to discuss the proposed design before investing significant implementation effort.

## Development Workflow

1. Fork the repository.
2. Create a feature branch from `master`.
3. Make your changes.
4. Add or update tests where appropriate.
5. Ensure the project builds successfully.
6. Submit a pull request.

Please keep pull requests focused on a single logical change whenever possible.

## General guidelines

Our commit guidelines mirror those of OpenStack:

* Only one logical change per commit.
* Do not mix whitespace changes with functional code changes.
* Do not mix unrelated functional changes.
* When writing a commit message:

  * Describe **why** a change is being made.
  * Do not assume the reviewer understands what the original problem was.
  * Do not assume the code is self-evident or self-documenting.
  * Describe any limitations of the current implementation.
* Keep pull requests focused on a single feature or bug fix.
* Follow the existing coding style and conventions throughout the project.
* Include tests whenever practical when fixing bugs or adding functionality.
* Review all code carefully before submitting, including any AI-generated code.

## Performance guidelines

PrimeFaces is a UI component library where performance matters. Please keep performance in mind when contributing.

Use index-based loops instead of enhanced `for-each` loops when iterating over `ArrayList`s, especially when traversing the Faces component tree.

See:
https://issues.apache.org/jira/browse/MYFACES-3130

Our loops usually look like:

```java
for (int i = 0; i < component.getChildCount(); i++) {
    UIComponent child = component.getChildren().get(i);
    ...
}
```

This has two benefits:

* Avoids creating an internal `List` instance when there are no children, as the list is lazily initialized by the Faces implementation (`component.getChildCount()` instead of `component.getChildren().size()`).
* Avoids creating a new iterator instance on each loop. While modern JVMs optimize this well, reducing object allocation also reduces GC pressure.

## Detailed Java code quality standards

* All code must compile and run on Java 11.
* All comments, class names, variable names, method names, log messages, and other identifiers must be in English.
* All new source files should include the standard PrimeFaces MIT license header where applicable.
* Follow the standard Java naming conventions for classes, methods, variables, and constants.
* Maximum line length is **160 characters**.
* Use **4 spaces** for indentation. Do not use tabs.
* Line endings should be UNIX (`\n`).
* All `.java` source files should contain only ASCII characters. All `.properties` files should use ISO-8859-1 encoding.
* Avoid unnecessary autoboxing and unboxing of primitive numeric types.
* Every class should define an explicit constructor, even when it is the default constructor, and include a call to `super()`.
* Use `/* ... */` comments to explain algorithms that involve non-trivial design decisions. Avoid comments that merely restate the code.
* All public classes and public methods intended for users should include comprehensive Javadoc.
* The project also defines additional coding rules in `checkstyle.xml`. Please ensure your code complies with these rules before submitting a pull request.

## Detailed HTML/XML code quality standards

* All tags, CSS classes, IDs, filenames, and other identifiers must be in English.
* Prefer lowercase for HTML/XML artifacts. The only exceptions are `DOCTYPE` and `CDATA`.
* All HTML should be XML-valid (properly closed tags, quoted attributes, etc.).
* Maximum line length is **160 characters**.
* Use **4 spaces** for indentation. Do not use tabs.
* Line endings should be UNIX (`\n`).
* All `.html` and `.xml` files should contain only ASCII characters.
* XHTML self-closing tags should include a space before `/>`.
* Inline JavaScript must be enclosed in a commented `<![CDATA[ ... ]]>` block where applicable.

## Pull Request Checklist

Before submitting your pull request, please verify:

* [ ] The project builds successfully.
* [ ] Existing tests pass.
* [ ] New functionality includes tests where appropriate.
* [ ] Documentation has been updated if necessary.
* [ ] The pull request addresses a single feature or bug fix.
* [ ] Commit messages clearly explain the purpose of the change.
* [ ] Code complies with the project's coding standards and Checkstyle rules.

## AI-assisted Contributions

The use of AI coding assistants is welcome.

If an AI tool materially contributed to a change, we encourage (but do not require) you to credit it in the commit message using a `Co-authored-by` trailer. Please include the model name and version whenever possible, since model behavior changes over time.

Examples:

```text
Co-authored-by: Claude Opus 4.8 <noreply@anthropic.com>

Co-authored-by: GitHub Copilot (GPT-5) <copilot@github.com>

Co-authored-by: Cursor (Claude Sonnet 4.5) <cursoragent@cursor.com>

Co-authored-by: Devin AI 2.0 <158243242+devin-ai-integration[bot]@users.noreply.github.com>

Co-authored-by: OpenAI Codex (GPT-5-Codex) <codex@openai.com>

Co-authored-by: Gemini 2.5 Pro <gemini@google.com>

Co-authored-by: Grok 4 <grok@x.ai>
```

Use whatever model and version your tool actually reports.

### Why we ask for this

* **Traceability** — makes it easier to identify AI-assisted changes when investigating regressions, security issues, or licensing questions.
* **Accountability** — identifies the specific tool and model used instead of a generic "AI" label.
* **Transparency** — helps future contributors understand how changes were produced.
* **Responsibility** — the human contributor remains responsible for every submitted change. Review, understand, test, and stand behind any AI-generated code before submitting it.

AI-generated code is expected to meet the same performance, style, testing, documentation, and review standards as any manually written contribution.

## Code Reviews

All contributions are reviewed by project maintainers.

During review, you may be asked to:

* Improve documentation.
* Add or update tests.
* Revise the implementation.
* Address review comments.

Code review is a collaborative process intended to improve the quality, maintainability, and long-term health of PrimeFaces.

## License

By submitting a contribution, you agree that your contribution will be licensed under the project's MIT License.

## Thank You

PrimeFaces has been successful because of its community.

Whether you're reporting bugs, answering questions, improving documentation, reviewing pull requests, or contributing code, your help is greatly appreciated.

Thank you for helping make PrimeFaces better for everyone!
