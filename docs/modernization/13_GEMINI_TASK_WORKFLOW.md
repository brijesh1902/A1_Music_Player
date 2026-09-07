# 13_GEMINI_TASK_WORKFLOW.md - AI Coding Task Workflow

Gemini must strictly follow this 10-step workflow for every coding/migration task:

```text
1. Understand      -> Comprehend user request and reference modernization docs.
2. Inspect         -> Read relevant files, XML layouts, and dependencies using read_file.
3. Impact Analysis -> Identify affected layers, potential side effects, and risks.
4. Plan            -> Outline the smallest safe change.
5. Confirm         -> Validate assumptions with the user if ambiguous.
6. Implement       -> Apply edits using surgical tools (replace_file_content / write_file).
7. Compile         -> Run gradle build / verification.
8. Test            -> Execute unit/UI tests.
9. Review          -> Verify that existing behavior was preserved.
10. Report         -> Summarize changes, preserved behavior, and next steps.
```
