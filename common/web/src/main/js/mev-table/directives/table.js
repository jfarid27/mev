(function(){

    define([], function(){
        return function(module){

            return function(){
                return {
                    restrict : 'E',
                    scope : {
                        data : "=data",
                        settings : "=settings"
                    },
                    templateUrl : module.path + '/templates/table.tpl.html',
                    link : function(scope) {
                        scope.rowCollection = scope.data
                    }
                }
            }

        }
    })

})
