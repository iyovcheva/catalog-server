# Brooklyn-Central Catalog Distribution Module

This module will gather and build the final WAR file that can be deployed onto a tomcat / JBoss webserver.

## How to build

You need to build and install the entire project's modules

    cd /path/to/catalog-server
    mvn clean install
    
## How to run

    mvn jetty:run
    
Then, open up a browser to

    http://localhost:8080/catalog
    
Enjoy!
    
    