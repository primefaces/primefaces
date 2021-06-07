# PrimeFaces Showcase

![PrimeFaces icon](https://www.primefaces.org/wp-content/uploads/2016/10/prime_logo_new.png)

### Getting Started

Deployable version of **PrimeFaces Showcase** war file can be downloaded manually or build it from sources.

##### Prebuilt war

For a full list of the available downloads, please visit the [download page](http://www.primefaces.org/downloads). Scroll down to showcase for war file link.

##### Build from sources

```
git clone https://github.com/primefaces/showcase-facelift.git
cd showcase-facelift
mvn clean                  -- clean temp files from target folder
mvn package                -- create war file (under target directory)
mvn jetty:run              -- run showcase project locally
```

##### Run from local sources

```
mvn clean jetty:run
```

[http://localhost:8080/showcase/](http://localhost:8080/showcase)

### Documentation

User Guide is available at [documentation](http://www.primefaces.org/documentation) page along with other additional resoures.

### Contribution

Visit [Contribution Wiki](https://github.com/primefaces/primefaces/wiki/Contributing-to-PrimeFaces) page for the detailed information.

### License

Licensed under the MIT License.
