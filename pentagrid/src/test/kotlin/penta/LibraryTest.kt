/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package penta

import com.github.javaparser.ast.stmt.BlockStmt
import kotlin.test.Test
import kotlin.test.assertTrue
import com.github.michaelbull.result.expect

class LibraryTest {
    @Test fun smokeTest() {
        assertTrue(extractBlocksDir("src/test/resources/smoke").component1()?.size ?: 0 > 0)
    }

    @Test fun compareSame() {
        val methods = extractBlocksDir("src/test/resources/equal").expect { "Reading of code has failed" }
        assert(TopDownCompare.compare(methods.get(0).block_content, methods.get(1).block_content).classification == Classification.FullEquality)
    }

    @Test fun compareOnHashBasis() {
        val methods = extractBlocksDir("src/test/resources/equal").expect { "Reading of code has failed" }

        val result = methods.get(0).hashCompare(methods.get(1))
        assert(result.commonSubTrees.get(0).first == methods.get(0).block_content && result.commonSubTrees.get(0).second == methods.get(1).block_content)
    }

    @Test fun sanityChechHashCompare() {
        val methods = extractBlocksDir("src/test/resources/equal").expect { "Could not parse test sources, please check" }
        
        val result = methods.get(3).hashCompare(methods.get(4))
        assert(result.commonSubTrees.size == 1)
    }
    
    @Test fun compareOnHashSimilar() {
        val methods = extractBlocksDir("src/test/resources/equal").expect { "Reading of code has failed" }

        val result = methods.get(1).hashCompare(methods.get(2))
        assert(result.commonSubTrees.get(0).first == (methods.get(1).block_content as BlockStmt).statements.get(0) && result.commonSubTrees.get(0).second == (methods.get(2).block_content as BlockStmt).statements.get(0))
        assert(result.commonSubTrees.get(1).first == (methods.get(1).block_content as BlockStmt).statements.get(1) && result.commonSubTrees.get(1).second == (methods.get(2).block_content as BlockStmt).statements.get(2))
    }

    @Test fun compareWithCommentsExpectFail() {
        val methods = extractBlocksDir("src/test/resources/equal_with_comments").expect { "Reading of code has failed" }
        assert(TopDownCompare.compare(methods.get(0).block_content, methods.get(1).block_content).classification != Classification.FullEquality)
    }

    @Test fun compareWithComments() {
        val methods = extractBlocksDir("src/test/resources/equal_with_comments").expect { "Reading of code has failed" }
        val processedMethods = methods.map { elem -> elem.preprocess(CommentTransformer) }
        assert(TopDownCompare.compare(processedMethods.get(0).block_content, processedMethods.get(1).block_content).classification == Classification.FullEquality)
    }

    @Test fun compareShouldNotFailWhenBothProcessed() {
        val methods = extractBlocksDir("src/test/resources/equal_with_comments").expect { "Reading of code has failed" }
        val processedMethods = methods.map { elem -> elem.preprocess(CommentTransformer) }
        assert(TopDownCompare.compare(processedMethods.get(0).block_content, processedMethods.get(2).block_content).classification == Classification.FullEquality)
    }

    @Test fun differentVariableNames() {
        val methods = extractBlocksFile("src/test/resources/NodeListTest.java").expect { "Reading of code has failed" }
        val processedMethods = methods.map { elem -> elem.preprocess(NameTransformer) }
        assert(TopDownCompare.compare(processedMethods.get(3).block_content, processedMethods.get(4).block_content).classification == Classification.FullEquality)
    }

    @Test fun sameVariableNames() {
        val methods = extractBlocksFile("src/test/resources/NodeListTest.java").expect { "Reading of code has failed" }
        val processedMethods = methods.map { elem -> elem.preprocess(NameTransformer) }
        assert(processedMethods.get(3).compare(processedMethods.get(5)).classification == Classification.FullEquality)
    }
    
    @Test fun classLengthHashTest() {
        /*
        Essentially it's important that the same node receives the same hash code determenistic and that two nodes which are equal receive the same hash code
         */
        val methods = extractBlocksFile("src/test/resources/equal_with_comments/test.java").expect { "Reading of code has failed" }
        val hashes = methods.map {elem -> ClassLengthHash.hashNode(elem.block_content)}
        assert(hashes.toSet().size == 1)
    }
    
    /*
    This itself is a clone of the method above for one difference.
    Bear in mind due to the comments being registered as nodes this requires at least one preprocessing step to result in the equal.
     */
    @Test fun classAndChildHash() {
        val methods = extractBlocksFile("src/test/resources/equal_with_comments/test.java").expect { "Reading of code has failed" }
        val hashes = methods.map {m -> m.preprocess(CommentTransformer).preprocess(NameTransformer)}.map { elem ->
            ClassAndChildHash.hashNode(elem.block_content)
        }
        assert(hashes.toSet().size == 1)
    }

    @Test fun bottomUpSmoke() {
        val methods = extractBlocksDir("src/test/resources/equal").expect { "Reading of code has failed" }
        val foo = methods.get(0).compare(methods.get(1), BottomUpCompare).classification
        assert(methods.get(0).compare(methods.get(1), BottomUpCompare).classification == Classification.FullEquality)
    }
}
