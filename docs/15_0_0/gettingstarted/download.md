# Download

PrimeFaces has a single JAR called **primefaces-{version}.jar**. There are two ways to download this
JAR, you can either download from PrimeFaces homepage or if you are a Maven user you can define
it as a dependency.

## Download Manually
Manual downloads are actually links to the maven repository, for more information please visit;

http://www.primefaces.org/downloads

## Download with Maven
Group id is _org.primefaces_ and artifact id is _primefaces._

### JSF / Java Server Faces (2.3, javax namespace)

```xml
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces</artifactId>
    <version>15.0.0</version>
</dependency>
```

### Jakarta Faces (3.0 - 4.X, jakarta namespace)

```xml
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces</artifactId>
    <version>15.0.0</version>
    <classifier>jakarta</classifier>
</dependency>
```


### Additional themes

```xml
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces-themes</artifactId>
    <version>15.0.0</version>
</dependency>
```