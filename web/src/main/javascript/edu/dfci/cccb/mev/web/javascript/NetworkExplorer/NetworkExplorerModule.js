(function(){
    define(['angular', './NetworkController'], function(angular, NetworkController){


        angular.module('NetworkExplorer', [])
        .factory('AvailableGraphTypesResource', function(){
            return null
        })
        .factory('GraphVisualizationResource', [function(){
            return null
        }])
        .factory('GraphResource', ['$resource','$q', function($resource, $q){
           return null
        }])
        .controller('NetworkController', ['$scope', '$rootScope', 'GraphResource', NetworkController])
        .directive('NetworkVisualization', ['GraphVisualizationResource', function(GraphVisualization){

            return {
                'restrict': 'E',
                'scope': {
                    'graph': '=',
                    'transition': '&',
                    'width': '@',
                    'height': '@'
                },
                'link': function(scope, element, attributes){

                    var visualization = new GraphVisualization(element[0], {
                        height: scope.height,
                        width: scope.width
                    });
                    
                    scope.$watch('graph.data', function(newval, oldval){
                        visualization.draw(newval)
                    })
                    
                } //Link
            } //Return

        }])


    })

})()

