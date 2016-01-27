/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

(function() {
    function listRepositories() {
        return {
            restrict: 'E',
            templateUrl: 'partial/directive-list-repositories.html',
            scope: {
                showAuthor: '@',
                showSearchThreshold: '@'
            },
            controller: ['$scope', '$routeParams', 'mRepository', function($scope, $routeParams, mRepository) {
                mRepository.query({author: $routeParams.author}).$promise.then(function(data) {
                    $scope.error = '';
                    $scope.repositories = data;
                }, function(err) {
                    $scope.error = err;
                });
            }]
        };
    }

    function author() {
        return {
            restrict: 'E',
            templateUrl: 'partial/directive-author.html',
            scope: {},
            controller: ['$scope', '$routeParams', function($scope, $routeParams) {
                $scope.author = $routeParams.author;
            }]
        };
    }

    function blueprint() {
        return {
            restrict: 'E',
            templateUrl: 'partial/directive-blueprint.html',
            scope: {},
            controller: ['$scope', '$routeParams', 'mBlueprint', function($scope, $routeParams, mBlueprint) {
                mBlueprint.get({author: $routeParams.author, repo: $routeParams.repo}).$promise.then(function(data) {
                    $scope.error = '';
                    $scope.blueprint = data;

                    if (angular.isDefined(data.documentation) && data.documentation != null) {
                        /* jshint evil:true */
                        eval(data.documentation);
                        if (angular.isDefined(items)) {
                            $scope.documentation = items;
                        }
                    }
                }, function(err) {
                    $scope.error = err;
                });
            }]
        };
    }

    function configurationCard() {
        return {
            restrict: 'E',
            scope: {
                item: '=',
                cssClass: '@'
            },
            templateUrl: 'partial/directive-configuration-card.html'
        };
    }

    function markdown() {
        return {
            restrict: 'E',
            scope: {
                data: '@',
                path: '@'
            },
            link: function(scope, element, attrs) {
                scope.$watch('data', function(newValue) {
                    if (newValue) {
                        element
                            .empty()
                            .append(marked(newValue));

                        angular.forEach(element[0].querySelectorAll('img'), function(elm) {
                            angular.element(elm).attr('src', 'https://raw.githubusercontent.com/' + attrs.path + '/master/' + angular.element(elm).attr('src'));
                        });
                        angular.forEach(element[0].querySelectorAll('a:not([href^=http])'), function(elm) {
                            angular.element(elm).attr('href', 'https://github.com/' + attrs.path + '/blob/master/' + angular.element(elm).attr('href'));
                        });
                    }
                });
            }
        };
    }

    angular.module('directives', ['ngRoute', 'models'])
        .directive('repositories', listRepositories)
        .directive('author', author)
        .directive('blueprint', blueprint)
        .directive('configurationCard', configurationCard)
        .directive('markdown', markdown);
})(window.angular);