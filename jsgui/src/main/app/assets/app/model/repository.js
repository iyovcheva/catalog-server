define([
    'underscore', 'backbone'
], function (_, Backbone) {

    var Repository = {};

    Repository.Model = Backbone.Model.extend({
       defaults: function() {
           return {
               repoUrl: null,
               repoName: null,
               author: null,
               token: null
           }
       }
    });

    Repository.Collection = Backbone.Collection.extend({
        model: Repository.Model,
        url: 'api/repositories'
    });

    return Repository;
});