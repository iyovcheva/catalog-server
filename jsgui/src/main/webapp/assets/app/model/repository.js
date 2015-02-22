define([
    "underscore", "backbone"
], function (_, Backbone) {

    var Repository = {}

    Repository.Model = Backbone.Model.extend({
        defaults:function () {
            return {
                url:null,
                name:null,
                owner:null
            }
        },
    })

    Repository.Collection = Backbone.Collection.extend({
        model:Repository.Model,
        url:'/repositories',
        
        find: function(path) {
          console.log(this);
          console.log("finding");
          console.log(path);
          for (ri in this.models) {
            var r = this.models[ri];
            console.log(r);
            if (r.get("owner")+"/"+r.get("name") == path)
              return r;
          }
          return null;
        }
    })

    return Repository
})
