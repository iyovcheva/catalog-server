
# Brooklyn-Central Catalog

This project allows setting up a catalog of repositories for Brooklyn blueprints
which can be served up in an easy-to-navigate way.

## How it Works 

The catalog project will:

* read a list of repos from one place
* read individual repositories to populate details
* expose that through REST
* run a JS app which gives access to such detail

### Repository List Format

The repository list file, e.g. `rest-server/src/test/content/repositories.yml`,
should be a list of URLs or maps containing `url` and optionally an `owner` and `name`.
`github:user/repo` (and `github:/path/at/github/dot/com` for subfolders) are supported
as URLs.  

The reason for this choice is that this is the minimal list of information which won't change
and which requires approval. Other data on a repository can be stored in the repository itself. 

For full details see RepositoryLink and associated tests.

### Repository Structure

A repository *must* contain the following:

* catalog.bom - the catalog being advertised
  * can provide name, description, version, icon - etc
  * TODO should this be `xxx.catalog.bom` to make it easier to disambiguate?

TODO may also support:

* `overview.md` - a nicely formatted overview shown on the front tab 
  * TODO support `{% include PATH %}` in the markdown where `PATH` is relative to the repository,
    e.g. to link to example
  * TODO include nice code formatting
* `examples/`
  * `ex1.bom` and optionally `ex1.md` 

TODO considering also support:

* `index.yml`
  * override description, icon, etc
  * point at different overview file
  * show the order that examples should be shown



Implementation is described in Repository.java


## Implementation Notes

* server side data is read once at startup; could add periodic update thereafter?, 
  or bring in a cache to which changes can be pushed


## TODO

Very short term:

* JS load repo list if needed when viewing a repo page
* server load repo content
* JS load repo content


Then:

* Validation tool - no two owner/repo combos the same
* Search - simple search of repo name


Longer term:

* Modify the brooklyn catalog file format to allow multiple entries and smoother integration
* Let brooklyn integrate with online catalogs


(much more in my notes