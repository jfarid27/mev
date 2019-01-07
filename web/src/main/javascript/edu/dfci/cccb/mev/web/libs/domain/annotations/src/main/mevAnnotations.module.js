define(["mui",
	"./endpoint/AnnotationProjectIdResource",
	"./endpoint/AnnotationFieldsResource",
	"./endpoint/AnnotationValuesResource",
	"./endpoint/AnnotationExportResource",
	"./model/AnnotationRepository",
	"./utils/OpenRefineBridge",
	], function(ng){
"use strict";
	return ng.module("mevAnnotations", arguments, arguments);	
});