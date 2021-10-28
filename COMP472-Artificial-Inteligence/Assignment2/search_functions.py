from state import *
import random
import numpy as np
import time


def get_sol_path(state):
    """
        goes up the search tree to provide solution path
        @params:
            state: goal state(must have been found by search and not just generated)
        @return:
            State list from start to goal
    """
    sol_path = [state]
    state = state.parent
    while state!=None:
        sol_path.append(state)
        state = state.parent
    sol_path.reverse()
    return sol_path


def DFS_search(start_order, goal_order, time_limit=np.inf):
    """
        performs DFS search until reaches state with goal_order, or time runs out
        @params:
            start_order: nxn np.array of original puzzle order
            goal_order: nxn np.array of goal puzzle order
            time_limit: time limit in seconds to stop the search. Default has no limit
        @return:
            sol_path: State list from start to goal. Could be None if no solution
            search_path: State list of all States visited during search, could be None if no solution
            time_elapsed: time in seconds that search took. None if no solution found.
    """
    #setup initial variables
    time_start = time.time()
    start_state = State(start_order)
    open_list = [start_state]
    open_set = set([start_state.strRep])
    close_set = set()
    search_path = []
    
    while open_list:
        #check if time limit is reached
        time_cur = time.time()
        if time_cur-time_start>time_limit:
            return None, None, None

        #get next State from list and see if goal
        curState = open_list.pop()
        open_set.remove(curState.strRep)
        search_path.append(curState)
        if (curState.order==goal_order).all():
            time_elapsed = time.time() - time_start
            sol_path = get_sol_path(curState)
            return sol_path, search_path, time_elapsed

        #add to close_set and get adjacent States
        close_set.add(curState.strRep)
        adjOrders = curState.explore()
        adjStates = [State(order, parent=curState, G=curState.G+1) for order in adjOrders]

        #append adjacent States to open if encountering for first time
        for state in adjStates:
            if state.strRep not in open_set and state.strRep not in close_set:
                open_list.append(state)
                open_set.add(state.strRep)
        
    #if explored all open states and still no solution(should never happend though..so printing to catch if does)
    print("Open is empty and couldn't find goal, something is wrong!")
    return None, None, None


def Iterative_Deepening_search(start_order, goal_order, time_limit=np.inf):
    """
        performs Iterative Depth Search until reaches state with goal_order, or time runs out
        @params:
            start_order: nxn np.array of original puzzle order
            goal_order: nxn np.array of goal puzzle order
            time_limit: time limit in seconds to stop the search. Default has no limit
        @return:
            sol_path: State list from start to goal. Could be None if no solution
            search_path: State list of all States visited during search, could be None if no solution
            time_elapsed: time in seconds that search took. None if no solution found.
    """
    time_start = time.time()
    depth_limit = 0
    search_path = []
    while(True):
        #setup initial variables
        start_state = State(start_order)
        open_list = [start_state]
        open_set = set([start_state.strRep])
        close_map = {}#map instead of set because need to also keep track of State.G (same speed to search)

        while open_list:
            #check if reached time limit
            time_cur = time.time()
            if time_cur - time_start > time_limit:
                return None, None, None

            #get next State and see if goal State
            curState = open_list.pop()
            open_set.remove(curState.strRep)
            search_path.append(curState)


            if (curState.order==goal_order).all():
                time_elapsed = time.time() - time_start
                sol_path = get_sol_path(curState)
                return sol_path, search_path, time_elapsed


            #add to close_map and don't expand if reached depth limit
            close_map[curState.strRep]=curState.G
            if curState.G==depth_limit:
                continue

            #if depth limit not reached, get adjacent states
            adjOrders = curState.explore()
            adjStates = [State(order, parent=curState, G=curState.G+1) for order in adjOrders]

            #TODO can optimize the next lines with difference operation
            for state in adjStates:
                #append adjacent States to open if encountering for first time
                if state.strRep not in open_set and state.strRep not in close_map:
                    open_list.append(state)
                    open_set.add(state.strRep)
                #revisit State in future if found better way to it(otherwise no guarantee for shortest path!)
                elif state.strRep in close_map:
                    if state.G < close_map[state.strRep]:
                        open_list.append(state)
                        open_set.add(state.strRep)
                        del close_map[state.strRep]

            
        #if open_list is empty, then increment depth and start over
        depth_limit+=1


