package org.liamjd.bascule.extra.sort

import org.liamjd.bascule.lib.generators.SortAndFilter
import org.liamjd.bascule.lib.model.Post
import org.liamjd.bascule.lib.model.Project
import java.io.File

object ManualSorter : SortAndFilter {
	val ORDER_FILE_NAME = "order.txt"
	lateinit var fileHandler: SortingFH

	override fun sortAndFilter(project: Project, posts: List<Post>): List<List<Post>> {
		val orderFile = fileHandler.getFile(project.dirs.root, ORDER_FILE_NAME)
		val filteredPosts: List<List<Post>>
		if (!orderFile.exists()) {
			println("$orderFile not found; creating")
			orderFile.createNewFile()
		}
		val manualOrder = orderFile.readLines()
		val newList = mutableListOf<Post>()
		manualOrder.forEach {
			val foundPost = posts.find { p -> p.sourceFileName == it }
			if (foundPost == null) {
				println("Looked for a post with filename $it but it wasn't found")
				return@forEach
			} else {
				newList.add(foundPost)
			}
		}

		filteredPosts = newList.asSequence().withIndex()
			.filter { indexedValue: IndexedValue<Post> -> project.postLayouts.contains(indexedValue.value.layout) }
			.groupBy { it.index / project.postsPerPage }
			.map { p -> p.value.map { it.value } }.toList()


		return filteredPosts
	}
}

interface SortingFH {
	fun getFile(path: File, name: String): File
}

class SortingFileHandler : SortingFH {
	override fun getFile(path: File, name: String): File {
		return File(path, name)
	}
}
