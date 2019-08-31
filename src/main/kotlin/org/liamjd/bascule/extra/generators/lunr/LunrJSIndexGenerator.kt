package org.liamjd.bascule.extra.generators.lunr

import org.jsoup.Jsoup
import org.liamjd.bascule.lib.FileHandler
import org.liamjd.bascule.lib.generators.GeneratorPipeline
import org.liamjd.bascule.lib.model.Post
import org.liamjd.bascule.lib.model.Project
import org.liamjd.bascule.lib.render.TemplatePageRenderer
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PostDocument(
    val body: String,
    val postDate: LocalDate,
    val headings: List<String>? = null,
    val codeBlocks: Array<String>? = null
)

class LunrJSIndexGenerator(val posts: List<Post>) : GeneratorPipeline {
    override val TEMPLATE: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override suspend fun process(project: Project, renderer: TemplatePageRenderer, fileHandler: FileHandler) {
        val outputFilename = project.dirs.output.absolutePath + "/lunrindex.json"
        File(outputFilename).delete()
        val stream = RandomAccessFile(outputFilename, "rw")
        val channel = stream.channel

        println("Writing lunrindex.json file for all posts")
        val start = "[\n".toByteArray()
        val end = "\n]".toByteArray()
        val startBuffer: ByteBuffer = ByteBuffer.allocate(start.size)
        val endBuffer: ByteBuffer = ByteBuffer.allocate(end.size)
        startBuffer.put(start).flip()
        endBuffer.put(end).flip()
        channel.write(startBuffer)
        posts.forEachIndexed { idx, post ->
            val json: String
            if (idx != posts.size - 1) {
                json = postToJson(post) + ","
            } else {
                json = postToJson(post)
            }
            val strBytes = json.toByteArray()
            val buffer = ByteBuffer.allocate(strBytes.size)
            buffer.put(strBytes)
            buffer.flip()
            channel.write(buffer)
        }
        channel.write(endBuffer)
        stream.close()
        channel.close()


    }

    fun postToJson(post: Post): String {
        val sb = StringBuilder()
        val postDoc = post.extractDocument()
        sb.appendln("{")
        sb.appendln("\t\"id\": \"${post.url.removePrefix('/'.toString())}\",")
        sb.appendln("\t\"title\": \"${post.title}\",")
        sb.appendln("\t\"date\": \"${formatDate(postDoc.postDate)}\",")
        sb.appendln("\t\"body\": \"${postDoc.body}\",")
        sb.appendln("\t\"headings\": \"${postDoc.headings?.let { convertListToString(postDoc.headings) }}\"")
        sb.append("}")
        return sb.toString()
    }

    fun convertListToString(list: List<String>): String {
        val headingsString = StringBuilder()
        if (list.isNotEmpty()) {
            list.forEachIndexed { idx, heading ->
                headingsString.append(heading)
                if (idx != list.size - 1) {
                    headingsString.append(",")
                }
            }
        }
        return headingsString.toString()
    }

    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return formatter.format(date)
    }
}

fun Post.extractDocument(): PostDocument {
    val jsoupDoc = Jsoup.parse(this.content)
    // get the codeblocks first
    val codeBlockList = mutableListOf<String>()
    jsoupDoc.select("code")?.forEach {
        codeBlockList.add(it.text())
    }
    // then remove them from the document
    jsoupDoc.select("code").remove()

    // then get the body from what remains, and the headings
    val body = jsoupDoc.body()
    val headingList = mutableListOf<String>()
    jsoupDoc.select("h2, h3, h4")?.forEach {
        headingList.add(it.text())
    }
    var headings: Array<String> = arrayOf()

    return PostDocument(body.text().replace("\"", ""), this.date, headingList, codeBlockList.toTypedArray())
}
