[![Maven](https://img.shields.io/maven-central/v/org.primefaces/primefaces.svg)](https://repo.maven.apache.org/maven2/org/primefaces/primefaces/)
[![Actions Status](https://github.com/primefaces/primefaces/workflows/Java%20CI/badge.svg)](https://github.com/primefaces/primefaces/actions)
[![Sonar](https://sonarcloud.io/api/project_badges/measure?project=org.primefaces%3Aprimefaces&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.primefaces%3Aprimefaces)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Discord Chat](https://img.shields.io/discord/591914197219016707.svg?color=7289da&label=chat&logo=discord&style=flat-square)](https://discord.gg/gzKFYnpmCY)
[![Stackoverflow](https://img.shields.io/badge/StackOverflow-primefaces-chocolate.svg)](https://stackoverflow.com/questions/tagged/primefaces+jsf)

[![PrimeFaces Hero](https://www.primefaces.org/wp-content/uploads/2020/03/primefaces-github-02032020.jpg "PrimeFaces Hero")](https://www.primefaces.org/showcase)

# PrimeFaces

This is an overview page, please visit [PrimeFaces.org](https://www.primefaces.org) for more information.

[![PrimeFaces Logo](https://www.primefaces.org/wp-content/uploads/2016/10/prime_logo_new.png)](https://www.primefaces.org/showcase)

### Overview
***

[PrimeFaces](https://www.primefaces.org/) is one of the most popular UI libraries in Java EE Ecosystem and widely used by software companies, world renowned brands, banks, financial institutions, insurance companies, universities and more. Here are [some of the users](https://www.primefaces.org/whouses) who notified us or subscribed to a [PrimeFaces Support Service](https://www.primefaces.org/support).

### Getting Started
***

**PrimeFaces** can be downloaded manually or via maven.

##### Latest Downloads

Version | Binary | Source | JSF version | Java version | Documentation
------------ | -------------  | ------------- | ------------- | ------------- | ------------- |
10.0.0|  |  | 2.0 - 3.0 | 1.8 - ? | [10.0.0 Documentation](https://primefaces.github.io/primefaces/10_0_0/#/)
8.0| [primefaces-8.0.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/8.0/primefaces-8.0.jar)  | [primefaces-8.0-sources.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/8.0/primefaces-8.0-sources.jar) | 2.0 - 2.3 | 1.8 - ? | [8.0 Documentation](https://primefaces.github.io/primefaces/8_0/#/)
7.0| [primefaces-7.0.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/7.0/primefaces-7.0.jar)  | [primefaces-7.0-sources.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/7.0/primefaces-7.0-sources.jar) | 2.0 - 2.3 | 1.7 - ? | [7.0 Documentation](https://primefaces.github.io/primefaces/7_0/#/)
6.2| [primefaces-6.2.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/6.2/primefaces-6.2.jar)  | [primefaces-6.2-sources.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/6.2/primefaces-6.2-sources.jar) | 2.0 - 2.3 | 1.6 - ? | [6.2 Documentation](https://www.primefaces.org/docs/guide/primefaces_user_guide_6_2.pdf)
6.1| [primefaces-6.1.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/6.1/primefaces-6.1.jar)  | [primefaces-6.1-sources.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/6.1/primefaces-6.1-sources.jar) | 2.0 - 2.3 | 1.5 - ? | [6.1 Documentation](https://www.primefaces.org/docs/guide/primefaces_user_guide_6_1.pdf)
6.0| [primefaces-6.0.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/6.0/primefaces-6.0.jar)  | [primefaces-6.0-sources.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/6.0/primefaces-6.0-sources.jar) | 2.0 - 2.2 | 1.5 - ? | [6.0 Documentation](https://www.primefaces.org/docs/guide/primefaces_user_guide_6_0.pdf)
5.3| [primefaces-5.3.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/5.3/primefaces-5.3.jar)  | [primefaces-5.3-sources.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/5.3/primefaces-5.3-sources.jar) | 2.0 - 2.2 | 1.5 - ? | [5.3 Documentation](https://www.primefaces.org/docs/guide/primefaces_user_guide_5_3.pdf)
5.2| [primefaces-5.2.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/5.2/primefaces-5.2.jar)  | [primefaces-5.2-sources.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/5.2/primefaces-5.2-sources.jar) | 2.0 - 2.2 | 1.5 - ? | [5.2 Documentation](https://www.primefaces.org/docs/guide/primefaces_user_guide_5_2.pdf)
5.1| [primefaces-5.1.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/5.1/primefaces-5.1.jar)  | [primefaces-5.1-sources.jar](http://search.maven.org/remotecontent?filepath=org/primefaces/primefaces/5.1/primefaces-5.1-sources.jar) | 2.0 - 2.2 | 1.5 - ? | [5.1 Documentation](https://www.primefaces.org/docs/guide/primefaces_user_guide_5_1.pdf)


For a full list of the available downloads, please visit the [download page](https://www.primefaces.org/downloads).

##### Maven

- Official release (javax; JSF 2.0 - JSF 2.3)

	```xml
	<dependency>
	    <groupId>org.primefaces</groupId>
	    <artifactId>primefaces</artifactId>
	    <version>8.0</version>
	</dependency>
	```

- Official release (jakarta; JSF 3.0; not yet released)

  ```xml
  <dependency>
      <groupId>org.primefaces</groupId>
      <artifactId>primefaces</artifactId>
      <version>10.0.0</version>
      <classifier>jakarta</classifier>
  </dependency>
  ```

- Snapshot (javax; JSF 2.0 - JSF 2.3; should NOT be used in production environments!)

	```xml
	<dependency>
	    <groupId>com.github.primefaces</groupId>
	    <artifactId>primefaces</artifactId>
	    <version>master-SNAPSHOT</version>
	</dependency>

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
	```

- Snapshot (jakarta; JSF 3.0; should NOT be used in production environments!)

  ```xml
  <dependency>
      <groupId>com.github.primefaces</groupId>
      <artifactId>primefaces</artifactId>
      <version>master-SNAPSHOT</version>
      <classifier>jakarta</classifier>  
  </dependency>

  <repositories>
      <repository>
          <id>jitpack.io</id>
          <url>https://jitpack.io</url>
      </repository>
  </repositories>
  ```
### Usage
***

##### Namespaces

The PrimeFaces namespace is necessary to add PrimeFaces components to your pages.

```xml
xmlns:p="http://primefaces.org/ui"
```

##### Test Run

```xml
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:p="http://primefaces.org/ui">

	<h:head>

	</h:head>

	<h:body>

		<p:spinner />

	</h:body>
</html>

```

### Demo
***
Please refer to the [showcase](https://www.primefaces.org/showcase) in order to see the full usage of the components. Sources of PrimeFaces showcase are available as a separate [project]( https://github.com/primefaces/showcase-facelift).

### Documentation
***
A User Guide is available [here](https://primefaces.github.io/primefaces/).

### Contribution
***
Visit the [Contribution Wiki](https://github.com/primefaces/primefaces/wiki/Contributing-to-PrimeFaces) page for detailed information.


### License
***
Licensed under the MIT License.


