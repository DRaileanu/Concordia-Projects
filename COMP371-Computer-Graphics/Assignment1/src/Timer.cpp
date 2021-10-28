#include "Timer.h"
/*
sources:
http://www.cplusplus.com/forum/beginner/76147/
*/
Timer::Timer() {
	//this model will remain the same
	column = new Model(':');
	addChild(column);
	//these four models objects are dynamic and will change with time
	leftMinute = new Model('0');
	rightMinute = new Model('0');
	leftSecond = new Model('0');
	rightSecond = new Model('0');
	//these integers are meant to represent the current Model as an int
	rightSecond_int = 0;
	leftSecond_int = 0;
	leftMinute_int = 0;
	rightMinute_int = 0;
	//positioning the leftMinute model to the left minute position in the timer
	leftMinute->translate(glm::vec3(-5.5f, 0.0f, 0.0f));
	addChild(leftMinute);
	//positioning the rightMinute model to the right minute position in the timer
	rightMinute->translate(glm::vec3(-2.0f, 0.0f, 0.0f));
	addChild(rightMinute);
	//posiioning the leftSecond model to the left second position in the timer
	leftSecond->translate(glm::vec3(2.0f, 0.0f, 0.0f));
	addChild(leftSecond);
	//positioning the rightSecond model to the right second position in the timer
	rightSecond->translate(glm::vec3(5.5f, 0.0f, 0.0f));
	addChild(rightSecond);
	//this is an integer to keep track of the seconds passed
	current_seconds = 0;
	timeStarted = false;
}
void Timer::start() {
	begTime = clock();
}
void Timer::pause() {
	timeStarted = false;
	current_seconds = 0;
}
void Timer:: reset() {
	//here we deconstruct all the models present.
	removeChild(leftMinute);
	delete leftMinute;
	removeChild(rightMinute);
	delete rightMinute;
	removeChild(leftSecond);
	delete leftSecond;
	removeChild(rightSecond);
	delete rightSecond;
	//reset all the corresponding ints to zero
	rightSecond_int = 0;
	leftSecond_int = 0;
	leftMinute_int = 0;
	rightMinute_int = 0;
	//create zeros for the reset
	leftMinute = new Model('0');
	leftMinute->translate(glm::vec3(-5.5f, 0.0f, 0.0f));
	addChild(leftMinute);
	rightMinute = new Model('0');
	rightMinute->translate(glm::vec3(-2.0f, 0.0f, 0.0f));
	addChild(rightMinute);
	leftSecond = new Model('0');
	leftSecond->translate(glm::vec3(2.0f, 0.0f, 0.0f));
	addChild(leftSecond);
	rightSecond = new Model('0');
	rightSecond->translate(glm::vec3(5.5f, 0.0f, 0.0f));
	addChild(rightSecond);
	timeStarted = false;
	current_seconds = 0;
}
unsigned long Timer::elapsedTime() {
	return ((unsigned long)clock() - begTime) / CLOCKS_PER_SEC;
}

void Timer::timeUpdate(unsigned long elapsedTime,bool timeStarted) {
	if (timeStarted) {
		//if one second has elapsed then we call for a update on the rightSecond model and int
		if (elapsedTime > (unsigned long)current_seconds) {

			current_seconds++;
			updateRightSecond();
		}
	}
}
//this function triggers the update of  left second model
void Timer::updateRightSecond() {
	if (rightSecond_int == 9) {
		rightSecond_int = 0;
		updateLeftSecond();
	}
	else {
		rightSecond_int++;
	}
	char newModelChar = getModelChar(rightSecond_int);
	removeChild(rightSecond);
	delete rightSecond;
	rightSecond = new Model(newModelChar);
	rightSecond->translate(glm::vec3(5.5f, 0.0f, 0.0f));
	addChild(rightSecond); 
}
// this function triggers the update of the right minute model
void Timer::updateLeftSecond() {
	if (leftSecond_int == 5) {
		leftSecond_int = 0;
		updateRightMinute();
	}
	else {
		leftSecond_int++;
	}
	char newModelChar = getModelChar(leftSecond_int);
	removeChild(leftSecond);
	delete leftSecond;
	leftSecond = new Model(newModelChar);
	leftSecond->translate(glm::vec3(2.0f, 0.0f, 0.0f));
	addChild(leftSecond);	

}
//this function triggers the update of the left minute model
void Timer::updateRightMinute() {
	if (rightMinute_int == 9) {
		rightMinute_int = 0;
		updateLeftMinute();
	}
	else {
		rightMinute_int++;
	}
	char newModelChar = getModelChar(rightMinute_int);
	removeChild(rightMinute);
	delete rightMinute;
	rightMinute = new Model(newModelChar);
	rightMinute->translate(glm::vec3(-2.0f, 0.0f, 0.0f));
	addChild(rightMinute);
}
//this function resets the timer when left minute reaches 6 for 60 minutes
void Timer::updateLeftMinute() {
	if (leftMinute_int == 5) {
		leftMinute_int = 0;
		reset();
	}
	else {
		leftMinute_int++;
	}
	char newModelChar = getModelChar(leftMinute_int);
	removeChild(leftMinute);
	delete leftMinute;
	leftMinute = new Model(newModelChar);
	leftMinute->translate(glm::vec3(-5.5f, 0.0f, 0.0f));
	addChild(leftMinute);
}
// this function allows us to exchange the int representing the model for its char equivalent.
char Timer::getModelChar(int number) {
	switch (number) {
	case 0: {return '0'; }
		  break;
	case 1: {return '1'; }
		  break;
	case 2: {return '2'; }

	case 3: {return '3'; }
		  break;
	case 4: {return '4'; }
		  break;
	case 5: {return '5'; }
		  break;
	case 6: {return '6'; }
		  break;
	case 7: {return '7'; }
		  break;
	case 8: {return '8'; }
		  break;

	case 9: {return '9'; }
		  break;

	}
}