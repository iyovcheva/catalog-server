# Brooklyn-Central Catalog

This project allows setting up a catalog of repositories for user-created Brooklyn blueprints
which can be served up in an easy-to-navigate way.

## How it Works 

### Overview 

The catalog server provides the following functionality:

* Reads a list of repositories from a central list
* Reads individual repositories to populate details
* Exposes those details via REST
* Runs a JS app which provides a GUI to browse the catalog

### Central Repository List

The list of repositories containing user-created Brooklyn blueprints can be found in the [Brooklyn Community Catalog](https://github.com/brooklyncentral/brooklyn-community-catalog) Github repository.

There you will find documentation on the required blueprint repository structure.


## Implementation Notes

* Server side data is read once at startup and never refreshed during a browsing session

	* Potential improvements: Periodic updates or hooks when changes are commited to the individual GitHub repos


## TODO

* Short term:
	* Validation tool
		* Avoid multiple repos with the same name and author
		* Automatically determine if a user repo is valid when a PR is submitted
	* Github API integration
		* More information about each repo would be accessible
		* Hooks 	


* Longer term:
	* Modify the brooklyn catalog file format to allow multiple entries and smoother integration
	* Java blueprint support
	* Brooklyn integration with online catalogs
