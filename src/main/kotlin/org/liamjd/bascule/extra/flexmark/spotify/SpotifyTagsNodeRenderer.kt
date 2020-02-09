package org.liamjd.bascule.flexmark.spotify

import com.vladsch.flexmark.html.CustomNodeRenderer
import com.vladsch.flexmark.html.HtmlWriter
import com.vladsch.flexmark.html.renderer.NodeRenderer
import com.vladsch.flexmark.html.renderer.NodeRendererContext
import com.vladsch.flexmark.html.renderer.NodeRendererFactory
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler
import com.vladsch.flexmark.util.options.DataHolder

class SpotifyTagsNodeRenderer(options: DataHolder) : NodeRenderer {

	val opts = options

	override fun getNodeRenderingHandlers(): MutableSet<NodeRenderingHandler<*>> {
		val self = this
		val set = HashSet<NodeRenderingHandler<*>>()

		set.add(
			NodeRenderingHandler(
				SpotifyLink::class.java,
				CustomNodeRenderer { node, context, html -> self.renderExtendedEmbedLink(node, context, html) })
		)

		return set
	}


	class Factory : NodeRendererFactory {
		override fun create(options: DataHolder): NodeRenderer {
			return SpotifyTagsNodeRenderer(options)
		}
	}

	private fun renderExtendedEmbedLink(node: SpotifyLink, context: NodeRendererContext, html: HtmlWriter) {
		val track = if (node.url.startsWith("spotify:track:")) {
			node.url.removePrefix("spotify:track:")
		} else {
			node.url
		}
		html.line()
		html.indent()
			.append("<iframe src=\"https://open.spotify.com/embed/track/${track}\" width=\"300\" height=\"80\" frameborder=\"0\" allowtransparency=\"true\" allow=\"encrypted-media\"></iframe>")
		html.line()
	}
}
