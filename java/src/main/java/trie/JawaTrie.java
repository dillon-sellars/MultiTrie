package trie;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JawaTrie {

    static class TrieNode {
        public TrieNode parent;
        public int index;
        public TrieNode[] arr = new TrieNode[26];
        public int count;

        public TrieNode(TrieNode parent, int index) {
            this.parent = parent;
            this.index = index;
        }

        public String getWord() {
            if (this.index >= 0) {
                return this.parent.getWord() + (char) (this.index + 97);
            } else {
                return "";
            }
        }
    }

    static class Trie {
        public List<TrieNode> allNodes = new ArrayList<>();
        private final TrieNode root = new TrieNode(null, -1);

        // Inserts a word into the trie.
        public void insert(String word) {
            TrieNode p = root;
            for (char element : word.toCharArray()) {
                int index = element - 'a';
                if (index > 26) {
                    throw new RuntimeException("Oh shit");
                }
                if (p.arr[index] == null) {
                    TrieNode temp = new TrieNode(p, index);
                    allNodes.add(temp);
                    p.arr[index] = temp;
                    p = temp;
                } else {
                    p = p.arr[index];
                }
            }
            p.count++;
        }
    }

    public static class App {
        private final String filename;
        public App(String filename) {
            this.filename = filename;
        }

        public void run() throws IOException {
            long start = System.currentTimeMillis();

            Trie trie = new Trie();

            Pattern regex = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE);

            Path path = Paths.get(filename);

            long buildStart = System.currentTimeMillis();
            BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                regex.matcher(line).results().forEach(
                        matchResult -> trie.insert(matchResult.group().toLowerCase())
                );
            }
            bufferedReader.close();
            long buildEnd = System.currentTimeMillis();

            System.out.println("Build time: " + (buildEnd - buildStart) + "ms");

            long sortStart = System.currentTimeMillis();
            trie.allNodes.sort((o1, o2) -> Integer.compare(o2.count, o1.count));
            List<TrieNode> finalList = trie.allNodes.subList(0, 10);
            long sortEnd = System.currentTimeMillis();
            System.out.println("Sort time: " + (sortEnd - sortStart) + "ms");
            long outputStart = System.currentTimeMillis();
            for (TrieNode node : finalList) {
                System.out.println(node.getWord() + " - " + node.count);
            }
            long outputEnd = System.currentTimeMillis();
            System.out.println("Output time: " + (outputEnd - outputStart) + "ms");

            long end = System.currentTimeMillis();

            System.out.println("Run time: " + (end - start) + "ms");
        }
    }


    public static void main(String[] args) throws IOException {
        String filename = args[0];
        App app = new App(filename);
//        for (int i = 0; i < 10; i++) {
//            app.run();
//        }
        app.run();
    }

}