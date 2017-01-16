(function() {
    'use strict';

    angular
        .module('vacationplannerApp')
        .controller('TeammateDeleteController',TeammateDeleteController);

    TeammateDeleteController.$inject = ['$uibModalInstance', 'entity', 'Teammate'];

    function TeammateDeleteController($uibModalInstance, entity, Teammate) {
        var vm = this;

        vm.teammate = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Teammate.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
