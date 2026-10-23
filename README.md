# Java 22 – 27 workshop

Hands-on exercises for the language features and APIs that arrived between
Java 22 and Java 27, plus one useful testing technique. Every exercise is a
small coding task with JUnit tests that turn green once the task is solved.

## Setup

You need **either** a JDK 27 and Maven (IntelliJ IDEA comes with Maven)
**or** Docker.

Exactly 27: two topics are preview features, and preview features only run
on the JDK they were compiled for. The project enables them in `pom.xml`
(`--enable-preview`). In IntelliJ, set the project SDK to 27 and the language
level to "27 (Preview)". This needs IntelliJ IDEA 2026.2 or newer: older
versions do not offer language level 27, whichever JDK you select.

```bash
git clone <repository-url> java-workshop
cd java-workshop
make install        # pulls the JDK 27 image and downloads the dependencies
make test           # runs all tests: they are all skipped for now
```

| Command                  | What it does                                         |
|--------------------------|------------------------------------------------------|
| `make test`              | Run all tests in Docker                              |
| `make test ex=03`        | Run the tests of one exercise in Docker              |
| `make test-local ex=03`  | The same with your local JDK 27 and Maven            |
| `make shell`             | Open a shell in the container (`java -version`, ...) |

### Without make (e.g. on Windows)

```bash
docker compose run --rm java mvn test                              # all tests
docker compose run --rm java mvn test -Dtest="workshop/ex03_*/**"  # one exercise
docker compose run --rm java bash                                  # shell
```

The container runs as user 1000:1000. If your Linux user has another id, put
`UID=<your uid>` and `GID=<your gid>` into a file named `.env` first.

## How the exercises work

- The code to work on is in `src/main/java/workshop/exNN_.../`, except for
  exercise 01, which lives in `ex01-compact-source/` (you will see why).
- Each file to work on starts with a Markdown comment (`///`, Java 23) that
  states the problem, the task, the constraints and some hints. IntelliJ
  renders it nicely with "Toggle Rendered View".
- Files marked "Given code: do not change" are part of the setting.
- The tests in `src/test/java/workshop/exNN_.../` check the behaviour and,
  where a task says "use feature X", also the source code. Do not change the
  tests.
- All tests start out `@Disabled`, so the build stays quiet. When you start
  on a task, remove `@Disabled` from its tests and watch them fail first.
- Exercise 02 is the other way round: the code is finished and you write the
  tests.
- Some tasks start with working legacy code and green behaviour tests: there
  the task is a refactoring.
- A compile error anywhere stops Maven before any test runs. Keep the code
  compiling, and run a single exercise (`make test ex=05`) to keep the output
  readable.

## Exercises

| #  | Topic                                       | Java               | Files to work on                                            | Minutes |
|----|---------------------------------------------|--------------------|-------------------------------------------------------------|---------|
| 01 | Compact source files and instance `main`    | 22, 25             | `ex01-compact-source/SlowestEndpoints.java`                 | 20      |
| 02 | AssertJ `usingRecursiveComparison()`        | –                  | `OrderServiceTest` (the test!)                              | 20      |
| 03 | Flexible constructor bodies                 | 25                 | `Isbn` · `SalesReport` · `OpeningHours`                     | 20      |
| 04 | Primitive patterns, unnamed variables (`_`) | 27 (preview), 22   | `Config` · `JsonWriter` · `Alerts`                          | 20      |
| 05 | Structured concurrency, scoped values       | 27 (preview), 25   | `RequestContext` · `ProductPageService` · `ShippingQuotes`  | 20      |
| 06 | Stream gatherers                            | 24                 | `Revenue` · `MoreGatherers`                                 | 20      |

A "·" separates the tasks of an exercise. The minutes are estimates and add
up to 120, which leaves 60 minutes of a three-hour session for introductions
to each topic and a break.

## Solutions

The reference solutions are on the branch `solution`:
`git switch solution && make test`.
