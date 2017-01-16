(function() {
    'use strict';

    angular
        .module('vacationplannerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('teammate', {
            parent: 'entity',
            url: '/teammate',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'vacationplannerApp.teammate.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/teammate/teammates.html',
                    controller: 'TeammateController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('teammate');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('teammate-detail', {
            parent: 'entity',
            url: '/teammate/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'vacationplannerApp.teammate.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/teammate/teammate-detail.html',
                    controller: 'TeammateDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('teammate');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Teammate', function($stateParams, Teammate) {
                    return Teammate.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'teammate',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('teammate-detail.edit', {
            parent: 'teammate-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/teammate/teammate-dialog.html',
                    controller: 'TeammateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Teammate', function(Teammate) {
                            return Teammate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('teammate.new', {
            parent: 'teammate',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/teammate/teammate-dialog.html',
                    controller: 'TeammateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                firstName: null,
                                lastName: null,
                                login: null,
                                isAdmin: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('teammate', null, { reload: 'teammate' });
                }, function() {
                    $state.go('teammate');
                });
            }]
        })
        .state('teammate.edit', {
            parent: 'teammate',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/teammate/teammate-dialog.html',
                    controller: 'TeammateDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Teammate', function(Teammate) {
                            return Teammate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('teammate', null, { reload: 'teammate' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('teammate.delete', {
            parent: 'teammate',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/teammate/teammate-delete-dialog.html',
                    controller: 'TeammateDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Teammate', function(Teammate) {
                            return Teammate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('teammate', null, { reload: 'teammate' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
