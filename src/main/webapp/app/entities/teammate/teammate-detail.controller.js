(function() {
    'use strict';

    angular
        .module('vacationplannerApp')
        .controller('TeammateDetailController', TeammateDetailController);

    TeammateDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Teammate', 'Vacation'];

    function TeammateDetailController($scope, $rootScope, $stateParams, previousState, entity, Teammate, Vacation) {
        var vm = this;

        vm.teammate = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('vacationplannerApp:teammateUpdate', function(event, result) {
            vm.teammate = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
