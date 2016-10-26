(function() {
    'use strict';

    angular
        .module('demoApp')
        .controller('CacheSettingsController', CacheSettingsController);

    function CacheSettingsController () {
        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.success = null;


        function save () {
        }
    }
})();
