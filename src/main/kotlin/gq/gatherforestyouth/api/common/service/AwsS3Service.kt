package gq.gatherforestyouth.api.common.service

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CopyObjectRequest
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * @author gm
 * 2021-08-25 sprtms16 옮김
 */
@Service
@Transactional(rollbackFor = [java.lang.Exception::class])
class AwsS3Service {
    @Autowired
    private val s3Client: AmazonS3? = null

    @Value("\${aws.s3.bucket}")
    private val bucketName: String? = null

    /**
     * @Method uploadMultiObject
     * @작성일 : 2020. 07. 14.
     * @작성자 : gm
     * @Method S3 파일 업로드 - 복수
     * @param files
     * @param bucketPath (분류에 따른 저장 경로)
     * @throws IOException
     */
    @Throws(IOException::class)
    fun uploadMultiObject(files: Array<MultipartFile>, bucketPath: String) {

        // 저장 경로
        val date = SimpleDateFormat("yyyyMM")
        val filePath = bucketPath + "/" + date.format(Date())
        val omd = ObjectMetadata()
        for (file: MultipartFile in files) {
            omd.contentType = file.contentType
            omd.contentLength = file.size
            omd.setHeader("filename", file.originalFilename)
            s3Client!!.putObject(
                PutObjectRequest(
                    "$bucketName/$filePath", file.originalFilename,
                    file.inputStream, omd
                )
            )
        }
    }

    /**
     * @Method uploadObject
     * @작성일 : 2020. 07. 14.
     * @작성자 : gm
     * @Method S3 파일 업로드 - 단수
     * @param multipartFile
     * @param bucketPath (분류에 따른 저장 경로)
     * @throws IOException
     */
    @Throws(IOException::class)
    fun uploadObject(multipartFile: MultipartFile, bucketPath: String): Map<String, String?>? {
        return uploadObjectBody(multipartFile, bucketPath, bucketName)
    }

    /**
     * @Method uploadObject
     * @작성일 : 2020. 07. 14.
     * @작성자 : gm
     * @Method S3 파일 업로드 - 단수
     * @param multipartFile, bucketPath (분류에 따른 저장 경로), bucketName
     * @throws IOException
     */
    @Throws(IOException::class)
    fun uploadObject(multipartFile: MultipartFile, bucketPath: String, bucketName: String?): Map<String, String?>? {
        return uploadObjectBody(multipartFile, bucketPath, bucketName)
    }

