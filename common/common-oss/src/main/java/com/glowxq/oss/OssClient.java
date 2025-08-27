package com.glowxq.oss;

import com.glowxq.core.common.constant.Constant;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.util.AppUtils;
import com.glowxq.core.util.DateUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.StringUtils;
import com.glowxq.tenant.utils.TenantUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;
import software.amazon.awssdk.transfer.s3.model.Upload;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author glowxq
 * @since 2024/11/12 14:56
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class OssClient {

    private static final String FILE_SEPARATOR = "/";

    private static final Long DEFAULT_EXPIRE_TIME = 3600L;

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    private final OssProperties properties;

    private final S3AsyncClient s3AsyncClient;

    private final S3TransferManager transferManager;

    private static String getFileKey(String objectKey) {
        String datePath = DateUtils.datePath();
        String extension = FilenameUtils.getExtension(objectKey);
        String envPath = AppUtils.getEnvironment().getCode();
        String fileKey = "%s/%s/%s/%s".formatted(envPath, extension, datePath, objectKey);
        return fileKey;
    }

    /**
     * 上传文件
     *
     * @param file      文件
     * @param mediaType 媒体类型
     * @return 上传结果
     * @throws IOException IO异常
     */
    public UploadResult upload(File file, MediaType mediaType) throws IOException {
        String dirTag = "markdown"; // 默认目录标签
        String objectName = dirTag + generateFileName(file.getName());
        String fileKey = getFileKey(objectName);

        try (InputStream inputStream = new FileInputStream(file)) {
            return upload(inputStream, fileKey, file.length(), mediaType.getType());
        }
    }

    /**
     * 上传文件
     *
     * @param file   文件
     * @param dirTag 目录
     * @return 上传结果
     *
     * @throws IOException IO异常
     */
    public UploadResult upload(MultipartFile file, String dirTag) throws IOException {
        FileNamingEnum naming = properties.getNaming();
        String objectName;
        if (naming.equals(FileNamingEnum.ORIGINAL)) {
            objectName = dirTag + generateOriginalFileName(file.getOriginalFilename());

            if (isFileExists(objectName)) {
                String ext = org.springframework.util.StringUtils.getFilenameExtension(objectName);
                assert ext != null;
                DateTimeFormatter df = DateTimeFormatter.ofPattern("HHmmss.SSS"); // 获取时分秒.毫秒
                String localTime = df.format(LocalDateTime.now());
                objectName = objectName.split("." + ext)[0] + " (" + localTime + ")." + ext;
            }
        }
        else {
            objectName = dirTag + generateFileName(file.getOriginalFilename());
        }
        String fileKey = getFileKey(objectName);
        return upload(file.getInputStream(), fileKey, file.getSize(), file.getContentType());
    }

    /**
     * 上传文件
     *
     * @param file
     * @param fileKey
     * @return
     */
    public UploadResult uploadFile(File file, String fileKey) {
        try {
            // 设置上传对象的 Acl 为公共读
            PutObjectRequest build = PutObjectRequest.builder().acl(ObjectCannedACL.PUBLIC_READ).bucket(properties.getBucketName()).key(fileKey).build();
            RequestBody asyncRequestBody = RequestBody.fromFile(file);
            s3Client.putObject(build, asyncRequestBody);
            String url = getUrl(fileKey);
            return UploadResult.builder().url(url).objectName(fileKey).dirTag(getDirTag(fileKey)).filename(getFileName(fileKey)).build();
        } catch (Exception e) {
            log.error("上传文件失败，错误信息:" + e.getMessage(), e);
            throw new AlertsException("上传文件失败，错误信息:[%s]".formatted(e.getMessage()));
        }
    }

    /**
     * 上传文件
     *
     * @param file     文件
     * @param dirTag   目录标签
     * @param filename 文件名
     * @return 上传结果
     *
     * @throws IOException IO异常
     */
    public UploadResult upload(MultipartFile file, String dirTag, String filename) throws IOException {
        String objectName = dirTag + "/" + filename;
        String fileKey = getFileKey(objectName);
        return upload(file.getInputStream(), fileKey, file.getSize(), file.getContentType());
    }

    /**
     * 上传文件
     *
     * @param inputStream 输入流
     * @param objectName  对象名（唯一，可包含路径）
     * @param size        文件大小
     * @param contextType 文件类型
     * @return 上传结果
     */
    public UploadResult upload(InputStream inputStream, String objectName, Long size, String contextType) {
        try {
            String fileKey = getFileKey(objectName);

            BlockingInputStreamAsyncRequestBody body = AsyncRequestBody.forBlockingInputStream(size);
            Upload upload = transferManager.upload(x -> x.requestBody(body)
                                                         .putObjectRequest(y -> y.bucket(properties.getBucketName()).key(fileKey).contentType(contextType).build())
                                                         .build());
            body.writeInputStream(inputStream);
            CompletedUpload uploadResult = upload.completionFuture().join();
            String eTag = normalizeETag(uploadResult.response().eTag());
            String url = getUrl(fileKey);
            return UploadResult.builder().url(url).objectName(fileKey).dirTag(getDirTag(fileKey)).filename(getFileName(fileKey))
                               .contextType(contextType).size(size).eTag(eTag).build();
        } catch (Exception e) {
            throw new IllegalArgumentException("上传文件失败，Message:[" + e.getMessage() + "]");
        }
    }

    /**
     * 验证文件是否存在
     *
     * @param key 文件名
     * @return 是否存在
     */
    public boolean isFileExists(String key) {
        try {
            s3Client.headObject(b -> b.bucket(properties.getBucketName()).key(key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取私有文件链接
     *
     * @param objectName 对象名（唯一，可包含路径）
     * @return 链接
     */
    public String getPrivateUrl(String objectName) {
        URL url = s3Presigner.presignGetObject(x -> x.signatureDuration(Duration.ofSeconds(DEFAULT_EXPIRE_TIME))
                                                     .getObjectRequest(y -> y.bucket(properties.getBucketName()).key(objectName).build()).build()).url();
        return url.toString();
    }

    /**
     * 获取私有文件的访问链接。
     * <p>
     * 该方法生成一个带有签名的私有文件访问链接，允许在指定的有效期内访问存储在 S3 兼容服务中的私有文件。 例如：
     *
     * <pre>
     * https://your_domain.com/test/user/20241125/6%E5%AD%97%E5%85%B8%E7%AE%A1%E7%90%86.png
     * ?X-Amz-Algorithm=AWS4-HMAC-SHA256
     * &X-Amz-Date=20241125T011936Z
     * &X-Amz-SignedHeaders=host
     * &X-Amz-Credential=EBPVrHftB014oEKdR9y8%2F20241125%2Fus-east-1%2Fs3%2Faws4_request
     * &X-Amz-Expires=120
     * &X-Amz-Signature=41e40237dc426d7a3e000014183219607cd709a799c9133e3ee61a882b2d3642
     * </pre>
     *
     * @param objectName 对象名（唯一，可包含路径），用于指定要访问的文件
     * @param second     链接的有效期（秒），决定该链接在多长时间内有效
     * @return 生成的私有文件访问链接
     */

    public String getPrivateUrl(String objectName, Long second) {
        URL url = s3Presigner.presignGetObject(x -> x.signatureDuration(Duration.ofSeconds(second))
                                                     .getObjectRequest(y -> y.bucket(properties.getBucketName()).key(objectName).build()).build()).url();
        return url.toString();
    }

    /**
     * 文件下载
     *
     * @param objectName   对象名（唯一，可包含路径）
     * @param outputStream 输出流
     */
    public void download(String objectName, OutputStream outputStream) {
        try {
            // 构建请求
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(properties.getBucketName()).key(objectName).build();

            // 下载对象到内存字节缓冲区
            s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).thenAccept(response -> {
                try {
                    // 写入输出流
                    outputStream.write(response.asByteArray());
                    outputStream.flush();
                } catch (Exception e) {
                    throw new IllegalArgumentException("写入输出流失败", e);
                }
            }).join(); // 同步等待完成
        } catch (Exception e) {
            throw new IllegalArgumentException("下载文件失败，Message:[" + e.getMessage() + "]", e);
        }
    }

    /**
     * 文件下载
     *
     * @param objectName 对象名（唯一，可包含路径）
     * @param response   响应
     */
    public void download(String objectName, HttpServletResponse response, String showFileName) {
        try {
            objectName = getFileKey(objectName);
            // 构建请求
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(properties.getBucketName()).key(objectName).build();
            String filename = showFileName;
            if (showFileName.isEmpty()) {
                filename = getFileName(objectName);
            }
            OutputStream os = FileUtils.getOutputStream(response, filename);
            // 下载对象到内存字节缓冲区
            s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()).thenAccept(responseBytes -> {
                try {

                    // 写入响应流
                    os.write(responseBytes.asByteArray());
                    os.flush();
                } catch (IOException e) {
                    throw new IllegalArgumentException("写入响应失败", e);
                }
            }).join(); // 同步等待完成
        } catch (Exception e) {
            throw new IllegalArgumentException("下载文件失败，Message:[" + e.getMessage() + "]", e);
        }
    }

    // 生成预签名URL（有效期10分钟）
    public String generatePreUrl(String objectKey) {
        String fileKey = getFileKey(objectKey);
        PutObjectRequest putRequest = PutObjectRequest.builder()
                                                      .bucket(properties.getBucketName())
                                                      .key(fileKey)
                                                      .build();

        PresignedPutObjectRequest preUrlRequest = s3Presigner.presignPutObject(r -> r
                .putObjectRequest(putRequest)
                .signatureDuration(Duration.ofMinutes(10)));

        return preUrlRequest.url().toString();
    }

    public void initBucket() {
        createBucket();
        CORSRule corsRule = CORSRule.builder().allowedHeaders("*").allowedMethods("GET", "PUT", "POST", "DELETE").allowedOrigins("*").build();
        CORSConfiguration configuration = CORSConfiguration.builder().corsRules(corsRule).build();
        PutBucketCorsRequest corsRequest = PutBucketCorsRequest.builder().corsConfiguration(configuration).bucket(properties.getBucketName()).build();
        s3Client.putBucketCors(corsRequest);
        PutBucketAclRequest bucketRequest = PutBucketAclRequest.builder()
                                                               .bucket(properties.getBucketName())
                                                               .acl(BucketCannedACL.PUBLIC_READ)
                                                               .build();
        s3Client.putBucketAcl(bucketRequest);
    }

    public void createBucket() {
        CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                                                               .bucket(properties.getBucketName())
                                                               .acl(BucketCannedACL.PUBLIC_READ)
                                                               .build();
        // 创建存储空间。
        s3Client.createBucket(bucketRequest);
    }

    @NotNull
    private String getUrl(String fileKey) {
        return getUrl() + Constant.URL_SEPARATOR + StringUtils.defaultIfBlank(TenantUtils.getTenantId(), "public") + Constant.URL_SEPARATOR + fileKey;
    }

    private String getUrl() {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String scheme = properties.isHttps() ? "https" : "http";
        scheme += "://";
        // 非 MinIO 处理
        if (!properties.getProvider().equals(OssProviderEnum.MINIO)) {
            if (StringUtils.isNotEmpty(domain)) {
                return (domain.startsWith("https://") || domain.startsWith("http://")) ? domain : scheme + domain;
            }
            else {
                return scheme + properties.getBucketName() + "." + endpoint; // eg: https://your_bucket_name.oss-cn-beijing.aliyuncs.com
            }
        }
        if (StringUtils.isNotEmpty(domain)) {
            return (domain.startsWith("https://") || domain.startsWith("http://"))
                   ? domain + "/" + properties.getBucketName()
                   : scheme + domain + "/" + properties.getBucketName();
        }
        else {
            return scheme + endpoint + "/" + properties.getBucketName();
        }
    }

    /**
     * 生成文件名 eg: /20231212/4150e7c6-b92f-419d-b804-0e8be80f5e71.png
     *
     * @param originalFilename 原始文件名
     * @return 文件名
     */
    private String generateFileName(String originalFilename) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String localTime = df.format(LocalDate.now());
        String pathStr = FILE_SEPARATOR + localTime + FILE_SEPARATOR;
        String extension = org.springframework.util.StringUtils.getFilenameExtension(originalFilename);
        String fileName = UUID.randomUUID().toString();
        return pathStr + fileName + "." + extension;
    }

    /**
     * 生成文件名 eg: /20231212/logo.jpg
     *
     * @param originalFilename 原始文件名
     * @return 文件名
     */
    private String generateOriginalFileName(String originalFilename) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String localTime = df.format(LocalDate.now());
        String pathStr = FILE_SEPARATOR + localTime + FILE_SEPARATOR;
        return pathStr + originalFilename;
    }

    private String getFileName(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        return (lastSlashIndex != -1) ? path.substring(lastSlashIndex + 1) : path;
    }

    private String getDirTag(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // 找到第一个'/'的位置
        int index = str.indexOf('/');
        // 如果第一个字符是'/'，找到第二个'/'的位置
        if (index == 0) {
            index = str.indexOf('/', index + 1);
        }
        if (index == -1) {
            return str; // 如果没有找到'/'，返回整个字符串
        }
        return str.substring(0, index);
    }

    private String normalizeETag(String eTag) {
        if (eTag == null || eTag.isEmpty()) {
            return eTag;
        }
        // 替换掉前后的双引号
        return eTag.replaceAll("(^\")|(\"$)", "");
    }
}