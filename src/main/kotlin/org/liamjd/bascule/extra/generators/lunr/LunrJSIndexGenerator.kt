package org.liamjd.bascule.extra.generators.lunr

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import org.jsoup.Jsoup
import org.liamjd.bascule.lib.FileHandler
import org.liamjd.bascule.lib.generators.GeneratorPipeline
import org.liamjd.bascule.lib.model.Post
import org.liamjd.bascule.lib.model.Project
import org.liamjd.bascule.lib.render.TemplatePageRenderer
import java.time.LocalDate

@Serializable
data class LunrPost(
	val id: String,
	val title: String,
	@Serializable(with = LocalDateSerializer::class) val date: LocalDate,
	val body: String,
	val headings: String? = null,
	@Transient
	val codeBlocks: List<String>? = null
)

class LunrJSIndexGenerator(val posts: List<Post>) : GeneratorPipeline {
	override val TEMPLATE: String
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

	private val LUNR_INDEX_JSON = "/lunrindex.json"

	@UnstableDefault
	override suspend fun process(
		project: Project,
		renderer: TemplatePageRenderer,
		fileHandler: FileHandler,
		clean: Boolean
	) {
		val outputFilename = project.dirs.output.absolutePath + LUNR_INDEX_JSON
		val json = Json(JsonConfiguration(prettyPrint = true))
		val lunrPosts = mutableListOf<LunrPost>()

		if (!clean) {
			val lunrString = fileHandler.readFileAsString(outputFilename)
			val existingLunrPosts = json.parse(LunrPost.serializer().list, lunrString)
			lunrPosts.addAll(existingLunrPosts)
			posts.forEach { p ->
				val foundExisting = lunrPosts.find { lunrPost: LunrPost -> lunrPost.id.equals(p.getLunrId()) }
				if (foundExisting == null) {
					lunrPosts.add(p.extractDocument())
				}
			}

		} else {
			// posts should be enough for us to construct all the LunrPosts we need
			posts.forEach { p ->
				lunrPosts.add(p.extractDocument())
			}

		}
		val lunrJson = json.stringify(LunrPost.serializer().list, lunrPosts)
		fileHandler.writeFile(project.dirs.output, LUNR_INDEX_JSON, lunrJson)
	}
}

fun Post.extractDocument(): LunrPost {
	val jsoupDoc = Jsoup.parse(this.content)
	// get the codeblocks first
	val codeBlockList = mutableListOf<String>()
	jsoupDoc.select("code")?.forEach {
		codeBlockList.add(it.text())
	}
	// then remove them from the document
	jsoupDoc.select("code").remove()

	// then get the body from what remains, and the headings
	val id = this.getLunrId()
	val title = this.title
	val body = jsoupDoc.body()
	val headingList = mutableListOf<String>()
	jsoupDoc.select("h2, h3, h4")?.forEach {
		headingList.add(it.text())
	}

	return LunrPost(
		id = id,
		title = title,
		body = body.text().replace("\"", ""),
		date = this.date,
		headings = headingList.joinToString(),
		codeBlocks = codeBlockList.toList()
	)
}

fun Post.getLunrId(): String = this.url.removePrefix('/'.toString())