def A_search(start_order, goal_order, heuristicFunc, time_limit=np.inf):
    """
        performs algorithm A Search until reaches state with goal_order, or time runs out
        Not A*, since expects heuristicFunc to be non-admissible, so we check if explored State is in closed state as maybe found better path to it
        @params:
            start_order: nxn np.array of original puzzle order
            goal_order: nxn np.array of goal puzzle order
            heuristicFunc: non-admissible function that takes a State.order as parameter to estimate cost to goal(goal is hardcoded as increasing order)
            time_limit: time limit in seconds to stop the search. Default has no limit
        @return:
            sol_path: State list from start to goal. Could be None if no solution
            search_path: State list of all States visited during search, could be None if no solution
            time_elapsed: time in seconds that search took. None if no solution found.
    """
    #setup initial variables
    time_start = time.time()
    h = heuristicFunc(start_order)
    start_state = State(start_order, G=0, H=h)
    open_list = [start_state]
    close_map = {}
    search_path = []
    while open_list:
        #check if time ran out
        time_cur = time.time()
        if time_cur-time_start>time_limit:
            return None, None, None

        #get next State, which is the one with has minimum f(n)=g(n)+h(n). And check if goal state
        curState = min(open_list, key=lambda el:el.G + el.H)
        search_path.append(curState)
        if (curState.order==goal_order).all():
            sol_path = get_sol_path(curState)
            time_elapsed = time.time() - time_start
            return sol_path, search_path, time_elapsed

        #if not goal state, remove from open and put in closed
        open_list.remove(curState)
        close_map[curState.strRep] = curState.G

        #get adjacent States and their coresponding estimated heuristic cost
        adjOrders = curState.explore()
        adjOrders_h = []
        for order in adjOrders:
            h = heuristicFunc(order)
            adjOrders_h.append(h)
        adjStates = []
        for order, h in zip(adjOrders, adjOrders_h):
            adjStates.append(State(order, parent=curState, G=curState.G+1, H=h))

        #check if adjacent States need to be added to open
        for state in adjStates:
            #if state in close list, but we found a lower estimated cost, update it and place in open
            if state.strRep in close_map:
                if state.G < close_map[state.strRep]:
                    open_list.append(state)
                    del close_map[state.strRep]

            #if state in open, but we found lower estimated cost, update it
            if state in open_list:
                old_state = open_list[open_list.index(state)]
                if state.G > old_state.G:
                    open_list.remove(old_state)
                    open_list.append(state)
            #if never seen the state, simply add to open
            else:
                open_list.append(state)
        
    time_elapsed = time.time() - time_start
    return None, search_path, time_elapsed


