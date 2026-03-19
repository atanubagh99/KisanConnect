# Directives — Layer 1 (SOPs)

This directory contains **Standard Operating Procedures** (SOPs) written in Markdown.

Each directive defines:

- **Objective** — what the task accomplishes
- **Inputs** — what data/context is needed
- **Tools/Scripts** — which execution scripts or services to use
- **Outputs** — what the expected result looks like
- **Edge Cases** — known failure modes and how to handle them
- **Acceptance Criteria** — how to verify the task was done correctly

## Template

```markdown
# Directive: [Task Name]

## Objective

[What this SOP accomplishes]

## Inputs

- [Input 1]
- [Input 2]

## Execution

1. [Step 1] → call `execution/ScriptName.java`
2. [Step 2] → call `ServiceName.methodName()`

## Outputs

- [Expected output]

## Edge Cases

- [Failure scenario] → [How to handle]

## Acceptance Criteria

- [ ] [Criterion 1]
- [ ] [Criterion 2]
```
