angular.module("search", [])
    .controller("searchController", function($scope, $log, $http, $sce) {

        $scope = angular.extend($scope, {
            search: {
                text: "",
                fuzzy: false
            }
        });

        $scope.explicitlyTrustedHtml = $sce.trustAsHtml;
        $scope.$watch("search.fuzzy", runSearch);
        $scope.$watch("search.text", runSearch);
        
        function runSearch() {
            var searchQuery = $scope.search.text;
            var fuzzy = $scope.search.fuzzy;

            $http.get("/search", {
                params: {
                    q: searchQuery,
                    fuzzy: fuzzy
                }
            }).then(function(result) {
                $scope.result = result.data;
                $scope.result.search_query = searchQuery;
                $scope.result.search_fuzzy = fuzzy;
            });
        }
        
    });
