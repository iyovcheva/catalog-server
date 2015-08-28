define([
    'underscore', 'backbone', 'app/model/repository'
], function (_, Backbone, Repository) {

    var Blueprint = {};

    Blueprint.Model = Backbone.Model.extend({
        url: function() {
            return '/items/' + this.id;
        },
        defaults: function() {
            return {
                repository: Repository.Model.default,
                token: null,
                description: null,
                documentation: null,
                catalogBomString: null,
                catalogBomYaml: null,
                masterCommitHash: null,
                license: null,
                changelog: null
            }
        }
    });

    return Blueprint;
});