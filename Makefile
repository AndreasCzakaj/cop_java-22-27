IMAGE := maven:3.10-eclipse-temurin-27
TTY   := $(shell [ -t 0 ] && echo -it)
# Runs as the host user; the Maven repository is cached in .m2/ inside the project.
RUN   := docker run --rm $(TTY) --user $(shell id -u):$(shell id -g) -e MAVEN_CONFIG=/app/.m2 \
         -v "$(CURDIR)":/app -w /app $(IMAGE)
MVN   := mvn -B -q -Dmaven.repo.local=/app/.m2/repository -Duser.home=/tmp
TESTS := $(if $(ex),-Dtest='workshop/ex$(ex)_*/**')

.PHONY: help install test test-local shell
.DEFAULT_GOAL := help

help: ## Show this help
	@grep -E '^[a-z-]+:.*## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*## "}; {printf "  make %-12s %s\n", $$1, $$2}'

install: ## Pull the JDK 27 image and download the dependencies
	docker pull $(IMAGE)
	$(RUN) $(MVN) test-compile

test: ## Run the tests in Docker, e.g. "make test" or "make test ex=03"
	$(RUN) $(MVN) test $(TESTS)

test-local: ## Run the tests with your local Maven and JDK 27, e.g. "make test-local ex=03"
	mvn -B -q test $(TESTS)

shell: ## Open a shell in the container (java -version, java ex01-compact-source/..., ...)
	$(RUN) bash
