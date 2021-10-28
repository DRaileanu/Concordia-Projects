import math
import numpy as np


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

class State:
    idcount = 0 #for unique id in tree graph
    numTokens = None #common to all States
    max_depth = None #common to all States
    reversedChilds = False
    
    def __init__(self, taken_tokens, depth):
        State.idcount+=1
        self.id=State.idcount
        self.maximizingPlayer = True if len(taken_tokens)%2==0 else False
        self.depth = depth
        self.alpha = -np.inf
        self.beta = np.inf
        self.score = None
        self.taken_tokens=taken_tokens
        self.childs = []
        self.validMoves = []

        #case: first move
        if len(self.taken_tokens)==0:
            self.validMoves = [i for i in range(1, math.ceil(State.numTokens/2))]
        #case: subsequent move
        else:
            lastMove = self.taken_tokens[-1]
            for i in range(1,lastMove):
                if lastMove%i==0 and i not in self.taken_tokens:
                    self.validMoves.append(i)
            for i in range(lastMove+1, State.numTokens+1):
                if i%lastMove==0 and i not in self.taken_tokens:
                    self.validMoves.append(i)

        if self.depth<State.max_depth:
            if State.reversedChilds:
                for move in reversed(self.validMoves):
                    self.childs.append(State(self.taken_tokens+[move], depth+1))
            else:
                for move in self.validMoves:
                    self.childs.append(State(self.taken_tokens+[move], depth+1))

    def static_board_evaluation(self):
        #if end game state, other one wins
        if len(self.validMoves)==0:
            if self.maximizingPlayer:
                self.score = -1.0
            else:
                self.score = 1.0
            return

        #if token 1 not yet taken, nobody has advantage
        if 1 not in self.taken_tokens:
            self.score=0
            return

        lastMove = self.taken_tokens[-1]
        numMoves = len(self.validMoves) 
        #if last move was 1, count number of possible successors
        if lastMove==1:
            numMoves = len(self.validMoves)        
            #return 0.5 or -0.5 depending if odd and if maximizingPlayer
            returnVal = 0.5 if numMoves%2 != 0 else -0.5
            if self.maximizingPlayer:
                self.score = returnVal
            else:
                self.score = -1 * returnVal
            return

        #if last move is a prime, count number of multiples of that prime in successors
        if is_prime(lastMove):
            count=0
            for i in range(lastMove, State.numTokens+1, lastMove):
                if i not in self.taken_tokens:
                    count+=1
            #return 0.7 or -0.7 depending if count is odd and if maximizingPlayer
            returnVal = 0.7 if count%2 != 0 else -0.7
            if self.maximizingPlayer:
                self.score = returnVal
            else:
                self.score = -1 * returnVal
            return
        
        #last move is composite, so find largest prime that divides it and count multiples of it in successors
        for i in range(lastMove-1, 1, -1):
            if is_prime(i):
                maxPrime = i
                break
        count=0
        for i in range(maxPrime, State.numTokens+1, maxPrime):
            if i not in self.taken_tokens:
                count+=1
        returnVal = 0.6 if count%2 != 0 else -0.6
        if self.maximizingPlayer:
            self.score = returnVal
        else:
            self.score = -1 * returnVal
        
    
    def bestMove(self):
        """
            Returns child with best score depending if maximizingPlayer or not
        """
        if self.maximizingPlayer:
            score = -np.inf
            bestMove = None
            for child in self.childs:
                if child.score>score:
                    score=child.score
                    bestMove = child.taken_tokens[-1]
            return bestMove

        else:
            score = np.inf
            bestMove = None
            for child in self.childs:
                if child.score<score:
                    score=child.score
                    bestMove = child.taken_tokens[-1]
            return bestMove
