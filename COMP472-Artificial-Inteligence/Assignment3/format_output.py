def print(move, value, nodes_visited, nodes_evaluated, max_depth, avg_branch_factor):
    print("Move: {}".format(move))
    print("Value: {:.1f}".format(value))
    print("Number of Nodes Visited: {}".format(nodes_visited))
    print("Number of Nodes Evaluated: {}".format(nodes_evaluated))
    print("Max Depth Reached: {}".format(max_depth))
    print("Avg Effective Branching Factor: {:.1f}".format(avg_branch_factor)) 
    