#include <iostream>
#include <cassert>
#include <vector>
#include <algorithm>

typedef int32_t Pointer;  // [0..node.size()), an index into the array of Nodes
typedef int32_t Count;
typedef int8_t Char;  // We'll usually just have 1 to 26.
struct Node {
  Pointer link;  // From a parent node to its children's "header", or from a header back to parent.
  Count count;  // The number of times this word has been encountered. Undefined for header nodes.
};
std::vector<Node> node; // Our "arena" for Node allocation.

std::string word_for(Pointer p) {
  std::vector<char> drow;  // The word backwards
  while (p != 0) {
    Char c = p % 27;
    drow.push_back('a' - 1 + c);
    p = (p - c) ? node[p - c].link : 0;
  }
  return std::string(drow.rbegin(), drow.rend());
}

// `p` has no children. Create `p`s family of children, with only child `c`.
Pointer create_child(Pointer p, Char c) {
  Pointer h = node.size();
  node.resize(node.size() + 27);
  node[h] = {.link = p, .count = -1};
  node[p].link = h;
  return h + c;
}

// Advance `p` to its `c`th child. If necessary, add the child.
Pointer find_child(Pointer p, Char c) {
  assert(1 <= c && c <= 26);
  if (p == 0) return c;  // Special case for first char.
  if (node[p].link == 0) return create_child(p, c);  // Case 1: `p` currently has *no* children.
  return node[p].link + c;  // Case 2 (easiest case): Already have the child c.
}

int main(int, char** argv) {
  auto start_c = clock();

  // Program startup
  std::ios::sync_with_stdio(false);

  // read in file contents
  FILE *fptr = fopen(argv[1], "rb");
  fseek(fptr, 0, SEEK_END);
  long dataLength = ftell(fptr);
  rewind(fptr);
  char* data = (char*)malloc(dataLength);
  fread(data, 1, dataLength, fptr);
  fclose(fptr);

  node.reserve(dataLength / 600);  // Heuristic based on test data. OK to be wrong.
  node.push_back({0, 0});
  for (Char i = 1; i <= 26; ++i) node.push_back({0, 0});

  // Loop over file contents: the bulk of the time is spent here.
  Pointer p = 0;
  for (long i = 0; i < dataLength; ++i) {
    Char c = (data[i] | 32) - 'a' + 1;  // 1 to 26, for 'a' to 'z' or 'A' to 'Z'
    if (1 <= c && c <= 26) {
      p = find_child(p, c);
    } else {
      ++node[p].count;
      p = 0;
    }
  }
  ++node[p].count;
  node[0].count = 0;

  // Brute-force: Accumulate all words and their counts, then sort by frequency and print.
  std::vector<std::pair<int, std::string>> counts_words;
  for (Pointer i = 1; i < static_cast<Pointer>(node.size()); ++i) {
    int count = node[i].count;
    if (count == 0 || i % 27 == 0) continue;
    counts_words.push_back({count, word_for(i)});
  }
  auto cmp = [](auto x, auto y) {
    if (x.first != y.first) return x.first > y.first;
    return x.second < y.second;
  };
  std::sort(counts_words.begin(), counts_words.end(), cmp);
  const int max_words_to_print = std::min<int>(counts_words.size(), atoi(argv[2]));
  for (int i = 0; i < max_words_to_print; ++i) {
    auto [count, word] = counts_words[i];
    std::cout << word << " " << count << std::endl;
  }

  return 0;
}
