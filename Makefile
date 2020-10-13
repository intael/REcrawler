.EXPORT_ALL_VARIABLES:

export CONTAINER=core_scrapper
export ARTIFACT=housing-scraper
export VERSION=1.0

.PHONY: bash
bash:
	docker exec -ti $(CONTAINER) bash

.PHONY: build
build:
	docker-compose up --build

.PHONY: compile
compile:
	docker exec -ti $(CONTAINER) mvn package

.PHONY: execute
execute:
	docker exec -ti $(CONTAINER) java -cp target/$(ARTIFACT)-$(VERSION)-SNAPSHOT.jar Main

.PHONY: up
up:
	docker-compose up
