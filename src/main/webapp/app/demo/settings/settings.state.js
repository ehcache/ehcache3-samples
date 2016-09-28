(function() {
    'use strict';

    angular
        .module('demoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('cacheSettings', {
            parent: 'demo',
            url: '/cachesettings',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Demo Settings'
            },
            views: {
                'content@': {
                    templateUrl: 'app/demo/settings/settings.html',
                    controller: 'CacheSettingsController',
                    controllerAs: 'vm'
                }
            }
        });
    }
})();
