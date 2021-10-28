def pnt_player(tokens, taken_tokens, list_of_taken_tokens, depth):
    pass

'''
function max-value(game, state, alpha, beta) returns a (utility, move) pair
    if game.IS-TERMINAL(state) then return game.UTILITY(state, player), null
    v <- -inf
    for each a in game.ACTIONS(state) do
        v2, a2 <- MIN-VALUE(game, game.RESULT(state, a), alpha, beta)
        if v2 > v then
            v, move <- v2, a
            alpha <- MAX(alpha, v)
        if v >= beta then return v, move
    return v, move

function MIN-VALUE(game, state, alpha, beta) returns a (utility, move) pair
    if game.IS-TERMINAL(state) then return game.UTILITY(state, player), null
    v <- +inf
    for each a in game.ACTIONS(state) do
        v2, a2 <- MAX-VALUE(game, game.RESULT(state, a), alpha, beta)
        if v2 < v then
            v, move <- v2, a
            beta <- MIN(Beta, v)
        if v <= alpha then return v, move
    return v, move
'''

## Playing around

