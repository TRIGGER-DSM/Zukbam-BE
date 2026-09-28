# GitHub Copilot Instructions

## Language Rule
- **Always respond in Korean.**
- Regardless of the input language, always reply in Korean.

## Code Review Rule
- When reviewing code or proposing code changes, **a `suggestion` block must always be included.**
- Do not provide only explanations — **always include actual code changes using a `suggestion` block.**
- A `suggestion` block must contain **code only**.
- Any explanation must be written **outside** the `suggestion` block.

## Review Comment Format
Every review comment must follow this structure, in this exact order:

1. **Problem** — what is wrong and why it matters
2. **Suggestion** — how to fix it, followed by a `suggestion` block

### Template
~~~
**문제 상황**
<Describe the problem and its impact>

**제안**
<Describe the fix>

```suggestion
<code only>
```
~~~

### Rules
- Keep the Problem section concise (1–3 sentences) and explain the impact, not just the symptom.
- If multiple unrelated issues exist, write separate comments for each.