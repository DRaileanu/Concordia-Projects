import sys
import math
import numpy as np

#global variables for output
nodes_parent_count = 0
nodes_visited = 0
nodes_evaluated = 0
max_depth_reached = 0

#####################
# Utility functions #
#####################

def is_prime(n):
    """
        Check if number is prime
        Optimized using the fact that any prime number except 2,3 is of the form 6k+1 or 6k-1
        Source: https://en.wikipedia.org/wiki/Primality_test
    """
    if n==2 or n==3: return True
    if n%2==0 or n%3==0: return False
    i=5 #i will always be 6k-1
    while i**2 <= n: #if haven't found divisor by sqrt(n), has to be prime
        if n%i==0 or n%(i+2)==0: return False  #check if 6k-1 or 6k+1 are dividing n
        i+=6
    return True

def getValidMoves(num_tokens, taken_tokens):
    """
        Returns array of valid moves from current taken_tokens
    """
    valid_moves = []
    #case: first move
    if len(taken_tokens)==0:
        valid_moves = [i for i in range(1, math.ceil(num_tokens/2))]
    #case: subsequent move
    else:
        lastMove = taken_tokens[-1]
        #divides lastMove
        for i in range(1,lastMove):
            if lastMove%i==0 and i not in taken_tokens:
                valid_moves.append(i)
        #multiple of last move
        for i in range(lastMove+1, num_tokens+1):
            if i%lastMove==0 and i not in taken_tokens:
                valid_moves.append(i)
    return valid_moves


def static_board_evaluation(num_tokens, taken_tokens, valid_moves, maxTurn):
    """
        Returns value for current state of the board (represented by taken_tokens)
    """
    global nodes_evaluated
    nodes_evaluated+=1

    #if end game state, other one wins
    if len(valid_moves)==0:
        return -1.0 if maxTurn else 1.0

    #if token 1 not yet taken, nobody has advantage
    if 1 not in taken_tokens:
        return 0

    last_move = taken_tokens[-1]
    #if last move was 1, count number of possible successors
    if last_move==1:   
        #return 0.5 or -0.5 depending if odd number of moves possible
        returnVal = 0.5 if len(valid_moves)%2 != 0 else -0.5
        return returnVal if maxTurn else -1 * returnVal

    #if last move is a prime, count number of multiples of that prime in successors
    if is_prime(last_move):
        count=0
        for i in range(last_move, num_tokens+1, last_move):
            if i not in taken_tokens:
                count+=1
        #return 0.7 or -0.7 depending if count of primes is odd
        returnVal = 0.7 if count%2 != 0 else -0.7
        return returnVal if maxTurn else -1 * returnVal
    
    #last move is composite, so find largest prime that divides it and count multiples of it in successors
    for i in range(last_move-1, 1, -1):
        if is_prime(i):
            maxPrime = i
            break
    count=0
    for i in range(maxPrime, num_tokens+1, maxPrime):
        if i not in taken_tokens:
            count+=1
    returnVal = 0.6 if count%2 != 0 else -0.6
    return returnVal if maxTurn else -1 * returnVal
        

def alpha_beta_search(num_tokens, taken_tokens, depth, alpha, beta, maxTurn, current_depth=0):
    global nodes_visited, nodes_parent_count, max_depth_reached
    nodes_visited+=1
    #check if reached deeper node. Remember that leaves have depth 0, so track min
    if current_depth>max_depth_reached:
        max_depth_reached = current_depth

    valid_moves = getValidMoves(num_tokens, taken_tokens)
    
    #reached leaf node, no move to return, just static evalution
    if depth==0 or len(valid_moves)==0:
        value = static_board_evaluation(num_tokens, taken_tokens, valid_moves, maxTurn)
        return value, None
        
    #add count to total number of parent nodes to compute branching factor later
    nodes_parent_count+=1

    if maxTurn:
        max_value = -np.inf
        bestMove = None
        for move in valid_moves:
            childState = taken_tokens+[move]
            value, trash = alpha_beta_search(num_tokens, childState, depth-1, alpha, beta, False, current_depth+1)

            if value>max_value:
                bestMove=move
                max_value=value
            alpha = max(alpha, value)
            #check if can prune
            if beta<=alpha:
                break
        return max_value, bestMove

    else:
        min_value = np.inf
        bestMove = None
        for move in valid_moves:
            childState = taken_tokens+[move]
            value, trash=alpha_beta_search(num_tokens, childState, depth-1, alpha, beta, True, current_depth+1)

            if value<min_value:
                bestMove=move
                min_value=value
            beta = min(beta, value)
            #check if can prune
            if beta<=alpha:
                break
        return min_value, bestMove



########
# MAIN #
########

def main():
    write_output = True
    #data from command line args
    num_tokens = int(sys.argv[1])
    taken_tokens = int(sys.argv[2])
    taken_tokens_list = list(map(int, sys.argv[3:-1]))
    depth = int(sys.argv[-1])

    maximizingPlayer = True if taken_tokens%2==0 else False
    #perform alpha-beta-search
    value, move = alpha_beta_search(num_tokens, taken_tokens_list, depth if depth!=0 else np.inf, -np.inf, np.inf, maximizingPlayer)

    #output results to console
    print(f"best move:{move}")
    print(f"value: {value}")
    print(f"number of nodes visited: {nodes_visited}")
    print(f"number of nodes evaluated: {nodes_evaluated}")
    print(f"Max Depth Reached: {max_depth_reached}")
    print(f"branching factor: {(nodes_visited-1)/(nodes_parent_count):.1f}")

    #output results to file
    if write_output==True:
        with open("output_{}_{}_{}_{}.txt".format(num_tokens, taken_tokens, ' '.join(str(x) for x in taken_tokens_list), depth), 'w') as f:
            f.write("Input: {} {} {} {}\n".format(num_tokens, taken_tokens, ' '.join(str(x) for x in taken_tokens_list), depth))
            f.write(f"best move:{move}\n")
            f.write(f"value: {value}\n")
            f.write(f"number of nodes visited: {nodes_visited}\n")
            f.write(f"number of nodes evaluated: {nodes_evaluated}\n")
            f.write(f"Max Depth Reached: {max_depth_reached}\n")
            f.write(f"branching factor: {(nodes_visited-1)/(nodes_parent_count):.1f}\n")


#EOF
if __name__ == "__main__":
    main()