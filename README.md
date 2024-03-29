# SQL Servant

A simple tool for running a configured SQL statements.

## Getting Started

1. Clone from this repository.
2. Open your favorite IDE and import as Gradle project.

### Prerequisites

Java 11

### Installing

1. Ensure that you can connect to the internet. This is for downloading gradle and the dependencies.
2. Ensure that you are running Java 11. 
    You can run the following command if you like to validate your java version:

   ```
   java -version
   ```

3. Open a terminal and run the following command:

    ```
    gradlew
    ```

    The command above will download the correct version of gradle and the needed dependencies.

## Running the tests

On a terminal, run the following command:

```
gradlew test
```

The output can be found in following directory:

```
build/reports/tests/test directory.
```

## Packaging

On a terminal, run the following command:

```
gradlew packAll
```

The output can be found in the following directory:

```
build/pack directory. 
```

## Deployment

1. Extract one of the generated package to your desired directory.
2. From this directory you can now try to execute the following command.

```
sqlsrvnt -v 
```

The command above will give you the current version.

## Configuration

The configuration files to be used must be in the **conf** directory of where you extracted a package. 
This directory contains **ss-h2-sample.json** as a sample configuration. The content of it is as follows:

```json
{
  "defaults": {
    "jdbcDriver" : "org.h2.Driver",
    "connectionString": "jdbc:h2:./db/test1"
  },
  "dbPoolConfig" : {
    "minIdle": 5,
    "maxIdle": 10,
    "maxOpenPreparedStatements" : 100
  },
  "params" : [
    {
      "name": "name",
      "description": "Name suffix"
    }
  ],
  "queries": [
    {
      "queries": [
        "CREATE TABLE PERSON(id int primary key, name varchar(255))",
        "insert into person (id, name) values (1, 'test1 \"name')",
        "insert into person (id, name) values (2, 'test2 name')"
      ],
      "next" : {
        "mode" : "script",
        "queries" : ["sample-h2-script.txt"],
        "next" : {
          "mode" : "sqs",
          "description" : "Sample Single Query Script",
          "queries" : ["sample-h2-query-script.txt"]
        }
      }
    },
    {
      "queries" : ["select * from person where name like '%%%%name%%%'"],
      "mode" : "query"
    },
    {
      "queries": ["CREATE TABLE PERSON(id int primary key, name varchar(255))"],
      "next" : {
        "queries": [
          "insert into person (id, name) values (1, 'test1 name')",
          "insert into person (id, name) values (2, 'test2 name')"
        ],
        "next" : {
          "queries" : ["select * from person"],
          "mode" : "query"
        }
      },
      "connectionString": "jdbc:h2:./db/test2",
      "parallel" : true
    },
    {
      "listeners": {
        "filter" : "\"",
        "onStart" : "sample-h2-start.bat",
        "onHeader" : "sample-h2-data.bat",
        "onData" : "sample-h2-data.bat",
        "onComplete": "sample-h2-complete.bat"
      },
      "parallel": true,
      "queries": ["CREATE TABLE PERSON(id int primary key, name varchar(255))"],
      "next" : {
        "queries": [
          "insert into person (id, name) values (1, 'test1 name')",
          "insert into person (id, name) values (2, 'test2 name')"
        ],
        "next" : {
          "queries" : ["select * from person"],
          "mode" : "query"
        }
      },
      "connectionString": "jdbc:h2:./db/test3",
      "parallel" : true
    }
  ]
}
```

There are 4 sections in the configuration file.

Section | Description
------------ | -------------
defaults | The default values to use if the equivalent property was not provided in the queries section.
queries | The actual SQL statements must be configured here.
dbPoolConfig | Configuration of the database pool.
params | The expected parameters.

#### defaults Section

Property | Type | Default | Value | Description
---------|------|---------|-------|------------
jdbcDriver | String | | | Must have the fully qualified class name to use as a driver. Overrides the entry in the externalized configuration.
mode | String | stmt | stmt, query, script, sqs | Use **stmt** _(i.e. the default)_, if the statement doesn't return a value. Use **query**, if the statement returns values. Use **script**, if the statements are stored in a separate script file and doesn't return a value. Use **sqs** _(i.e. single query script)_, if a select statement is stored in a script file. Overrides the entry in the externalized configuration.
connectionString | String | | | The connection string of the database not including the username and password. Overrides the entry in the externalized configuration.
username | String | | | Must have the valid username to use on connecting to the database. Overrides the entry in the externalized configuration.
password | String | | | Must have the valid password to use on connecting to the database. Overrides the entry in the externalized configuration.
windowsAuthentication | Boolean | false | | This property is for SQL server only. If set to **true**, it will use the currently signed-in user in Windows and will ignore the username and password properties. Thus, it is better not to provide the username and password. Overrides the entry in the externalized configuration.
parallel | Boolean | false | | If set to **true**, the configured queries will be executed in parallel. Overrides the entry in the externalized configuration.
listeners | ListenersConfig | | | Holds the listeners to be executed with the query processing. Overrides the entry in the externalized configuration.
filename | String | | | Must have the filename of the externalized configuration.

##### ListenersConfig Type

