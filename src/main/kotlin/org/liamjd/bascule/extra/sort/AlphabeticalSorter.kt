package org.liamjd.bascule.extra.sort

import org.liamjd.bascule.lib.generators.SortAndFilter
import org.liamjd.bascule.lib.model.Post
import org.liamjd.bascule.lib.model.Project

/**
 * Sorting by post title
 */
object AlphabeticalSorter : SortAndFilter {

	override fun sortAndFilter(project: Project, posts: List<Post>): List<List<Post>> {
		println("alphabetical sort and filter")
		return posts.reversed().asSequence().withIndex()
			.filter { indexedValue: IndexedValue<Post> -> project.postLayouts.contains(indexedValue.value.layout) }
			.sortedBy { it.value.title }
			.groupBy { it.index / project.postsPerPage }
			.map { p -> p.value.map { it.value } }.toList()
	}
}
