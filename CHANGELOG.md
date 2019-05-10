# Changelog

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