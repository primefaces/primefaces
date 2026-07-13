# Contributing to PrimeFaces

First of all, thank you for considering contributing to PrimeFaces!

PrimeFaces is now a community-driven, MIT-licensed open source project. Contributions of all sizes are welcome, whether you're fixing a typo, improving documentation, fixing bugs, adding tests, or implementing new features.

Please take a few minutes to review these guidelines before opening an issue or pull request.

## Code of Conduct

By participating in this project, you agree to abide by our Code of Conduct. Please help us keep the community welcoming, respectful, and inclusive for everyone.

## Before You Start

Before beginning work on a new feature or large change:

* Search existing issues and pull requests to avoid duplicate work.
* For significant changes, open an issue first so the community can discuss the proposed design.
* Keep pull requests focused on a single logical change whenever possible.

Small bug fixes and documentation improvements generally do not require prior discussion.

## Reporting Bugs

When reporting a bug, please include as much information as possible:

* PrimeFaces version
* Jakarta Faces implementation and version (Mojarra/MyFaces)
* Application server
* Java version
* Browser (if applicable)
* A minimal reproducer, preferably based on the PrimeFaces Showcase or a small GitHub repository
* Stack traces, screenshots, or recordings when applicable

Clear reproduction steps greatly increase the chances of a quick fix.

## Suggesting Features

Feature requests are welcome.

Please describe:

* The problem you're trying to solve
* Why the feature would benefit other users
* Any proposed API or implementation ideas (optional)

Well-explained use cases are much more valuable than implementation details.

## Development Workflow

1. Fork the repository.
2. Create a feature branch from `master`.
3. Make your changes.
4. Add or update tests where appropriate.
5. Ensure the project builds successfully.
6. Submit a pull request.

Please keep pull requests as focused as possible.

## Coding Guidelines

Please follow the existing code style and conventions used throughout the project.

When submitting code:

* Write clear, maintainable code.
* Avoid unrelated formatting-only changes.
* Update documentation when behavior changes.
* Include tests whenever practical.
* Keep public APIs backwards compatible unless the change is intentional and discussed.

## Commit Messages

Use clear and descriptive commit messages.

Examples:

```
Fix DataTable row selection with lazy loading

Improve TreeTable keyboard accessibility

Add support for Jakarta Faces 5
```

Small, logically grouped commits make reviews easier.

## Pull Request Checklist

Before submitting a pull request, please verify:

* [ ] The project builds successfully.
* [ ] Existing tests pass.
* [ ] New functionality includes tests where appropriate.
* [ ] Documentation has been updated if necessary.
* [ ] The pull request is focused on a single change.
* [ ] Commit messages clearly describe the changes.

## AI-assisted Contributions

Use of AI coding assistants is welcome.

If an AI tool materially contributed to a change, it is appreciated (but not required) to credit it in the commit message using a `Co-authored-by` trailer. Please include the model name and version whenever possible, as model versions differ in behavior over time.

Examples:

```
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
* **Accountability** — identifies the specific tool and model used instead of a generic "AI" label. Different models often produce different results.
* **Responsibility** — the human contributor remains responsible for every submitted change. Please review, understand, test, and stand behind any AI-generated code before submitting it.

AI assistance does not reduce the expectations for code quality, testing, documentation, or review.

## Licensing

By submitting a contribution, you agree that your contribution will be licensed under the project's MIT License.

## Thank You

PrimeFaces exists because of its community.

Whether you're reporting bugs, improving documentation, reviewing pull requests, answering questions, or contributing code, your help is greatly appreciated.
