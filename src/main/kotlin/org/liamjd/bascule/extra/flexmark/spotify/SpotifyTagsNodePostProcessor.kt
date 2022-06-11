package org.liamjd.bascule.extra.flexmark.spotify

import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.ast.Text
import com.vladsch.flexmark.ext.media.tags.internal.AbstractMediaLink
import com.vladsch.flexmark.parser.block.NodePostProcessor
import com.vladsch.flexmark.parser.block.NodePostProcessorFactory
import com.vladsch.flexmark.util.ast.Document
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.ast.NodeTracker
import com.vladsch.flexmark.util.misc.CharPredicate
import com.vladsch.flexmark.util.sequence.BasedSequence

class SpotifyTagsNodePostProcessor() : NodePostProcessor() {
	override fun process(state: NodeTracker, node: Node) {
		if (node is Link) {
			val previous = node.previous

			if (previous is Text) {
				val chars = previous.chars
				if (chars.isContinuedBy(node.chars)) {
					val mediaLink: AbstractMediaLink
					if (chars.endsWith(SpotifyLink.PREFIX) && !isEscaped(chars, SpotifyLink.PREFIX)) {
						mediaLink = SpotifyLink(node)
					} else {
						// abort
						return
					}

					mediaLink.takeChildren(node)
					node.unlink()
					state.nodeRemoved(node)
					previous.insertAfter(mediaLink)
					state.nodeAddedWithChildren(mediaLink)
					previous.setChars(chars.subSequence(0, chars.length - mediaLink.getPrefix().length))
					if (previous.getChars().length == 0) {
						previous.unlink()
						state.nodeRemoved(previous)
					}
				}
			}
		}
	}

	private fun isEscaped(chars: BasedSequence, prefix: String): Boolean {
		val backslash = CharPredicate.anyOf('\\')
		val backslashCount = chars.subSequence(0, chars.length - prefix.length).countTrailing(backslash)
		return backslashCount and 1 != 0
	}

	class Factory() : NodePostProcessorFactory(false) {
		init {
			addNodes(Link::class.java)
		}

		override fun apply(document: Document): NodePostProcessor {
			return SpotifyTagsNodePostProcessor()
		}
	}

}
