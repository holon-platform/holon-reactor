# Holon platform Project Reactor integration

This is the __Reactor__ module of the [Holon Platform](https://holon-platform.com), which provides integration with the [Project Reactor](https://projectreactor.io) APIs, a reactive library for building non-blocking applications on the JVM based on the Reactive Streams Specification. 

> THIS MODULE IS UNDER DEVELOPMENT.
> The module documentation and usage guide will be available when the first stable release will be published.

## Code structure

See [Holon Platform code structure and conventions](https://github.com/holon-platform/platform/blob/master/CODING.md) to learn about the _"real Java API"_ philosophy with which the project codebase is developed and organized.

## Getting started

### System requirements

The Holon Platform is built using __Java 8__, so you need a JRE/JDK version 8 or above to use the platform artifacts.

### Releases

See [releases](https://github.com/holon-platform/holon-reactor/releases) for the available releases. Each release tag provides a link to the closed issues.

### Obtain the artifacts

The [Holon Platform](https://holon-platform.com) is open source and licensed under the [Apache 2.0 license](LICENSE.md). All the artifacts (including binaries, sources and javadocs) are available from the [Maven Central](https://mvnrepository.com/repos/central) repository.

The Maven __group id__ for this module is `com.holon-platform.reactor` and a _BOM (Bill of Materials)_ is provided to obtain the module artifacts:

_Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform.reactor</groupId>
        <artifactId>holon-reactor-bom</artifactId>
        <version>5.2.0-alpha2</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Using the Platform BOM

The [Holon Platform](https://holon-platform.com) provides an overall Maven _BOM (Bill of Materials)_ to easily obtain all the available platform artifacts:

_Platform Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform</groupId>
        <artifactId>bom</artifactId>
        <version>${platform-version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Build from sources

You can build the sources using Maven (version 3.3.x or above is recommended) like this: 

`mvn clean install`

## Getting help

* Check the [platform documentation](https://docs.holon-platform.com/current/reference) or the specific [module documentation](https://docs.holon-platform.com/current/reference/holon-reactor.html).

* Ask a question on [Stack Overflow](http://stackoverflow.com). We monitor the [`holon-platform`](http://stackoverflow.com/tags/holon-platform) tag.

* Report an [issue](https://github.com/holon-platform/holon-reactor/issues).

* A [commercial support](https://holon-platform.com/services) is available too.

## Examples

See the [Holon Platform examples](https://github.com/holon-platform/holon-examples) repository for a set of example projects.

## Contribute

See [Contributing to the Holon Platform](https://github.com/holon-platform/platform/blob/master/CONTRIBUTING.md).

[![Gitter chat](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/holon-platform/contribute?utm_source=share-link&utm_medium=link&utm_campaign=share-link) 
Join the __contribute__ Gitter room for any question and to contact us.

## License

All the [Holon Platform](https://holon-platform.com) modules are _Open Source_ software released under the [Apache 2.0 license](LICENSE).

## Artifacts list

Maven _group id_: `com.holon-platform.reactor`

Artifact id | Description
----------- | -----------
`holon-reactor-datastore` | Project Reactor integration for the Holon Platform `Datastore` API, using `Mono` and `Flux` as API operations result
`holon-reactor-http` | Project Reactor integration for the Holon Platform `RestClient` API, using `Mono` and `Flux` as API operations result
`holon-reactor-spring` | __Spring__ webflux `WebClient` implementation of the Holon Platform `RestClient` API
`holon-reactor-bom` | Bill Of Materials
`holon-reactor-bom-platform` | Bill Of Materials with Project Reactor dependencies
`documentation-reactor` | Documentation
