define([
    "jquery", "underscore", "backbone",
    "text!app/repos.html",
], function ($, _, Backbone, 
        ReposHtml) {

    var ReposView = Backbone.View.extend({
        tagName:"div",
        events:{
//            'click #add-new-application':'createApplication',
        },
        
        repos: [],
        initialize:function () {
            var that = this;
            this.catalog = this.options.catalog;
            this.repositories = this.catalog.repositories;
            
            this.repositories.on('all', this.onRepoEvent, this);
            this.repositories.fetch({
                success: function () {
                    that.repositories.loaded = true;
                    that.render();
                }
                });
        },
        
        onRepoEvent: function(evt, coll, resp) {
            if (evt == "error") {
                this.$el.html("ERROR contacting server for repositories");
                console.log("error");
                console.log(resp);
            } else if (evt == "add" || evt == "change" || evt == "remove" || evt == "reset") {
                if (!this.repositories.loaded) {
                    this.render();
                }
            }
        },
        
        render:function () {
            if (!this.repositories.loaded) {
                // TODO replace with overlay
                this.$el.html("LOADING...");
            } else {
                this.$el.html(_.template(ReposHtml)({ repos: this.repositories.models }))
            }
            
            return this;
        },
    })

    return ReposView
})
