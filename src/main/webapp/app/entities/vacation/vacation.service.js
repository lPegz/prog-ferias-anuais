(function() {
    'use strict';
    angular
        .module('vacationplannerApp')
        .factory('Vacation', Vacation);

    Vacation.$inject = ['$resource', 'DateUtils'];

    function Vacation ($resource, DateUtils) {
        var resourceUrl =  'api/vacations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.startDate = DateUtils.convertDateTimeFromServer(data.startDate);
                        data.endDate = DateUtils.convertDateTimeFromServer(data.endDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
