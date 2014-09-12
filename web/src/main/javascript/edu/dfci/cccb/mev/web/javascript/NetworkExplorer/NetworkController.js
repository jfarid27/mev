define([], function(){
    
    
    return function($scope, $rootScope, GraphResource){
        
        $scope.graph = undefined;
        
        $scope.graphParameters = undefined;
        
        $scope.selectedGraph = undefined;
        
        $scope.availableGraphs = [];
        
        $scope.predecessors = [];
        
        $scope.$on('GraphTransitionEvent', function(event, params){
            
            if (params.direction == 'down'){
                
                GraphResource.get({id: params.id, node: params.node}).$promise.then(function(data){
                    $scope.graph = data;
                    
                    $scope.predecessors.push(params.node)
                    
                });
                
            } else {
                    
                if ($scope.predecessors.length > 1) {
                    
                    GraphResource.get({id: params.id, 
                                       node: $scope.predecessors[$scope.predecessors.length - 2]})
                    .$promise.then(function(data){
                        $scope.predecessors = $scope.predecessors.slice(0, $scope.predecessors.length - 1)
                        $scope.graph = data;
                    });
                    
                } else {
                    $rootScope.$emit('GraphDownloadEvent', params.id)
                }
                
            }
            
        })
        
        $scope.graphTransitionCallback = function(params){
        //Exposes graph transition event to scope
            $scope.$emit('GraphTransitionEvent', params)
        }
        
        
        $rootScope.$on('GraphDownloadEvent', function(event, graphId){
            
            GraphResource.get({id: graphId}).$promise.then(function(data){
                $scope.graph = data;
                $scope.predecessors = []
            });
            
        });
        
        $scope.$on('GraphTypeChangeEvent', function(event, graphParameters){
            $scope.graphParameters = graphParameters;
        });
        
    }
    
})