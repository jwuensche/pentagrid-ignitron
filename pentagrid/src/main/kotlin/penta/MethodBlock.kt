package penta

import com.codepoetics.protonpack.StreamUtils
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.expr.AnnotationExpr
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.Statement
import java.lang.reflect.Method
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

class NodePair(val original: Original, val generalized: Generalized)

typealias Original = Node
typealias Generalized = Node

fun neighboringNodes(left: Node, right: Node): Boolean {
    if (left.parentNode.isPresent && right.parentNode.isPresent && left.parentNode.get() == right.parentNode.get()) {
        val parent = left.parentNode.get()
        return parent.childNodes.indexOf(left) - parent.childNodes.indexOf(right) == -1
    }
    return false
}

/**
 * HashResult is returned as the result of a comparison of two MethodBlocks, each of which carries a reference to the block itself and to the node in the CompilationUnit tree.
 * The HashResult contains all pssobile maximum subtree matches which we have found, with a node detached from the rest of the tree, if modification or other altering operations are necessary.
 */
class HashResult(val first: MethodBlock, val second: MethodBlock, val commonSubTrees: List<Pair<Original, Original>>) {
    /**
     * This function searches for direct sequences of statements, we want statements here
     * since these are the sequences we may potentially extract out of the functiono.
     *
     * Base Nodes are on the left side and Changes Nodes on the right
     *
     * For further references, read the accompanying paper.
     */
    fun directSequences(): List<Pair<Sequence, Sequence>> {
        val expressions = this.commonSubTrees.filter { node ->
            node.first is Statement
        }

        if (expressions.size <= 1) {
            return listOf()
        }

        return expressions.fold(listOf()) {acc: List<Pair<Sequence, Sequence>>, nodePair: Pair<Original, Original> ->
            val updatedSequences = acc.flatMap { previousSequence ->
                val lastNodeBase = previousSequence.first.last()
                val lastNodeChanges = previousSequence.second.last()

                val newSequence: Pair<Sequence, Sequence>
                if (neighboringNodes(lastNodeBase, nodePair.first) && neighboringNodes(lastNodeChanges, nodePair.second)) {
                    newSequence = Pair(previousSequence.first + nodePair.first, previousSequence.second + nodePair.second)
                } else {
                    newSequence = previousSequence
                }
                listOf(previousSequence, newSequence)
            }

            val newSequence: Pair<Sequence, Sequence> = Pair(listOf(nodePair.first), listOf(nodePair.second))

            updatedSequences + newSequence
        }.filter { elem ->
            elem.first.size >= 2 && elem.second.size >= 2
        }
    }
}

fun <T> checkSubList(left: List<T>, right: List<T>): Boolean {
    for (idx in 0 until right.size - left.size) {
        if (right.subList(idx, idx + left.size) == left) {
            return true
        }
    }
    return false
}

/**
 * A MethodBlock contains the original tree of a method, the method_name, the file which it is occurring.
 *
 * They can be compared via the `Comparator` interface.
 */
class MethodBlock(val method_name: String, val original: Node, val block_content: Node, val occurring_file: CompilationUnit, val annotations: NodeList<AnnotationExpr>) {
    fun preprocess(transformer: Transformer): MethodBlock {
        return MethodBlock(this.method_name, this.original, transformer.translate(block_content), this.occurring_file, this.annotations)
    }

    fun compare(other: MethodBlock, comparator: Comparator = TopDownCompare): ComparisonResult {
        return comparator.compare(this.block_content, other.block_content)
    }

    fun hashCompare(other: MethodBlock): HashResult {
        // We could improve this by hashing only one of them and check subsequences of the other for now this is alright
        val fst = this.subHashes()
        val snd = other.subHashes().toList()

        val commonHashes = fst
            .flatMap { elem -> snd.stream().map { other -> Pair(elem, other) } }
            .filter { (left, right) ->
            // this leads to exact matches, for fuzzy matches this is not applicable though we have evidence of lower matches still remaining, if this is a large portion of the tree we see that partially they can be abstracted to include less clones
            // but this will not work in all cases
            val list: List<Int> = left.second.second
            val otherList: List<Int> = right.second.second
            // left.second.first == right.second.first
            // checkSubList(list, otherList)
            // Ensure they are of the same class
            list == otherList && left.second.first.javaClass == right.second.first.javaClass
        }
        val secure = commonHashes.toList()
        val topmost = secure.filter { (left, right) ->
            // Reference ourselves as the first element in our hashlist
            val own = left.second.second.first()
            secure.filter { (l,r) -> l.second.second.stream().skip(1).toList().contains(own) }.count() < 1
        }

        return HashResult(
                first = this,
                second = other,
                commonSubTrees = topmost.map { (left, right) ->
                    Pair((this.original as MethodDeclaration).body.get().stream().toList().get(left.first.toInt()),
                        (other.original as MethodDeclaration).body.get().stream().toList().get(right.first.toInt())
                    )
                }.filter { (left, right)  ->
                    left.javaClass == right.javaClass
                }
        )
    }

    fun hashTree(hasher: BadHash = ClassAndChildHash): List<HashedNode> {
        return block_content.stream().map { elem ->
            // Try to remove this node from the parent so the comparison from bottom up may work
            val removedElem = elem.clone()
            removedElem.remove()
            HashedNode(hasher.hashNode(removedElem), removedElem, block_content)
        }.toList()
    }
    
    // Return a list of all hashes contained in this block, pre order traversal (so as to match sequences as sub trees)
    fun hashList(): List<Int> {
        return block_content.stream().map { elem ->
            DefaultHash.hashNode(elem)
        }.toList()
    }

    // Return a list of all nodes of this tree paired with their hashList, pre order traversal both
    private fun subHashes(): Stream<Pair<Long, Pair<Generalized, List<Int>>>> {
        return StreamUtils.zipWithIndex(block_content.stream()).parallel().map { elem ->
            Pair(
                elem.index,
                Pair(
                    elem.value,
                    elem.value.stream()
                        .map { node -> DefaultHash.hashNode(node) }
                        .toList()
                )
            )
        }
    }
}
