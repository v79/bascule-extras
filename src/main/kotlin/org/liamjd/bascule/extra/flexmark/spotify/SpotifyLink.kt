package org.liamjd.bascule.extra.flexmark.spotify

import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.ext.media.tags.internal.AbstractMediaLink

class SpotifyLink : AbstractMediaLink {

	constructor() : super(PREFIX, TYPE) {}

	constructor(other: Link) : super(PREFIX, TYPE, other) {}

	companion object {

		const val PREFIX = "!S"
		const val TYPE = "Spotify"
	}

	// This class leaves room for specialization, should we need it.
	// Additionally, it makes managing different Node types easier for users.
}
