package penta

import kotlin.collections.ArrayList

/**
 * This function provides a zip that does not clip after the end of the shortest sequence, instead an optional value is used to indicate that the value does not need to exist.
 * The length of the resulting sequence is limited to the __longest__ input sequence.
 */
fun <T> noClipZip(l: List<T>, r: List<T>): List<Pair<T?, T?>> {
    var ld = l.toList()
    var rd = r.toList()
    var left = ld.firstOrNull()
    var right = rd.firstOrNull()
    val newSeq: MutableList<Pair<T?, T?>> = ArrayList()
    while (left != null || right != null) {
        newSeq.add(Pair(left,right))
        rd = rd.drop(1)
        ld = ld.drop(1)
        left = ld.firstOrNull()
        right = rd.firstOrNull()
    }
    return newSeq
}