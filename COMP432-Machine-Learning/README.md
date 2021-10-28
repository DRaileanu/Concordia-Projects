README for G40 
-------------- 
Our project relies mainly on sklearn.
Our code submission contains the following files:<br>
./Notebook.ipynb <-- Contains all the code for the project<br>
./datasets <-- directory containing all datasets<br>
./savefigs <-- directory for images of the results<br>
./savefiles <-- directory for intermediary and final results<br>

In Notebook.ipynb, select which dataset name from datasets/ you wish to run dimensional reduction on.<br>
For demo purposes, please use "forest_types"
* Simply run all cells
* GPU is not required. 
* Training varies largely on number of samples and attributes. Running whole notebook on demo dataset takes a couple of minutes, but it took over 24h some datasets

**Source of datasets**:<br>
indian_pines: https://www.openml.org/d/41972 <br>
micro_mass:  https://www.openml.org/d/1515 <br>
olivetti_faces: https://www.openml.org/d/41083 <br>
forest_types(demo): https://archive.ics.uci.edu/ml/datasets/Forest+type+mapping <br>

This research relies theoretically on the previous research 
Further Experiments on A Combination of Linear SVM Weight and ReliefF for Dimensionality Reduction, Buathong, Wipawan and Jarupunphol, Pita (2018).
