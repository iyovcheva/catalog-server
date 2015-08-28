define([
    'jquery', 'underscore', 'backbone',
    'text!app/blueprints.html',
], function ($, _, Backbone, BlueprintsHtml) {

    var BlueprintsView = Backbone.View.extend({
        tagName: 'div',

        initialize: function() {
            var that = this;
            this.catalog = this.options.catalog;
            this.repositories = this.catalog.repositories;
            
            this.repositories.on('all', this.onEvent, this);
            this.repositories.fetch({
                success: function() {
                    that.repositories.loaded = true;
                    that.render();
                }
            });
        },

        events: {
            'keyup input.blueprint-search': 'onSearch'
        },
        
        onEvent: function(event, coll, response) {
            if (event == 'error') {
                this.$el.html('ERROR contacting server for blueprints');
                console.log('error');
                console.log(response);
            } else if (event == 'add' || event == 'change' || event == 'remove' || event == 'reset') {
                if (!this.repositories.loaded) {
                    this.render();
                }
            }
        },

        onSearch: function() {
            var list = this.repositories.models;

            if ($('input.blueprint-search').val() != '') {
                list = _.filter(this.repositories.models, function(item) {
                    return item.get('repoName').indexOf($('input.blueprint-search').val()) > -1;
                });
            }

            $('h2').html(this.renderTitle(list.length));
            $('ul.list-repositories').empty();
            _.each(list, function(item) {
                var tpl = _.template('<li><a href="#blueprint/<%= repository.get("token") %>"><%= repository.get("repoName") %></a></li>');
                $('ul.list-repositories').append(tpl({repository: item}));
            });
        },
        
        render: function() {
            if (!this.repositories.loaded) {
                this.$el.html('LOADING...');
            } else {
                this.$el.html(_.template(BlueprintsHtml)({
                    view: this,
                    repositories: this.repositories.models
                }))
            }
            
            return this;
        },

        renderTitle: function(number) {
            return number + ' blueprint' + (number == 1 ? '' : 's') + ' available';
        }
    });

    return BlueprintsView;
});