package penta

import com.github.javaparser.JavaParser
import com.github.javaparser.Problem
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.utils.Log
import com.github.javaparser.utils.SourceRoot
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import java.nio.file.Path


/**
 * Extracts all MethodBlocks from a singular file.
 * In case of an Error a list of Problems will be returned containing, all problems.
 */
fun extractBlocksFile(path: String): Result<List<MethodBlock>, List<Problem>> {
    val userPath = Path.of(path)
    val parser = JavaParser()
    val parseResult = parser.parse(userPath)
    return if (parseResult.isSuccessful) {
        Ok(getTestBlocks(parseResult.result.get()))
    } else {
        Err(parseResult.problems)
    }
}

/**
 * Extracts all `MethodBlock`s from java files found under the given [path], only `src` and below are applicable
 * , if you want to compare to another directory which is above `src` have a look at java `FileVisitor`.
 *
 * In case of an Error a list of Problems will be returned containing, all problems.
 */
fun extractBlocksDir(path: String): Result<List<MethodBlock>, List<Problem>> {
    val toUserPath = Path.of(path)
    val sourceRoot = SourceRoot(toUserPath)
    val readFiles = sourceRoot.tryToParseParallelized()

    if (readFiles.filter { elem -> !elem.isSuccessful }.isNotEmpty()) {
        return Err(readFiles.flatMap { elem -> elem.problems })
    }

    return Ok(readFiles
            .filter { result -> result.isSuccessful }
            .map { result -> result.result.get() }
            .flatMap { unit -> getTestBlocks(unit) })
}

/**
 * Internal methods to extract methods with an @Test annotation out of a CompilationUnit
 */
private fun getTestBlocks(unit: CompilationUnit): List<MethodBlock> {
    val checks = ExtractMethod()
    val methodBlocks = ArrayList<MethodBlock>()
    checks.visit(unit, Pair(unit, methodBlocks))
    return methodBlocks
}
