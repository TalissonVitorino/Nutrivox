---
name: Use UV for Python
description: User requires uv to run python commands, not bare python/python3
type: feedback
---

Always use `uv run python` instead of `python` or `python3` for running Python scripts.

**Why:** User's environment requires uv as the Python runner.
**How to apply:** Any time you need to execute Python, prefix with `uv run`.
