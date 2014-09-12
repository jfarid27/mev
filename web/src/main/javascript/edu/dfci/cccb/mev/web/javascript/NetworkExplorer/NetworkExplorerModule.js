define(['angular', './NetworkController', 'd3'], function(angular, NetworkController, d3){
    
    
    angular.module('NetworkExplorer', [])
    .factory('GraphResource', ['$resource','$q', function($resource, $q){
        
        //Mock for the resource until the server is ready
        return {
            get: function(params, postdata, success, error){
                
                var deferred = $q.defer()
                
                var graphData = {
                    'id': '53453',
                    'data': {
                        'nodes': [{'id':'23429', 'name':'SG1', 'children':true},
                                  {'id':'N1', 'name':'N1'},
                                  {'id':'N6', 'name':'N6'},
                                  {'id':'N7', 'name':'N7'},
                                  {'id':'N8', 'name':'N8'},
                                  {'id':'N9', 'name':'N9'},
                                  {'id':'N10', 'name':'N10'},
                                 
                                 ],
                        'edges': [{'source':'N1', 'target':'SG1'},
                                  {'source':'N6', 'target':'SG1'},
                                  {'source':'N6', 'target':'N7'},
                                  {'source':'N7', 'target':'N8'},
                                  {'source':'N8', 'target':'N9'},
                                  {'source':'N8', 'target':'N10'}
                                 ]
                    }
                }
                
                var subgraphData = {
                    'id': '23429',
                    'data': {
                        'nodes': [{'id':'N2', 'name':'N2', 'children':[]},
                                  {'id':'N3', 'name':'N3', 'children':[]},
                                  {'id':'N4', 'name':'N4', 'children':[]},
                                  {'id':'N5', 'name':'N5', 'children':[]}
                                 ],
                        'edges': [{'source':'N2', 'target':'N5'},
                                  {'source':'N2', 'target':'N3'},
                                  {'source':'N2', 'target':'N4'}
                                 ]
                    }
                }
                
                if (params.id == subgraphData.id){
                    deferred.resolve(subgraphData)
                } else if (params.id == graphData.id) {
                    deffered.resolve(graphData)
                } else {
                    deferred.reject()
                }
                
                return {
                    $promise: deferred.promise
                }
            }
            
        }
        
    }])
    .controller('NetworkController', ['$scope', '$rootScope', 'GraphResource', NetworkController])
    .directive('NetworkVisualization', [function(){
        
        return {
            'restrict': 'E',
            'scope': {
                'graph': '=',
                'transition': '&',
                'width': '@',
                'height': '@'
            },
            'link': function(scope, element, attributes){
                
                var svg = d3.select(element[0]).append("svg")
                    .attr("width", scope.width)
                    .attr("height", scope.height);
                
                var force = d3.layout.force()
                    .charge(-120)
                    .linkDistance(30)
                    .size([scope.width, scope.height]);
                
                scope.$watch('graph.data', function(newval, oldval){
                    
                    if (newval) {
                        
                        var graph = newval;
                        
                        force
                            .nodes(graph.nodes)
                            .links(graph.edges)

                        var link = svg.selectAll(".link")
                            .data(graph.edges)
                        .enter().append("line")
                            .attr("class", "link")
                            .style("stroke-width", function(d) { return Math.sqrt(d.value); });

                        var node = svg.selectAll(".node")
                            .data(graph.nodes)
                        .enter().append("circle")
                            .attr("class", "node")
                            .attr("r", 5)
                            .style("fill", function(d) { return d.group ? color(d.group) : 'blue'; })
                            .call(force.drag)
                            .on('mouseclick', function(d){
                                scope.transition({id:scope.graph.id, node:d.id})
                            })

                        node.append("title")
                            .text(function(d) { return d.name; });

                        force.on("tick", function() {
                            link.attr("x1", function(d) { return d.source.x; })
                                .attr("y1", function(d) { return d.source.y; })
                                .attr("x2", function(d) { return d.target.x; })
                                .attr("y2", function(d) { return d.target.y; });

                            node.attr("cx", function(d) { return d.x; })
                                .attr("cy", function(d) { return d.y; });
                        })
                        
                        force.start()
                        
                        setTimeout(function(){  force.stop()  }, 3000)
                        
                        
                    } //If
                }) //Watch
                
            } //Link
        } //Return
        
    }])
    
    
})