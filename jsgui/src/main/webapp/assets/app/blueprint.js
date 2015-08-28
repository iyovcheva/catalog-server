define([
    'jquery', 'underscore', 'bootstrap', 'backbone', 'marked', 'app/model/blueprint',
    'text!app/blueprint.html', 'text!app/card.html'
], function ($, _, bootstrap, Backbone, Marked, Blueprint, BlueprintHtml, CardHtml) {

    var BlueprintView = Backbone.View.extend({
        tagName: 'div',

        initialize: function() {
            _.bindAll(this, 'render', 'renderCard');

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

        render: function() {
            if (!this.blueprint.loaded) {
                this.$el.html('LOADING...');
            } else {
                this.$el.html(_.template(BlueprintHtml)({
                    $: $,
                    marked: Marked,
                    view: this,
                    trail: this.options.trail,
                    catalog: this.options.catalog,
                    blueprint: this.blueprint
                }));
            }

            return this;
        },

        renderCard: function(item, cssClass) {
            var card = _.template(CardHtml);
            return card({
                item: item,
                cssClass: cssClass
            });
        }
    });

    return BlueprintView;
});