# Coding Rules

These rules apply to all code generated or modified by the agent.

## 1. Naming

- **Variables**: use clear, descriptive names that reflect the object they represent.
  - Good: `documents`, `userProfile`, `retryCount`
  - Bad: `data`, `temp`, `x`, `obj`
- **Collections**: use plural nouns for lists/arrays of objects.
  - Good: `documents`, `activeUsers`
  - Bad: `documentList`, `data1`
- **Booleans**: prefix with `is`, `has`, `can`, or `should`.
  - Good: `isLoading`, `hasPermission`
- **Functions**: name with a verb (or verb phrase) that describes the action taken, and keep the return type/intent obvious from the name.
  - Good: `fetchDocuments()`, `calculateTotalPrice()`, `isValidEmail()`
  - Bad: `handle()`, `process()`, `doStuff()`
- Avoid abbreviations unless they are widely understood (`id`, `url`, `db` are fine; `usrDoc` is not).
- Avoid single-letter names except for short-lived loop indices (`i`, `j`) in tight, obvious loops.

## 2. APIs and Libraries

- Never use deprecated APIs, methods, or classes.
- Before using any API, verify it is the current, non-deprecated version (check official docs/changelog if uncertain).
- If a deprecated API is encountered in existing code, replace it with its recommended modern equivalent — don't just leave a warning suppressed.
- Prefer stable/LTS APIs over experimental or preview ones unless explicitly requested.

## 3. General Code Quality

- Keep functions focused on a single responsibility.
- Keep naming consistent across the codebase — don't mix conventions (e.g., `docList` in one file and `documents` in another) for the same concept.
- Prefer explicit, self-documenting names over comments that explain what a poorly-named variable/function does.

## 4. Error Handling

- Handle errors explicitly — never swallow exceptions silently (no empty catch blocks).
- Fail loudly in development; fail gracefully (with proper logging/user feedback) in production.
- Validate inputs at boundaries (function entry points, API endpoints, form submissions) before acting on them.

## 5. Comments

- Comments should explain **why**, not **what** — the code itself should make the "what" obvious through good naming.
- Avoid comments that just restate the code (`// increment i` above `i++`).
- Keep comments up to date; remove or update stale comments when the code changes.

## 6. Formatting & Style

- Follow the project's existing linter/formatter configuration rather than introducing a new style.
- Match existing code patterns in the surrounding file/module.
- Keep indentation, spacing, and brace style consistent with the rest of the codebase.

## 7. Security

- Never hardcode secrets, API keys, tokens, or passwords — use environment variables or a secrets manager.
- Validate and sanitize all external input (user input, API responses, file contents).
- Avoid introducing known-vulnerable dependency versions.

## 8. Testing

- New logic should include or update corresponding tests.
- Don't break existing tests — run the test suite before submitting changes.
- Cover edge cases, not just the happy path.

## 9. Dependencies

- Don't add a new library when the standard library or an existing dependency already solves the problem.
- Check license compatibility before adding a new dependency.
- Prefer well-maintained, actively supported packages.

## 10. Commits

- Keep commits small and focused on a single change.
- Write clear commit messages that describe intent ("why"), not just a diff summary.

## 11. Enforcement

- Before submitting code, review variable and function names against the rules above.
- Before submitting code, confirm no deprecated APIs were introduced.
- Before submitting code, confirm tests pass and no secrets are hardcoded.
