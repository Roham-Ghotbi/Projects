"""CS 61A Presents The Game of Hog."""

from dice import four_sided, six_sided, make_test_dice
from ucb import main, trace, log_current_line, interact

GOAL_SCORE = 100  # The goal of Hog is to score 100 points.


######################
# Phase 1: Simulator #
######################

def roll_dice(num_rolls, dice=six_sided):
    """Simulate rolling the DICE exactly NUM_ROLLS>0 times. Return the sum of
    the outcomes unless any of the outcomes is 1. In that case, return the
    number of 1's rolled.
    """
    # These assert statements ensure that num_rolls is a positive integer.
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls > 0, 'Must roll at least once.'
    # BEGIN PROBLEM 1
    sum = 0
    sum_ones = 0
    
    while( num_rolls > 0):
        num = dice()
        
        if(num == 1):
            sum_ones += 1
        elif(sum_ones == 0):
            sum += num
            
        num_rolls -= 1
        
    if(sum_ones == 0):
        return sum
    else:
        return sum_ones
    # END PROBLEM 1

def free_bacon(opponent_score):
    """Return the points scored from rolling 0 dice (Free Bacon)."""
    # BEGIN PROBLEM 2
    if(opponent_score == 0):
        return 1
    else:
        num1 = opponent_score % 10
        num2 = opponent_score // 10
        
        return (max(num1, num2) + 1)
    # END PROBLEM 2

def is_prime(number):
    """ Return True if the input is prime otherwise returns False
    >>> from hog import *
    >>> is_prime(1)
    False
    >>> is_prime(29)
    True
    >>> is_prime(98)
    False
    >>> is_prime(47)
    True
    """
    k = 2
    while(k <= number ** 0.5 + 1):
        if(number % k == 0 and number != 2 or number == 1):
            return False
        
        k += 1
    return True    

def next_prime(number):
    """ Returns the next prime number after the input prime number
    >>> next_prime(2)
    3
    >>> next_prime(19)
    23
    >>> next_prime(11)
    13
    """
    while(number < 62):
        number += 1                  
        
        if(is_prime(number)):
            return number


def take_turn(num_rolls, opponent_score, dice=six_sided):
    """Simulate a turn rolling NUM_ROLLS dice, which may be 0 (Free Bacon).
    Return the points scored for the turn by the current player. Also
    implements the Hogtimus Prime and When Pigs Fly rules.

    num_rolls:       The number of dice rolls that will be made.
    opponent_score:  The total score of the opponent.
    dice:            A function of no args that returns an integer outcome.
    """
    # Leave these assert statements here; they help check for errors.
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls >= 0, 'Cannot roll a negative number of dice in take_turn.'
    assert num_rolls <= 10, 'Cannot roll more than 10 dice.'
    assert opponent_score < 100, 'The game should be over.'
    # BEGIN PROBLEM 2
    limit = 25 - num_rolls
    score = 0
    
    if(num_rolls != 0):
        score = roll_dice(num_rolls, dice)
    else:
        score = free_bacon(opponent_score)
        
    if(is_prime(score)):
        score = next_prime(score)
        
    if(score > limit):
        score = limit
    return score
    
    # END PROBLEM 2


def reroll(dice):
    """Return dice that return even outcomes and re-roll odd outcomes of DICE."""
    def rerolled():
        # BEGIN PROBLEM 3
        num = dice()
        
        if(num % 2 == 0):
            return num
        else:
            return dice()
        # END PROBLEM 3
    return rerolled


def select_dice(score, opponent_score, dice_swapped):
    """Return the dice used for a turn, which may be re-rolled (Hog Wild) and/or
    swapped for four-sided dice (Pork Chop).

    DICE_SWAPPED is True if and only if four-sided dice are being used.
    """
    # BEGIN PROBLEM 4
    if(dice_swapped == True):
        dice = four_sided
    else:
        dice = six_sided
    # END PROBLEM 4
    if((score + opponent_score) % 7 == 0):
        dice = reroll(dice)
        
    return dice


