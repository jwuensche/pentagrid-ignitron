package penta

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.stmt.BlockStmt
import javassist.bytecode.analysis.ControlFlow

object TopDownCompare: Comparator {
    /**
     * Implements a top down comparison on two roots.
     * The result will contain the amount of matched nodes and the amount of nodes not matched of the first
     * node given.
     */
    override fun compare(l: Node, r: Node): ComparisonResult {
        if (l != r) {
            // Here we resolve the complete stream of nodes to count them, we may want to disregard that, in order to optimize the runtime
            return ComparisonResult(0, l.stream().count(), r.stream().count(), Classification.Unequal)
        }
        val children = noClipZip(l.childNodes, r.childNodes)
        return children
                .map { (fst: Node?, snd: Node?) ->
                    if (fst != null && snd != null) {
                        compare(fst, snd)
                    } else if (fst != null) {
                        ComparisonResult(0, fst.stream().count(), 0, Classification.Unequal)
                    } else if (snd != null) {
                        ComparisonResult(0, 0, snd.stream().count(), Classification.Unequal)
                    } else {
                        ComparisonResult(0, 0, 0, Classification.Unequal)
                    }
                }
                .fold(
                        ComparisonResult(1, 1, 1, Classification.FullEquality),
                        { acc: ComparisonResult, elem: ComparisonResult ->
                            ComparisonResult(acc.matching_nodes + elem.matching_nodes, acc.left_nodes + elem.left_nodes, acc.right_nodes + elem.right_nodes, acc.classification.combine(elem.classification))
                        }
                )
    }
}

/**
 * We need another comparison here.
 * Problem is that the default comparison does a deep check on all lower nodes which might be fine
 */

fun nodeCompare(other: BlockStmt, arg: BlockStmt): Boolean {
    if (other.comment != arg.comment) {
        return false
    }
    return true
}