    private fun uploadObjectBody(
        multipartFile: MultipartFile,
        bucketPath: String,
        bucketName: String?
    ): Map<String, String?> {
        try {

            // 저장 경로 - 버킷명 / 구분 / 년월 / 파일명
            val date = SimpleDateFormat("yyyyMM")
            val filePath = bucketPath + "/" + date.format(Date())

            // file name
//			String OriginFileName = new String(multipartFile.getOriginalFilename().getBytes("8859_1"), "UTF-8");
            val originFileName = multipartFile.originalFilename
            var storedName: String
            //파일 이름 암호화
            val uuid = UUID.randomUUID()
            storedName = uuid.toString()
            storedName += LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
            //            do {
//
//
//                /*storedName 있는지 체크.*/
////                Map<String, Object> storedNameMap = adminMapper.checkStoredName(storedName);
////                if (!"".equals(storedNameMap) && storedNameMap != null) {
////                    nameValid = true;
////                }
//            } while (nameValid);

            // split > body : ext
            val arrFileName = originFileName!!.split("\\.(?=[^\\.]+$)".toRegex()).toTypedArray()
            var fileName: String?
            var fileExt = ""

            // 확장자가 있을 경우
            if (arrFileName.size >= 2) {
                fileName = arrFileName[0]
                fileExt = arrFileName[1]
            } else {
                fileName = originFileName
            }

            // s3에 중복 파일명이 있을 경우 숫자 suffix 추가
//            int suffix = 0;
//            boolean exists = false;

            // 한글 >> 유니코드 인코딩
//            String newFileName = convertToUnicode(fileName);

//            do {
//                exists = s3Client.doesObjectExist(bucketName, filePath + "/" + newFileName + "." + fileExt);
//
//                 존재할 경우 파일명 변경
//                if (exists) {
//                    newFileName = newFileName + "_" + suffix;
//                    suffix++;
//                }
//
//            } while (exists);


            // 최종 파일명
//            fileName = newFileName + "." + fileExt;
            val omd = ObjectMetadata()
            omd.contentType = multipartFile.contentType
            omd.contentLength = multipartFile.size
            //            omd.setHeader("filename", fileName);
            omd.setHeader("filename", "$storedName.$fileExt")

//            s3Client.putObject(
//                    new PutObjectRequest(bucketName + "/" + filePath, fileName, multipartFile.getInputStream(), omd));
//
//            String storedUrl = s3Client.getUrl(bucketName + "/" + filePath, fileName).toString();
            s3Client!!.putObject(
                PutObjectRequest(
                    "$bucketName/$filePath",
                    "$storedName.$fileExt", multipartFile.inputStream, omd
                )
            )
            val storedUrl = s3Client.getUrl(bucketName, "$filePath/$storedName.$fileExt").toString()
            val map: MutableMap<String, String?> = HashMap()
            map["storedUrl"] = storedUrl
            //            map.put("filePath", "/" + date.format(new Date()) + "/" + fileName); // '200720/fileName.ext'
//            map.put("fileName", newFileName);
            map["filePath"] = "/" + date.format(Date()) + "/" + storedName + "." + fileExt
            map["storedName"] = "$storedName.$fileExt"
            map["displayName"] = fileName
            map["fileExt"] = fileExt
            return map
        } catch (ase: AmazonServiceException) {
            println(
                "Caught an AmazonServiceException, which means your request made it "
                        + "to Amazon S3, but was rejected with an error response for some reason."
            )
            println("Error Message:    " + ase.message)
            println("HTTP Status Code: " + ase.statusCode)
            println("AWS Error Code:   " + ase.errorCode)
            println("Error Type:       " + ase.errorType)
            println("Request ID:       " + ase.requestId)
        } catch (ace: AmazonClientException) {
            println(
                ("Caught an AmazonClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with S3, "
                        + "such as not being able to access the network.")
            )
            println("Error Message: " + ace.message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyMap<String, String>()
    }

    /**
     * @Method uploadMultiObject
     * @작성일 : 2020. 07. 14.
     * @작성자 : gm
     * @Method S3 파일 삭제
     * @param filePath
     * @throws IOException
     */
    @Throws(AmazonServiceException::class)
    fun deleteObject(filePath: String?) {
        s3Client!!.deleteObject(DeleteObjectRequest(bucketName, filePath))
    }

    /**
     * @Method uploadMultiObject
     * @작성일 : 2021. 05. 31.
     * @작성자 : sprtms16
     * @Method S3 파일 삭제
     * @param filePath, bucketName
     * @throws IOException
     */
    @Throws(AmazonServiceException::class)
    fun deleteObject(filePath: String?, bucketName: String?) {
        s3Client!!.deleteObject(DeleteObjectRequest(bucketName, filePath))
    }

    /**
     * @Method moveObject
     * @작성일 : 2021. 05. 31.
     * @작성자 : sprtms16
     * @Method S3 파일 이동
     * @param sourceKey, destinationKey
     * @throws SdkClientException
     */
    @Throws(SdkClientException::class)
    fun moveObject(sourceKey: String?, destinationKey: String?) {
        try {
            // Copy the object into a new object in the same bucket.
            val copyObjRequest = CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey)
            s3Client!!.copyObject(copyObjRequest)
        } catch (e: SdkClientException) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace()
        } // Amazon S3 couldn't be contacted for a response, or the client
        // couldn't parse the response from Amazon S3.
    }

    /**
     * @Method moveObject
     * @작성일 : 2021. 05. 31.
     * @작성자 : sprtms16
     * @Method S3 파일 이동
     * @param sourceKey, destinationKey, sourceBucketName, destinationBucketName
     * @throws SdkClientException
     */
    @Throws(SdkClientException::class)
    fun moveObject(
        sourceKey: String?,
        destinationKey: String?,
        sourceBucketName: String?,
        destinationBucketName: String?
    ) {
        try {
            // Copy the object into a new object in the same bucket.
            val copyObjRequest = CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey)
            s3Client!!.copyObject(copyObjRequest)
        } catch (e: SdkClientException) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace()
        } // Amazon S3 couldn't be contacted for a response, or the client
        // couldn't parse the response from Amazon S3.
    }
}
