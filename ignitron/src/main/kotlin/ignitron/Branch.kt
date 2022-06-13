package ignitron

import com.github.michaelbull.result.expect
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import penta.MethodBlock
import penta.extractBlocksDir
import penta.extractBlocksFile
import java.io.File
import java.nio.file.Paths

typealias FilePath = String

fun relevantParentDir(path: FilePath): File {
    var file = File(path)
    while (file.parent != null) {
        if (file.parentFile.name == "test") {
            break
        }
        file = file.parentFile
    }
    return file
}

class Branch(val name: String, val git: Git){
    // We're always executing from the $PWD so no worries here
    private var testDirs: List<File>
    private val branchHeadIterator: AbstractTreeIterator

    init {
        // get current state and local git repository
        // Take the parent here as the `.git` directory is the original one here
        val list = File(git.repository.directory.parent).walk().toList()
        // list.forEach { elem -> println(elem.path) }
        testDirs = list.filter { file ->
            file.isDirectory && file.name == "test"
        }

        val walk = RevWalk(git.repository)
        val commit = walk.parseCommit(git.repository.resolve(name))
        val tree = walk.parseTree(commit.tree.id)

        val treeParser = CanonicalTreeParser()
        treeParser.reset(git.repository.newObjectReader(), tree.id)
        walk.dispose()
        branchHeadIterator = treeParser
    }

    fun parseAllTestCode(): List<MethodBlock> {
        // Ensure that the command is run on the correct branch
        git.checkout().setName(name).call()
        return testDirs.flatMap { dir ->
            extractBlocksDir(dir.path).expect { "Invalid directory $dir while parsing independent branch. This might be because the git is in an unexpected state (should be in $name)" }
        }
    }
    
    fun parseFile(path: String): List<MethodBlock> {
        // Ensure that the command is run on the correct branch
        git.checkout().setName(name).call()
        return extractBlocksFile(path).expect { "Error while reading source code, please check the file under $path" }
    }
    
    fun parseFiles(paths: List<String>): Map<FilePath, List<MethodBlock>>  {
        // Ensure that the command is run on the correct branch
        git.checkout().setName(name).call()
        return paths.map { path ->
            Pair(relevantParentDir(path).path, extractBlocksFile(path).expect { "Error while reading source code, please check the under $path" })
        }.fold(HashMap()) { acc, elem ->
            val exist = acc.getOrDefault(elem.first, listOf())
            acc.put(elem.first, exist + elem.second)
            acc
        }
    }

    fun parseDirs(paths: List<String>): Map<FilePath, List<MethodBlock>> {
        // Ensure that the command is run on the correct branch
        git.checkout().setName(name).call()
        return paths.map { path ->
            Pair(path,extractBlocksDir(path).expect { "Error while reading source code, please check the under $path" })
        }.toMap()
    }

    fun relevantTestDirectories(paths: List<String>): List<String> {
        return paths.map { elem ->
            // Retrieve parents
            relevantParentDir(elem)
        }.filter { elem ->
            // Make sure the path lies in test directory
            elem.parent != null && elem.parentFile.name == "test" && (elem.name == "java" || elem.name != "resources")
        }.map { elem ->
            elem.path
        }
    }
    
    fun diffsTo(other: Branch): List<DiffEntry> {
        return git.diff().setOldTree(branchHeadIterator).setNewTree(other.branchHeadIterator).call()
    }
}