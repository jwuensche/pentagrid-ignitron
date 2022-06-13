package penta

import com.github.javaparser.ast.Node

/**
 * Representation of an classification result, they can be either Full Equality, Partial Full Equality, Partial Equality or Unequal
 * They are combinable and displayable.
 */
enum class Classification {
    // PartialFullEquality is almost always bound to context, most of the time functions will return a pure Full Equality or Partial Equality or None
    FullEquality, PartialFullEquality, PartialEquality, Unequal;
    
    // This function is intended to be called when two subtrees are compared who have the same parent, for the parent the result should be equal to what result is emitted here
    fun combine(other: Classification): Classification {
        return when(this) {
            FullEquality -> when(other) {
                FullEquality -> FullEquality
                // This should not be combined therefore we return Id here so a faulty result will be carried on, without tainting the following results
                PartialFullEquality -> PartialFullEquality
                else -> PartialEquality
            }
            PartialFullEquality -> PartialFullEquality
            PartialEquality -> when (other) {
                PartialFullEquality -> PartialFullEquality
                else -> PartialEquality
            }
            Unequal -> when (other) {
                Unequal -> Unequal
                PartialFullEquality -> PartialFullEquality
                else -> PartialEquality
            }
        }
    }

    override fun toString(): String {
        return when(this) {
            FullEquality -> "Full Equality"
            PartialFullEquality -> "Partial Full Equality"
            PartialEquality -> "Partial Equality"
            Unequal -> "Unequal"
        }
    }
}

/**
 * Result type containing more detail than the pure classification, with the matching nodes and the amount of nodes which could not be matched.
 * 
 * The not matched nodes are taken from the left as the reference tree.
 */
class ComparisonResult(val matching_nodes: Long, val left_nodes: Long, val right_nodes: Long, val classification: Classification)

/**
 * Interface for all Comparators, this tries to make them interchangable.
 */
interface Comparator {
    fun compare(l: Node, r: Node): ComparisonResult
}
