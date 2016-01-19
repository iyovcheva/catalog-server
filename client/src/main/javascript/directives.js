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
    angular.module('directives', ['ngRoute', 'models'])
        .directive('repositories', listRepositories)
        .directive('blueprint', blueprint)
        .directive('configurationCard', configurationCard);

    function listRepositories() {
        return {
            restrict: 'E',
            templateUrl: 'partial/directive-list-repositories.html',
            scope: {},
            controller: ['$scope', 'mRepository', function($scope, mRepository) {
                mRepository.query().$promise.then(function(data) {
                    $scope.error = '';
                    $scope.repositories = data
                }, function(err) {
                    $scope.error = err;
                });
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
                        eval(data.documentation);
                        if (angular.isDefined(items)) {
                            $scope.documentation = items;
                        }
                    }
                }, function(err) {
                    $scope.error = err;
                });

                $('#blueprint-tabs a').click(function(e) {
                    e.preventDefault();
                    $(e.target).tab('show')
                    return false;
                });

                //$('#overview img').each(function() {
                //    $(this).attr('src', 'https://raw.githubusercontent.com/' + blueprint.token + '/master/' + $(this).attr("src"));
                //});
                //$('#overview a:not([href^=http])').each(function() {
                //    $(this).attr('href', 'https://github.com/' + blueprint.token + '/blob/master/' + $(this).attr("href"))
                //});
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
})();