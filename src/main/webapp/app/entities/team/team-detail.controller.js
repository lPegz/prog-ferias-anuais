(function() {
    'use strict';

    angular
        .module('vacationplannerApp')
        .controller('TeamDetailController', TeamDetailController);

    TeamDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Team', 'Department', 'Teammate'];

    function TeamDetailController($scope, $rootScope, $stateParams, previousState, entity, Team, Department, Teammate) {
        var vm = this;

        vm.team = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('vacationplannerApp:teamUpdate', function(event, result) {
            vm.team = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
