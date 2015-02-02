(function(){

    var deps = ['../module' './directives/table']

    define(deps, function(mod, table){

        var module = mod.module('mev-table', ['smart-table'])

        module.path = '.'

        //directives
        module.directive('mev-table', ['$filter', table(module)])

        //controllers

        //services

        return mod
    })

})()
