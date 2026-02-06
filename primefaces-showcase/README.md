[![Maven](https://img.shields.io/maven-central/v/org.primefaces/primefaces.svg)](https://repo.maven.apache.org/maven2/org/primefaces/primefaces-showcase/)
[![Javadocs](http://javadoc.io/badge/org.primefaces/primefaces-selenium.svg)](http://javadoc.io/doc/org.primefaces/primefaces-showcase)

# primefaces-showcase

Deployable version of **PrimeFaces Showcase** WAR file can be downloaded manually or build it from sources.

## Prebuilt WAR

The showcase can be downloaded from [Maven](https://repo.maven.apache.org/maven2/org/primefaces/primefaces-showcase).
The prebuilt WAR is the version for Servlet Containers (like Tomcat or Jetty) and comes with a Faces and CDI implementation (MyFaces and Weld).

## Build from sources

```
git clone https://github.com/primefaces/primefaces.git
cd primefaces
mvn clean install            -- first build PrimeFaces
cd primefaces-showcase
```

#### for a EE Application Server

```
mvn clean package                             -- create war file (under target directory)
```

#### for a Servlet Container with Mojarra 4.0

```
mvn clean package -Pnon-ee,mojarra-4.0        -- create war file (under target directory)
```

#### for a Servlet Container with MyFaces 4.0

```
mvn clean package -Pnon-ee,myfaces-4.0        -- create war file (under target directory)
```

## Run from sources on [http://localhost:8080/showcase/](http://localhost:8080/showcase)

```
git clone https://github.com/primefaces/primefaces.git
cd primefaces
mvn clean install            -- first build PrimeFaces
cd primefaces-showcase
```

##### Development Mode (runs MyFaces 4.0 in Development mode)

Requires Java 17+ (because of Jetty 12)

```
mvn -Ddev
```

##### Mojarra 4.0

```
mvn clean jetty:run -Pnon-ee,mojarra-4.0
```

##### MyFaces 4.0

```
mvn clean jetty:run -Pnon-ee,myfaces-4.0
```

##### Mojarra 4.1

```
mvn clean jetty:run -Pnon-ee,mojarra-4.1
```

##### MyFaces 4.1

```
mvn clean jetty:run -Pnon-ee,myfaces-4.1
```
