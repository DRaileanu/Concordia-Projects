# Space Concordia Initiation Task
Implementation of basic web-based application that updates a displayed value using ROS publisher-subscriber design.

## Dependencies
[Ubuntu 18.04 LTS](https://releases.ubuntu.com/18.04/)\
[ROS Melodic Morenia](http://wiki.ros.org/melodic)\
[rosbridge_server](http://wiki.ros.org/rosbridge_suite)\
[Flask](https://flask.palletsprojects.com/en/1.1.x/installation/)\
[Python 2.7](https://www.python.org/download/releases/2.7/)

## How to Run
Unless already done, make sure this line is present in your .bashrc file
```bash
source /opt/ros/melodic/setup.bash
```
Exctract the repo files and cd into main directory.
Run the following 2 commands in 2 separate terminals:
```bash
roscore
roslaunch rosbridge_server rosbridge_websocket.launch
```
Open one more terminal and run:
```bash
source devel/setup.bash
rosrun intro_task listener.py
```
Go to http://localhost:5050 , open one more terminal and run:
```bash
source devel/setup.bash
rosrun intro_task talker.py
```
You will be prompted for voltages values, which are immediately updated on the previously opened web page.
