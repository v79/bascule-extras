package org.liamjd.bascule.flexmark.spotify

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataHolder

class SpotifyExtension : Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

	override fun extend(parserBuilder: Parser.Builder?) {
		parserBuilder?.postProcessorFactory(SpotifyTagsNodePostProcessor.Factory(parserBuilder))
	}

	override fun extend(rendererBuilder: HtmlRenderer.Builder?, rendererType: String?) {
		rendererBuilder?.nodeRendererFactory(SpotifyTagsNodeRenderer.Factory())
	}

	override fun parserOptions(options: MutableDataHolder?) {
	}

	override fun rendererOptions(options: MutableDataHolder?) {
	}

	companion object {
		fun create(): SpotifyExtension {
			return SpotifyExtension()
		}
	}

}
