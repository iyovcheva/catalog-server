define([
    'underscore', 'backbone', 'app/model/repository'
], function (_, Backbone, Repository) {

    var Catalog = Backbone.Model.extend({
        repositories: new Repository.Collection()
        // TODO: Add other type of catalog items, i.e. policies, tools, etc...
    });

    return Catalog;
})
