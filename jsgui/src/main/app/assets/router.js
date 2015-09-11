/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/
define([
    "underscore", "jquery", "backbone",
    "app/model/catalog",
    "app/home", "app/blueprints", "app/blueprint",
    "text!app/examples.html", "text!app/tools.html",
], function (_, $, Backbone, Catalog,
        HomeView, BlueprintsView, BlueprintView, ExamplesHtml, ToolsHtml) {

    var catalog = new Catalog();
    
    var Router = Backbone.Router.extend({
        routes:{
            'home':'homePage',
            'blueprints': 'blueprintsPage',
            'blueprint/*trail': 'blueprintPage',
            'examples':'examplesPage',
            'tools':'toolsPage',
            
            '*path':'defaultRoute'
        },

        showView: function(selector, view) {
                // close the previous view - does binding clean-up and avoids memory leaks
                if (this.currentView && this.currentView.close) {
                    this.currentView.close();
                }
                // render the view inside the selector element
                view.render()
                $(selector).html(view.el);
                this.currentView = view;
                return view
        },

        defaultRoute: function(path) {
            this.homePage('auto')
        },

        showPage: function(content, tabSelector) {
            if (typeof content === "string" || typeof content === "function") {
                $("#application-content").html(content);
            } else {
                this.showView("#application-content", content);
            }
            $("nav .active").removeClass("active");
            if (tabSelector) $(tabSelector).closest("li").addClass("active");
        },
                
        homePage:function () {
            this.showPage(new HomeView({}))
        },
        blueprintsPage:function () {
            this.showPage(new BlueprintsView({catalog: catalog}), ".nav1_blueprints");
        },
        blueprintPage: function(trail) {
            this.showPage(new BlueprintView({trail: trail, catalog: catalog}), ".nav1_blueprints");
        },
        examplesPage:function () {
            this.showPage(_.template(ExamplesHtml, {}), ".nav1_examples");
        },
        toolsPage:function () {
            this.showPage(_.template(ToolsHtml, {}), ".nav1_tools");
        },
    });

    return Router;
});
