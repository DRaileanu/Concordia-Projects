#pragma once
#include "GroupNode.h"
#include "DrawNode.h"
#include "Model.h"
#include <time.h>
/*
sources:
http://www.cplusplus.com/forum/beginner/76147/
*/
class Timer : public GroupNode
{
private:
	unsigned long begTime = 0;

public:
	Timer();
	~Timer() {};

	void start();
	void pause();
	void reset();
	unsigned long elapsedTime();
	void timeUpdate(unsigned long elapsedTime,bool timeStarted);
	void updateRightSecond();
	void updateLeftSecond();
	void updateRightMinute();
	void updateLeftMinute();
	char getModelChar(int);

	Model* column;
	Model* leftMinute;
	Model* rightMinute;
	Model* leftSecond;
	Model* rightSecond;

	int rightSecond_int;
	int leftSecond_int;
	int rightMinute_int;
	int leftMinute_int;
	int current_seconds;
	bool timeStarted;

};