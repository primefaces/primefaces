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
cd primefaces/primefaces
mvn clean install            -- first build PrimeFaces
cd ../primefaces-showcase
mvn clean                    -- clean temp files from target folder
```

#### for a EE Application Server

```
mvn package                               -- create war file (under target directory)
```

#### for a Servlet Container with Mojarra 2.3.x

```
mvn package -Pnon-ee,mojarra-2.3.x        -- create war file (under target directory)
```

#### for a Servlet Container with MyFaces 2.3.x

```
mvn package -Pnon-ee,myfaces-2.3.x        -- create war file (under target directory)
```

#### for a Servlet Container with MyFaces 2.3.x-next

```
mvn package -Pnon-ee,myfaces-next-2.3.x   -- create war file (under target directory)
```

## Run from sources on [http://localhost:8080/showcase/](http://localhost:8080/showcase)

##### Mojarra 2.3.x

```
mvn clean jetty:run -Pnon-ee,mojarra-2.3.x
```

##### MyFaces 2.3.x

```
mvn clean jetty:run -Pnon-ee,myfaces-2.3.x
```

##### MyFaces 2.3.x-next

```
mvn clean jetty:run -Pnon-ee,myfaces-next-2.3.x
```

