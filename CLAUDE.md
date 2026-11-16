# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

An IntelliJ Platform plugin adding language support for [TaskJuggler](https://taskjuggler.org/) project files (`.tjp`, `.tji`): highlighting, completion, navigation/rename, quick docs, inspections, formatter, structure view, and `tj3` integration (syntax check, run configuration). Java 21, IntelliJ Platform Gradle Plugin 2.x, targets IntelliJ IDEA 2025.1+ (`sinceBuild = 251`).

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

Sources are under `src/main/java/fr/ledcom/tj3/taskjugglerplugin/`; all extensions are registered in `src/main/resources/META-INF/plugin.xml` (language id `TaskJuggler`).

**Metadata drives everything.** `metadata.KeywordRegistry` loads `src/main/resources/tj3/keywords.json`, generated from the installed TaskJuggler's syntax rules by `ruby tools/extract-keywords.rb > src/main/resources/tj3/keywords.json` (never edit by hand). Each keyword *variant* has an id like `account.task` (name `account`, valid inside `task`), its allowed contexts, attributes, literal values, syntax and HTML docs. The top level is the pseudo-context `properties` plus `project`. The lexer, parser, completion, docs and inspections all use it, so supporting a new TaskJuggler version is mostly re-running the script.

**Tolerant parser.** `TaskJuggler.flex` mirrors TaskJuggler's `ProjectFileScanner.rb` (keywords = words known to the registry; a word after `.`/`!` is always `ID`). `TaskJuggler.bnf` parses every statement as `head arguments* block?`; since TaskJuggler statements have no terminator, `language.parser.TJParserUtil.argumentAllowed` decides where a statement's arguments end (keyword that can't be an argument of the current head, or a word starting a new line that doesn't continue a list/expression). Hand-written PSI behaviour lives in mixins (`language.psi.impl.*Mixin`) exposed through `TJStatementBase`, `TJIdRefBase`, `TJLiteralBase`. `TJParserTest` parses `/usr/share/doc/tj3/examples` when present and must stay error-free.

**Semantics.** `language.semantics.TJSemantics` answers what a statement declares (`PropertyKind`: task, resource, account, shift, scenario, report, macro, flag, user attribute...) and what an identifier refers to, based on keyword variant ids. `language.references` builds on it:
- `TJDeclarations` collects declarations per file (cached) with full ids (dotted for tasks/reports, flat for resources/accounts/shifts/scenarios/flags/macros, as in TaskJuggler).
- `TJIncludeGraph` scopes lookups to one TaskJuggler project (main file + transitive `include`s); several projects can coexist in one IDE project.
- `TJPropertyReference` (one per id segment; `!` = one level up from the enclosing task, plain ids are absolute) and `TJNameReference` (macros, `scenario:` prefixes, user attributes in statement heads). Declared ids (`TJIdRef` in declaration position) are the named elements for find usages/rename.

Other packages: `highlighting` (lexer colours + `TJHighlightingAnnotator` semantic colours), `completion`, `documentation`, `inspections` (tested to report nothing on the TaskJuggler examples), `editor`, `formatter`, `structure`, `tj3` (settings, `tj3 --check-syntax` external annotator, run configuration, new-file action).

Tests use the IntelliJ test framework (`BasePlatformTestCase`, `ParsingTestCase`, `LexerTestCase`); expected parser/lexer outputs live in `src/test/testData` and are created on first run when missing.
