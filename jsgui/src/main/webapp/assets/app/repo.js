define([
    "jquery", "underscore", "backbone",
    "text!app/repo.html",
], function ($, _, Backbone,  
        RepoHtml) {

    var RepoView = Backbone.View.extend({
        tagName:"div",
        
        initialize: function() {
            _.bindAll(this, 'renderRepository', 'renderError');
        },
        
        render:function () {
            var loaded = this.options.catalog.repositories.detail(this.options.trail, this.renderRepository, this.renderError);
            if (loaded) return;
            this.$el.html("LOADING..."+
                _.template(RepoHtml)({ repo: null, trail: this.options.trail, catalog: this.options.catalog }));
            return this;
        },
        
        renderRepository: function(repo) {
            this.$el.html(_.template(RepoHtml)({ repo: repo, trail: this.options.trail, catalog: this.options.catalog }));
        },
        renderError: function(msg) {
            this.$el.html(_.escape("ERROR: "+msg));
        }
    })

    return RepoView
})
