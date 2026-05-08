# Contributing

Thanks for considering a contribution. The repo is primarily a personal portfolio, but PRs that fix bugs, improve clarity, or add coverage are welcome.

---

## Branch naming

Use a short, descriptive prefix matching the kind of change:

| Prefix       | When                                                       |
| ------------ | ---------------------------------------------------------- |
| `feat/`      | new functionality (page, client, test, profile)            |
| `fix/`       | bug fix in existing code                                   |
| `refactor/`  | restructuring without behavior change                      |
| `docs/`      | README / docs / comments                                   |
| `chore/`     | build, deps, tooling, formatting                           |
| `ci/`        | GitHub Actions workflow changes                            |

Examples: `feat/web-login-test`, `fix/api-base-url-fallback`, `refactor/loghelper-step-nesting`.

Avoid prefixes like `wip/` or unscoped names like `kenny-changes`.

---

## Commit messages

This repo follows [Conventional Commits](https://www.conventionalcommits.org/) loosely. Format:

```
<type>(<scope>): <subject>

<body>
```

- **type**: `feat`, `fix`, `refactor`, `docs`, `chore`, `ci`, `test`, `style`, `perf`.
- **scope** (optional but encouraged): `mobile`, `web`, `api`, `core`, `config`, `reporting`, `build`, `reliability`.
- **subject**: imperative mood, ≤72 characters, no trailing period. "add", "fix", "remove" — not "added", "fixed".
- **body**: explain the *why*, not the *what*. The diff already shows what changed.

Example:

```
fix(web): dismiss cart modal before navigation

ProductsPage.addProductToCartByIndex was clicking the global nav link
while AE's #cartModal was still rendered, intercepting the click.
Wait for the modal and dismiss via 'Continue Shopping' before returning.

Smoke run was deterministically failing on this; now green.
```

Do **not** include AI attribution lines (`Co-Authored-By: Claude`, `Generated with ...`). The `commit-msg` hook in `.githooks/` rejects these — install it via `bash scripts/install-hooks.sh` after clone.

---

## Pull request template

When opening a PR, the description should answer:

1. **What changed?** One sentence. The PR title is part of this — make the title carry weight.
2. **Why?** What user-facing or developer-facing problem does this solve?
3. **How was it tested?** Smoke run output, manual reproduction steps, or "compile-only — exercised via existing CI".
4. **Anything reviewers should look at first?** A specific file, a tradeoff you weren't sure about, a TODO that's intentional.

Linking the relevant issue (`Closes #N`) is preferred but not mandatory.

---

## Code review checklist

Reviewers look for, in roughly this order:

- [ ] **Compile + smoke green.** CI must pass on the PR. If it doesn't, address before requesting re-review.
- [ ] **Scope discipline.** Does the PR do one thing? Or did it grow opportunistic cleanups? Latter should be split.
- [ ] **No dead code.** Removed code is removed; commented-out blocks belong in git history, not in source.
- [ ] **No assertions in page objects.** Pages return data or perform actions. Tests assert. (See [ARCHITECTURE.md ADR-2](docs/ARCHITECTURE.md#adr-2--no-pagefactory-in-page-objects).)
- [ ] **No `System.out.println` in production code.** Use SLF4J. Test code is allowed to use `System.out` for ad-hoc debugging only.
- [ ] **Locators preferred CSS over XPath.** XPath is acceptable when CSS can't express the selector (text matching, axis traversal).
- [ ] **`@Step` on every public page-object / API-client method.** Allure trace readability matters.
- [ ] **New tests are tagged.** `@Test(groups = {"layer", "smoke|regression"})` — never untagged.
- [ ] **Test data via Datafaker.** Hardcoded user data only for static fixtures (e.g. payment card numbers on a demo site).
- [ ] **Cleanup is `alwaysRun=true`.** `@AfterMethod(alwaysRun=true)` so cleanup fires even after a setup failure.
- [ ] **No secrets committed.** Credentials → env vars or `${VAR}` placeholders. `BROWSERSTACK_USERNAME`, `BROWSERSTACK_ACCESS_KEY`, `UDID` are the canonical examples.

---

## Before opening a PR

```bash
mvn clean compile             # compile main
mvn clean test-compile        # compile tests
mvn spotless:check            # formatting
mvn test -P api -Dgroups=smoke  # API smoke
mvn test -P web -Dgroups=smoke -Dheadless=true  # web smoke
```

If any of the four checks fails, fix locally before pushing. CI will run the same gates on PR.

---

## Questions?

Open an issue. Include enough detail that the question can be answered without re-reading the entire PR.
