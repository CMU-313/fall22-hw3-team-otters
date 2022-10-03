'use strict';

/**
 * Evaluation controller.
 */
angular.module('docs').controller('Evaluation', function(Restangular, $scope, $state) {
  // Load evaluation
  Restangular.one('reviewer').get({
    sort_column: 1,
    asc: true
  }).then(function(data) {
    $scope.evaluation = data.reviewer;
  });

  // Open a evaluation
  $scope.openEvaluation = function(reviewer) {
    $state.go('reviewer', { name: reviewer.name });
  };

  $scope.addEvaluation = function() {
    $scope.evaluation.push({
      'Reviewer Name': $scope.reviewer,
      'Skill': $scope.skill,
      'Experience': $scope.experience,
      'Hire': $scope.hire
    });
  };
});