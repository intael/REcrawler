.EXPORT_ALL_VARIABLES:

export CONTAINER=REcrawler
export DATABASE=db_scrapper
export ARTIFACT=REcrawler
export VERSION=1.0

.PHONY: bash
bash:
	docker exec -ti $(CONTAINER) bash

.PHONY: rebuild_db
rebuild_db:
	docker-compose down
	docker-compose up

.PHONY: build
build:
	docker-compose up --build

.PHONY: compile
compile:
	docker exec -ti $(CONTAINER) mvn package

.PHONY: database
database:
	docker exec -ti $(DATABASE) mysql -u root -ppasswd -D core

.PHONY: execute
execute:
	docker exec -ti $(CONTAINER) java -cp target/$(ARTIFACT)-$(VERSION)-SNAPSHOT.jar Main

.PHONY: test
test:
	docker exec -ti $(CONTAINER) mvn test

.PHONY: up
up:
	docker-compose up

.PHONY: down
down:
	docker-compose down