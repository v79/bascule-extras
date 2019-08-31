package org.liamjd.bascule.extra.generators.aws.s3

import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import org.liamjd.bascule.lib.FileHandler
import org.liamjd.bascule.lib.generators.GeneratorPipeline
import org.liamjd.bascule.lib.model.Project
import org.liamjd.bascule.lib.render.TemplatePageRenderer

class S3BucketUpload : GeneratorPipeline {
	override val TEMPLATE: String
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

	override suspend fun process(project: Project, renderer: TemplatePageRenderer, fileHandler: FileHandler) {

		val s3Config = project.configMap["s3"] as Map<String, Any>?
		val bucket: String

		if (s3Config == null || s3Config["bucket"] == null) {
			println("Error - s3 bucket not configured. Unable to upload.")
			return
		} else {
			bucket = s3Config["bucket"] as String
		}
		println("Uploading to Amazon S3 bucket ${bucket}...")

		val s3credentials = ProfileCredentialsProvider().credentials
		if (s3credentials != null) {
			if (s3credentials.awsAccessKeyId.isNullOrEmpty() || s3credentials.awsSecretKey.isNullOrEmpty()) {
				println("Error - s3 bucket credentials not found. Unable to upload.")
				return
			} else {
				// continue
			}
		} else {
			println("Error - s3 bucket credentials not found. Unable to upload.")
			return
		}

		val s3 = AmazonS3ClientBuilder.standard().withCredentials(AWSStaticCredentialsProvider(s3credentials)).build()
		val transferManager = TransferManagerBuilder.standard().withS3Client(s3).build();
		if (transferManager == null) {
			println("Unable to start transfer manager with s3 client. Unable to upload.")
			return
		}

		try {
			println("Attempting to upload all of ${project.dirs.output.absolutePath}")
			val xref = transferManager.uploadDirectory(bucket, null, project.dirs.output, true)
			xref.waitForCompletion()
		} catch (ase: AmazonServiceException) {
			println("Error!: ${ase.message}")
		}
		transferManager.shutdownNow()

	}

}
