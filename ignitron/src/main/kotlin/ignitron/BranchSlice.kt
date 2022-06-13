package ignitron

import com.github.javaparser.ast.CompilationUnit
import penta.HashedNode
import penta.MethodBlock

class BranchSlice(val branch: Branch, blocks: List<MethodBlock>) {
    val fileGroupedBlocks: Map<String, List<MethodBlock>> = blocks.groupBy { block -> block.occurring_file.storage.get().fileName }

    fun hashSliceTrees(): List<HashedNode> {
        return fileGroupedBlocks.toList().flatMap { pair -> pair.second }.flatMap { block ->
            // Generate all subtrees hashes
            // We then can check which one is most high up
            block.hashTree()
        }
    }
}