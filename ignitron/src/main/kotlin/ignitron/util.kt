package ignitron

import kotlin.streams.toList


fun <T>listMatches(left: List<T>, right: List<T>): List<Pair<T,T>> {
    return left.map { elem ->
        right.toList().map { other ->
            Pair(elem, other)
        }
    }.flatten()
}