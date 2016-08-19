angular.
	module('choiceQuestion').
	component('choiceQuestionList',{
		templateUrl:'choice-question/choice-question-list.template.html',
        controller: function ChoiceQuestionListController($http) {
        	var self = this;
        	$http.get('data/question-data.json').then(function(response) {
                self.questions = response.data;
            });
          }    
	});