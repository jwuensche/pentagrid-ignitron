package penta

import com.github.javaparser.ast.Node
import kotlin.streams.toList

enum class Comp {
    Match,
    LeftMiss,
    RightMiss,
    None
}

/**
 * Simple bottom up comparison for two nodes, this runs at the assumption that we give two leaves to this function though we may change this to
 * the root nodes as in the other comparison for a better coherence between the interface objects.
 *
 * This comparison also requires a bit more work in the way that the returning result could be a vector of results as multiple could be returned.
 * Question is I would like this to be relatively equal to the TopDownCompare, for this we need a singular result, we can get the maximum of each matched, but this can get skewed.
 */
object BottomUpCompare: Comparator {
    // This is the top level comparison begin on the root node, we may continue to the leaves to make this a true bottom level compare
    // This approach is comparable to normal type-1 and type-2 clones
    override fun compare(l: Node, r: Node): ComparisonResult {
        val leftLeaves = l.stream().filter { node -> node.childNodes.size == 0 }.toList()
        val rightLeaves = r.stream().filter { node -> node.childNodes.size == 0 }.toList()

        return bottomCompare(leftLeaves, rightLeaves)
    }

    private fun bottomCompare(l: List<Node>, r: List<Node>): ComparisonResult {
        if (l.firstOrNull() == null) {
            // subsumed by right, the left tree is inferior and included in the right tree
            return ComparisonResult(0, 0,0, Classification.FullEquality)
        }
        if (r.firstOrNull() == null) {
            // subsumed by left, the right tree is inferior and included in the left tree
            return ComparisonResult(0, 0,0, Classification.Unequal)
        }
        val zipped = noClipZip(l, r)
        // TODO: Beautify this, at the moment this is needlessly complex
        val result = zipped.map { (left, right) -> 
            if (left != null) {
                if (right != null) {
                    if (left == right) {
                        Comp.Match
                    } else {
                        Comp.None
                    }
                } else {
                    Comp.RightMiss
                }
            } else {
                Comp.LeftMiss
            }
        }
        result.forEach { elem ->
            when(elem) {
                Comp.RightMiss, Comp.None -> return ComparisonResult(0, 0, 0, Classification.Unequal)
                else -> {}
            }
        }
        return bottomCompare(l.map { elem -> elem.parentNode }.filter { elem -> elem.isPresent }.map { elem -> elem.get() }, r.map { elem -> elem.parentNode }.filter { elem -> elem.isPresent }.map { elem -> elem.get() })
    }
}