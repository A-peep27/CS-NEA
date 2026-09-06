import random

#        1  2  3  4  5  6  7  8  9
grid = [[5, 5, 5, 5, 5, 5, 5, 5, 5],#1
        [5, 5, 5, 5, 5, 5, 5, 5, 5],#2
        [5, 5, 5, 5, 5, 5, 5, 5, 5],#3
        [5, 5, 5, 5, 5, 5, 5, 5, 5],#4
        [5, 5, 5, 5, 4, 5, 5, 5, 5],#5
        [5, 5, 5, 5, 5, 5, 5, 5, 5],#6
        [5, 5, 5, 5, 5, 5, 5, 5, 5],#7
        [5, 5, 5, 5, 5, 5, 5, 5, 5],#8
        [5, 5, 5, 5, 5, 5, 5, 5, 5] #9
        ]


def checkAvalabe(x, y, grid):
    free = [False, False, False, False]
    count = 0
    if grid[y+1][x] == 5:
        free[0] = True
        count +=1
    if grid[y][x+1] == 5:
        free[1] = True
        count +=1
    if grid[y-1][x] == 5:
        free[2] = True
        count +=1
    if grid[y][x-1] == 5:
        free[3] = True 
        count +=1

    return count, free


grid[4][4] = 4
allSpacesFilled = False
x = 4
y = 4
yTemp = 4
xTemp = 4
choiceTemp = 0
count = 1



while not allSpacesFilled:
    try:
        if grid[y][x] != 5:

            temp = checkAvalabe(x, y, grid)
            if grid[y][x] == 3:
                if temp[0] == 0:
                    temp[1][0] == False
                else:
                    while True:
                        tempRan = random.randint(0,3)
                        if temp[1][tempRan]:
                            temp[1][tempRan] = False
                            break

            if grid[y][x] == 2:
                if temp[0] == 0:
                    temp[1][0] == False
                elif temp[0] == 1:
                    tempCount = 0
                    while tempCount == 0:
                        tempRan = random.randint(0,3)
                        if temp[1][tempRan]:
                            temp[1][tempRan] = False
                            tempCount += 1
                else:
                    tempCount = 0
                    while tempCount >= 1:
                        tempRan = random.randint(0,3)
                        if temp[1][tempRan]:
                            temp[1][tempRan] = False
                            tempCount += 1

            if grid[y][x] == 1:
                if temp[0] == 4:
                    tempCount = 0
                    while tempCount >= temp[0]-1:
                        tempRan = random.randint(0,3)
                        if temp[1][tempRan]:
                            temp[1][tempRan] = False
                            tempCount += 1        
            else:
                temp[1][0] == False

                        
            yTemp = y- 1
            if grid[yTemp][x] == 5 and temp[1][2]:
                grid[yTemp][x] = random.randint(1,4)
                count +=1
                
            yTemp = y + 1
            if grid[yTemp][x] == 5 and temp[1][0]:
                grid[yTemp][x] = random.randint(1,4)
                count +=1

            xTemp = x - 1
            if grid[y][xTemp] == 5 and temp[1][3]:
                grid[y][xTemp] = random.randint(1,4)
                count +=1

            xTemp = x + 1
            if grid[y][xTemp] == 5 and temp[1][1]:
                grid[y][xTemp] = random.randint(1,4)
                count +=1
            print(count)
            print(x, ' ', y)
            for row in grid:
                for square in row:
                    if square == 5:
                        print('  ', end="")
                    else:
                        print(square, '',end="")
                print()
            input()
    except:
        allSpacesFilled = True

    x= random.randint(0,8)
    y= random.randint(0,8)

