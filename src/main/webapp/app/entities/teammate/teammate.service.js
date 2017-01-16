(function() {
    'use strict';
    angular
        .module('vacationplannerApp')
        .factory('Teammate', Teammate);

    Teammate.$inject = ['$resource'];

    function Teammate ($resource) {
        var resourceUrl =  'api/teammates/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
