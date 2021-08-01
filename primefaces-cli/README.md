# PrimeFaces CLI

## PrimeFlex 2 --> PrimeFlex 3 migration

This CLI-Tool replaces PrimeFlex 2 - CSS - classes in  your HTML, XHTML, ... - files with PrimeFlex 3 - CSS - classes. 

### Start CLI and show help

```java -cp "primefaces-cli-11.0.0-SNAPSHOT.jar" org.primefaces.cli.primeflexmigration.PrimeFlexMigration --help```

### Migrate all XHTML and HTML - files in one directory (including subdirectories)

```java -cp "primefaces-cli-11.0.0-SNAPSHOT.jar" org.primefaces.cli.primeflexmigration.PrimeFlexMigration c:\projects\myapp -e=html,xhtml```

It´s suggested to have no uncommited changes before running this CLI-tool. So you should get a good overview of what this migration-tool changed afterward it´s run. You also manually have to remove PrimeFlex 2 - CSS - file from your project and add PrimeFlex 3 - CSS - file.

There always may be corner-cases which need some additional manual work. So it´s suggest to do a visual check of your application afterwards.

Look at https://www.primefaces.org/primeflex/ for additional information.