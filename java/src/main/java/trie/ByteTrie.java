package trie;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ByteTrie {

    static class Node {
        public Node parent;
        public int index;
        public Node[] nodes = null;
        public int count;

        public Node(Node parent, int index) {
            this.parent = parent;
            this.index = index;
            Trie.allNodes.add(this);
        }

        public String getWord() {
            if (this.index >= 0) {
                return this.parent.getWord() + (char) (this.index + 97);
            } else {
                return "";
            }
        }

        public Node traverse(int b) {
            if (this.nodes == null) {
                this.nodes = new Node[26];
                return this.nodes[b] = new Node(this, b);
            }
            if (this.nodes[b] == null) return this.nodes[b] = new Node(this, b);
            return this.nodes[b];
        }
    }

    static class Trie {
        public static List<Node> allNodes = new ArrayList<>();
        private final Node root = new Node(null, -1);
    }

    public static class App {
        private final String filename;
        private final int k;

        public App(String filename, int k) {
            this.filename = filename;
            this.k = k;
        }

        public void run() throws IOException {
            Trie trie = new Trie();

            Path path = Paths.get(filename);

            Node current = trie.root;

            InputStream inputStream = Files.newInputStream(path);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            int b;
            boolean inWord = false;
            while ((b = bufferedInputStream.read()) != -1) {
                int u = ((b | 32) - 97);
                if (u >= 26 || u < 0) {
                    if (inWord) {
                        ++current.count;
                    }
                    inWord = false;
                } else {
                    if (!inWord) {
                        current = trie.root;
                    }
                    inWord = true;
                    current = current.traverse(u);
                }
            }

            bufferedInputStream.close();
            inputStream.close();
            Trie.allNodes.sort((o1, o2) -> Integer.compare(o2.count, o1.count));
            List<Node> finalList = Trie.allNodes.subList(0, k);
            for (Node node : finalList) {
                System.out.println(node.getWord() + "\t" + node.count);
            }
        }

    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Filename required");
            System.exit(1);
        }
        String filename = args[0];
        int k = args.length > 1 ? Integer.parseInt(args[1]) : 10;
        App app = new App(filename, k);
        app.run();
    }

}