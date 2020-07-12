package org.liamjd.bascule.extra.sort

import org.liamjd.bascule.lib.generators.SortAndFilter
import org.liamjd.bascule.lib.model.Post
import org.liamjd.bascule.lib.model.Project
import java.io.File

object ManualSorter : SortAndFilter {
	val ORDER_FILE_NAME = "order.txt"
	lateinit var fileHandler: SortingFH

	override fun sortAndFilter(project: Project, posts: List<Post>): List<List<Post>> {
		// really poor man's DI...
		if (!this::fileHandler.isInitialized) {
			fileHandler = SortingFileHandler()
		}
		val orderFile = fileHandler.getFile(project.dirs.root, ORDER_FILE_NAME)
		val filteredPosts: List<List<Post>>
		if (!orderFile.exists()) {
			println("$orderFile not found; creating")
			orderFile.createNewFile()
		}
		val originalManualOrder = orderFile.readLines()
		val manualOrder = originalManualOrder.toMutableList().withIndex()
		val newList = Array<Post?>(posts.size) { null }
		val missingFromOrder = mutableListOf<String>()

		var extraPostsPositionCounter =
			originalManualOrder.size // newList[0..originalManualOrder.size-1] will be filled from manualOrder, leaving newList[originalManualOrderSize..posts.size-1] unoccupied
		posts.forEach { p ->
			val foundInOrder = manualOrder.find { s -> s.value == p.sourceFileName }
			if (foundInOrder != null) {
				println("Inserting ${foundInOrder.value} at ${foundInOrder.index}")
				newList[foundInOrder.index] = p
			} else {
				println("We have a post ${p.sourceFileName} but I can't find it in the order file. Add it now")
				missingFromOrder.add(p.sourceFileName)
				// not found, but I still need to add it to newList. But where?
				// ideally, at the front of 'newList', but that is not possible here
				newList[extraPostsPositionCounter++] = p
			}
		}

		filteredPosts = newList.filterNotNull().toList().asSequence().withIndex()
			.filter { indexedValue: IndexedValue<Post> -> project.postLayouts.contains(indexedValue.value.layout) }
			.groupBy { it.index / project.postsPerPage }
			.map { p -> p.value.map { it.value } }.toList()

		// update the ordering file with newly found MD files
		if (missingFromOrder.size > 0) {
			// add the original list to the end
			missingFromOrder.addAll(originalManualOrder)
			fileHandler.writeFile(project.dirs.root, ORDER_FILE_NAME, missingFromOrder.joinToString("\n"))
		}

		return filteredPosts
	}
}

interface SortingFH {
	fun getFile(path: File, name: String): File
	fun writeFile(path: File, name: String, text: String)
}

class SortingFileHandler : SortingFH {
	override fun getFile(path: File, name: String): File {
		return File(path, name)
	}

	override fun writeFile(path: File, name: String, text: String) {
		val outFile = File(path, name)
		outFile.writeText(text)
	}
}
