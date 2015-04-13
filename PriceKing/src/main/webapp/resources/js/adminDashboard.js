var app = angular.module('myModule', ['ui.bootstrap','xeditable','isteven-multi-select','ngTable']);

app.controller('ModalDemoCtrl', ['$scope','$modal','$log','ngTableParams','$filter','$http', function ($scope, $modal, $log, ngTableParams,$filter,$http) {

  $scope.items = ['item1', 'item2', 'item3'];
  $scope.inputList = [
                      { firstName: "Peter",    lastName: "Parker", selected: false },
                      { firstName: "Mary",     lastName: "Jane",   selected: false },
                      { firstName: "Bruce",    lastName: "Wayne",  selected: false  },
                      { firstName: "David",    lastName: "Banner",  selected: false },
                      { firstName: "Natalia",  lastName: "Romanova", selected: false },
                      { firstName: "Clark",    lastName: "Kent",  selected: true  }
   ];
  getUsers();
  
 function getUsers(){
	 $http.get('/getUsers').then(function(response) {
		 $scope.users = response.data;
	 });

 } 
  
 

  
  $scope.tableParams = new ngTableParams(

  {

		  page : 1, // show first page

		  count : 10, // count per page

		  filter : {

			  fName : '' // initial

		  // filter

		  },

		  sorting : {

			  fName : 'asc' // initial

		  // sorting

		  	}

		  },

		  {

			  total : $scope.inputList.length, // length

		  // of

		  // data

			  getData : function($defer, params) {

			  console.log($scope.inputList);

		  // use build-in angular filter

			  var filteredData = params.filter() ? $filter('filter')($scope.inputList,params.filter()): $scope.inputList;

			  var orderedData = params.sorting() ? $filter('orderBy')(filteredData,params.orderBy()): $scope.inputList;

			  params.total(orderedData.length); // set

		  // total

		  // for

		  // recalc

		  // pagination

		  $scope.tbrPlans = orderedData.slice((params.page() - 1) * params.count(),

		  params.page() * params.count());

		  	$defer.resolve($scope.tbrPlans);

		  }

	});



  $scope.openvendor = function (size) {
	  
    var modalInstance = $modal.open({
      templateUrl: 'addProduct.html',
      controller: 'productController',
      size: size,
      resolve: {
        items: function () {
          return $scope.items;
        },
        inputList: function (){
        	return $scope.inputList;
        }
      }
    });

    modalInstance.result.then(function (selectedItem) {
      $scope.selected = selectedItem;
    }, function () {
      $log.info('Modal dismissed at: ' + new Date());
    });
  };
  
  $scope.openCouponModal = function (size) {
	  
	    var modalInstance = $modal.open({
	      templateUrl: 'addCoupon.html',
	      controller: 'couponController',
	      size: size,
	      resolve: {
	        items: function () {
	          return $scope.items;
	        },
	        inputList: function (){
	        	return $scope.inputList;
	        }
	      }
	    });

	    modalInstance.result.then(function (selectedItem) {
	      $scope.selected = selectedItem;
	    }, function () {
	      $log.info('Modal dismissed at: ' + new Date());
	    });
	  };
	  
	  
}]);



// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

app.controller('productController', function ($scope, $modalInstance, items, inputList) {

  $scope.items = items;
  $scope.inputList = inputList;
  $scope.selected = {
    item: $scope.items[0]
  };
  
  

  $scope.ok = function () {
    $modalInstance.close($scope.selected.item);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});

app.controller('couponController', function ($scope, $modalInstance, items, inputList) {

	  $scope.items = items;
	  $scope.inputList = inputList;
	  $scope.selected = {
	    item: $scope.items[0]
	  };
	  
	  

	  $scope.ok = function () {
	    $modalInstance.close($scope.selected.item);
	  };

	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	  };
	});

$.fn.pageMe = function(opts){
    var $this = this,
        defaults = {
            perPage: 7,
            showPrevNext: false,
            hidePageNumbers: false
        },
        settings = $.extend(defaults, opts);
    
    var listElement = $this;
    var perPage = settings.perPage; 
    var children = listElement.children();
    var pager = $('.pager');
    
    if (typeof settings.childSelector!="undefined") {
        children = listElement.find(settings.childSelector);
    }
    
    if (typeof settings.pagerSelector!="undefined") {
        pager = $(settings.pagerSelector);
    }
    
    var numItems = children.size();
    var numPages = Math.ceil(numItems/perPage);

    pager.data("curr",0);
    
    if (settings.showPrevNext){
        $('<li><a href="#" class="prev_link">«</a></li>').appendTo(pager);
    }
    
    var curr = 0;
    while(numPages > curr && (settings.hidePageNumbers==false)){
        $('<li><a href="#" class="page_link">'+(curr+1)+'</a></li>').appendTo(pager);
        curr++;
    }
    
    if (settings.showPrevNext){
        $('<li><a href="#" class="next_link">»</a></li>').appendTo(pager);
    }
    
    pager.find('.page_link:first').addClass('active');
    pager.find('.prev_link').hide();
    if (numPages<=1) {
        pager.find('.next_link').hide();
    }
  	pager.children().eq(1).addClass("active");
    
    children.hide();
    children.slice(0, perPage).show();
    
    pager.find('li .page_link').click(function(){
        var clickedPage = $(this).html().valueOf()-1;
        goTo(clickedPage,perPage);
        return false;
    });
    pager.find('li .prev_link').click(function(){
        previous();
        return false;
    });
    pager.find('li .next_link').click(function(){
        next();
        return false;
    });
    
    function previous(){
        var goToPage = parseInt(pager.data("curr")) - 1;
        goTo(goToPage);
    }
     
    function next(){
        goToPage = parseInt(pager.data("curr")) + 1;
        goTo(goToPage);
    }
    
    function goTo(page){
        var startAt = page * perPage,
            endOn = startAt + perPage;
        
        children.css('display','none').slice(startAt, endOn).show();
        
        if (page>=1) {
            pager.find('.prev_link').show();
        }
        else {
            pager.find('.prev_link').hide();
        }
        
        if (page<(numPages-1)) {
            pager.find('.next_link').show();
        }
        else {
            pager.find('.next_link').hide();
        }
        
        pager.data("curr",page);
      	pager.children().removeClass("active");
        pager.children().eq(page+1).addClass("active");
    
    }
};

