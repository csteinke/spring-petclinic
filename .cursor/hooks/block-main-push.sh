#!/bin/bash
# VIS-142 demo guardrail (beforeShellExecution hook).
# Blocks "git push" to main and any force push. Bash, sed -E, and grep -E only, so it runs on macOS and Linux.
# Cursor sends JSON on stdin, for example {"command":"git push origin main","cwd":"...","sandbox":false}
input=$(cat)
field() { printf '%s' "$input" | sed -nE "s/.*\"$1\"[[:space:]]*:[[:space:]]*\"(([^\"\\\\]|\\\\.)*)\".*/\\1/p"; }
cmd=$(field command)
cwd=$(field cwd)
deny() {
  echo '{"permission":"deny","user_message":"Blocked by team policy: no pushes to main and no force pushes. Push your branch and open a pull request.","agent_message":"This command was blocked by a project hook. Do not push to main or force push. Push the feature branch and open a pull request instead."}'
  exit 0
}
if printf '%s' "$cmd" | grep -Eq '(^|[;&|[:space:]])git[[:space:]]+push'; then
  # Explicit main target, or a force flag.
  if printf '%s' "$cmd" | grep -Eq '([[:space:]:/+]main([[:space:]]|$))|--force|[[:space:]]-f([[:space:]]|$)'; then
    deny
  fi
  # Bare "git push" while the current branch is main.
  branch=$(git -C "${cwd:-.}" branch --show-current 2>/dev/null)
  if [ "$branch" = "main" ] && ! printf '%s' "$cmd" | grep -Eq 'git[[:space:]]+push[[:space:]]+[^-[:space:]]+[[:space:]]+[^-[:space:]]+'; then
    deny
  fi
fi
echo '{"permission":"allow"}'
exit 0
