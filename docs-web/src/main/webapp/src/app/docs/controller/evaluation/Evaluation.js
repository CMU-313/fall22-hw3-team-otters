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

  $scope.addSkill = function() {
    $scope.evaluation.push({
      'Skill': $scope.skill
    });
  };

  $scope.addExperience = function() {
    $scope.evaluation.push({
      'Experience': $scope.experience
    });
  };

  $scope.addHire = function() {
    $scope.evaluation.push({
      'Hire': $scope.hire
    });
  };
});