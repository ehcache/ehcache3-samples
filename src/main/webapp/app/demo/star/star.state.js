(function() {
    'use strict';

    angular
        .module('demoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('star', {
            parent: 'demo',
            url: '/star',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Stars'
            },
            views: {
                'content@': {
                    templateUrl: 'app/demo/star/stars.html',
                    controller: 'StarController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        });
    }

})();
