define(["mui",
	"./_controllers/DatasetsVM",
	"./_templates/views.datasets2.tpl.html",
	"./_templates/views.datasets.tutorials.tpl.html",
	"./_templates/views.datasets.google.tpl.html",
	"./_templates/views.datasets.upload.tpl.html",
	"./_templates/views.datasets.geods.tpl.html",
	"./_templates/views.datasets.cccb.tpl.html",
	"./_templates/views.datasets.imports.tpl.html",
	"../../widgets/presets/widgets.presets.module",
	"./session/views.datasets.session.module",
	"./error/views.datasets.error.module",
		"mev-bs-modal",
		"mev-workspace",
		"mev-subscriber",
		"mev-domain-common",
		'js-data-angular',
		"../../domain/domain.module",
		'ng-grid',
		'blob-util',
		"geods"],
function(ng, DatasetsVM, datasetsTemplate, tutorialsTemplate, googleTemplate, uploadTemplate, geodsTemplate, cccbTemplate, importsTemplate){
	var module = ng.module("mui.views.datasets", ["Mev.GeodsModule","ngGrid"], arguments);
	module.config(['$stateProvider', '$urlRouterProvider', "$$animateJsProvider",
	     	function($stateProvider, $urlRouterProvider, $$animateJsProvider){
				$urlRouterProvider.when("/datasets", "/datasets/workspace");
	     		$stateProvider
					.state("root.datasets", {
						url: "/datasets",
						parent: "root",
						displayName: "datasets",
						template: datasetsTemplate,
						redirectTo: "root.datasets.imports.upload",
						controller: ["$scope", "cccbProjects", function(scope, cccbProjects){
							function hasProjects(){
								return _.isArray(cccbProjects) && cccbProjects.length>0
							}
							scope.vm = {
								showCccbTab: hasProjects
							}
						}],
						resolve: {
							"cccbProjects": ["$http", function($http){
								return $http({
									method: 'GET',
									url: '/cccb-projects',
									params: {
										format: "json"
									}
								}).then(function successCallback(response) {
									return response.data;
								}, function errorCallback(response) {
									return [];
								});
							}]
						},
						data: {
							headerUrl: "app/views/datasets/_templates/views.datasets.header.tpl.html"
						},
					})
					.state("root.datasets.imports", {
						url: "",
						parent: "root.datasets",
						displayName: false,
						views: {
							"imports": {
								template: "<div ui-view></div>"
							}
						},
						sticky: true,
						onEnter: ["mevFetchSrc", "mevSubscriberPrompt", function(mevFetchSrc, mevSubscriberPrompt) {
							return mevFetchSrc.fetch("app/views/datasets/views.datasets.module", $$animateJsProvider)
								.then(function(){
									mevSubscriberPrompt();
								})
								.catch(function(e){
									throw e;
								});
						}]
					})
					.state("root.datasets.imports.tutorials", {
						url: "/tutorials",
						parent: "root.datasets.imports",
						template: tutorialsTemplate,
						displayName: "tutorials"
					})
					.state("root.datasets.imports.upload", {
						url: "/upload",
						parent: "root.datasets.imports",
						template: uploadTemplate,
						displayName: "upload"
					})
					.state("root.datasets.imports.tcga", {
						url: "/tcga",
						parent: "root.datasets.imports",
						template: "<div id=\"presetMgr\" presets-list></div>",
						displayName: "TCGA"
					})
					.state("root.datasets.imports.geods", {
						url: "/geods",
						parent: "root.datasets.imports",
						template: geodsTemplate,
						displayName: "GEO"
					})
					.state("root.datasets.imports.cccb", {
						url: "/cccb",
						parent: "root.datasets.imports",
						template: cccbTemplate,
						templateUrl: "", 
						displayName: "CCCB",
						resolve: {
							"projects": ["$http", function($http){
								return $http({
									method: 'GET',
									url: '/cccb-projects',
									params: {
										format: "json"
									}
								}).then(function successCallback(response) {
									return response.data;
								}, function errorCallback(response) {
									return [];
								});
							}]
						},
						controllerAs: "vm",
						controller: ["$scope", "projects", "mevWorkspace", "$http", "$rootScope",
							function(scope, cccbProjects, mevWorkspace, $http, $rootScope){
							_.assign(this, {
								projects: cccbProjects,
								pagination: {
									current: 1
								},
								getProjects: function(){
									return scope.vm.projects;
								},
								import: function(project, file){

									function doImport(payload){
										if(!payload)
											return;

										return $http({
											method: 'POST',
											url: '/cccb-projects',
											data: [payload]
										})
										.then(function(response){
											$rootScope.$broadcast("mev:datasets:list:refreshed");
										})
										.catch(function(error){
											alert("Could not import file:\n" + JSON.stringify(payload));
										});
									}
									function renameIfExists(dataset){
										if(dataset) {
											var name = prompt("A dataset with this name already exists. Please specify a different name", file.name);
											if (_.isNil(name) || _.isEmpty(name) || name === file.name)
												return;
											payload.files[0].name = name;
											return payload;
										}else{
											return payload;
										}
									}
									var payload  = _.cloneDeep(project);
									payload.files=[_.cloneDeep(file)];
									mevWorkspace.getDataset(file.name)
										.then(renameIfExists)
										.then(doImport);



								}
							});
						}]
					})

					.state("root.datasets.imports.google", {
						url: "/google",
						parent: "root.datasets.imports",
						template: googleTemplate,
						displayName: "Google Drive"
					});
	}]);
	// module.controller("DatasetsVM", DatasetsVM);
	return module;
});
