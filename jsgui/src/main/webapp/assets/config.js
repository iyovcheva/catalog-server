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
/*
 * set the require.js configuration for your application
 */
require.config({
    /* Give 30s (default is 7s) in case it's a very poor slow network */
    waitSeconds:30,
    
    /* Libraries */
    baseUrl:"assets",
    paths:{
        "jquery":"deps/jquery",
        "underscore":"deps/underscore",
        "text":"deps/text",
        "backbone":"deps/backbone",
        "bootstrap":"deps/bootstrap",
        "marked": "deps/marked"
    },
    
    shim:{
        "underscore":{
            exports:"_"
        },
        "backbone":{
            deps:[ "underscore", "jquery" ],
            exports:"Backbone"
        },
        "bootstrap": { deps: [ "jquery" ] /* http://stackoverflow.com/questions/9227406/bootstrap-typeerror-undefined-is-not-a-function-has-no-method-tab-when-us */ },
        "marked": {
            exports: "marked"
        }
    }
});

/*
 * Main application entry point.
 */
require([
    "backbone", "router"
], function (Backbone, Router) {
    // Replace new line by <br/> HTML tag. We add that to the String prototype as it might be extensively used.
    String.prototype.nl2br = function () {
        return this.replace(new RegExp("\r?\n", "g"), "<br />");
    };

    var router = new Router();
    Backbone.history.start();
});
