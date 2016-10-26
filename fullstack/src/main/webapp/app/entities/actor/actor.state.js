(function() {
    'use strict';

    angular
        .module('demoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('actor', {
            parent: 'entity',
            url: '/actor',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Actors'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/actor/actors.html',
                    controller: 'ActorController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('actor-detail', {
            parent: 'entity',
            url: '/actor/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Actor'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/actor/actor-detail.html',
                    controller: 'ActorDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Actor', function($stateParams, Actor) {
                    return Actor.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'actor',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('actor-detail.edit', {
            parent: 'actor-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/actor/actor-dialog.html',
                    controller: 'ActorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Actor', function(Actor) {
                            return Actor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('actor.new', {
            parent: 'actor',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/actor/actor-dialog.html',
                    controller: 'ActorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                firstName: null,
                                lastName: null,
                                birthDate: null,
                                birthLocation: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('actor', null, { reload: 'actor' });
                }, function() {
                    $state.go('actor');
                });
            }]
        })
        .state('actor.edit', {
            parent: 'actor',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/actor/actor-dialog.html',
                    controller: 'ActorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Actor', function(Actor) {
                            return Actor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('actor', null, { reload: 'actor' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('actor.delete', {
            parent: 'actor',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/actor/actor-delete-dialog.html',
                    controller: 'ActorDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Actor', function(Actor) {
                            return Actor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('actor', null, { reload: 'actor' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
