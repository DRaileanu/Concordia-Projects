import sys
import math
from state import *
import numpy as np
import pygraphviz as pgv

#global variables for output
nodes_parent_count = 0
nodes_visited = 0
nodes_evaluated = 0
max_depth_reached = 0


def alpha_beta_search(state, depth):
    global nodes_visited, nodes_evaluated, max_depth_reached, nodes_parent_count
    nodes_visited+=1
    if state.depth>max_depth_reached:#depth 0 are leaves, so we track minimum depth
        max_depth_reached = state.depth

    #if leaf node, return its static evaluation
    if depth==0 or len(state.validMoves)==0:
        state.static_board_evaluation()
        nodes_evaluated+=1
        return
    
    #add count to total number of parent nodes to compute branching factor later
    if len(state.childs)>0:
        nodes_parent_count+=1

    if state.maximizingPlayer:
        state.score = -np.inf
        for child in state.childs:
            #pass down alpha and beta to child
            child.alpha = state.alpha
            child.beta = state.beta
            #calculate child.score (no need to return, it's a side effect)
            alpha_beta_search(child, depth-1)
            #update current state score and alpha
            state.score = max(state.score, child.score)
            state.alpha = max(state.alpha, state.score)
            #check if can prune
            if state.beta<=state.alpha:
                return

    else:
        state.score = np.inf
        for child in state.childs:
            #pass down alpha and beta to child
            child.alpha = state.alpha
            child.beta = state.beta
            #calculate child.score
            alpha_beta_search(child, depth-1)
            #update current state score and beta
            state.score = min(state.score, child.score)
            state.beta = min(state.beta, state.score)
            #check if can prune
            if state.beta<=state.alpha:
                return




def main():
    write_output = False
    #data from command line args
    num_tokens = int(sys.argv[1])
    taken_tokens = int(sys.argv[2])
    taken_tokens_list = list(map(int, sys.argv[3:-1]))
    depth = int(sys.argv[-1])

    #setup State variables for search
    State.numTokens = num_tokens
    State.max_depth = depth if depth!=0 else np.inf
    State.reversedChilds=False
    state = State(taken_tokens_list, depth=0)

    #perform alpha-beta-search
    alpha_beta_search(state, State.max_depth)

    #output results to console
    print(f"best move:{state.bestMove()}")
    print(f"value: {state.score}")
    print(f"number of nodes visited: {nodes_visited}")
    print(f"number of nodes evaluated: {nodes_evaluated}")
    print(f"Max Depth Reached: {max_depth_reached}")
    print(f"branching factor: {(nodes_visited-1)/nodes_parent_count:.1f}")

    #output to results to file
    if write_output==True:
        with open("output_{}_{}_{}_{}.txt".format(num_tokens, taken_tokens, ' '.join(str(x) for x in taken_tokens_list), depth), 'w') as f:
            f.write("Input: {} {} {} {}\n".format(num_tokens, taken_tokens, ' '.join(str(x) for x in taken_tokens_list), depth))
            f.write(f"best move:{state.bestMove()}\n")
            f.write(f"value: {state.score}\n")
            f.write(f"number of nodes visited: {nodes_visited}\n")
            f.write(f"number of nodes evaluated: {nodes_evaluated}\n")
            f.write(f"Max Depth Reached: {max_depth_reached}\n")
            f.write(f"branching factor: {(nodes_visited-1)/nodes_parent_count:.1f}\n")


    #utility function to generate .dot file for tree graph
    def generate_tree(state, G):
        G.add_node(f"{state.id}")
        node = G.get_node(f"{state.id}")
        node.attr['fillcolor']="#FFFFFF" if state.maximizingPlayer else "#000000"
        node.attr['fontcolor']="#000000" if state.maximizingPlayer else "#FFFFFF"
        node.attr['label']=f"{state.taken_tokens}\nscore:{state.score}\nalpha:{state.alpha}\nbeta:{state.beta}"
        for child in state.childs:
            generate_tree(child,G)
            G.add_edge(f"{state.id}", f"{child.id}", label=child.taken_tokens[-1])

    #write tree of the alpha beta search results
    G=pgv.AGraph()
    G.node_attr['style']='filled'
    generate_tree(state,G)
    with open("graph.dot", 'w') as f:
        f.write(G.to_string())
    


#EOF
if __name__ == "__main__":
    main()

