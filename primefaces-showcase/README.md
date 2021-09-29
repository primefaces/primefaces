[![Maven](https://img.shields.io/maven-central/v/org.primefaces/primefaces.svg)](https://repo.maven.apache.org/maven2/org/primefaces/primefaces-showcase/)
[![Javadocs](http://javadoc.io/badge/org.primefaces/primefaces-selenium.svg)](http://javadoc.io/doc/org.primefaces/primefaces-showcase)

# primefaces-showcase

Deployable version of **PrimeFaces Showcase** WAR file can be downloaded manually or build it from sources.

## Prebuilt WAR

The showcase can be downloaded from [Maven](https://repo.maven.apache.org/maven2/org/primefaces/primefaces-showcase) or
via the [PrimeFaces download page](http://www.primefaces.org/downloads) (scroll down to showcase for WAR file link)

## Build from sources (for a EE Application Server)

```
git clone https://github.com/primefaces/primefaces.git
cd primefaces/primefaces
mvn clean install            -- first build PrimeFaces
cd ../primefaces-showcase
mvn clean                    -- clean temp files from target folder
mvn package                  -- create war file (under target directory)
```

## Build from sources (for a Servlet Container like Tomcat / Jetty)

##### Mojarra 2.3.x

```
git clone https://github.com/primefaces/primefaces.git
cd primefaces/primefaces
mvn clean install                                                 -- first build PrimeFaces
cd ../primefaces-showcase
mvn clean                                                         -- clean temp files from target folder
mvn package -Pjsf-mojarra,cdi-owb,bv-hibernate,jaxrs-resteasy     -- create war file (under target directory)
```

##### MyFaces 2.3.x

```
git clone https://github.com/primefaces/primefaces.git
cd primefaces/primefaces
mvn clean install                                                 -- first build PrimeFaces
cd ../primefaces-showcase
mvn clean                                                         -- clean temp files from target folder
mvn package -Pjsf-myfaces,cdi-owb,bv-hibernate,jaxrs-resteasy     -- create war file (under target directory)
```

## Run from sources on [http://localhost:8080/showcase/](http://localhost:8080/showcase)

##### Mojarra 2.3.x

```
mvn clean jetty:run -Pjsf-mojarra,cdi-owb,bv-hibernate,jaxrs-resteasy
```

##### MyFaces 2.3.x

```
mvn clean jetty:run -Pjsf-myfaces,cdi-owb,bv-hibernate,jaxrs-resteasy
```
