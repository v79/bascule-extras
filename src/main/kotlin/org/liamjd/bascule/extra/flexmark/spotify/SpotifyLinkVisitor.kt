package org.liamjd.bascule.extra.flexmark.spotify

import com.vladsch.flexmark.util.ast.VisitHandler
import com.vladsch.flexmark.util.ast.Visitor

interface SpotifyLinkVisitor {
	fun visit(node: SpotifyLink)
}

object SpotifyLinkVisitorExt {
	fun <V : SpotifyLinkVisitor> VISIT_HANDLERS(visitor: V): Array<VisitHandler<*>> {
		return arrayOf(VisitHandler(SpotifyLink::class.java, Visitor { node -> visitor.visit(node) }))
	}
}
