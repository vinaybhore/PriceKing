<!DOCTYPE html>
<html class="bg-black" ng-app="signUpModule">
    <head>
        <meta charset="UTF-8">
        <title>AdminLTE | Registration Page</title>
        <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
        <!-- bootstrap 3.0.2 -->
        <link href="/resources/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
        <!-- font Awesome -->
        <link href="/resources/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
        <!-- Theme style -->
        <link href="/resources/css/AdminLTE.css" rel="stylesheet" type="text/css" />
        <script src="/resources/js/jquery.min.js"></script>
        <script src="//code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
		<link rel="stylesheet" href = "http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
		<script src= "http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.min.js"></script>
		<script src="/resources/js/angular-route.min.js"></script>
		<script src="/resources/js/angular-resource.min.js" ></script>
		<script src="/resources/js/signup.js" ></script>
         <script src="/resources/js/ui-bootstrap-tpls-0.12.1.min.js"></script>

    </head>
    <body class="bg-black" ng-controller="signupController">

        <div class="form-box" id="login-box">
            <div class="header">Register New Membership</div>
            <form action="/resources/index.html" method="post">
                <div class="body bg-gray">
                    <div class="form-group">
                        <input type="text" name="firstname" ng-model="user.firstname"  class="form-control" placeholder="First Name"/>
                    </div>
                    
                    <div class="form-group">
                        <input type="text" name="lastname" ng-model="user.lastname" class="form-control" placeholder="Last Name"/>
                    </div>
                    <div class="form-group">
                        <input type="text" name="username" ng-model="user.username" class="form-control"  ng-blur="checkUserName(user)" placeholder="Username"/>
                        <div class="has-error" style="color:red" ng-show="userNameError">
							<small class="error">Username already exist, please try another</small>
  						</div>	
                    </div>
                    <div class="form-group">
                        <input type="password" name="password" ng-model="user.password" class="form-control" placeholder="Password"/>
                    </div>
                    <div class="form-group">
                        <input type="password" name="password2" ng-model="user.password2" class="form-control" placeholder="Confirm password"/>
                        <div class="has-error" style="color:red" ng-show="showPasswordError">
							<small class="error">Password does not match</small>
  						</div>	
  						<div class="has-error" style="color:red" ng-show="blankPassword">
							<small class="error">Password does not match</small>
  						</div>	
                    </div>
                     <div class="form-group">
                        <input type="text" name="email" ng-model="user.email" class="form-control" placeholder="Email"/>
                    </div>
                    <div class="form-group">
                        <input type="text" name="phone" ng-model="user.phone" class="form-control" placeholder="Phone"/>
                    </div>
                </div>
                <div class="footer">                    

                    <button type="button" class="btn bg-olive btn-block" ng-click="signup(user)">Sign me up</button>

                    <a href="login.html" class="text-center">I already have a membership</a>
                </div>
            </form>

            <!-- <div class="margin text-center">
                <span>Register using social networks</span>
                <br/>
                <button class="btn bg-light-blue btn-circle"><i class="fa fa-facebook"></i></button>
                <button class="btn bg-aqua btn-circle"><i class="fa fa-twitter"></i></button>
                <button class="btn bg-red btn-circle"><i class="fa fa-google-plus"></i></button>

            </div> -->
        </div>


        <!-- jQuery 2.0.2 -->
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
        <!-- Bootstrap -->
        <script src="/resources/js/bootstrap.min.js" type="text/javascript"></script>

    </body>
</html>