# PrimeFaces Showcase

## Getting Started

Deployable version of **PrimeFaces Showcase** WAR file can be downloaded manually or build it from sources.

#### Prebuilt WAR

For a full list of the available downloads, please visit the [download page](http://www.primefaces.org/downloads). Scroll down to showcase for WAR file link.

#### Build from sources (for a EE Application Server)

```
git clone https://github.com/primefaces/primefaces.git
cd primefaces/primefaces
mvn clean install            -- first build PrimeFaces
cd ../primefaces-showcase
mvn clean                    -- clean temp files from target folder
mvn package -Pee             -- create war file (under target directory)
```

#### Build from sources (for a Servlet Container like Tomcat / Jetty)

###### Mojarra 2.3.x

```
git clone https://github.com/primefaces/primefaces.git
cd primefaces/primefaces
mvn clean install                                                 -- first build PrimeFaces
cd ../primefaces-showcase
mvn clean                                                         -- clean temp files from target folder
mvn package -Pjsf-mojarra,cdi-owb,bv-hibernate,jaxrs-resteasy     -- create war file (under target directory)
```

###### MyFaces 2.3.x

```
git clone https://github.com/primefaces/primefaces.git
cd primefaces/primefaces
mvn clean install                                                 -- first build PrimeFaces
cd ../primefaces-showcase
mvn clean                                                         -- clean temp files from target folder
mvn package -Pjsf-myfaces,cdi-owb,bv-hibernate,jaxrs-resteasy     -- create war file (under target directory)
```

#### Run from local sources on [http://localhost:8080/showcase/](http://localhost:8080/showcase)

###### Mojarra 2.3.x

```
mvn clean jetty:run -Pjsf-mojarra,cdi-owb,bv-hibernate,jaxrs-resteasy
```

###### MyFaces 2.3.x

```
mvn clean jetty:run -Pjsf-myfaces,cdi-owb,bv-hibernate,jaxrs-resteasy
```

### Documentation

User Guide is available at [documentation](http://www.primefaces.org/documentation) page along with other additional resoures.

### Contribution

Visit [Contribution Wiki](https://github.com/primefaces/primefaces/wiki/Contributing-to-PrimeFaces) page for the detailed information.

### License

Licensed under the MIT License.