def Astar_search(start_order, goal_order, heuristicFunc, time_limit=np.inf):
    """
        performs algorithm A* Search until reaches state with goal_order, or time runs out
        EXPECTS heuristicFunc TO BE ADMISSIBLE, so we don't check if explored State is in closed state as it can't be better than before
        @params:
            start_order: nxn np.array of original puzzle order
            goal_order: nxn np.array of goal puzzle order
            heuristicFunc: ADMISSIBLE function that takes a State.order as parameter to estimate cost to goal(goal is hardcoded as increasing order)
            time_limit: time limit in seconds to stop the search. Default has no limit
        @return:
            sol_path: State list from start to goal. Could be None if no solution
            search_path: State list of all States visited during search, could be None if no solution
            time_elapsed: time in seconds that search took. None if no solution found.
    """
    #setup initial variables
    time_start = time.time()
    h = heuristicFunc(start_order)
    start_state = State(start_order, G=0, H=h)
    open_list = [start_state]
    close_map = {}
    search_path = []
    while open_list:
        #check if time ran out
        time_cur = time.time()
        if time_cur-time_start>time_limit:
            return None, None, None

        #get next State, which is the one with has minimum f(n)=g(n)+h(n). And check if goal state
        curState = min(open_list, key=lambda el:el.G + el.H)
        search_path.append(curState)
        if (curState.order==goal_order).all():
            sol_path = get_sol_path(curState)
            time_elapsed = time.time() - time_start
            return sol_path, search_path, time_elapsed

        #if not goal state, remove from open and put in closed
        open_list.remove(curState)
        close_map[curState.strRep] = curState.G

        #get adjacent States and their coresponding estimated heuristic cost
        adjOrders = curState.explore()
        adjOrders_h = []
        for order in adjOrders:
            h = heuristicFunc(order)
            adjOrders_h.append(h)
        adjStates = []
        for order, h in zip(adjOrders, adjOrders_h):
            adjStates.append(State(order, parent=curState, G=curState.G+1, H=h))

        #check if adjacent States need to be added to open
        for state in adjStates:
            #if state in open, but we found lower estimated cost, update it
            if state in open_list:
                old_state = open_list[open_list.index(state)]
                if state.G > old_state.G:
                    open_list.remove(old_state)
                    open_list.append(state)
            #if never seen the state, simply add to open
            else:
                open_list.append(state)
        
    time_elapsed = time.time() - time_start
    return None, search_path, time_elapsed


#NON-ADMISSIBLE
def heuristic_misplaced_tiles(order):
    order = order.ravel()
    goal_order = np.arange(1, len(order)+1)
    return np.sum(order!=goal_order)

#NON-ADMISSIBLE
def heuristic_manhattan(order):
    size = len(order)
    count = 0
    for y, row in enumerate(order):
        for x, val in enumerate(row):
            #y_goal, x_goal = np.where(goal_order==val)
            #count+=abs(x-x_goal[0]) + abs(y-y_goal[0])
            ydist = abs((val-1)//size - y)
            xdist = abs((val-1)%size - x)
            count+=ydist+xdist
    return count

#NON-ADMISSIBLE
def heuristic_permutations(order):
    order = list(order.ravel())
    #goal_order = goal_order.ravel()
    #goal_index_list = [np.where(goal_order==i)[0][0] for i in order]
    return perm_inversions(order)
def perm_inversions(order):
    """
    finds permutation inversions by finding permutations for max value, then add to recursive call with max value removed
    permList: list of ordering of a state
    return: sum of permutation inversions
    """
    if len(order)==1:
        return 0
    else:
        max_val = max(order)
        numInversion=len(order)-order.index(max_val)-1
        order.remove(max_val)
        return numInversion+perm_inversions(order)

#NON-ADMISSIBLE
def heuristic_euclidean(order):
    size = len(order)
    count = 0
    for y, row in enumerate(order):
        for x, val in enumerate(row):
            ydist = abs((val-1)//size - y)
            xdist = abs((val-1)%size - x)
            count+=np.sqrt(ydist**2 + xdist**2)
    return count

#NON-ADMISSIBLE
def heuristic_wrong_rows_cols(order):
    size = len(order)
    count = 0
    for y, row in enumerate(order):
        for x, val in enumerate(row):
            if abs((val-1)//size - y)!=0:
                count+=1
            if abs((val-1)%size - x)!=0:
                count+=1
    return count

#ADMISSIBLE
def heuristic_max_manhattan(order):
    size = len(order)
    maxdist = 0
    for y, row in enumerate(order):
        for x, val in enumerate(row):
            #y_goal, x_goal = np.where(goal_order==val)
            #count+=abs(x-x_goal[0]) + abs(y-y_goal[0])
            ydist = abs((val-1)//size - y)
            xdist = abs((val-1)%size - x)
            count=ydist+xdist
            if count>maxdist:
                maxdist=count
    return maxdist