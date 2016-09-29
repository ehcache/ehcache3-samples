(function() {
    'use strict';

    angular
        .module('demoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('cache-settings', {
            parent: 'demo',
            url: '/cachesettings',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Cache Settings'
            },
            views: {
                'content@': {
                    templateUrl: 'app/demo/settings/settings.html',
                    controller: 'CacheSettingsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        });
    }
})();
