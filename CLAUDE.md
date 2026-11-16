# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

An IntelliJ Platform plugin adding language support for [TaskJuggler](https://taskjuggler.org/) project files (`.tjp`): lexer, parser/PSI, syntax highlighting, color settings page, and (early-stage) completion. Written in Java 21, built with the IntelliJ Platform Gradle Plugin 2.x against IntelliJ IDEA Community 2025.1 (`sinceBuild = 251`).

TaskJuggler language reference: https://taskjuggler.org/tj3/manual/index.html, also available locally via `tj3man` (`tj3man` lists all keywords; `tj3man <keyword>` shows syntax, context, attributes and examples for one keyword). Use it as the source of truth for syntax when extending the lexer/grammar.

## Commands

Gradle 9 runs on any JDK ≥ 17; compilation uses a Java 21 toolchain that Gradle downloads automatically (foojay resolver). This machine has no system JDK: use IntelliJ's bundled runtime, e.g. `export JAVA_HOME=~/.local/share/JetBrains/Toolbox/apps/intellij-idea/jbr`.

```bash
./gradlew build              # generate lexer/parser, compile, test, and package the plugin
./gradlew runIde             # launch a sandbox IDE with the plugin installed (also: .run/"Run IDE with Plugin")
./gradlew test               # run tests (IntelliJ test framework, JUnit 4/3 style)
./gradlew test --tests 'fr.ledcom.tj3.taskjugglerplugin.language.TJLexerTest'   # single test class
./gradlew verifyPlugin       # run the plugin verifier against recommended IDEs
./gradlew buildPlugin        # produce the distributable zip in build/distributions
```

## Generated code

Grammar sources live in `src/main/grammars/` (`TaskJuggler.flex`, `TaskJuggler.bnf`). The `generateTJLexer` / `generateTJParser` Gradle tasks (IntelliJ Platform Gradle Plugin's `grammarkit` subplugin) run before `compileJava` and write into `build/generated/sources/grammarkit/{lexer,parser}` — never commit generated code, and don't use the Grammar-Kit IDE actions to generate into `src/`. The two tasks need separate output roots because each purges its whole root.

Generated classes: `language.lexer.TaskJugglerLexer`, `TJParser`, `language.psi.TJTypes` (token/element type holder) and PSI interfaces/impls under `language.psi` / `language.psi.impl`.

## Architecture

All sources are under `src/main/java/fr/ledcom/tj3/taskjugglerplugin/`. Extensions are registered in `src/main/resources/META-INF/plugin.xml` under the language ID `TaskJuggler` (file type `TjFileType`, extension `tjp`).

Adding a new language construct typically touches several files in lockstep:

1. `TaskJuggler.flex` — define the token macro and a `<YYINITIAL>` rule returning `TJTypes.<TOKEN>`. Keyword rules must appear **before** the generic `ID` rule (JFlex picks the first rule on equal-length matches).
2. `TaskJuggler.bnf` — use the token and add/extend grammar rules (e.g. add to `task_body_element`). Token names in the BNF must match those returned by the lexer.
3. Rebuild (generation runs automatically).
4. `TJSyntaxHighlighter.getTokenHighlights` — map new token types to a `TextAttributesKey`; if a new key is added, expose it in `TJColorSettingsPage` (descriptors + demo text).
5. `psi/TJTokenSets` — update if the token is a comment/string/whitespace set used by `TJParserDefinition`.

Supporting resources:
- `src/main/resources/tj3/keywords.json` — keyword metadata (syntax, docs, allowed contexts/attributes, literal values) for every TaskJuggler keyword, loaded by `metadata.KeywordRegistry`. Generated, do not edit by hand: regenerate with `ruby tools/extract-keywords.rb > src/main/resources/tj3/keywords.json` (uses the locally installed TaskJuggler's syntax rules, the same data as `tj3man`). Keyword variants have ids like `account.task` (name `account`, valid inside `task`); the top level is the pseudo-context `properties` plus `project`.
- `src/main/resources/example.tjp` — sample TaskJuggler file useful for manual testing in `runIde`.
