(function() {
    'use strict';

    angular
        .module('demoApp')
        .controller('StarDetailController', StarDetailController);

    StarDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Star'];

    function StarDetailController($scope, $rootScope, $stateParams, previousState, entity, Star) {
        var vm = this;

        vm.dto = entity;
        vm.previousState = previousState.name;
    }
})();
