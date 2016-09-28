(function() {
    'use strict';
    angular
        .module('demoApp')
        .factory('Star', Star);

    Star.$inject = ['$resource', 'DateUtils'];

    function Star ($resource, DateUtils) {
        var resourceUrl =  'api/stars/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.birthDate = DateUtils.convertLocalDateFromServer(data.birthDate);
                    }
                    return data;
                }
            }
        });
    }
})();
