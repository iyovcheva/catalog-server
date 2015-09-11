define([
    "jquery", "underscore", "backbone",
    "text!app/home.html",
], function ($, _, Backbone,  
        HomeHtml) {

    var HomeView = Backbone.View.extend({
        tagName:"div",
        
        render:function () {
            this.$el.html(_.template(HomeHtml))
            return this;
        },
    })

    return HomeView
})
