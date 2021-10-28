import numpy as np

class State:
    """
        Class to hold all info about a state of the puzzle.
        @attributes:
            order: nxn np.array with ordering of tiles
            strRep: a string represenation of the ordering. Used so that we can place it in set(), as arrays are non-hashable
            parent: parent node from whom the state was explored
            H: estimated cost to goal
            G: cost from start state to this State
    """
    def __init__(self, order, parent=None, H=0, G=0):
        self.order = order
        self.strRep = ",".join(order.ravel().astype(str))
        self.parent = parent
        self.H = H
        self.G = G

    def explore(self):
        """
            generates list with 12 possible adjacent states
        """
        dimSize = len(self.order)
        adjOrders = []
        for y,row in enumerate(self.order):
            for x,val in enumerate(row):
                if(y<dimSize-1):
                    adjOrder = np.copy(self.order)
                    adjOrder[y,x], adjOrder[y+1,x] = adjOrder[y+1,x], adjOrder[y,x]
                    adjOrders.append(adjOrder)
                if(x<dimSize-1):
                    adjOrder = np.copy(self.order)
                    adjOrder[y,x], adjOrder[y,x+1] = adjOrder[y,x+1], adjOrder[y,x]
                    adjOrders.append(adjOrder)
        adjOrders.reverse()
        return adjOrders

    #two States are equal if the ordering of tiles is the same
    def __eq__(self, other):
        if isinstance(other, State):
            return (self.order==other.order).all()
        return False
    
    def __neq__(self, other):
        return not self.__eq__(other)








