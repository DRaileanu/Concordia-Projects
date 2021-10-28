from state import *
from search_functions import *
import numpy as np
import re
import os 
import shutil
import pathlib

#setup paths
puzzles_path = "puzzles.txt"
out_path = "./out/"
if os.path.exists(out_path) and os.path.isdir(out_path):
    shutil.rmtree(out_path)
pathlib.Path(out_path).mkdir(exist_ok=True)


def solve_puzzle(start_order, goal_order, search_func, search_name, puzzle_num, stats_dict,  **kwargs):
    print(f"Solving puzzle {puzzle_num} using {search_name}")
    sol_path, search_path, time_elapsed = search_func(start_order, goal_order, **kwargs)
    if(sol_path!=None):
        stats_dict["sol_found"].append(True)
        stats_dict["sol_path_lengths"].append(len(sol_path))
        stats_dict["search_path_lengths"].append(len(search_path))
        stats_dict["exec_times"].append(time_elapsed)

        with open(f"{out_path}[{puzzle_num}]-[{search_name}]-[search].txt", 'w') as f:
            for state in search_path:
                f.write("{}\n\n".format(np.array2string(state.order)))

        with open(f"{out_path}[{puzzle_num}]-[{search_name}]-[sol].txt", 'w') as f:
            for state in sol_path:
                f.write("{}\n\n".format(np.array2string(state.order)))

    else:
        stats_dict["sol_found"].append(False)
        stats_dict["sol_path_lengths"].append(None)
        stats_dict["search_path_lengths"].append(None)
        stats_dict["exec_times"].append(None)

        with open(f"{out_path}[{puzzle_num}]-[{search_name}]-[search].txt", 'w') as f:
            f.write("no solution")

        with open(f"{out_path}[{puzzle_num}]-[{search_name}]-[sol].txt", 'w') as f:
            f.write("no solution")
    


#stats for analysis
dfs_stats = {
    "sol_path_lengths": [],
    "search_path_lengths": [],
    "sol_found": [],
    "exec_times": []
}
iter_deepening_stats = {
    "sol_path_lengths": [],
    "search_path_lengths": [],
    "sol_found": [],
    "exec_times": []
}
astarH1_stats = {
    "sol_path_lengths": [],
    "search_path_lengths": [],
    "sol_found": [],
    "exec_times": []
}
astarH2_stats = {
    "sol_path_lengths": [],
    "search_path_lengths": [],
    "sol_found": [],
    "exec_times": []
}
astarH3_stats = {
    "sol_path_lengths": [],
    "search_path_lengths": [],
    "sol_found": [],
    "exec_times": []
}
astarH4_stats = {
    "sol_path_lengths": [],
    "search_path_lengths": [],
    "sol_found": [],
    "exec_times": []
}
astarH5_stats = {
    "sol_path_lengths": [],
    "search_path_lengths": [],
    "sol_found": [],
    "exec_times": []
}
astarH6_stats = {
    "sol_path_lengths": [],
    "search_path_lengths": [],
    "sol_found": [],
    "exec_times": []
}

#read puzzles
with open(puzzles_path, "r") as f:
    puzzles = f.readlines()

#solve each puzzle
for i in range(len(puzzles)):
    #parse puzzle text by converting to 2d square np.array
    start_order = re.findall(r"\d+", puzzles[i])
    start_order = np.fromiter(map(np.int32, start_order), dtype=np.int32)
    dimSize = int(np.sqrt(len(start_order)))
    start_order = np.reshape(start_order, (dimSize, dimSize))
    
    goal_order = np.arange(1, dimSize*dimSize+1, dtype=np.int32)
    goal_order = np.reshape(goal_order, start_order.shape)

    solve_puzzle(start_order, goal_order, DFS_search, search_name="DFS", puzzle_num=i+1, stats_dict=dfs_stats, time_limit=60)
    solve_puzzle(start_order, goal_order, Iterative_Deepening_search, search_name="Iter Deepening", puzzle_num=i+1, stats_dict=iter_deepening_stats, time_limit=60)
    solve_puzzle(start_order, goal_order, A_search, search_name="AstarH1", puzzle_num=i+1, stats_dict=astarH1_stats, time_limit=60, heuristicFunc=heuristic_euclidean)
    solve_puzzle(start_order, goal_order, A_search, search_name="AstarH2", puzzle_num=i+1, stats_dict=astarH2_stats, time_limit=60, heuristicFunc=heuristic_manhattan)
    solve_puzzle(start_order, goal_order, A_search, search_name="AstarH3", puzzle_num=i+1, stats_dict=astarH3_stats, time_limit=60, heuristicFunc=heuristic_misplaced_tiles)
    solve_puzzle(start_order, goal_order, A_search, search_name="AstarH4", puzzle_num=i+1, stats_dict=astarH4_stats, time_limit=60, heuristicFunc=heuristic_wrong_rows_cols)
    solve_puzzle(start_order, goal_order, A_search, search_name="AstarH5", puzzle_num=i+1, stats_dict=astarH5_stats, time_limit=60, heuristicFunc=heuristic_permutations)
    solve_puzzle(start_order, goal_order, Astar_search, search_name="AstarH6", puzzle_num=i+1, stats_dict=astarH6_stats, time_limit=60, heuristicFunc=heuristic_max_manhattan)

