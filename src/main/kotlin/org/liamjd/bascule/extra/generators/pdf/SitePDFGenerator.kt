package org.liamjd.bascule.extra.generators.pdf

import org.apache.fop.apps.FopFactory
import org.apache.fop.apps.MimeConstants
import org.liamjd.bascule.lib.FileHandler
import org.liamjd.bascule.lib.generators.GeneratorPipeline
import org.liamjd.bascule.lib.model.Post
import org.liamjd.bascule.lib.model.Project
import org.liamjd.bascule.lib.render.TemplatePageRenderer
import java.io.FileOutputStream
import java.net.URL
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.stream.StreamSource

class SitePDFGenerator(val posts: List<Post>) : GeneratorPipeline {
	override val TEMPLATE: String
		get() = "fullPDF.fo"    // TODO get this from the project?
	val FOLDER_NAME = "pdf" // TODO: move this to project model


	val fopFactory = FopFactory.newInstance(readFileFromResources("/", "fopConf.xconf").toURI())

	override suspend fun process(
		project: Project,
		renderer: TemplatePageRenderer,
		fileHandler: FileHandler,
		clean: Boolean
	) {

		val pdfFolder = fileHandler.createDirectory(project.dirs.output.absolutePath, FOLDER_NAME)

		val outputFilename = pdfFolder.absolutePath + "/${project.name}.pdf"

		val model = mutableMapOf<String, Any>()
		model.putAll(project.model)
		model.put("posts", posts)

		println("Generating PDF: " + outputFilename)
        FileOutputStream(outputFilename).use { outStream ->
            val fop = fopFactory.newFop(MimeConstants.MIME_PDF, outStream)

            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()

            // Ask handlebars to populate the .fo file with the project model before passing to FOP to convert to PDF
            val renderedFOString = renderer.render(model, TEMPLATE)

            val fopInputStream = renderedFOString.byteInputStream()
            val src = StreamSource(fopInputStream)
            val res = SAXResult(fop.defaultHandler)
            transformer.transform(src, res)
        }
    }

    private fun readFileFromResources(sourceDir: String, fileName: String): URL {
        return javaClass.getResource(sourceDir + fileName)
    }
}