def other(player):
    """Return the other player, for a player PLAYER numbered 0 or 1.

    >>> other(0)
    1
    >>> other(1)
    0
    """
    return 1 - player


def play(strategy0, strategy1, score0=0, score1=0, goal=GOAL_SCORE):
    """Simulate a game and return the final scores of both players, with
    Player 0's score first, and Player 1's score second.

    A strategy is a function that takes two total scores as arguments
    (the current player's score, and the opponent's score), and returns a
    number of dice that the current player will roll this turn.

    strategy0:  The strategy function for Player 0, who plays first
    strategy1:  The strategy function for Player 1, who plays second
    score0   :  The starting score for Player 0
    score1   :  The starting score for Player 1
    """
    player = 0  # Which player is about to take a turn, 0 (first) or 1 (second)
    dice_swapped = False  # Whether 4-sided dice have been swapped for 6-sided
    # BEGIN PROBLEM 5
    score = 0
    opponent_score = 0
    while(score0 < goal and score1 < goal):
        if(player == 0):
            score = score0
            opponent_score = score1
            num_rolls = strategy0(score, opponent_score)
        else:
            score = score1
            opponent_score = score0
            num_rolls = strategy1(score, opponent_score)
            
        dice = select_dice(score, opponent_score, dice_swapped)
        
        if(num_rolls == -1):
            dice_swapped = not dice_swapped
            score += 1
        else:
            score += take_turn(num_rolls, opponent_score, dice) 
                   
        if(score == opponent_score * 2 or opponent_score == score * 2):
            temp = score
            score = opponent_score
            opponent_score = temp
            
        if(player == 0):
            score0 = score
            score1 = opponent_score
        else:
            score1 = score
            score0 = opponent_score
            
        player = other(player)
        
    # END PROBLEM 5
    return score0, score1


#######################
# Phase 2: Strategies #
#######################

def always_roll(n):
    """Return a strategy that always rolls N dice.

    A strategy is a function that takes two total scores as arguments
    (the current player's score, and the opponent's score), and returns a
    number of dice that the current player will roll this turn.

    >>> strategy = always_roll(5)
    >>> strategy(0, 0)
    5
    >>> strategy(99, 99)
    5
    """
    def strategy(score, opponent_score):
        return n
    return strategy


def check_strategy_roll(score, opponent_score, num_rolls):
    """Raises an error with a helpful message if NUM_ROLLS is an invalid
    strategy output. All strategy outputs must be integers from -1 to 10.

    >>> check_strategy_roll(10, 20, num_rolls=100)
    Traceback (most recent call last):
     ...
    AssertionError: strategy(10, 20) returned 100 (invalid number of rolls)

    >>> check_strategy_roll(20, 10, num_rolls=0.1)
    Traceback (most recent call last):
     ...
    AssertionError: strategy(20, 10) returned 0.1 (not an integer)

    >>> check_strategy_roll(0, 0, num_rolls=None)
    Traceback (most recent call last):
     ...
    AssertionError: strategy(0, 0) returned None (not an integer)
    """
    msg = 'strategy({}, {}) returned {}'.format(
        score, opponent_score, num_rolls)
    assert type(num_rolls) == int, msg + ' (not an integer)'
    assert -1 <= num_rolls <= 10, msg + ' (invalid number of rolls)'


def check_strategy(strategy, goal=GOAL_SCORE):
    """Checks the strategy with all valid inputs and verifies that the
    strategy returns a valid input. Use `check_strategy_roll` to raise
    an error with a helpful message if the strategy returns an invalid
    output.

    >>> def fail_15_20(score, opponent_score):
    ...     if score != 15 or opponent_score != 20:
    ...         return 5
    ...
    >>> check_strategy(fail_15_20)
    Traceback (most recent call last):
     ...
    AssertionError: strategy(15, 20) returned None (not an integer)
    >>> def fail_102_115(score, opponent_score):
    ...     if score == 102 and opponent_score == 115:
    ...         return 100
    ...     return 5
    ...
    >>> check_strategy(fail_102_115)
    >>> fail_102_115 == check_strategy(fail_102_115, 120)
    Traceback (most recent call last):
     ...
    AssertionError: strategy(102, 115) returned 100 (invalid number of rolls)
    """
    # BEGIN PROBLEM 6
    score = 0
   
    while(score < goal):
        opponent_score = 0
        
        while(opponent_score < goal):
            strategy_result = strategy(score, opponent_score)   #just to make it more legible
            check_strategy_roll(score, opponent_score,strategy_result )
            opponent_score += 1
            
        score += 1 
    # END PROBLEM 6


