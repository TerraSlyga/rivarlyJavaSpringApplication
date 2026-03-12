# Linting Guide

## Selected linter
Checkstyle 3.3.0 — chosen due to native integration with Maven and wide
use in the Java community.

## Basic rules
- NamingConventions — class/method names in camelCase/PascalCase
- MethodLength (max: 50) — methods no longer than 50 lines
- MagicNumber — prohibit numeric literals
- UnusedImports — prohibit unused imports

## Running
mvn checkstyle:check # check
mvn checkstyle:checkstyle # generate HTML report

## Git Hooks
The pre-commit hook automatically runs Checkstyle before each commit.
Install: cp scripts/pre-commit .git/hooks/ && chmod +x .git/hooks/pre-commit

## Build integration
Checkstyle runs during the `verify` phase: mvn verify

## Static typing
SpotBugs analyzes bytecode for potential bugs (null-pointer, resource leaks).
Run: mvn spotbugs:check

## Comprehensive checking
scripts\check-code.bat