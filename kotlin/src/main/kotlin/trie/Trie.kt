package trie

import java.nio.file.Paths
import kotlin.system.exitProcess

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    val start = System.currentTimeMillis()
    if (args.isEmpty()) {
        println("Filename required")
        exitProcess(1)
    }
    val filename = args[0]
    val k = if (args.size > 1) args[1].toInt() else 10

    val data: UByteArray = Paths.get(filename).toFile().readBytes().asUByteArray()
    for (index in data.indices) {
        val b = data[index]
        data[index] = ((b or 32u) - 97u).toUByte()
    }

    val trie = Trie()
    var current: Node = trie.root

    val iterator = data.iterator()
    while (iterator.hasNext()) {
        val ch = iterator.next()
        if (ch < 26u) {
            current = current.traverse(ch)
            while (iterator.hasNext()) {
                val chi = iterator.next()
                if (chi < 26u) {
                    current = current.traverse(chi)
                } else {
                    break
                }
            }
            ++current.count
            current = trie.root
        }
    }

    Trie.allNodes.sortByDescending { it.count }
    val finalList: List<Node> = Trie.allNodes.take(k)
    for (node in finalList) {
        println(node.word + "\t" + node.count)
    }

    System.err.println("Time taken: ${System.currentTimeMillis() - start}ms")

}

class Trie {
    val root = Node(null, -1)

    companion object {
        val allNodes: MutableList<Node> = ArrayList()
    }
}

class Node(private val parent: Node?, private val index: Int) {
    var nodes: Array<Node?>? = null
    var count = 0

    init {
        Trie.allNodes.add(this)
    }

    val word: String
        get() = if (parent != null) {
            parent.word + (index + 97).toChar()
        } else {
            ""
        }

    @ExperimentalUnsignedTypes
    fun traverse(b: UByte): Node {
        val idx = b.toInt()

        nodes?.let { myNodes ->
            return myNodes[idx] ?: Node(this, idx).also { myNodes[idx] = it }
        }

        nodes = Array(26) { null }
        return Node(this, idx).also { nodes!![idx] = it }
    }
}
