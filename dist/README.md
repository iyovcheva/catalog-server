# Brooklyn-Central Catalog Distribution Module

This module will gather and build the final WAR file that can be deployed onto a tomcat / JBoss webserver.

## How to build

You need to build and install the entire project's modules

    mvn clean install
    
## How to run

    cd /path/to/dist
    mvn jetty:run
    
Then, open up a browser to

    http://localhost:8080/catalog
    
Enjoy!
    
    