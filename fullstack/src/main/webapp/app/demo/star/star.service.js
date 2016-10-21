(function() {
    'use strict';
    angular
        .module('demoApp')
        .factory('Star', Star);

    Star.$inject = ['$resource', 'DateUtils'];

    function Star ($resource, DateUtils) {
        var resourceUrl =  'api/stars/:id';

        return $resource(resourceUrl, {}, {
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.birthDate = DateUtils.convertLocalDateFromServer(data.actor.birthDate);
                    }
                    return data;
                }
            }
        });
    }
})();
