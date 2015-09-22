define([
    'jquery', 'underscore', 'bootstrap', 'backbone', 'marked',
    'app/model/blueprint',
    'text!app/blueprint.html', 'text!app/card.html'
], function ($, _, bootstrap, Backbone, Marked, Blueprint, BlueprintHtml, CardHtml) {

    var BlueprintView = Backbone.View.extend({
        tagName: 'div',

        initialize: function() {
            var that = this;

            this.blueprint = new Blueprint.Model({id: this.options.trail});
            this.blueprint.on('all', this.onEvent, this);
            this.blueprint.fetch({
                success: function() {
                    that.blueprint.loaded = true;
                    that.render();
                }
            });
        },

        onEvent: function(event, coll, response) {
            if (event == 'error') {
                this.$el.html('ERROR contacting server for blueprint: ' + this.options.trail);
                console.log('error');
                console.log(response);
            } else if (event == 'add' || event == 'change' || event == 'remove' || event == 'reset') {
                if (!this.blueprint.loaded) {
                    this.render();
                }
            }
        },

        events: {
            'click #blueprint-tabs a': 'onClickTab',
        },

        onClickTab: function(e) {
            e.preventDefault();
            $(e.target).tab('show')
            return false;
        },

        renderCard: _.template(CardHtml),

        render: function() {
            if (!this.blueprint.loaded) {
                this.$el.html('LOADING...');
            } else {
                var that = this;
                this.$el.html(_.template(BlueprintHtml)({
                    $: $,
                    marked: Marked,
                    trail: this.options.trail,
                    catalog: this.options.catalog,
                    blueprint: this.blueprint,
                    renderCard: this.renderCard
                }));
                $('#overview img').each(function() {
                    $(this).attr('src', 'https://raw.githubusercontent.com/' + that.blueprint.get('token') + '/master/' + $(this).attr("src"));
                });
                $('#overview a:not([href^=http])').each(function() {
                    $(this).attr('href', 'https://github.com/' + that.blueprint.get('token') + '/blob/master/' + $(this).attr("href"))
                });
            }

            return this;
        }
    });

    return BlueprintView;
});