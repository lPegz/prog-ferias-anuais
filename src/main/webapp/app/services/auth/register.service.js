(function () {
    'use strict';

    angular
        .module('progFeriasAnuaisApp')
        .factory('Register', Register);

    Register.$inject = ['$resource'];

    function Register ($resource) {
        return $resource('api/register', {}, {});
    }
})();
