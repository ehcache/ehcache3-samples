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
        })
        .state('star-detail', {
            parent: 'demo',
            url: '/star/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Star'
            },
            views: {
                'content@': {
                    templateUrl: 'app/demo/star/star-detail.html',
                    controller: 'StarDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Star', function($stateParams, Star) {
                    return Star.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'star',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
    }

})();