# Experiments

def make_averaged(fn, num_samples=1000):
    """Return a function that returns the average_value of FN when called.

    To implement this function, you will have to use *args syntax, a new Python
    feature introduced in this project.  See the project description.

    >>> dice = make_test_dice(3, 1, 5, 6)
    >>> averaged_dice = make_averaged(dice, 1000)
    >>> averaged_dice()
    3.75
    """
    # BEGIN PROBLEM 7
    def function_average(*args):
        sum = 0
        iterator = 0
        
        while(iterator < num_samples):
            sum += fn(*args)
            iterator += 1
            
        return sum / num_samples
    
    return function_average
    # END PROBLEM 7


def max_scoring_num_rolls(dice=six_sided, num_samples=1000):
    """Return the number of dice (1 to 10) that gives the highest average turn
    score by calling roll_dice with the provided DICE over NUM_SAMPLES times.
    Assume that the dice always return positive outcomes.

    >>> dice = make_test_dice(3)
    >>> max_scoring_num_rolls(dice)
    10
    """
    # BEGIN PROBLEM 8
    num_dice = 1
    sum_max = 0
    best_num = 1
    
    while(num_dice <= 10):
        iterator = 0
        sum = 0
        
        while(iterator < num_samples):
            sum += roll_dice(num_dice, dice)
            iterator += 1
            
        if(sum > sum_max):  
            sum_max = sum
            best_num = num_dice
            
        num_dice += 1
        
    return best_num
    # END PROBLEM 8


def winner(strategy0, strategy1):
    """Return 0 if strategy0 wins against strategy1, and 1 otherwise."""
    score0, score1 = play(strategy0, strategy1)
    if score0 > score1:
        return 0
    else:
        return 1


def average_win_rate(strategy, baseline=always_roll(4)):
    """Return the average win rate of STRATEGY against BASELINE. Averages the
    winrate when starting the game as player 0 and as player 1.
    """
    win_rate_as_player_0 = 1 - make_averaged(winner)(strategy, baseline)
    win_rate_as_player_1 = make_averaged(winner)(baseline, strategy)

    return (win_rate_as_player_0 + win_rate_as_player_1) / 2


