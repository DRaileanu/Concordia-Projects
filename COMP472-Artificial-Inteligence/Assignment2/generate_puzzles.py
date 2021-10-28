import numpy as np

generate_path = "puzzles.txt"
num_puzzles = 20
size = 3

with open(generate_path, "w") as f:   
    for i in range(1,num_puzzles+1):
        numbers = np.arange(1, size*size+1)
        np.random.shuffle(numbers)
        numbers = np.reshape(numbers, (size,size))
        numbers = numbers.astype(str, copy=False)

        f.write('(')
        for y,row in enumerate(numbers):
            f.write('(')
            for x,num in enumerate(row):
                f.write(f"{num}")
                if x<size-1:
                    f.write(',')
            f.write(')')
            if y<size-1:
                f.write(',')
        f.write(")\n")