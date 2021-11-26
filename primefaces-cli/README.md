[![Maven](https://img.shields.io/maven-central/v/org.primefaces/primefaces.svg)](https://repo.maven.apache.org/maven2/org/primefaces/primefaces-cli/)
[![Javadocs](http://javadoc.io/badge/org.primefaces/primefaces-selenium.svg)](http://javadoc.io/doc/org.primefaces/primefaces-cli)

# PrimeFaces Command Line Interface

PrimeFaces CLI currently comes with some handy tools to vastly automate some migration-tasks.

PrimeFaces CLI may be expanded in future. Maybe someone contributes a generator which generates eg ViewBeans and XHTMLs based one JPA-model-classes.
Contributions are welcome.

## General rules

It´s suggested to have no uncommited changes before running this CLI-tool. So you should get a good overview of what this migration-tool changed afterward it´s run.

There always may be corner-cases which need some additional manual work. So it´s suggest to do a visual check of your application afterwards.

## Download

**Windows PowerShell:**

```powershell
Invoke-WebRequest -Uri https://repo.maven.apache.org/maven2/org/primefaces/primefaces-cli/11.0.0-RC1/primefaces-cli-11.0.0-RC1.jar -OutFile C:\cli\pfcli.jar
```

**Linux WGET:**

```bash
wget -O pfcli.jar https://repo.maven.apache.org/maven2/org/primefaces/primefaces-cli/11.0.0-RC1/primefaces-cli-11.0.0-RC1.jar
```

## PrimeFlex 2 --> PrimeFlex 3 migration

This tool replaces PrimeFlex 2 - CSS - classes in your HTML, XHTML, ... - files with PrimeFlex 3 - CSS - classes.

#### Start CLI and show help

```shell
java -cp "pfcli.jar" org.primefaces.cli.migration.primeflex.PrimeFlexMigration --help
```

#### Migrate all XHTML and HTML - files in one directory (including subdirectories)

```shell
java -cp "pfcli.jar" org.primefaces.cli.migration.primeflex.PrimeFlexMigration c:\projects\myapp -e=html,xhtml
```

Look at https://www.primefaces.org/primeflex/ for additional information.

You manually have to remove PrimeFlex 2 - CSS - file from your project and add PrimeFlex 3 - CSS - file.

## Grid CSS --> PrimeFlex 2 migration

This tool replaces legacy Grid CSS classes (e.g. ui-g, see https://www.primefaces.org/showcase-v8/ui/panel/grid.xhtml) with PrimeFlex 2 - CSS - classes (e.g. p-grid, see https://github.com/primefaces/primeflex/tree/2.0.0).

#### Start CLI and show help

```shell
java -cp "pfcli.jar" org.primefaces.cli.migration.primeflex.GridCssMigration --help
```

#### Migrate all XHTML and HTML - files in one directory (including subdirectories)

```shell
java -cp "pfcli.jar" org.primefaces.cli.migration.primeflex.GridCssMigration c:\projects\myapp -e=html,xhtml
```