def run_experiments():
    """Run a series of strategy experiments and report results."""
    if True:  # Change to False when done finding max_scoring_num_rolls
        six_sided_max = max_scoring_num_rolls(six_sided)
        print('Max scoring num rolls for six-sided dice:', six_sided_max)
        rerolled_max = max_scoring_num_rolls(reroll(six_sided))
        print('Max scoring num rolls for re-rolled dice:', rerolled_max)
        
    if True:  # Change to False when done finding max_scoring_num_rolls
        four_sided_max = max_scoring_num_rolls(four_sided)
        print('Max scoring num rolls for four-sided dice:', four_sided_max)
        rerolled_max = max_scoring_num_rolls(reroll(four_sided))
        print('Max scoring num rolls for re-rolled dice:', rerolled_max)    

    if False:  # Change to True to test always_roll(8)
        print('always_roll(8) win rate:', average_win_rate(always_roll(8)))

    if False:  # Change to True to test bacon_strategy
        print('bacon_strategy win rate:', average_win_rate(bacon_strategy))

    if False:  # Change to True to test swap_strategy
        print('swap_strategy win rate:', average_win_rate(swap_strategy))

    if False:  # Change to True to test always_roll(3)
        print('always roll 3 win rate:', average_win_rate(always_roll(3)))
        
    if False:  # Change to True to test always_roll(4)
        print('always roll 4 win rate:', average_win_rate(always_roll(4)))
    
    if True:  # Change to True to test always_roll(5)
        print('always roll 4 average score:',make_averaged(take_turn, 100000)(4, 50, six_sided))
    
    if True:  # Change to True to test always_roll(5)
        print('always roll 4 average score four-sided:',make_averaged(take_turn, 100000)(4, 50, four_sided)) 
        
    if True:  # Change to True to test always_roll(5)
        print('always roll 3 average score four-sided:',make_averaged(take_turn, 100000)(3, 50, four_sided)) 
        
    if True:  # Change to True to test always_roll(5)
        print('always roll 5 average score four-sided:',make_averaged(take_turn, 100000)(5, 50, four_sided))
          
    if True:  # Change to True to test always_roll(5)
        print('always roll 6 average score four-sided:',make_averaged(take_turn, 100000)(6, 50, four_sided))
          
    if True:  # Change to True to test always_roll(5)
        print('always roll 7 average score four-sided:',make_averaged(take_turn, 100000)(7, 50, four_sided))
    
    if True:  # Change to True to test always_roll(6)
        
        print('always roll 6 win rate:', average_win_rate(always_roll(6)))
                
    if False:  # Change to True to test swap_strategy
        print('final_strategy win rate:', average_win_rate(final_strategy))    


# Strategies

def bacon_strategy(score, opponent_score, margin=8, num_rolls=4):
    """This strategy rolls 0 dice if that gives at least MARGIN points,
    and rolls NUM_ROLLS otherwise.
    """
    # BEGIN PROBLEM 9
    points = free_bacon(opponent_score)
    
    if((is_prime(points))):
        points = next_prime(points)
        
    if(points >= margin):
        return 0
    
    return num_rolls
    # END PROBLEM 9
check_strategy(bacon_strategy)


def swap_strategy(score, opponent_score, margin=8, num_rolls=4):
    """This strategy rolls 0 dice when it triggers a beneficial swap. It also
    rolls 0 dice if it gives at least MARGIN points. Otherwise, it rolls
    NUM_ROLLS.
    """
    # BEGIN PROBLEM 10
    points = bacon_strategy(score, opponent_score, margin, num_rolls)
    
    if(points == 0):
        return points
    else:
        if(opponent_score == (score + 1) * 2):
            return 0
        
    return num_rolls
    # END PROBLEM 10
check_strategy(swap_strategy)

def final_strategy(score, opponent_score):
    """
    The First and most important part is to make sure that you start the game 
    by changing the dice to a four-sided dice. After that you have to check whether 
    we can have a swap of scores by using free-bacon or pork-chop rule. Also as a 
    last resort, tried to make sure we will use free-bacon it is is more than 5
    
    """
    num_rolls = swap_strategy(score, opponent_score, 11, 4)
    
    if(score == 0):
        num_rolls = -1
    elif(num_rolls == 0):
        num_rolls = 0
    elif((free_bacon(opponent_score) + score) * 2 == opponent_score):
        num_rolls = 0 
    elif((score + 1) * 2 == opponent_score):
        num_rolls = -1
    elif((score + opponent_score ) % 7 == 0):
        num_rolls = 6
        num_rolls = bacon_strategy(score, opponent_score, 9, num_rolls)
    elif(free_bacon(opponent_score) >= 5):
        num_rolls = 0
        
    return num_rolls
    # END PROBLEM 11
check_strategy(final_strategy)


##########################
# Command Line Interface #
##########################

# NOTE: Functions in this section do not need to be changed. They use features
# of Python not yet covered in the course.

@main
def run(*args):
    """Read in the command-line argument and calls corresponding functions.

    This function uses Python syntax/techniques not yet covered in this course.
    """
    import argparse
    parser = argparse.ArgumentParser(description="Play Hog")
    parser.add_argument('--run_experiments', '-r', action='store_true',
                        help='Runs strategy experiments')

    args = parser.parse_args()

    if args.run_experiments:
        run_experiments()