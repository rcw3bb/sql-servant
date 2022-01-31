# Changelog

## 2.1.1 : 2022-01-31

### Changed

* Gradle and log4j2 update.
* Made the noarc sqlsrvnt script behave as the win batch.
* Update dependencies

## 2.1.0 : 2021-11-10

### New

* Implement **exit codes**.
* **JAVA_HOME** environment variable is recognized.
* Introduced **SS2_JAVA_HOME** to override JAVA_HOME. If the later is pointing to a wrong java version.
* The *filename* property in the json configuration is now environment variable aware.

## 2.0.0 : 2021-07-22

### New

* Java 11 compatibility.

### Changed

* Dependencies update.

## 1.4.2 : 2019-09-18

### Changed
- Update the batch script to change to the main script directory first before executing any command.

## 1.4.1 : 2019-05-10
### Fixed
- The _null_ is treated as false when resolving value.

### Changed
- README.md _default mode value_ correction.

## 1.4.0 : 2018-12-08
### Added
- Configuration directory can now be changed using **-cd command** line argument. This argument takes precedence over **SQL_SERVANT_CONF_DIR** environment variable.

### Fixed
- The _next_ attribute of the queries section is now being processed even if it is in an external file.
- The _description_ attribute of the queries section in an external file is now being considered.

## 1.3.0 : 2018-11-22
### Added
- Defaults, Queries, DBPoolConfig, Params and ListenersConfig now support **filename** property in the configuration file.
- Configuration location can now be customized using the **SQL_SERVANT_CONF_DIR** environment variable.
- Output the **rows affected** in the log.

## 1.2.0 : 2018-10-21
### Added
- Support to script mode.
- Support to single query script _(i.e. sqs)_ mode.
- Support listeners.
- Support parameterized query based on configured parameters.
- Shell script.

## 1.1.0 : 2018-10-07
### Added
- Packages for windows.
- Support SQL server windows authentication.
- More validation was introduced.
- Improve the logging format.
- Improve the build manager.

### Changed
- If the parent QueriesConfig failed the next QueriesConfig attached to it won't execute.