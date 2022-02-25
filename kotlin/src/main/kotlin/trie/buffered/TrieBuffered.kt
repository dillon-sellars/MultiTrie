//package trie.buffered
//
//import java.nio.file.Files
//import java.nio.file.Paths
//import kotlin.system.exitProcess
//
//@ExperimentalUnsignedTypes
//fun main(args: Array<String>) {
//    if (args.isEmpty()) {
//        println("Filename required")
//        exitProcess(1)
//    }
//    val filename = args[0]
//    val k = if (args.size > 1) args[1].toInt() else 10
//
//    val bis = Files.newInputStream(Paths.get(filename)).buffered()
//    val trie = Trie()
//    var current: Node = trie.root
//
//    val iterator = bis.iterator()
//    while (iterator.hasNext()) {
//        val ch = iterator.nextUByte()
//        if (ch < 26u) {
//            current = current.traverse(ch)
//            while (iterator.hasNext()) {
//                val chi = iterator.nextUByte()
//                if (chi < 26u) {
//                    current = current.traverse(chi)
//                } else {
//                    break
//                }
//            }
//            ++current.count
//            current = trie.root
//        }
//    }
//
//    Trie.allNodes.sortWith(Comparator { o1: Node, o2: Node -> o2.count.compareTo(o1.count) })
//    val finalList: List<Node> = Trie.allNodes.take(k)
//    for (node in finalList) {
//        println(node.word + "\t" + node.count)
//    }
//
//}
//
//@ExperimentalUnsignedTypes
//private fun ByteIterator.nextUByte(): UByte {
//    return ((nextByte().toUByte() or 32u) - 97u).toUByte()
//}
//
//class Trie {
//    val root = Node(null, -1)
//
//    companion object {
//        var allNodes: MutableList<Node> = ArrayList()
//    }
//}
//
//class Node(var parent: Node?, var index: Int) {
//    var nodes: Array<Node?>? = null
//    var count = 0
//
//    val word: String
//        get() = if (index >= 0) {
//            parent!!.word + (index + 97).toChar()
//        } else {
//            ""
//        }
//
//    @ExperimentalUnsignedTypes
//    fun traverse(b: UByte): Node {
//        val idx = b.toInt()
//        if (nodes == null) {
//            nodes = arrayOfNulls(26)
//            return Node(this, idx).also { nodes!![idx] = it }
//        }
//        return if (nodes!![idx] == null) Node(this, idx).also { nodes!![idx] = it } else nodes!![idx]!!
//    }
//
//    init {
//        Trie.allNodes.add(this)
//    }
//}