Property | Type | Default | Description
---------|------|---------|------------
filter | String | "% | The characters to be converted to underscore _(i.e. \_)_. Overrides the entry in the externalized configuration.
command | String | cmd.exe /c | The OS level command that will invoke the listeners. For the noarch package, command must always be configured. Overrides the entry in the externalized configuration.
onStart | String | | The listener to be invoked upon starting of the query processing. This listener will receive the arguments **current date**, **query description**, **the actual query** and **first time processing** respectively. Overrides the entry in the externalized configuration.
onHeader | String | | The listener to be invoked upon doing the select query processing. This listener will receive the arguments **current date**, **query description**, **the actual query** and **header** respectively. Overrides the entry in the externalized configuration.
onData | String | | The listener to be invoked upon doing the select query processing. This listener will receive the arguments **current date**, **query description**, **the actual query** and **record** respectively. Overrides the entry in the externalized configuration.
onComplete | String | | The listener to be invoked upon completion of the query processing. This listener will receive the arguments **current date**, **query description**, **the actual query** and **success status** respectively. Overrides the entry in the externalized configuration.
filename | String | | Must have the filename of the externalized configuration.

_Note: If the ListenersConfig was configured, the queries section that has it won't be executed in parallel._

#### queries Section

Property | Type | Default | Description
---------|------|---------|-------------------
jdbcDriver | String | defaults.jdbcDriver | Must have the fully qualified class name to use as a driver. Overrides the entry in the externalized configuration.
mode | String | defaults.mode | If the query doesn't return a value and must not log in the log file, use **stmt** otherwise use **query**. Overrides the entry in the externalized configuration.
connectionString | String | defaults.connectionString | The connection string of the database not including the username and password. Overrides the entry in the externalized configuration.
username | String | defaults.username | Must have the valid username to use on connecting to the database. Overrides the entry in the externalized configuration.
password | String | defaults.password | Must have the valid password to use on connecting to the database. Overrides the entry in the externalized configuration.
windowsAuthentication | Boolean | defaults.windowsAuthentication | This property is for SQL server only. If set to **true**, it will use the currently signed-in user in Windows and will ignore the username and password properties. Thus, it is better not to provide the username and password. Overrides the entry in the externalized configuration.
parallel | Boolean | defaults.parallel | If set to **true**, the configured queries will be executed in parallel. Overrides the entry in the externalized configuration.
listeners | ListenersConfig | defaults.listeners | Holds the listeners to be executed with the query processing. Overrides the entry in the externalized configuration.
description| String | Query \<index\> \[NEXT\] | Must have a meaningful description for the group of queries. Otherwise, it will be defaulted to Query with the \<index\> number based on it entry in the configuration file. If the \[NEXT\] is displayed, it is the depth of the configured next queries section. Overrides the entry in the externalized configuration.
queries | String[] | | Must have all the SQL statements that can be executed in parallel. Overrides the entry in the externalized configuration.
next | queries section | | If after the execution of the provided queries requires another set of queries, use this property to define another queries section. Overrides the entry in the externalized configuration.
filename | String | | Must have the filename of the externalized configuration.

#### dbPoolConfig Section

Property | Type | Default | Description
---------|------|---------|-------------------
minIdle | Integer | 1 | The minimum number of DataSource instances that are always ready to create a connection. Overrides the entry in the externalized configuration.
maxIdle | Integer | 1 | The maximum number of DataSource instances that can be added from the minIdle property. Overrides the entry in the externalized configuration.
maxOpenPreparedStatements | Integer | 50 | The maximum number of PreparedStatement instances before blocking. Overrides the entry in the externalized configuration.
filename | String | | Must have the filename of the externalized configuration. 

#### params Section

Property | Type | Description
---------|------|----------------------------
name | String | The name of the expected parameter. Overrides the entry in the externalized configuration.
description | String | A user friendly description of the parameter. Overrides the entry in the externalized configuration.
value | String | The default value of the parameter. Overrides the entry in the externalized configuration.
filename | String | Must have the filename of the externalized configuration.

## Environment Variables

#### SQL_SERVANT_CONF_DIR Environment Variable

Set the **SQL_SERVANT_CONF_DIR** environment variable to have a different location for the following configuration folders:
- conf
- listeners
- scripts

_Note: This environment variable can be overridden by the -cd command line argument._

#### SS2_JAVA_HOME Environment Variable

If the existing **JAVA_HOME** or the **java executable** in not the supported version. Set the **SS2_JAVA_HOME** to the correct java version.

## Usage

To know the usage, execute the following on a terminal:

```
sqlsrvnt -h 
```

To run a particular configuration *(e.g. ss-h2-sample.json)*, execute the following command *(i.e. only the filename must be specified without the extension)*:

```
sqlsrvnt -c ss-h2-sample
```

To run a particular configuration *(e.g. ss-h2-sample.json)* without executing it, execute the following command:

```
sqlsrvnt -n -c ss-h2-sample
```

To run a particular configuration with parameters *(e.g. ss-h2-sample.json)*, execute the following command:

```
sqlsrvnt -c ss-h2-sample -P name="test"
```

To run a particular configuration *(e.g. ss-h2-sample.json)* in parallel overriding the one in configuration, execute the following command:

```
sqlsrvnt -p -c ss-h2-sample
```

To run a particular configuration on a particular environment *(e.g. ss-h2-sample.test.json)*, execute the following command:

```
sqlsrvnt -e test -c ss-h2-sample
```

To know the current version, execute the following command:

```
sqlsrvnt -v
```

## Built With

* [Gradle](https://gradle.org/) - Dependency Management
* [Maven](https://maven.apache.org/) - Dependency Repository
* [Java](https://www.oracle.com/nz/java/) - Coding Language

## Author

* **Ronaldo Webb**

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
