'use strict';

/**
 * Evaluation controller.
 */
angular.module('docs').controller('Evaluation', function(Restangular, $scope, $state) {
  // Load evaluation
  
  Restangular.one('reviewer', 'list').get().then(function(data) {
    $scope.reviewers = data.reviewers;
  });

  // Open a evaluation
  $scope.openEvaluation = function (reviewer) {
    $state.go('reviewer', { name: reviewer.name });
  };

  $scope.addEvaluation = function () {
    Restangular.one('reviewer').put({
      name: $scope.reviewer,
      skill_score: $scope.skill,
      experience_score: $scope.experience,
      hire: ($scope.hire == 'no') ? -1 : 1
    });
  };

  $scope.getAverages = function () {
    Restangular.one('reviewer', 'average').get().then(function(data) {
      $scope.name_average = data.name;
      $scope.skill_average = data.skill_score;
      $scope.experience_average = data.experience_score;
      $scope.hire_average = data.hire;
    });
  }
});