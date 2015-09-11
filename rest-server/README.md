# Brooklyn-Central Catalog REST Server

This module contains the REST server that can be deployed as a standalone Java app onto a tomcat / JBoss webserver.

## How to build

You need to build and install the entire project's modules

    mvn clean install
    
## How to run

    mvn jetty:run
    
Then, open up a browser to

    http://localhost:8080/rest/repositories
    
The list of all available repositories should appear there.
    
    