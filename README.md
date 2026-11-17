# TaskJuggler plugin for IntelliJ IDEA

Language support for [TaskJuggler](https://taskjuggler.org/) project files (`.tjp`, `.tji`) in IntelliJ IDEA and
other IntelliJ Platform IDEs (2026.2 and later).

## Features

- **Highlighting**: keywords, declarations, references, literal values (`asap`, `html`, column names...), functions,
  macros, dates, durations and strings, including `-8<-` ... `->8-` blocks. Colours are configurable in
  *Settings | Editor | Color Scheme | TaskJuggler*.
- **Completion**: only the keywords valid in the current block, attribute values, and references to tasks,
  resources, accounts, shifts, scenarios, flags and macros.
- **Live templates**: `project`, `task`, `taske`, `milestone`, `resource`, `account`, `journal`, `macro`,
  `taskreport`, `resourcereport`, `ml`.
- **Navigation**: go to declaration, find usages, rename and *Go to Symbol* for tasks, resources, accounts, shifts,
  scenarios, reports, flags, macros and user-defined attributes. Task ids follow TaskJuggler's rules (dotted ids,
  `!` for the enclosing task). A project's main file and all the files it `include`s are treated as one project, so
  several TaskJuggler projects can live in the same IDE project.
- **Quick documentation** (`Ctrl+Q` / `F1`) for every keyword, taken from the TaskJuggler syntax reference.
- **Inspections**: unknown or misplaced keywords (with "did you mean" quick fixes), unresolved references,
  duplicate ids, deprecated keywords, unmatched braces.
- **`tj3` integration**: errors from `tj3 --check-syntax` are shown in the editor (once the file is saved), and a
  *TaskJuggler* run configuration schedules the project and generates its reports. Right-click a `.tjp` file and
  choose *Run* to create one.
- **Editor**: formatter, code folding, structure view, breadcrumbs, comment toggling, brace and quote matching,
  *File | New | TaskJuggler File*.

## Requirements

- An IntelliJ Platform IDE, version 2026.2 or newer.
- [TaskJuggler 3](https://taskjuggler.org/download.html) (`tj3`) for the syntax check and the run configuration.
  Everything else works without it. The plugin finds `tj3` on the `PATH`; set another location in
  *Settings | Languages & Frameworks | TaskJuggler*.

## Installation

From the IDE: *Settings | Plugins | Marketplace*, search for "TaskJuggler".

From a build: *Settings | Plugins | ⚙ | Install Plugin from Disk...* and pick the zip from
`build/distributions` (see below).

## Building from source

The build needs a JDK 17 or newer to run Gradle; the Java 25 toolchain used for compilation is downloaded
automatically if no matching JDK is installed.

```bash
./gradlew build          # generate lexer and parser, compile, run the tests, package the plugin
./gradlew runIde         # start a sandbox IDE with the plugin installed
./gradlew buildPlugin    # build/distributions/*.zip
./gradlew verifyPlugin   # check compatibility with the recommended IDE versions
```

Some tests parse the examples shipped with TaskJuggler (`/usr/share/doc/tj3/examples`) and run `tj3`; they are
skipped when TaskJuggler is not installed (on Debian/Ubuntu: `apt install tj3`).

## How it works

The list of keywords, where each one may appear, its attributes, values and documentation are extracted from the
installed TaskJuggler by `tools/extract-keywords.rb` into `src/main/resources/tj3/keywords.json`. Lexer, parser,
completion, documentation and inspections are all driven by that file, so supporting a new TaskJuggler release is
mostly a matter of running:

```bash
ruby tools/extract-keywords.rb > src/main/resources/tj3/keywords.json
```

The parser is deliberately tolerant: every statement is read as `keyword arguments* { block }?`, and the
keyword-specific rules are checked by inspections. This keeps the editor usable on incomplete or unusual files.

## Known limitations

- Tasks or resources declared inside macro bodies are not seen; references to them are not reported as unresolved.
- `taskprefix` / `reportprefix` on `include` are ignored.
- References inside rich text (e.g. `<-query attribute="..."->`, `<[report id="..."]>`) are not resolved.
