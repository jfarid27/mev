define([ 'angular', './NetworkExplorerModule', 'angular_mocks'], function(angular, NWEM){
    
    beforeEach(function(){
        
        angular.mock.module('NetworkExplorer')
        
    })
    
    describe('Network Explorer Module', function(){
        
        
        
        describe('NetworkController', function(){
            
            var scope, rootScope, graphResource, deferred, promise, timeout
            
            beforeEach(function(){
                
                inject(function($rootScope, $controller, $q, $timeout){
                    
                    //Deferred flusher
                    timeout = $timeout
                    
                    //Mock graph resource
                    graphResource = jasmine.createSpyObj('graphResource', ['get'])
                    
                    //Create deferred object
                    deferred = $q.defer()
                    
                    //pull Promise
                    promise = deferred.promise
                
                    //Add $promise to resource object
                    graphResource.get
                        .andReturn( {$promise: promise} )
                    
                    scope = $rootScope.$new();
                    rootScope = $rootScope;
                    
                    ctrl = $controller('NetworkController', {
                        $scope: scope,
                        $rootScope: rootScope,
                        GraphResource: graphResource
                    })
                    
                })
                
            })
            
            describe('scope', function(){
                it('should be defined', function(){
                    expect(scope).toBeDefined()
                })
            })
            
            describe('when GraphDownloadEvent is raised', function(){
                
                beforeEach(function(){
                    
                   //Emit call that will launch async
                   rootScope.$emit('GraphDownloadEvent', 'foo')
                   
                   //Resolve the given promise with this data
                   deferred.resolve({
                        id:'foo', 
                        data: 'bar'
                    })
                   
                   
                })
                
                it('should call graphResource with specified node id', function(){
                    expect(graphResource.get).toHaveBeenCalledWith({id:'foo'})
                })
                                
                it('should process response and add returned network object to scope', function(){
                    
                    //Dequeue deferred promise
                    timeout.flush()
                    
                    expect(scope.graph.id).toBe('foo')
                    expect(scope.graph.data).toBe('bar')
                })
                
            })
            
            describe('when GraphTypeChangeEvent is raised', function(){
                
                beforeEach(function(){
                    
                    graphParameters = {'type': 'foo'}
                    
                   //Emit call that will launch async
                   scope.$emit('GraphTypeChangeEvent', graphParameters)
                   
                })
                
                it('should update graphParameters with received object', function(){
                    expect(scope.graphParameters.type).toBe('foo')
                })
                
            })
            
            describe('when GraphTransitionEvent is raised', function(){
                
                describe('with direction down property', function(){
                    
                    beforeEach(function(){

                       //Emit call that will launch async
                       scope.$emit('GraphTransitionEvent', {id:'foo', node:'bar', direction:'down'})

                       //Resolve with this data
                       deferred.resolve({
                            id:'foo', 
                            data: 'bar'
                        })


                    })

                    it('should call graphResource with specified node id', function(){
                        expect(graphResource.get).toHaveBeenCalledWith({id:'foo', node:'bar'})
                    })
                    
                    it('should add parent level main node to stack', function(){
                        
                        timeout.flush()
                        expect(scope.predecessors.length).toBe(1)
                        expect(scope.predecessors[0]).toBe('bar')
                    })
                    
                    it('should load node subgraph with returned object', function(){
                        
                        timeout.flush()
                        expect(scope.graph.id).toBe('foo')
                        expect(scope.graph.data).toBe('bar')
                    })
                    
                })
                
                describe('with no direction property to go up a level', function(){
                    
                    describe('from general level for inductive case', function(){
                        beforeEach(function(){

                            //Emit call that will launch async
                            scope.predecessors.push('bar')
                            scope.predecessors.push('zed')
                            scope.predecessors.push('ant')

                            //Recall to go a level up
                            scope.$emit('GraphTransitionEvent', {id:'foo', direction:'up'})

                            deferred.resolve({
                                id:'foo', 
                                data: 'zed'
                            })


                        })
                        
                        it('should call for the graph one level up from current level', function(){
                            timeout.flush()
                            expect(graphResource.get).toHaveBeenCalledWith({id:'foo', node:'zed'})
                        })
                        
                        it('should remove level from stack if return was successful', function(){

                            timeout.flush()
                            expect(scope.predecessors.length).toBe(2)
                        })

                        it('should load base graph if return was successful', function(){

                            timeout.flush()
                            expect(scope.graph.id).toBe('foo')
                            expect(scope.graph.data).toBe('zed')
                        })
                    })
                    
                    describe('from one level below base level for basis case', function(){
                        
                        //Basis case
                        
                        beforeEach(function(){

                            //Emit call that will launch async
                            scope.predecessors.push('bar')

                            //Recall to go a level up
                            scope.$emit('GraphTransitionEvent', {id:'foo', direction:'up'})

                            deferred.resolve({
                                id:'foo', 
                                data: 'zed'
                            })


                        })
                        
                        it('should call for the base graph with no node parameter', function(){
                            timeout.flush()
                            expect(graphResource.get).toHaveBeenCalledWith({id:'foo'})
                        })
                        
                        it('should remove level from stack if return was successful', function(){

                            timeout.flush()
                            expect(scope.predecessors.length).toBe(0)
                        })

                        it('should load base graph if return was successful', function(){

                            timeout.flush()
                            expect(scope.graph.id).toBe('foo')
                            expect(scope.graph.data).toBe('zed')
                        })
                    })
                    
                    
                })
                
            })
            
            describe('scoped callback transformer for graphTransitionEvent', function(){
                
                it('should raise graphTransitionEvent when fired with specified node id', function(){
                    
                    spyOn(scope, '$emit')
                    
                    scope.graphTransitionCallback({'id':'432', 'node':'777', 'direction': 'down'})
                    
                    expect(scope.$emit).toHaveBeenCalledWith('GraphTransitionEvent', {'id':'432', 'node':'777', 'direction': 'down'})
                    
                })
                
            })
            
        })
        
    })
    
    
})