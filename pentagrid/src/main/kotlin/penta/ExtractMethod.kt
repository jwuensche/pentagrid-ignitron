package penta

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.ast.body.MethodDeclaration

/**
 * Visitor to find methods with Test annotations, and add them to an argument list of MethodBlocks
 */
internal class ExtractMethod : VoidVisitorAdapter<Pair<CompilationUnit,MutableList<MethodBlock>>>() {
    @Override
    override fun visit(n: MethodDeclaration, args: Pair<CompilationUnit, MutableList<MethodBlock>>) {
        var fqn = n.name.asString()
        var node: Node = n
        while (node.parentNode.isPresent) {
            node = node.parentNode.get()
            if (node is ClassOrInterfaceDeclaration) {
                fqn = node.nameAsString + "." + fqn
            } else if (node is CompilationUnit) {
                if (node.packageDeclaration.isPresent) {
                    fqn = node.packageDeclaration.get().nameAsString + "." + fqn
                }
            }
        }
        if (n.body.isPresent) {
            val block = n.body.get().clone()
            // IMPORTANT: Here we remove the rest of the tree above the node
            block.remove()
            args.second.add(MethodBlock(fqn, n, block, args.first, n.annotations))
        }
    }
}
