[![Maven](https://img.shields.io/maven-central/v/org.primefaces/primefaces.svg)](https://repo.maven.apache.org/maven2/org/primefaces/primefaces-showcase/)
[![Javadocs](http://javadoc.io/badge/org.primefaces/primefaces-selenium.svg)](http://javadoc.io/doc/org.primefaces/primefaces-showcase)

# primefaces-showcase

Deployable version of **PrimeFaces Showcase** WAR file can be downloaded manually or build it from sources.

## Prebuilt WAR

The showcase can be downloaded from [Maven](https://repo.maven.apache.org/maven2/org/primefaces/primefaces-showcase) or
via the [PrimeFaces download page](http://www.primefaces.org/downloads) (scroll down to showcase for WAR file link)

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

#### for a Servlet Container with Mojarra 2.3

```
mvn clean package -Pnon-ee,mojarra-2.3        -- create war file (under target directory)
```

#### for a Servlet Container with MyFaces 2.3

```
mvn clean package -Pnon-ee,myfaces-2.3        -- create war file (under target directory)
```

#### for a Servlet Container with MyFaces 2.3-next

```
mvn clean package -Pnon-ee,myfaces-next-2.3   -- create war file (under target directory)
```

## Run from sources on [http://localhost:8080/showcase/](http://localhost:8080/showcase)

```
git clone https://github.com/primefaces/primefaces.git
cd primefaces
mvn clean install            -- first build PrimeFaces
cd primefaces-showcase
```

##### Mojarra 2.3

```
mvn clean jetty:run -Pnon-ee,mojarra-2.3
```

##### MyFaces 2.3

```
mvn clean jetty:run -Pnon-ee,myfaces-2.3
```

##### MyFaces 2.3-next

```
mvn clean jetty:run -Pnon-ee,myfaces-next-2.3
```

