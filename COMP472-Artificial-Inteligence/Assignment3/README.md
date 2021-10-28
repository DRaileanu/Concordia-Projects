https://github.com/DRaileanu/COMP472-AI/tree/main/Assignment3

# COMP472 AI - Assignment #3
by
* Dan Raileanu (40019882)
* Jonathan Andrei (40051683)

## Dependencies
- Python3.8
- Numpy
- pygraphviz (only for alternative implementation)

## Usage
The default solution to the assignment is in the pntgame.py file. Run using:
>python3.8 pntgame.py [#tokens] [#taken_tokens] [list_of_taken_tokens] [depth]

The results will be displayed in console. Can also write to a file if setting variable `write_output` to True at line 152


## Extra alternative implementation
You can also run an alternative implementation of the assignment, which uses State objects to keep track of all data and outputs to a graph.dot file what the alpha-beta-search tree looks like. It requires pygraphviz library to be installed. Run using:
>python3.8 pntgame_using_state.py [#tokens] [#taken_tokens] [list_of_taken_tokens] [depth]

The results will be displayed in console and a 'graph.dot' file should also be created.