#pragma once
#include "Drawable.h"

//creates square grid of size 100*100 unit squares

class Grid : public Drawable {
public:
	Grid();
	~Grid();

private:
	void addSquare(float x, float z);
};

