describe('Sign in', function () {
    var $httpBackend, $location, scope,
        element, form, constants, identity,
        validTemplate = '<seed-credentials-sign-in-form></seed-credentials-sign-in-form>';

    beforeEach(module('seed', 'seed.signIn'));

    beforeEach(module('public/js/app/modules/sign-in/credentials-sign-in-form.html', 'public/js/app/modules/sign-in/social-sign-in.html'));

    beforeEach(inject(function (_$httpBackend_, _$location_, $rootScope, $compile, _constants_, _identity_) {
        $httpBackend = _$httpBackend_;
        $location = _$location_;
        constants = _constants_;
        identity = _identity_;

        scope = $rootScope.$new();
        element = jQuery(validTemplate);
        $compile(element)(scope);

        scope.$apply();

        form = scope.credentials_sign_in_form;
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

        // When
        scope.submit();
        scope.$digest();

        // Then
        expect(element.find('.email-password-not-valid')).toExist();
        expect(element.find('.email-password-not-valid')).not.toHaveClass('ng-hide');
    });

    it('should show error if password is too short', function () {
        // Given
        form.email.$setViewValue("valid@email.com");
        form.password.$setViewValue("pass");

        // When
        scope.submit();
        scope.$digest();

        // Then
        expect(element.find('.email-password-not-valid')).toExist();
        expect(element.find('.email-password-not-valid')).not.toHaveClass('ng-hide');
    });

    it('should show error if user does not exist', function () {
        // Given
        form.email.$setViewValue("valid@email.com");
        form.password.$setViewValue("valid_password");

        $httpBackend.expectPOST(constants.api.signInWithCredentials).respond(500, {
            "code": constants.errorCodes.userNotFound
        });

        // When
        scope.submit();
        $httpBackend.flush();
        scope.$digest();

        // Then
        expect(element.find('.email-password-not-valid')).toExist();
        expect(element.find('.email-password-not-valid')).not.toHaveClass('ng-hide');
    });

    it('should modify identity value on successful log in', function () {
        // Given
        var email = "valid@email.com";
        form.email.$setViewValue(email);
        form.password.$setViewValue("valid_password");

        $httpBackend.expectPOST(constants.api.signInWithCredentials).respond({
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
