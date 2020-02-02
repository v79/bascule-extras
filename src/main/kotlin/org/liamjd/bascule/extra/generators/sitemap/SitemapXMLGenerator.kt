package org.liamjd.bascule.extra.generators.sitemap

import org.liamjd.bascule.lib.FileHandler
import org.liamjd.bascule.lib.generators.GeneratorPipeline
import org.liamjd.bascule.lib.model.Post
import org.liamjd.bascule.lib.model.Project
import org.liamjd.bascule.lib.render.TemplatePageRenderer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SitemapXMLGenerator(val posts: List<Post>) : GeneratorPipeline {
    override val TEMPLATE: String
        get() = ""

    override suspend fun process(
        project: Project,
        renderer: TemplatePageRenderer,
        fileHandler: FileHandler,
        clean: Boolean
    ) {

        val outputFilename = "sitemap.xml"
        val model = mutableMapOf<String, Any>()
        model.putAll(project.model)
        model.put("posts", posts)

        println("Generating sitemap.xml")
        val xmlBuilder = StringBuilder()
        val host = if (project.configMap["host"] == null) {
            "http://localhost:8080/"
        } else {
            project.configMap["host"] as String
        }
        xmlBuilder.append(
            """<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
"""
        )
        posts.forEach { post ->
            xmlBuilder.appendln("\t<url>")
            xmlBuilder.append(loc(host, post))
            xmlBuilder.append(lastmod(post.date)) // TODO: what about the file last modified time?
            // TODO: changeFreg
            // TODO: priority
            xmlBuilder.appendln("\t</url>")
        }
        xmlBuilder.appendln("</urlset>")

        fileHandler.writeFile(project.dirs.output, outputFilename, xmlBuilder.toString())

    }

    private fun loc(host: String, post: Post): String {
        val locBuilder = StringBuilder()
        locBuilder.append("\t\t<loc>")
        locBuilder.append(host)
        locBuilder.append(post.url.trim())
        locBuilder.appendln("</loc>")
        return locBuilder.toString()
    }

    private fun lastmod(date: LocalDate): String {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val lastmodBUilder = StringBuilder()
        lastmodBUilder.append("\t\t<lastmod>")
        lastmodBUilder.append(formatter.format(date))
        lastmodBUilder.appendln("</lastmod>")
        return lastmodBUilder.toString()
    }
}
