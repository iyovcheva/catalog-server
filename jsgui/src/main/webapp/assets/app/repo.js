define([
    "jquery", "underscore", "backbone",
    "text!app/repo.html",
], function ($, _, Backbone,  
        RepoHtml) {

    var RepoView = Backbone.View.extend({
        tagName:"div",
        
        render:function () {
            var repo = this.options.catalog.repositories.find(this.options.trail);
            if (repo==null) {
                this.$el.html("ERROR: missing or invalid repo: "+this.options.trail);
            } else {
                this.$el.html(_.template(RepoHtml)({ repo: repo, trail: this.options.trail, catalog: this.options.catalog }));
            }
            return this;
        },
    })

    return RepoView
})
