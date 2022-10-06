'use strict';

/**
 * Evaluation controller.
 */
angular.module('docs').controller('Evaluation', function(Restangular, $window, $scope, $state) {
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
    $window.location.reload();
  };

  $scope.getAverages = function () {
    Restangular.one('reviewer', 'average').get().then(function(data) {
      var table = document.getElementById("evaluations");
      var row = table.insertRow();
      var name_cell = row.insertCell(0);
      var skill_cell = row.insertCell(1);
      var experience_cell = row.insertCell(2);
      var hire_cell = row.insertCell(3);
      name_cell.innerHTML = data.name;
      skill_cell.innerHTML = data.skill_score;
      experience_cell.innerHTML = data.experience_score;
      hire_cell.innerHTML = data.hire;
    });
  }
});