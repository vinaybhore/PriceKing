var app = angular.module('myModule', ['ngRoute','ngResource','ui.bootstrap','xeditable','isteven-multi-select','ngTable','angularFileUpload']);

app.config(function($routeProvider, $httpProvider) {

	$httpProvider.defaults.headers.post['Content-Type'] = 'application/json;charset=utf-8';

	$httpProvider.defaults.headers["delete"] = {'Content-Type': 'application/json;charset=utf-8'};

});

app.controller('ModalDemoCtrl', ['$scope','$modal','$log','ngTableParams','$filter','$http',
                                 '$upload', function ($scope, $modal, $log, ngTableParams,$filter, $http,$upload,fileReader) {

  $scope.items = ['item1', 'item2', 'item3'];
  $scope.inputList = [
                      { firstName: "Electronics",    lastName: "Parker", selected: false },
                      { firstName: "Clothing",     lastName: "Jane",   selected: false },
                      { firstName: "Home",    lastName: "Wayne",  selected: false  },
                      { firstName: "Furniture",    lastName: "Banner",  selected: false },
                      { firstName: "footware",  lastName: "Romanova", selected: false },
                      { firstName: "Clark",    lastName: "Kent",  selected: true  }
   ];
  
  getProducts();
  
  function getProducts(){
 	 $http.get('/getProducts').then(function(response) {
 		 $scope.productsList = response.data;
 		 console.log($scope.productsList);
 		 $scope.productTable();
 		 
 	 });

  } 
  
 getCoupons();
  
  function getCoupons(){
 	 $http.get('/getCoupons').then(function(response) {
 		 $scope.coupons = response.data;
 		 
 	 });

  } 
  $scope.productTable = function(){
	  $scope.productTableParams = new ngTableParams(
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
	
				  total : $scope.productsList.length, // length
	
			  // of
	
			  // data
	
				  getData : function($defer, params) {
	
	
			  // use build-in angular filter
	
				  var filteredData = params.filter() ? $filter('filter')($scope.inputList,params.filter()): $scope.productsList;
	
				  var orderedData = params.sorting() ? $filter('orderBy')(filteredData,params.orderBy()): $scope.productsList;
	
				  params.total(orderedData.length); // set
	
			  // total
	
			  // for
	
			  // recalc
	
			  // pagination
	
			  $scope.products = orderedData.slice((params.page() - 1) * params.count(),
	
			  params.page() * params.count());
	
			  	$defer.resolve($scope.products);
	
			  }
	
		});
  }



  $scope.openProductModal = function (size,productEdit) {
	console.log(productEdit);
    var modalInstance = $modal.open({
      templateUrl: 'addProduct.html',
      controller: 'productController',
      size: size,
      resolve: {
        items: function () {
          return $scope.items;
        },
        productsList: function (){
        	return $scope.productsList;
        },
        fileReader: function(){
        	return fileReader;
        },
        productEdit:function(){
        	return productEdit;
        }
      }
    });

    modalInstance.result.then(function (selectedItem) {
      $scope.selected = selectedItem;
    }, function () {
      $log.info('Modal dismissed at: ' + new Date());
    });
  };
  
  $scope.openCouponModal = function (size,couponEdit) {
	  
	    var modalInstance = $modal.open({
	      templateUrl: 'addCoupon.html',
	      controller: 'couponController',
	      size: size,
	      resolve: {
	        items: function () {
	          return $scope.items;
	        },
	        couponsList: function (){
	        	return $scope.couponsList;
	        },
	        couponEdit: function(){
	        	return $scope.couponEdit;
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

app.controller('productController', function ($scope, $http, $modalInstance, productsList, productEdit) {

  $scope.productsList = productsList;
  
  var prodIndex = $scope.productsList.indexOf(productEdit);
  console.log(prodIndex);
  
  
  
  if(prodIndex == -1) {
	  $scope.showSaveProduct = true;
	  $scope.showEditProduct = false;
  }else {
	  $scope.showEditProduct = true;
	  $scope.showSaveProduct = false;
	  $scope.product = $scope.productsList[prodIndex];
	  
  }
	  
  
  $scope.pictures = [];
  
  function getBase64Image(img) {
	    // Create an empty canvas element
	  console.log(img);
	  var fileImg = new Image(img);
	    var canvas = document.createElement("canvas");
	    canvas.width = fileImg.width;
	    canvas.height = fileImg.height;

	    // Copy the image contents to the canvas
	    canvas.getContext("2d").drawImage(fileImg, 0, 0);
	    
	   
		

	    // Get the data-URL formatted image
	    // Firefox supports PNG and JPEG. You could check img.src to
	    // guess the original format, but be aware the using "image/jpg"
	    // will re-encode the image.
	    var dataURL = canvas.toDataURL("image/png");
	    console.log(dataURL);

	    return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
	}

  
  $scope.fileSelected = function(selectedFiles){
	  console.log(selectedFiles.length);
	  for(var i=0;i<selectedFiles.length;i++){
		  $scope.pictures.push(selectedFiles[i]);
		 var result = getBase64Image(selectedFiles[i]);
		 console.log(result);
              $scope.pictures[i].imageSrc = result;
         
	  }
  }

  $scope.saveProduct = function (product) {
	  
	  product.category='test';
	  product.picture='test';
	  var formData = angular.toJson(product);
	  
	  console.log(formData);
	  $http.post('/product', formData,
			  {headers: {'Content-Type': 'application/json'}
	  }).success(function(response) {
	        console.log(response);
	   	});
	  
    $modalInstance.close();
  };
  
  $scope.updateProduct = function (product){
	  product.category='test';
	  product.picture='test';
	  id = product._id;
	  delete product["_id"];
	  var formData = angular.toJson(product);
	  
	  console.log(formData);
	  $http.put('/product/'+id, formData,
			  {headers: {'Content-Type': 'application/json'}
	  }).success(function(response) {
	        console.log(response);
	  });
	  
    $modalInstance.close();
  }

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});

app.controller('couponController', function ($scope, $modalInstance, couponsList,$http, couponEdit) {
		
	$scope.open = function($event) {
	    $event.preventDefault();
	    $event.stopPropagation();

	    $scope.opened = true;
	  };
	  $scope.format = 'dd-MMMM-yyyy';
	  $scope.couponsList = couponsList;
	  $scope.ok = function () {
	    $modalInstance.close($scope.selected.item);
	  };
	  $scope.saveCoupon = function (coupon) {
		
		  var formData = angular.toJson(coupon);
		  
		  console.log(formData);
		  $http.post('/coupon', formData,
				  {headers: {'Content-Type': 'application/json'}
		  }).success(function(response) {
		        console.log(response);
		     });
		  
	  };
  
	  $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	  };
	});

app.directive("jqdatepicker", function () {
	  return {
	    restrict: "A",
	    require: "ngModel",
	    link: function (scope, elem, attrs, ngModelCtrl) {
	      var updateModel = function (dateText) {
	        scope.$apply(function () {
	          ngModelCtrl.$setViewValue(dateText);
	        });
	      };
	      var options = {
	        dateFormat: "dd/mm/yy",
	        onSelect: function (dateText) {
	          updateModel(dateText);
	        }
	      };
	      elem.datepicker(options);
	    }
	  }
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

