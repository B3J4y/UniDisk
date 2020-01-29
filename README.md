# UniDisk
A Crawler to search for keywords and compare the score

[![Build Status](https://travis-ci.org/B3J4y/UniDisk.svg?branch=master)](https://travis-ci.org/B3J4y/UniDisk)

## Installation
### Prerequisites

* Java 8
* Maven
* Tomcat 8.X
* IntelliJ Ultimate (preferred)

### Installation steps for primefaces
* build an artifact
* mvn install
* mvn test
* run with tomcat and artifact war


### Database

The project contains configuration files for an in memory as well as a MySQL database.
The configuration can be changed by replacing the content of the config variable in the 
HibernateUtil class. 

Additional configurations can be added by placing a configuration file into the resource directory. Afterwards
reference that file in the HibernateUtil class by changing the config variable to
```
public class HibernateUtil {
    private static final String someOtherConfig = "hibernate.foo.bar.cfg.xml";
    private static String config = someOtherConfig;  
    ...
}
```
#### Mock setup

You can populate the database by changing the content of the TestSetupBean class. The init
function stores predefined data from the ApplicationState in the currently configured database.
This happens every time the server starts. It's therefore recommended to remove the content
of the init function after the first start of the app (unless you use an in memory database).

##### In Memory Problems

If you are on pages that require a specific entity id (e.g. project page) and redeploy the server, the
former id might not exist anymore and the page is unable to load. 

#### MySQL Development Problems

##### org.hibernate.id.IdentifierGenerationException: could not read a hi value - you need to populate the table: hibernate_sequence
This error occurs if you truncate the hibernate_sequence table. Delete the database instance and restart the server to fix this problem.
