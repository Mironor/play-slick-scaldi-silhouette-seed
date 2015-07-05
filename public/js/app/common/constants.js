// Inject server variable
var pathToApp = window.pathToApp || 'public/js/app/'; // path for karma tests if undefined

angular.module('seed')
    .constant('constants', {
        pathToApp: pathToApp,

        api: {
            signInWithCredentials: '/authenticate/credentials',
            signUp: '/sign-up'
        },

        applicationUrls: {
            signIn: '/',
            signUp: '/sign-up'
        },

        errorCodes: {
            userNotFound: 4004,
            accessDenied: 4002,
            userAlreadyExists: 4005
        }
    });
