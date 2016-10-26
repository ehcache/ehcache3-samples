(function() {
    'use strict';

    angular
        .module('demoApp')
        .controller('ActorDialogController', ActorDialogController);

    ActorDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Actor'];

    function ActorDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Actor) {
        var vm = this;

        vm.actor = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.actor.id !== null) {
                Actor.update(vm.actor, onSaveSuccess, onSaveError);
            } else {
                Actor.save(vm.actor, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('demoApp:actorUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.birthDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
