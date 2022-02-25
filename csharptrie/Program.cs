using System;
using System.IO;
using System.Diagnostics;
using System.Collections.Generic;
using System.Linq;
using static System.Console;

class Node {
    public Node Parent;
    public Node[] Nodes;
    public int Index;
    public int Count;

    public static readonly List<Node> AllNodes = new List<Node>();

    public Node(Node parent, int index) {
        this.Parent = parent;
        this.Index = index;
        AllNodes.Add(this);
    }

    public Node Traverse(uint u) {
        int b = (int)u;
        if (this.Nodes is null) {
            this.Nodes = new Node[26];
            return this.Nodes[b] = new Node(this, b);
        }
        if (this.Nodes[b] is null) return this.Nodes[b] = new Node(this, b);
        return this.Nodes[b];
    }

    public string GetWord() => this.Index >= 0 
        ? this.Parent.GetWord() + (char)(this.Index + 97)
        : "";
}

class Freq {
    const int DefaultBufferSize = 0x10000;

    public static void Main(string[] args) {
        var sw = Stopwatch.StartNew();

        if (args.Length < 2) {
            WriteLine("Usage: freq.exe {filename} {k} [{buffersize}]");
            return;
        }

        string file = args[0];
        int k = int.Parse(args[1]);
        int bufferSize = args.Length >= 3 ? int.Parse(args[2]) : DefaultBufferSize;

        Node root = new Node(null, -1) { Nodes = new Node[26] }, current = root;
        int b;
        uint u;

        using (var fr = new FileStream(file, FileMode.Open))
        using (var br = new BufferedStream(fr, bufferSize)) {
            outword:
                b = br.ReadByte() | 32;
                if ((u = (uint)(b - 97)) >= 26) {
                    if (b == -1) goto done; 
                    else goto outword;
                }
                else current = root.Traverse(u);
            inword:
                b = br.ReadByte() | 32;
                if ((u = (uint)(b - 97)) >= 26) {
                    if (b == -1) goto done;
                    ++current.Count;
                    goto outword;
                }
                else {
                    current = current.Traverse(u);
                    goto inword;
                }
            done:;
        }

        List<Node> finalList =  Node.AllNodes
            .OrderByDescending(count => count.Count)
            .Take(k)
            .ToList();

        foreach (Node n in finalList) {
            WriteLine(n.GetWord() + "\t" + n.Count);
        }

        // WriteLine(string.Join("\n", Node.AllNodes
        //     .OrderByDescending(count => count.Count)
        //     .Take(k)
        //     .Select(node => node.GetWord())));

        WriteLine("Self-measured milliseconds: {0}", sw.ElapsedMilliseconds);
    }
}