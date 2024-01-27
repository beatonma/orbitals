import org.gradle.api.Project
import java.io.File

data class GitData(val sha: String, val tag: String, val commitCount: Int, val branch: String)
object Git {
    fun resolveData(project: Project) = GitData(
        sha = sha(project),
        tag = tag(project),
        commitCount = commitCount(project),
        branch = branch(project)
    )

    private fun sha(project: Project): String {
        // query git for the SHA, Tag and commit count. Use these to automate versioning.
        return "git rev-parse --short HEAD".execute(project.rootDir, "none")
    }

    private fun tag(project: Project): String {
        return "git describe --tags".execute(project.rootDir, "dev")
    }

    private fun commitCount(project: Project): Int {
        return "git rev-list --count HEAD".execute(project.rootDir, "0").toInt()
    }

    private fun branch(project: Project): String {
        return "git rev-parse --abbrev-ref HEAD".execute(project.rootDir, "unknown-branch")
    }
}

private fun String?.letIfEmpty(fallback: String): String = when {
    isNullOrEmpty() -> fallback
    else -> this
}

private fun String?.execute(workingDir: File, fallback: String): String {
    Runtime.getRuntime().exec(this, null, workingDir).let { process ->
        process.waitFor()
        return try {
            process.inputStream.reader().readText().trim().letIfEmpty(fallback)
        } catch (e: Exception) {
            fallback
        }
    }
}
