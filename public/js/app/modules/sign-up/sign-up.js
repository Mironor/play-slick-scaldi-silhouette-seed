angular.module('seed.signUp', [])
    .directive('seedCredentialsSignUpForm', function (constants) {
        return {
            restrict: 'E',
            templateUrl: constants.pathToApp + 'modules/sign-up/credentials-sign-up-form.html',
            controller: function ($scope, $http, $location, constants, identity) {
                $scope.translationData = {
                    existingEmail: $scope.existingEmail
                };

                $scope.model = {
                    email: "",
                    password: "",
                    rePassword: ""
                };

                $scope.passwordsAreEqual = true;

                $scope.submit = function () {
                    var form = $scope.credentials_sign_up_form;
                    $scope.emailNotValid = form.email.$invalid;
                    $scope.passwordNotValid = form.password.$invalid || form.repassword.$invalid;
                    $scope.passwordsAreEqual = $scope.model.password === $scope.model.rePassword;

                    if (form.$valid && $scope.passwordsAreEqual) {
                        $http.post('/sign-up', {
                            "email": $scope.model.email,
                            "password": $scope.model.password
                        }).success(function (data) {
                            identity.email = data.email;
                            location.href = "/"
                        }).error(function (data) {
                            $scope.userAlreadyExists = data.code === constants.errorCodes.userAlreadyExists;
                            $scope.existingEmail = $scope.model.email;
                        });
                    }
                };
            }
        }
    })
    .directive('seedSocialSignUp', function (constants) {
        return {
            restrict: 'E',
            templateUrl: constants.pathToApp + 'modules/sign-up/social-sign-up.html'
        }
    });

