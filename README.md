# UniDisk

A Crawler to search for keywords and compare the score

[![Build Status](https://travis-ci.org/B3J4y/UniDisk.svg?branch=master)](https://travis-ci.org/B3J4y/UniDisk)

## Installation

### Prerequisites

- Java 8
- Maven
- Tomcat 8.X
- IntelliJ Ultimate (preferred)

### Installation steps for primefaces

- build an artifact
- mvn install
- mvn test
- run with tomcat and artifact war

### Docker

We use Docker to simplify development.
Run `docker compose up -d` to create a development environment with a MySQL, Solr and Tomcat Server.
The API is then available under _localhost:8081/unidisk/rest_.
In order for code changes to take effect, the services need to be rebuilt (`docker-compose up -d --build`). This takes some time therefore
it makes sense to instead kill the web service (`docker rm -fv unidisk_api_1`) and start the server via IntelliJ (or any other way).
If the server is started via IntelliJ the API is accessible from _localhost:8080/unidisk_war/rest_.

#### Admin Dashboards

Go to _localhost:8082_ to open Adminer (MySQL Dashboard) or _http://localhost:8983_ for the Solr dashboard.
Enter the following crendetials for Adminer:
Server: _db_
Benutzer: _user_
Passwort: _secret_
Datenbank: _unidisk_

### Authentication

We use Firebase for authentication purposes. Accounts must be created via the Firebase console or CLI. Self sign up is currently not possible.

#### Authentication Method

The authentication method can be changed by modifying the `authentication` property in _unidisk.properties_. If the specified value is not `firebase` all incoming requests are authenticated if any bearer token is set in the request header.

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

### Solr

Follow the installation guide from https://lucene.apache.org/solr/guide/7_0/index.html.

#### Setup

If solr isn't running execute `solr start -p 8983`. The further setup will use port 8983 as default.
If you chose another one make sure to adapt the urls and commands.

##### Firebase

Ask one of the maintainers for the Firebase service account file and place it into _crawler/src/main/resources_ as _firebase-sa.json_. This is only necessary if you want to use Firebase authentication.

##### Create Core

Run `solr create -c unidisc`. After the command finished you should see the message
_Created new core 'unidisc'_ in the console/terminal. You should now see the
unidisc core section at http://localhost:8983/solr/#/unidisc/core-overview.

##### Verify Setup

The test case `testFieldInputAndQuery` in _SimpleCrawlTest_ should now run successfully.
You can also run `shootTheMoon` which crawls websites and posts the result to solr. The test doesn't
terminate but the number of documents in the _unidisc core_ should increase.
