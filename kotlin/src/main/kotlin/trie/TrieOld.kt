//package trie
//
//import java.nio.file.Files
//import java.nio.file.Paths
//import kotlin.system.exitProcess
//
//fun main(args: Array<String>) {
//    if (args.isEmpty()) {
//        println("Filename required")
//        exitProcess(1)
//    }
//    val filename = args[0]
//    val k = if (args.size > 1) args[1].toInt() else 10
//    val app = App(filename, k)
//    app.run()
//}
//
//internal class Node(var parent: Node?, var index: Int) {
//    var nodes: Array<Node?>? = null
//    var count = 0
//    val word: String
//        get() = if (index >= 0) {
//            parent!!.word + (index + 97).toChar()
//        } else {
//            ""
//        }
//
//    fun traverse(b: Int): Node? {
//        if (nodes == null) {
//            nodes = arrayOfNulls(26)
//            return Node(this, b).also { nodes!![b] = it }
//        }
//        return if (nodes!![b] == null) Node(this, b).also { nodes!![b] = it } else nodes!![b]
//    }
//
//    init {
//        Trie.allNodes.add(this)
//    }
//}
//
//internal class Trie {
//    public val root = Node(null, -1)
//
//    companion object {
//        var allNodes: MutableList<Node> = ArrayList()
//    }
//}
//
//class App(private val filename: String, private val k: Int) {
//    fun run() {
//        val trie = Trie()
//        val path = Paths.get(filename)
//        var current: Node? = trie.root
//        val inputStream = Files.newInputStream(path).buffered()
//        var b: Int
//        var inWord = false
//        inputStream.use { bis ->
//            while (bis.read().also { b = it } != -1) {
//                val u = (b or 32) - 97
//                if (u >= 26 || u < 0) {
//                    if (inWord) {
//                        ++current!!.count
//                    }
//                    inWord = false
//                } else {
//                    if (!inWord) {
//                        current = trie.root
//                    }
//                    inWord = true
//                    current = current!!.traverse(u)
//                }
//            }
//        }
//        Trie.allNodes.sortWith(Comparator { o1: Node, o2: Node -> o2.count.compareTo(o1.count) })
//        val finalList: List<Node> = Trie.allNodes.take(k)
//        for (node in finalList) {
//            println(node.word + "\t" + node.count)
//        }
//    }
//}