#output analysis data
with open(f"{out_path}analysis.txt", 'w') as f:
    stats_list = [dfs_stats, iter_deepening_stats, astarH1_stats, astarH2_stats, astarH3_stats, astarH4_stats, astarH5_stats, astarH6_stats]
    search_name_list = ["DFS", "Iterative Deepening", "A* Heuristic: Euclidean Distance", "A* Heuristic: Manhattan Distance", "A* Heuristic: Misplaced Tiles", "A* Heuristic: Wrong rows+cols", "A* Heuristic: Sum of Permutation Inversions", "A* Heuristic: Max Manhattan Distance"]
    for stats, search_name in zip(stats_list, search_name_list):
        f.write(f"##########   {search_name}    ##########\n")
        if stats["sol_found"].count(True)==0:
            f.write("No solutions have been found for this search method, so no data to show!\n")
            f.write("===================================================\n"*2+"\n")
            continue
        #sol found
        f.write("***Solution found***\n")
        for i, val in enumerate(stats["sol_found"]):
            f.write(f"{i+1}: {val}\n")
        total_sols = stats["sol_found"].count(True)
        avg = total_sols / len(stats["sol_found"])
        f.write("-----\n")
        f.write(f"Total solutions found: {total_sols}\n")
        f.write(f"Average solutions found: {avg:.1%}\n")
        f.write("====================\n")
        #execution times
        f.write("***Execution Time***\n")
        total = 0
        for i, val in enumerate(stats["exec_times"]):
            if val != None:
                total+=val
            f.write(f"{i+1}: {val}\n")
        avg = total / total_sols
        f.write("-----\n")
        f.write(f"Average succesful execution time: {avg:.3f} sec\n")
        f.write("Minimum execution time: {:.3f}\n".format(min([x for x in stats["exec_times"] if x is not None], default=None)))
        f.write("Maximum succesful execution time: {:.3f}\n".format(max([x for x in stats["exec_times"] if x is not None], default=None)))
        f.write("====================\n")
        #Solution path length
        f.write("***Solution Path Length***\n")
        total = 0
        for i, val in enumerate(stats["sol_path_lengths"]):
            if val != None:
                total+=val
            f.write(f"{i+1}: {val}\n")
        avg = total / total_sols
        f.write("-----\n")
        f.write(f"Average succesful solution path length: {avg:.2f}\n")
        f.write("Minimum succesful solution path length: {:.0f}\n".format(min([x for x in stats["sol_path_lengths"] if x is not None], default=None)))
        f.write("Maximum succesful solution path length: {:.0f}\n".format(max([x for x in stats["sol_path_lengths"] if x is not None], default=None)))
        f.write("====================\n")
        #Search path length
        f.write("***Search Path Length***\n")
        total = 0
        for i, val in enumerate(stats["search_path_lengths"]):
            if val != None:
                total+=val
            f.write(f"{i+1}: {val}\n")
        avg = total / total_sols
        f.write("-----\n")
        f.write(f"Average succesful search path length: {avg:.2f}\n")
        f.write("Minimum succesful search path length: {}\n".format(min([x for x in stats["search_path_lengths"] if x is not None], default=None)))
        f.write("Maximum succesful search path length: {}\n".format(max([x for x in stats["search_path_lengths"] if x is not None], default=None)))
        f.write("===================================================\n"*2+"\n")