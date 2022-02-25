#!/usr/bin/env python3

'''
A script to generate text files that look like a novel in TXT form.
Words are completely made up, but vaguely resemble the Finnish language.
The resulting text uses ASCII encoding with only printable characters.
Distribution of words follows Zipf's law.

Standard parameters generate 1 GB text with 148391 distinct words.

Used to benchmark solutions of the Bentley's k most frequent words problem:
    https://codegolf.stackexchange.com/q/188133/
'''

import re, sys
from random import randint, seed, shuffle, expovariate
from collections import Counter
import urllib.request

# NOTE: changing these parameters, apart from the book size, will change the text contents

BOOK_SIZE = 1<<30               # 1 GB
DISTINCT_WORDS = 5000000        # bigger number will allow longer longest word
MEAN = 15000                    # bigger number will increase average word length

VOWELS = 'aeiouy'
FORBIDDEN = ['satan', 'lenin', 'stalin', 'hitl', 'naz', 'rus', 'putin']

seed(63245986)      # with this seed, the title should be "Itera Aeno", md5sum = 4dcf116dc35156ec939f8cafd61bdf18

print('Getting the reference text', file=sys.stderr)
text = urllib.request.urlopen('http://www.gutenberg.org/files/11940/11940-8.txt').read().decode('ISO-8859-1').lower()
text = text.replace('ä','a').replace('å','a').replace('ö','o')     # specific to Finnish
start = re.search('start of th.*$', text, re.M).end()
end = re.search('^.*end of th', text, re.M).start()
text = text[start:end].strip()
_, __, text = text.split('\n',2)        # discard two first lines
print(text[:100], file=sys.stderr)
print('...', file=sys.stderr)
print(text[-100:], file=sys.stderr)

print('Getting reference words', file=sys.stderr)
all_words = re.findall('[a-z]+', text.lower())
all_words = set(all_words)
print(len(all_words),'reference words', file=sys.stderr)

print('Training Markov chain', file=sys.stderr)
markov = {'total': 0}
def train_markov(word):
    global markov
    m = markov
    for letter in word:
        if m==markov:
            m['total'] += 1
        if letter in m:
            m = m[letter]
            m['total'] += 1
        else:
            m[letter] = {'total': 1}
            m = m[letter]

for word in all_words:
    while len(word)>=3:
        train_markov(word)
        word = word[1:]

print('Generating artificial words', file=sys.stderr)
def next_letter(word):
    global markov
    m = markov
    for letter in word[-2:]:
        if letter not in m:
            m = markov
        m = m[letter]
    i = randint(0, m['total']-1)
    for c in range(ord('a'), ord('z')+1):
        c = chr(c)
        if c not in m:
            continue
        if m[c]['total'] > i:
            return c
        else:
            i -= m[c]['total']

word_set = set()
word_list = []
while len(word_set)<DISTINCT_WORDS:
    w = ''
    m = markov
    while not w or w in word_set:
        w += next_letter(w)
    if not [b for b in FORBIDDEN if b in w] and [v for v in VOWELS if v in w]:
        word_set.add(w)
        word_list.append(w)
        if len(word_set) % 100000 == 0:
            print(len(word_set),'words generatated', file=sys.stderr)
del word_set

print('Capitalizing some words', file=sys.stderr)
for i in range(len(word_list)):
    if not randint(0,100):
        word_list[i] = word_list[i].capitalize()

print('Generating text', file=sys.stderr)
class Book:

    TAB = '   '
    LINE_WIDTH = 76

    def __init__(self):
        self.title = ''
        self.author = ''
        self.year = '2019'
        self.verlag = ''        # "Publisher"
        self.line = ''
        self.front = False
        self.capitalize = True
        self.counter = Counter()
        self.length = 0

    def len(self):
        return self.length

    def next_word(self, word):
        self.counter.update([word])
        if not self.front:
            if self.title.count(' ') < 2:
                self.title += word.capitalize() + ' '
            elif self.author.count(' ') < 2:
                self.author += word.capitalize() + ' '
            else:
                self.verlag = word.capitalize()
                self.print_front()
            return
        if self.capitalize:
            word = word.capitalize()
            self.capitalize = False
        paragraph = False
        if not randint(0,9):
            word += ','
        elif not randint(0,9):
            word += '.'
            self.capitalize = True
            if not randint(0,9):
                paragraph = True
        if len(self.line + ' ' + word) > self.LINE_WIDTH:
            self.length += len(self.line) + 1
            print(self.line)
            self.line = word
        elif paragraph:
            self.line = self.line + ' ' + word + '\n'
            self.length += len(self.line) + 1
            print(self.line)
            self.line = self.TAB
        else:
            self.line += ' ' + word

    def print_front(self):
        print(self.title.rstrip().upper()+'\n')
        print(self.author.rstrip()+'\n')
        print('(c) {}, {}, Public domain\n'.format(self.year, self.verlag))
        self.line = self.TAB
        self.front = True

    def end(self):
        if self.line.strip():
            print(self.line+'.')
        print('\n--' + '\n'*7 + 'Most common words:')
        for w,f in self.counter.most_common()[:10]:
            print('-',w)

LAMBDA = 1 / MEAN
book = Book()
while book.len() < BOOK_SIZE:
    i = int(expovariate(LAMBDA))
    if i < len(word_list):
        book.next_word(word_list[i])
book.end()
