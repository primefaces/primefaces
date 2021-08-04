# PrimeFaces CLI

PrimeFaces CLI currently comes with some handy tools to vastly automate some migration-tasks.

PrimeFaces CLI may be expanded in future. Maybe someone contributes a generator which generates eg ViewBeans and XHTMLs based one JPA-model-classes.
Contributions are welcome.

## General rules

It´s suggested to have no uncommited changes before running this CLI-tool. So you should get a good overview of what this migration-tool changed afterward it´s run. 

There always may be corner-cases which need some additional manual work. So it´s suggest to do a visual check of your application afterwards.

## PrimeFlex 2 --> PrimeFlex 3 migration

This CLI-Tool replaces PrimeFlex 2 - CSS - classes in  your HTML, XHTML, ... - files with PrimeFlex 3 - CSS - classes. 

### Start CLI and show help

```java -cp "primefaces-cli-11.0.0-SNAPSHOT.jar" org.primefaces.cli.primeflexmigration.PrimeFlexMigration --help```

### Migrate all XHTML and HTML - files in one directory (including subdirectories)

```java -cp "primefaces-cli-11.0.0-SNAPSHOT.jar" org.primefaces.cli.primeflexmigration.PrimeFlexMigration c:\projects\myapp -e=html,xhtml```

Look at https://www.primefaces.org/primeflex/ for additional information.

You manually have to remove PrimeFlex 2 - CSS - file from your project and add PrimeFlex 3 - CSS - file.

## Grid CSS --> PrimeFlex 2 migration

### Start CLI and show help

```java -cp "primefaces-cli-11.0.0-SNAPSHOT.jar" org.primefaces.cli.primeflexmigration.GridCssMigration --help```

### Migrate all XHTML and HTML - files in one directory (including subdirectories)

```java -cp "primefaces-cli-11.0.0-SNAPSHOT.jar" org.primefaces.cli.primeflexmigration.GridCssMigration c:\projects\myapp -e=html,xhtml```
