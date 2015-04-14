define([
    "underscore", "backbone"
], function (_, Backbone) {

    var Repository = {}
    var RepositoryDetail = {}

    Repository.Model = Backbone.Model.extend({
        defaults:function () {
            return {
                url:null,
                name:null,
                owner:null
            }
        },
        getOwnerSlashName: function() {
            return this.get("owner")+"/"+this.get("name");
        }
    });

    RepositoryDetail.Model = Backbone.Model.extend({
        constructor: function(repositoryModel) {
            this.repositoryModel = repositoryModel;
            Backbone.Model.apply( this );
        },
        url: function() { return "/repo/"+this.repositoryModel.getOwnerSlashName(); },
        defaults: function() {
            result = this.repositoryModel.defaults();
            result.bomText = null;
            result.bomMap = null;
            return result;
        }
    });

    Repository.Collection = Backbone.Collection.extend({
        model:Repository.Model,
        cache: {},
        url:'/repos',
        
        // gets detail, using cache if cached (and returned true)
        // or looking up async if not yet known (and returning false)
        // or throwing "no-such-repo"
        detail: function(path, callback, onError) {
            if (callback==null) throw "callback required";
            var that = this;
            var result = that.cache[path];
            if (result) {
                callback(result);
                return true;
            }

            loadModelFn = function() {
              try {
                for (ri in that.models) {
                    var r = that.models[ri];
                    if (r.getOwnerSlashName() == path) {
                        detail = new RepositoryDetail.Model(r);
                        detail.fetch({
                            success: function() {
                                that.cache[path] = detail;
                                callback(detail);
                            },
                            error: function(_detail, result) {
                                    onError("repository load error for "+r.getOwnerSlashName());
                                    console.log(result); 
                                }
                        });
                        return true;
                    }
                }
                onError("no such repository "+r.getOwnerSlashName()); 
              } catch ( e ) {
                onError("could not load repository "+r.getOwnerSlashName()); 
                console.log(e);
                throw e; 
              }
            }
            
            if (!that.models || !that.models.length) {
                that.fetch({success: loadModelFn, 
                    error: function(x) { 
                        console.log("Error fetching models");
                        console.log(x);
                        if (onError) onError("Repository list unavailable"); 
                    } 
                });
            } else {
                loadModelFn();
            }
          
        }
    })

    return Repository
})
