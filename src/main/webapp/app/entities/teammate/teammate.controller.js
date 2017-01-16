(function() {
    'use strict';

    angular
        .module('vacationplannerApp')
        .controller('TeammateController', TeammateController);

    TeammateController.$inject = ['$scope', '$state', 'Teammate'];

    function TeammateController ($scope, $state, Teammate) {
        var vm = this;

        vm.teammates = [];

        loadAll();

        function loadAll() {
            Teammate.query(function(result) {
                vm.teammates = result;
                vm.searchQuery = null;
            });
        }
    }
})();
