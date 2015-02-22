define([
    "underscore", "backbone", "app/model/repository"
], function (_, Backbone, Repository) {

    var Catalog = Backbone.Model.extend({
        repositories: new Repository.Collection()
    });

    return Catalog;
})
