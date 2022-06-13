package penta

import com.github.javaparser.ast.Node
import kotlin.streams.asSequence

class HashedNode(val hashValue: Int, val node: Node, val parentTree: Node?)

/**
 * Interface for hashing a node in a non-unique and collision heavy way.
 */
interface BadHash {
    fun hashNode(node: Node): Int
}

/**
 * Take the length of the type name of a node. This is a very arbitrary way to hash them, but it could prove bad enough.
 */
object ClassLengthHash: BadHash {
    override fun hashNode(node: Node): Int {
        // println(node.javaClass.toString())
        node.childNodes.forEach {
            elem -> println(elem.javaClass.toString())
        }
        return node.childNodes.fold(node.javaClass.toString().length, { acc, elem -> return acc + ClassLengthHash.hashNode(elem)})
    }
}

object DefaultHash: BadHash {
    override fun hashNode(node: Node): Int {
        return node.hashCode()
    }
}

/**
 * A more detailed hash, this calculation takes into consideration how many children a node does have.
 */
// Do we need this recursive, or might actually a none recursive approach be better? This one we should experiment with.
// And what other things can we use for hashing? Another idea is using the characteristics of a node, but there aren't too many somewhat identifying there except for the structure of the tree.
object ClassAndChildHash: BadHash {
    override fun hashNode(node: Node): Int {
        // println("%s, %d".format(node.javaClass.toString(), node.childNodes.size))
        return node.childNodes.fold(node.javaClass.toString().length + node.childNodes.size, { acc, elem -> return acc + hashNode(elem)})

    }
}

/**
  * A variation of the approach suggested by Ira Baxter in the 1998 paper about cloning detection. This proposes to exclude these lower leave nodes.
 * We'll experiment with an variation of this to find better (or worse in this case) matches to put into buckets.
  */
object BaxterHash: BadHash {
    override fun hashNode(node: Node): Int {
        if (node.childNodes.count() < 1) {
            return 0
        }
        return node.stream().filter {n ->
            n.childNodes.count() > 0
        }.asSequence().fold(0) { acc: Int, elem: Node ->
            acc + elem.javaClass.toString().length
        }
    }
}
