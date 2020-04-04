package org.liamjd.bascule.flexmark.spotify

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataHolder

class SpotifyExtension : Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

	override fun extend(parserBuilder: Parser.Builder?) {
		parserBuilder?.postProcessorFactory(SpotifyTagsNodePostProcessor.Factory(parserBuilder))
	}

	override fun parserOptions(options: MutableDataHolder?) {
	}

	companion object {
		fun create(): SpotifyExtension {
			return SpotifyExtension()
		}
	}

	override fun rendererOptions(options: MutableDataHolder) {
	}

	override fun extend(htmlRendererBuilder: HtmlRenderer.Builder, rendererType: String) {
		htmlRendererBuilder.nodeRendererFactory(SpotifyTagsNodeRenderer.Factory())
	}

}
