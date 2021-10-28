https://github.com/DRaileanu/COMP472-AI/tree/main/Assignment2

# COMP472 AI - Assignment #2
by
* Dan Raileanu (40019882)
* Jonathan Andrei (40051683)

## Running
Requires at least Python3.6, as using python's f-strings, but run on Python3.8 to be safe

### Same size puzzles
* Need to ensure puzzles.txt file is present in main directory (is there by default).
* If want to generate new puzzles, simply run generate_puzzles.py, edit variables num_puzzles and size if needed.
* Run main.py to run all 9 search methods on puzzle.txt file. Can edit variables "puzzles_path" and "out_path" at beggining of file if needed.
* Output will be in ./out/ folder by default.

### Scaled up puzzles
* Need to ensure puzzles files are in upscaled_puzzles/ directory. Have to manually create and add them there.
* Ensure puzzle files names are called puzzles<i>.txt, where <i> is an integer representing size of puzzle
* Run driver_scaledup.py, will output results in ./output-upscaled/ directory.

### Extra
out(3x3) and out-upscaled directories contain files we previously ran

