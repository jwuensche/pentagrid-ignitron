package penta

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import kotlin.streams.asSequence

interface Transformer {
    /**
     * Function to transform the tree from one state to another.
     * The return value is a Node to be able to chain them together.
     */
    fun translate(block: Node): Node
}

class DeclarationVisitor : VoidVisitorAdapter<MutableList<Pair<SimpleName, SimpleName>>>() {
    @Override
    override fun visit(n: VariableDeclarator, args: MutableList<Pair<SimpleName, SimpleName>>) {
        args.add(Pair(n.name, SimpleName(n.type.asString())))
        n.name = SimpleName(n.type.asString())
    }
}

/**
 * Transforms all occurrences of declarations to make names non-unique, this can be used to find higher type clones.
 * The strategy applied here is that the we omit names and check the underlying type used, this is done by setting the name to the stringified Type name.
 * This rather crude technique leads to an acceptable result, because we check for the overlying structure of the code.
 */
object NameTransformer : Transformer {
    override fun translate(block: Node): Node {
        val nameList = ArrayList<Pair<SimpleName, SimpleName>>()
        block.accept(DeclarationVisitor(), nameList)
        block.walk(NameExpr().javaClass) { node: NameExpr -> nameList.filter { elem -> elem.first == node.name }.forEach { name -> node.name = name.second }}
        // block.walk(VariableDeclarator().javaClass) { node: VariableDeclarator -> node.name = SimpleName(node.type.asString()) }
        return block
    }
}

/**
 * Transforms the comments property of nodes, this Transformer simply removes all existing comments, for this all direct and orphaned comments are counted.
 * The resulting tree will be free of comments of any kind.
 */
object CommentTransformer : Transformer {
    override fun translate(block: Node): Node {
        val newNodePair = block.stream().map { node ->
            val cleanNode = node.removeComment()
            node.orphanComments.forEach {com ->
                cleanNode.removeOrphanComment(com)
            }
            Pair(node, cleanNode)
        }.asSequence()

        newNodePair.forEach { pair ->
            block.replace(pair.first, pair.second)
        }
        return block
    }
}

object LiteralsTransformer : Transformer {
    override fun translate(block: Node): Node {
        block.walk(CharLiteralExpr().javaClass) {node ->
                node.setChar('a')
        }
        block.walk(StringLiteralExpr().javaClass) { node ->
            node.setString("generalized")
        }
        block.walk(DoubleLiteralExpr().javaClass) { node ->
            node.setDouble(0.0)
        }
        block.walk(LongLiteralExpr().javaClass) { node ->
            node.value = "0L"
        }
        block.walk(IntegerLiteralExpr().javaClass) { node ->
            node.value = "0"
        }
        block.walk(BooleanLiteralExpr().javaClass) { node ->
            node.value = true
        }
        block.walk(TextBlockLiteralExpr().javaClass) { node ->
            node.value = "generalized block"
        }
        return block
    }
}