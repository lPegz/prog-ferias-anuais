(function() {
    'use strict';

    angular
        .module('vacationplannerApp')
        .controller('VacationDetailController', VacationDetailController);

    VacationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Vacation', 'Teammate'];

    function VacationDetailController($scope, $rootScope, $stateParams, previousState, entity, Vacation, Teammate) {
        var vm = this;

        vm.vacation = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('vacationplannerApp:vacationUpdate', function(event, result) {
            vm.vacation = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
