def words_from_file(filename):
    import re

    pattern = re.compile('[a-z]+')

    for line in open(filename):
        yield from pattern.findall(line.lower())


def freq(textfile, k):
    frequencies = {}

    for word in words_from_file(textfile):
        frequencies[word] = frequencies.get(word, 0) + 1

    most_frequent = sorted(frequencies.items(), key=lambda item: item[1], reverse=True)

    for i, (word, frequency) in enumerate(most_frequent):
        if i == k:
            break

        yield word, frequency


from time import time

start = time()
print('\n'.join('{}:\t{}'.format(f, w) for w, f in freq('../ulysses64', 10)))
end = time()
print(end - start)
