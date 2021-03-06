# Headless Cms Service

[![Release](https://github.com/AMPnet/headless-cms/actions/workflows/gradle-release.yml/badge.svg?branch=master)](https://github.com/AMPnet/headless-cms/actions/workflows/gradle-release.yml) [![codecov](https://codecov.io/gh/AMPnet/headless-cms/branch/master/graph/badge.svg?token=irz7LiqAK4)](https://codecov.io/gh/AMPnet/headless-cms)

Headless cms service is a part of the AMPnet crowdfunding project.

## Requirements

Service must have running and initialized database. Default database url is `locahost:5432`.
To change database url set configuration: `spring.datasource.url` in file `application.properties`.
To initialize database run script in the project root folder:

```sh
./initialize-local-database.sh
```

## Start

Application is running on port: `8130`. To change default port set configuration: `server.port`.

### Build

```sh
./gradlew build
```

### Run

```sh
./gradlew bootRun
```

After starting the application, API documentation is available at: `localhost:8130/docs/index.html`.
If documentation is missing generate it by running gradle task:

```sh
./gradlew copyDocs
```

### Test

```sh
./gradlew test
```
