Final Project for COMP371-DD

-----
Build
-----
Build using Visual Studio 2017, SDK version: 10.0
Used 32-bit libraries, so run in x86 mode.
Open FinalProject.sln from base directory. Debug->Start without debugging.
If error, try Project->Target Solution, chose SDK version: 10.0.16299.0 (or latest)

All dependencies are included in the project already.

---------

CONTROLS:

---------

---Camera---

Camera Pan	-> mouse movement in x direction
Camera Tilt 	-> mouse movement in y direction
Camera Zoom	-> right Control + mouse movement in y direction (up to zoom-in, down to zoom-out)
Move Forward	-> UP Arrow
Move Backward	-> DOWN arrow
Strafe Left	-> LEFT arrow
Strafe Right	-> Right arrow

---Render Modes---

P		-> points
L		-> lines
T		-> triangles (fill mode)
O		-> toggle shadows
I		-> toggle textures


---Selecting Rubic Cube---

7		-> Colors Rubic Cube
8		-> Textures Rubic Cube
9		-> Jigsaw Rubic Cube
0		-> Particles Rubic Cube

---Selecting Rubic Cube Face---

1		-> Face with normal towards +y
2		-> Face with normal towards +z
3		-> Face with normal towards +x
4		-> Face with normal towards -y
5		-> Face with normal towards -x
6		-> Face with normal towards -z

---Transforming Rubic Cube---

C		-> Rotate selected face clockwise by 90 degrees
V		-> Rotate selected face counter-clockwise by 90 degrees
R		-> Rotate whole rubic cube counter-clockwise on x-axis by 90 degrees
F		-> Rotate whole rubic cube clockwise on x-axis by 90 degrees
Q		-> Rotate whole rubic cube counter-clockwise on y-axis by 90 degrees
E		-> Rotate whole rubic cube clockwise on y-axis by 90 degrees
Z		-> Rotate whole rubic cube counter-clockwise on z-axis by 90 degrees
X		-> Rotate whole rubic cube clockwise on z-axis by 90 degrees

---Timer---
B		-> Start
N		-> Pause
M		-> Reset

---Light---
A		-> Light source strafes left
D		-> Light source strafes right
W		-> Light source moves forward
S		-> Light source moves backward
W + left_shift	-> Light source goes up
S + left_shift	-> Light source goes down


---OTHER---

- (minus)	-> Remove 1000 decorative cubes
= (equal)	-> Add 1000 decorative cubes (max 10 000)



