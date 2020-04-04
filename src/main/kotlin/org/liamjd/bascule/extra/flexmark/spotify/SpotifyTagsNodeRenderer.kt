package org.liamjd.bascule.flexmark.spotify

import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRenderer
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRendererFactory
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler
import com.vladsch.flexmark.util.data.DataHolder

class SpotifyTagsNodeRenderer(options: DataHolder) : NodeRenderer {

	val opts = options

	override fun getNodeRenderingHandlers(): MutableSet<NodeRenderingHandler<*>> {
		val self = this
		val set = HashSet<NodeRenderingHandler<*>>()

		set.add(
			NodeRenderingHandler(
				SpotifyLink::class.java,
				NodeRenderingHandler.CustomNodeRenderer { node, context, html ->
					self.renderExtendedEmbedLink(
						node,
						context,
						html
					)
				})
		)

		return set
	}


	class Factory : NodeRendererFactory {
		override fun apply(options: DataHolder): NodeRenderer {
			return SpotifyTagsNodeRenderer(options)
		}
	}

	private fun renderExtendedEmbedLink(node: SpotifyLink, context: NodeRendererContext, html: HtmlWriter) {

		val track = if (node.url.startsWith("spotify:track:")) {
			node.url.removePrefix("spotify:track:")
		} else {
			node.url
		}

		val templateString = readFileFromResources("/org/liamjd/bascule/extra/flexmark/spotify/", "spotify.html")
		val regexes =
			mapOf(Regex("\\[title\\]") to node.title, Regex("\\[track\\]") to track, Regex("\\[text\\]") to node.text)
		var processedString = templateString
		for (r in regexes) {
			processedString = processedString.replace(r.key, r.value.toString())
		}

		html.line()
		html.indent().append(processedString)
		html.line()
	}

	private fun readFileFromResources(sourceDir: String, fileName: String): String {
		return javaClass.getResource(sourceDir + fileName).readText()
	}

}
