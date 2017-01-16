(function() {
    'use strict';

    angular
        .module('vacationplannerApp')
        .controller('TeammateDialogController', TeammateDialogController);

    TeammateDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Teammate', 'Vacation'];

    function TeammateDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Teammate, Vacation) {
        var vm = this;

        vm.teammate = entity;
        vm.clear = clear;
        vm.save = save;
        vm.vacations = Vacation.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.teammate.id !== null) {
                Teammate.update(vm.teammate, onSaveSuccess, onSaveError);
            } else {
                Teammate.save(vm.teammate, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('vacationplannerApp:teammateUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
