(function() {
    'use strict';

    angular
        .module('vacationplannerApp')
        .controller('DepartmentDialogController', DepartmentDialogController);

    DepartmentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Department', 'Teammate'];

    function DepartmentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Department, Teammate) {
        var vm = this;

        vm.department = entity;
        vm.clear = clear;
        vm.save = save;
        vm.admins = Teammate.query({filter: 'department-is-null'});
        $q.all([vm.department.$promise, vm.admins.$promise]).then(function() {
            if (!vm.department.admin || !vm.department.admin.id) {
                return $q.reject();
            }
            return Teammate.get({id : vm.department.admin.id}).$promise;
        }).then(function(admin) {
            vm.admins.push(admin);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.department.id !== null) {
                Department.update(vm.department, onSaveSuccess, onSaveError);
            } else {
                Department.save(vm.department, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('vacationplannerApp:departmentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
