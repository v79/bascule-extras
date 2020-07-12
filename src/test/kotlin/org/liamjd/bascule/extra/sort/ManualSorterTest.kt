package org.liamjd.bascule.extra.sort

import io.mockk.*
import org.liamjd.bascule.lib.model.Directories
import org.liamjd.bascule.lib.model.Post
import org.liamjd.bascule.lib.model.Project
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import kotlin.test.assertEquals

internal class ManualSorterTest : Spek({

	val mDirectories = mockk<Directories>()
	val mProject = mockk<Project>(relaxed = true) {
		mockk {
			every { postsPerPage } returns 10
			every { dirs } returns mDirectories
			every { postLayouts } returns setOf("post")
		}
	}
	val mFileHandler = mockk<SortingFileHandler>()
	val mRoot = mockk<File>()
	val mOrderFile = mockk<File>()


	var posts: List<Post> = mutableListOf(DATA.post1, DATA.post2, DATA.post3, DATA.post4)
	var extendedPosts: List<Post> =
		mutableListOf(DATA.post1, DATA.post2, DATA.post3, DATA.post4, DATA.post5, DATA.post6)


	every { mDirectories.root } returns mRoot
	every { mFileHandler.getFile(any(), any()) } returns mOrderFile
	every { mFileHandler.writeFile(any(), any(), any()) } just Runs
	every { mOrderFile.exists() } returns true

	mockkStatic("kotlin.io.FilesKt__FileReadWriteKt")
	every { mOrderFile.readLines() } returns DATA.targetorder

	describe("Fully test") {
		it("returns a fully sorted and grouped list of posts") {
			// setup
			ManualSorter.fileHandler = mFileHandler

			// execute
			val sortedList = ManualSorter.sortAndFilter(mProject, posts)

			// verify
			assertEquals(1, sortedList.size)
			assertEquals(4, sortedList[0].size)
			sortedList[0].let { listItem ->
				for (i in listItem.indices) {
					println("$i: ${DATA.targetorder[i]} -> ${listItem[i].sourceFileName}")
					assertEquals(DATA.targetorder[i], listItem[i].sourceFileName)
				}
			}
		}

		it("will update the order list when new MD files are found") {
			// setup
			ManualSorter.fileHandler = mFileHandler

			// execute
			val sortedList = ManualSorter.sortAndFilter(mProject, extendedPosts)

			// verify
			assertEquals(1, sortedList.size)
			assertEquals(6, sortedList[0].size)
			sortedList[0].let { listItem ->
				for (i in listItem.indices) {
					println("$i: ${DATA.extendedExpectedTargetOrder[i]} -> ${listItem[i].sourceFileName}")
					assertEquals(DATA.extendedExpectedTargetOrder[i], listItem[i].sourceFileName)
				}
			}

			verify(exactly = 1) { mFileHandler.writeFile(ofType(), any(), any()) }
		}

		it("will not reorder if no order file found, and will create an order file") {
			// setup
			every { mOrderFile.exists() } returns false
			every { mOrderFile.createNewFile() } returns false
			ManualSorter.fileHandler = mFileHandler

			// execute
			val sortedList = ManualSorter.sortAndFilter(mProject, posts)

			// verify
			assertEquals(1, sortedList.size)
			assertEquals(4, sortedList[0].size)
			verify(exactly = 1) { mFileHandler.writeFile(ofType(), any(), any()) }
		}
	}
}
)

object DATA {
	val targetorder = arrayOf("APPLE.md", "BANANA.md", "PEAR.md", "APRICOT.md").toList()
	val mdFileNames = arrayOf("APPLE.md", "PEAR.md", "APRICOT.md", "BANANA.md").toList()
	val mdFileNamesAdditional =
		arrayOf("APPLE.md", "PEAR.md", "APRICOT.md", "BANANA.md", "WATERMELON.md", "GRAPEFRUIT.md").toList()
	val extendedExpectedTargetOrder =
		arrayOf("APPLE.md", "BANANA.md", "PEAR.md", "APRICOT.md", "WATERMELON.md", "GRAPEFRUIT.md").toList()
	val post1 = mockk<Post>() {
		mockk {
			every { layout } returns "post"
			every { sourceFileName } returns mdFileNames[0]
		}
	}
	val post2 = mockk<Post>() {
		mockk {
			every { layout } returns "post"
			every { sourceFileName } returns mdFileNames[1]
		}
	}
	val post3 = mockk<Post>() {
		mockk {
			every { layout } returns "post"
			every { sourceFileName } returns mdFileNames[2]
		}
	}
	val post4 = mockk<Post>() {
		mockk {
			every { layout } returns "post"
			every { sourceFileName } returns mdFileNames[3]
		}
	}
	val post5 = mockk<Post>() {
		mockk {
			every { layout } returns "post"
			every { sourceFileName } returns mdFileNamesAdditional[4]
		}
	}
	val post6 = mockk<Post>() {
		mockk {
			every { layout } returns "post"
			every { sourceFileName } returns mdFileNamesAdditional[5]
		}
	}
}
