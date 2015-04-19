var app = angular.module('signUpModule', ['ngRoute','ngResource','ui.bootstrap']);

app.config(function($routeProvider, $httpProvider) {

	$httpProvider.defaults.headers.post['Content-Type'] = 'application/json;charset=utf-8';

	$httpProvider.defaults.headers["delete"] = {'Content-Type': 'application/json;charset=utf-8'};

});

app.controller('signupController', ['$scope','$modal','$log','$http', function ($scope, $modal, $log, $http) {
	
	$scope.user = {};
	
	$scope.checkUserName = function(user){
		console.log(user);
		 $http({
	          method: 'GET',
	          url: 'checkUserName?name=' + user.username
	        }).success(function(data, status, headers, cfg) {
	        	console.log(data);
	        	if(data == 'BAD_REQUEST')
	        		$scope.userNameError = true;
	        	else
	        		$scope.userNameError = false;
	        }).error(function(data, status, headers, cfg) {
	        });
	}
	
	$scope.checkPassword = function(user){
		if(user.password != user.password2)
			$scope.showPasswordError = true;
		else
			$scope.showPasswordError = false;
	}
	
	$scope.signup = function(user){
		$scope.checkPassword(user);
		console.log(user);
	}
	
	
}]);
