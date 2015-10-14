define(["ng"], function(ng){
	var VoomModalDirective = function(alertService, voomVM) { 
         return {
             restrict : 'C',
             scope : {
             	dataset : '=heatmapDataset'
             },
             templateUrl : "app/widgets/analysis/topgo/modal/voomModal.tpl.html",
             link : function(scope, elems, attrs) {
                 scope.voomVM = voomVM;
                 scope.params = {
                        experiment: undefined,
                 		control: undefined
                 };
             	var getPostData = function(){
             		var postRequest = {
             			name: scope.params.name,
             			datasetName : scope.dataset.datasetName,
             			experiment : scope.params.experiment.keys,
             			control : scope.params.control.keys
             		};             		
             		return postRequest;                                		
             	};
                scope.testInit = function(){                	
                	console.debug("VOOM params", scope.params);                    			
                	var postData = getPostData();
                	console.debug("VOOM postData", postData);                    			
        			scope.dataset.analysis.post({
        				datasetName : scope.dataset.datasetName, 
        				analysisType : "voom"
        			}, postData);
                };
             }
         };
	};
	VoomModalDirective.$inject=["alertService", "VoomVM"];
	return VoomModalDirective;
});