describe('Sign up', function () {
    var $httpBackend, $location, scope,
        element, form, constants, identity,
        validTemplate = '<seed-credentials-sign-up-form></seed-credentials-sign-up-form>';

    beforeEach(module('seed', 'seed.signUp'));

    beforeEach(module('public/js/app/modules/sign-up/credentials-sign-up-form.html', 'public/js/app/modules/sign-up/social-sign-up.html'));

    beforeEach(inject(function (_$httpBackend_, _$location_, $rootScope, $compile, _constants_, _identity_) {
        $httpBackend = _$httpBackend_;
        $location = _$location_;
        constants = _constants_;
        identity = _identity_;

        scope = $rootScope.$new();
        element = jQuery(validTemplate);
        $compile(element)(scope);

        scope.$apply();

        form = scope.credentials_sign_up_form;
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('doesn\'t show any error by default', function () {
        // Given
        var $shownErrorDivs = element.find('.error').not('.ng-hide');

        // When

        // Then
        expect($shownErrorDivs).not.toExist();
    });

    it('should show error if email is invalid', function () {
        // Given
        form.email.$setViewValue("invalid@}@email.com");
        form.password.$setViewValue("valid_password");
        form.repassword.$setViewValue("valid_password");

        // When
        scope.submit();
        scope.$digest();

        // Then
        expect(element.find('.email-not-valid')).toExist();
        expect(element.find('.email-not-valid')).not.toHaveClass('ng-hide')
    });

    it('should show error if password is too short', function () {
        // Given
        form.email.$setViewValue("valid@email.com");
        form.password.$setViewValue("pass");
        form.repassword.$setViewValue("pass");

        // When
        scope.submit();
        scope.$digest();

        // Then
        expect(element.find('.password-not-valid')).toExist();
        expect(element.find('.password-not-valid')).not.toHaveClass('ng-hide');
    });

    it('should show error if passwords are not the same', function () {
        // Given
        form.email.$setViewValue("valid@email.com");
        form.password.$setViewValue("valid_password");
        form.repassword.$setViewValue("second_valid_password");

        // When
        scope.submit();
        scope.$digest();

        // Then
        expect(element.find('.passwords-not-equal')).toExist();
        expect(element.find('.passwords-not-equal')).not.toHaveClass('ng-hide')
    });

    it('should show error if user already exists', function () {
        // Given
        form.email.$setViewValue("valid@email.com");
        form.password.$setViewValue("valid_password");
        form.repassword.$setViewValue("valid_password");

        $httpBackend.expectPOST(constants.api.signUp).respond(500, {
            "code": constants.errorCodes.userAlreadyExists
        });

        // When
        scope.submit();
        $httpBackend.flush();
        scope.$digest();

        // Then
        expect(element.find('.user-already-exists')).toExist();
        expect(element.find('.user-already-exists')).not.toHaveClass('ng-hide');
    });

    it('should modify identity value on successful sign up', function () {
        // Given
        var email = "valid@email.com";
        form.email.$setViewValue(email);
        form.password.$setViewValue("valid_password");
        form.repassword.$setViewValue("valid_password");

        $httpBackend.expectPOST(constants.api.signUp).respond({
            "email": email
        });

        // When
        scope.submit();
        $httpBackend.flush();
        scope.$digest();

        // Then
        expect(identity.email).toBe(email);
    });

});
