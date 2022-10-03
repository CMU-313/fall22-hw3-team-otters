'use strict';

/**
 * Evaluation controller.
 */
angular.module('docs').controller('Evaluation', function(Restangular, $scope, $state) {
  // Load evaluation
  Restangular.one('evaluation').get({
    sort_column: 1,
    asc: true
  }).then(function(data) {
    $scope.evaluation = data.evaluation;
  });

  // Open a evaluation
  $scope.openEvaluation = function(evaluation) {
    $state.go('evaluation', { student_name: evaluation.student_name });
  };

  $scope.addEvaluation = function() {
    $scope.evaluation.push({
      'Skill': $scope.skill,
      'Experience': $scope.experience,
      'Hire': $scope.hire
    });
  };
});