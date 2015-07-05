angular.module('seed', [
    'ui.router',
    'pascalprecht.translate',
    'seed.signIn',
    'seed.signUp'
])
    .config(function ($stateProvider, $urlRouterProvider, $locationProvider, constants) {
        $stateProvider
            .state('index', {
                templateUrl: constants.pathToApp + 'modules/index/index.html'
            })
            .state('index.signIn', {
                url: constants.applicationUrls.signIn,
                templateUrl: constants.pathToApp + 'modules/sign-in/sign-in.html'
            })
            .state('index.signUp', {
                url: constants.applicationUrls.signUp,
                templateUrl: constants.pathToApp + 'modules/sign-up/sign-up.html'
            });

        $urlRouterProvider.otherwise('/');

        $locationProvider.html5Mode(true);
    })
    .config(function ($translateProvider, i18nEn) {
        $translateProvider.translations('en', i18nEn);
        $translateProvider.preferredLanguage('en');
    });

