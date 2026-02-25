package com.glowxq.core.util;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.glowxq.core.common.exception.common.AlertsException;
import com.glowxq.core.common.exception.common.BusinessException;
import com.glowxq.core.util.bo.ByteArrayMultipartFile;
import com.glowxq.core.util.bo.DownloadResult;
import com.glowxq.core.util.http.HttpUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author glowxq
 * @since 2023/12/25 10:01
 */
@Slf4j
public class FileUtils extends FileUtil {

    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    private FileUtils() {
        throw new IllegalStateException("FileUtils class Illegal");
    }

    /**
     * 下载模板文件并写入到响应输出流中。
     * <p>
     * 该方法从指定路径加载模板文件，并将其内容写入到 `HttpServletResponse` 的输出流中，供客户端下载。
     * </p>
     *
     * @param resourceLoader   用于加载资源的 `ResourceLoader`
     * @param response         HTTP 响应对象
     * @param templateFileName 模板文件名
     * @throws IOException 如果在读取模板文件或写入响应时发生 I/O 错误
     */
    public static void downloadTemplateFile(ResourceLoader resourceLoader, HttpServletResponse response, String templateFileName) throws IOException {
        String templatePath = "classpath:/templates/" + templateFileName;
        Resource resource = resourceLoader.getResource(templatePath);
        OutputStream os = getOutputStream(response, templateFileName);
        InputStream inputStream = resource.getInputStream();
        FileCopyUtils.copy(inputStream, os);
        os.flush();
    }

    /**
     * 通用 YAML 解析方法，从文件路径解析
     *
     * @param parentFolder 父目录（可以为 null，表示使用当前工作目录）
     * @param childName    子文件或子目录的名称
     * @param clazz        目标类型的 Class
     * @param <T>          泛型类型
     * @return 解析后的对象
     *
     * @throws IOException 如果文件读取或解析失败
     */
    public static <T> T parseYamlFile(File parentFolder, String childName, Class<T> clazz) throws IOException {
        File file = checkFileOrDirectoryExist(parentFolder, childName, File::isFile, "文件不存在或无法读取：" + childName);
        return yamlMapper.readValue(file, clazz);
    }

    /**
     * 检查给定的磁盘路径是否存在且为目录。
     * <p>
     * 该方法用于验证指定路径是否存在并且是一个目录。
     * </p>
     *
     * @param path 待检查的路径
     * @return 如果路径存在且为目录返回 `true`，否则返回 `false`
     */
    public static boolean isPathExists(String path) {
        if (path == null || path.trim().isEmpty() || path.isEmpty()) {
            return false;
        }
        Path pathObj = Paths.get(path);
        return Files.exists(pathObj) && Files.isDirectory(pathObj);
    }

    /**
     * 获取响应的 `OutputStream`，用于文件下载。
     * <p>
     * 该方法设置响应头，以支持文件下载，并返回 `ServletOutputStream`。
     * </p>
     *
     * @param response HTTP 响应对象
     * @param fileName 文件名，例如：教师统计.xlsx
     * @return 用于写入文件内容的 `OutputStream`
     *
     * @throws IOException 如果在获取输出流时发生 I/O 错误
     */
    public static OutputStream getOutputStream(HttpServletResponse response, String fileName) throws IOException {
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        // 设置响应头
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return response.getOutputStream();
    }

    /**
     * 获取响应的 `OutputStream`，用于文件下载，并可设置内容长度。
     * <p>
     * 该方法设置响应头，以支持文件下载，并返回 `ServletOutputStream`，还可以指定内容长度。
     * </p>
     *
     * @param response      HTTP 响应对象
     * @param fileName      文件名，例如：教师统计.xlsx
     * @param contentLength 文件内容长度（可选）
     * @return 用于写入文件内容的 `OutputStream`
     *
     * @throws IOException 如果在获取输出流时发生 I/O 错误
     */
    public static OutputStream getOutputStream(HttpServletResponse response, String fileName, Integer contentLength) throws IOException {
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        // 设置响应头
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        if (contentLength != null) {
            response.setContentLength(contentLength);
        }
        return response.getOutputStream();
    }

    /**
     * 从指定URL下载文件并封装为MultipartFile对象
     *
     * @param url 文件下载地址，示例：https://glowxq-oj.oss-cn-guangzhou.aliyuncs.com/.../testcase.zip
     * @return MultipartFile 文件对象
     *
     * @throws IOException 当下载失败或URL不合法时抛出
     */
    public static MultipartFile downloadFile(String url) {
        try {
            // 1. 使用HttpUtils下载文件字节流和元数据
            DownloadResult result = HttpUtils.download(url);

            // 2. 从URL中解析原始文件名（自动解码URL编码）
            String filename = extractFilename(url);

            // 3. 构造MultipartFile实现类
            return new ByteArrayMultipartFile(
                    "file",
                    filename,
                    result.getContentType(),
                    result.getData()
            );
        } catch (URISyntaxException e) {
            throw new AlertsException("URL格式不合法: " + url);
        } catch (RuntimeException e) {
            throw new AlertsException("文件下载失败");
        }
    }

    /**
     * 从URL路径中提取并解码文件名
     *
     * @param url 文件地址
     * @return 解码后的文件名，如 problem_2612_testcase.zip
     */
    private static String extractFilename(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String path = uri.getPath();
        String[] pathSegments = path.split("/");
        if (pathSegments.length == 0) {
            throw new IllegalArgumentException("URL中未包含文件名: " + url);
        }
        String encodedFilename = pathSegments[pathSegments.length - 1];
        return URLDecoder.decode(encodedFilename, StandardCharsets.UTF_8);
    }

    /**
     * 去掉文件扩展名
     *
     * @param fileName 文件名
     * @return 去掉扩展名后的文件名
     */
    public static String stripFilenameExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
    }

    /**
     * 去掉文件扩展名
     *
     * @param file 文件
     * @return 去掉扩展名后的文件名
     */
    public static String stripFilenameExtension(File file) {
        return stripFilenameExtension(file.getName());
    }

    /**
     * 基于源文件生成与其名称相关的目标目录(不包含创建过程)
     *
     * @param sourceFile 源文件
     * @return 与源文件名称相关的目标目录
     */
    public static File generateDirectoryBySourceFile(File sourceFile) {
        // 获取源文件的父目录
        File parentDir = sourceFile.getParentFile();
        if (parentDir == null || !parentDir.exists()) {
            log.error("源文件的父目录不存在");
            return null;
        }

        // 获取文件的基本名称（不包含扩展名）
        String baseName = stripFilenameExtension(sourceFile.getName());

        // 构造目标目录
        File targetDir = new File(parentDir, baseName);
        log.info("生成目标目录: {}", targetDir.getAbsolutePath());
        return targetDir;
    }

    /**
     * 创建与源文件名称相关的目标目录
     *
     * @param sourceFile 源文件
     * @return 与源文件名称相关的创建好的目标目录
     *
     * @throws BusinessException 如果创建目录失败
     */
    public static File createTargetDirectoryBySourceFile(File sourceFile) throws BusinessException {
        // 获取目标目录
        File targetDir = FileUtils.generateDirectoryBySourceFile(sourceFile);
        if (targetDir == null) {
            throw new BusinessException("无法获取目标目录");
        }

        // 如果目标目录不存在，则创建
        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                throw new BusinessException("创建目录失败: " + targetDir.getAbsolutePath());
            }
        }
        log.info("创建目录: {}", targetDir.getAbsolutePath());
        return targetDir;
    }

    /**
     * 校验文件格式是否符合指定的扩展名
     *
     * @param file        文件对象
     * @param expectedExt 期望的文件扩展名（例如 "zip" 或 "txt"，无需包含 "."）
     * @throws BusinessException 如果文件格式不符合期望的扩展名
     */
    public static void validateFileFormat(File file, String expectedExt) throws BusinessException {
        // 确保扩展名以 "." 开头
        String normalizedExt = "." + expectedExt.toLowerCase();
        if (!file.getName().toLowerCase().endsWith(normalizedExt)) {
            throw new BusinessException("文件必须是 ." + expectedExt + " 格式");
        }
    }

    /**
     * 解压 ZIP 文件到指定目录，支持多种ZIP格式和压缩方法
     *
     * @param zipFile ZIP 文件
     * @param destDir 解压目标目录
     * @throws IOException 如果解压过程中发生 I/O 错误
     */
    public static void unzip(File zipFile, File destDir) throws IOException {
        log.debug("开始解压ZIP文件: {} 到目录: {}", zipFile.getAbsolutePath(), destDir.getAbsolutePath());

        // 确保目标目录存在
        ensureDirectoryExists(destDir);

        try {
            // 优先使用Hutool的ZipUtil，它对各种ZIP格式兼容性更好
            cn.hutool.core.util.ZipUtil.unzip(zipFile, destDir, StandardCharsets.UTF_8);
            log.debug("使用Hutool ZipUtil成功解压ZIP文件");
        } catch (Exception hutoolException) {
            log.warn("Hutool ZipUtil解压失败，尝试使用原生ZipInputStream: {}", hutoolException.getMessage());

            // 如果Hutool解压失败，回退到原生方法
            try {
                unzipWithNativeMethod(zipFile, destDir);
                log.debug("使用原生ZipInputStream成功解压ZIP文件");
            } catch (Exception nativeException) {
                log.error("原生ZipInputStream解压也失败: {}", nativeException.getMessage());

                // 如果原生方法也失败，尝试使用不同的字符编码
                try {
                    unzipWithDifferentCharsets(zipFile, destDir);
                    log.debug("使用不同字符编码成功解压ZIP文件");
                } catch (Exception charsetException) {
                    log.error("所有解压方法都失败了");
                    throw new AlertsException(
                            "ZIP文件解压失败，可能是文件损坏或格式不支持。原始错误: %s; 备用方法错误: %s".formatted(hutoolException.getMessage(), nativeException.getMessage())
                            , hutoolException);
                }
            }
        }

        log.debug("ZIP文件解压完成");
    }

    /**
     * 使用原生ZipInputStream解压ZIP文件
     */
    private static void unzipWithNativeMethod(File zipFile, File destDir) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // 跳过系统文件
                if (isSystemFile(entry.getName())) {
                    log.debug("跳过系统文件: {}", entry.getName());
                    zipInputStream.closeEntry();
                    continue;
                }

                File destFile = new File(destDir, entry.getName());

                // 安全检查：防止ZIP炸弹和路径遍历攻击
                if (!isValidZipEntry(destFile, destDir)) {
                    log.warn("跳过不安全的ZIP条目: {}", entry.getName());
                    zipInputStream.closeEntry();
                    continue;
                }

                if (entry.isDirectory()) {
                    ensureDirectoryExists(destFile);
                }
                else {
                    ensureParentExists(destFile);
                    try (OutputStream outputStream = Files.newOutputStream(destFile.toPath())) {
                        IOUtils.copy(zipInputStream, outputStream);
                    }
                }
                zipInputStream.closeEntry();
            }
        }
    }

    /**
     * 尝试使用不同的字符编码解压ZIP文件
     */
    private static void unzipWithDifferentCharsets(File zipFile, File destDir) throws IOException {
        // 尝试常见的字符编码
        java.nio.charset.Charset[] charsets = {
                StandardCharsets.UTF_8,
                java.nio.charset.Charset.forName("GBK"),
                java.nio.charset.Charset.forName("GB2312"),
                StandardCharsets.ISO_8859_1
        };

        IOException lastException = null;

        for (java.nio.charset.Charset charset : charsets) {
            try {
                log.debug("尝试使用字符编码: {}", charset.name());
                cn.hutool.core.util.ZipUtil.unzip(zipFile, destDir, charset);
                return; // 成功解压，直接返回
            } catch (Exception e) {
                log.debug("字符编码 {} 解压失败: {}", charset.name(), e.getMessage());
                lastException = new IOException("使用字符编码 " + charset.name() + " 解压失败", e);
            }
        }

        throw lastException;
    }

    /**
     * 验证ZIP条目是否安全（防止路径遍历攻击）
     */
    private static boolean isValidZipEntry(File destFile, File destDir) throws IOException {
        String destDirPath = destDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        return destFilePath.startsWith(destDirPath + File.separator) || destFilePath.equals(destDirPath);
    }

    /**
     * 确保目录存在
     *
     * @param dir 目录
     */
    private static void ensureDirectoryExists(File dir) throws IOException {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("创建目录失败: " + dir.getAbsolutePath());
            }
        }
    }

    /**
     * 确保目标文件的父目录存在
     *
     * @param file 目标文件
     */
    private static void ensureParentExists(File file) throws IOException {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("创建目录失败: " + parentDir.getAbsolutePath());
            }
        }
    }

    /**
     * 获取目录中所有指定扩展名的文件
     *
     * @param directory 目标目录
     * @param fileExt   文件扩展名（例如 "json" 或 "txt"，无需包含 "."）
     * @return 指定扩展名的文件列表
     */
    public static List<File> listFilesByExtension(File directory, String fileExt) {
        // 确保扩展名以 "." 开头
        String normalizedExt = "." + fileExt.toLowerCase();
        List<File> filesWithExt = new ArrayList<>();
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(normalizedExt));
        if (files != null) {
            filesWithExt.addAll(Arrays.asList(files));
        }
        return filesWithExt;
    }

    /**
     * 获取目录中的所有子文件夹
     *
     * @param directory 目标目录
     * @return 子文件夹列表
     */
    public static List<File> listSubdirectories(File directory) {
        List<File> subdirectories = new ArrayList<>();
        File[] files = directory.listFiles(File::isDirectory);
        if (files != null) {
            subdirectories.addAll(Arrays.asList(files));
        }
        return subdirectories;
    }

    /**
     * 删除目录及其内容
     *
     * @param dir 目录
     */
    public static void deleteDirectory(File dir) {
        if (dir == null || !dir.exists()) {
            return;
        }

        Stack<File> stack = new Stack<>();
        stack.push(dir);

        while (!stack.isEmpty()) {
            File current = stack.pop();
            File[] files = current.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        stack.push(file);
                    }
                    else {
                        if (!file.delete()) {
                            log.error("删除文件失败: {}", file.getAbsolutePath());
                        }
                    }
                }
            }
            if (!current.delete()) {
                log.error("删除目录失败: {}", current.getAbsolutePath());
            }
        }
    }

    /**
     * 读取文件内容为字符串
     */
    public static String readFileContent(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    /**
     * 检查指定的文件或目录是否存在并满足给定条件。
     *
     * @param parentFolder     父目录（可以为 null，表示使用当前工作目录）
     * @param childName        子文件或子目录的名称
     * @param predicate        判断条件（例如 File::isFile 或 File::isDirectory）
     * @param exceptionMessage 异常消息（当检查失败时抛出）
     * @return
     *
     * @throws BusinessException 如果检查失败，抛出业务异常
     */
    public static File checkFileOrDirectoryExist(File parentFolder, String childName, Predicate<File> predicate, String exceptionMessage) {

        // 构造完整的文件或目录路径
        File target = new File(parentFolder != null ? parentFolder : new File(""), childName);

        // 检查目标是否存在且满足条件
        if (!target.exists() || !predicate.test(target)) {
            throw new BusinessException(exceptionMessage);
        }

        // 返回 target 对象
        return target;
    }

    public static boolean isSystemFile(String entryName) {
        return entryName.startsWith("__MACOSX") ||
                entryName.startsWith("._") ||
                entryName.equals(".DS_Store") ||
                entryName.endsWith("/.DS_Store") ||
                entryName.contains("Thumbs.db") ||        // Windows缩略图文件
                entryName.contains("desktop.ini");        // Windows桌面设置文件
    }

    /**
     * 跨平台创建目录，支持Mac、Windows、Linux
     *
     * @param parentDir 要创建的目录
     * @throws AlertsException 当创建目录失败时抛出
     */
    public static void forceMkdir(File parentDir) {
        try {
            // 获取规范化的目录路径
            File normalizedDir = normalizeDirectoryPath(parentDir);

            // 检查父目录权限
            validateDirectoryPermissions(normalizedDir);

            // 使用Apache Commons IO创建目录
            org.apache.commons.io.FileUtils.forceMkdir(normalizedDir);

            log.debug("成功创建目录: {}", normalizedDir.getAbsolutePath());
        } catch (IOException e) {
            log.error("创建目录失败: {}", parentDir.getAbsolutePath(), e);
            throw new AlertsException("无法创建目录：%s，错误信息：%s".formatted(
                    parentDir.getAbsolutePath(), e.getMessage()), e);
        }
    }

    /**
     * 规范化目录路径，确保跨平台兼容性
     *
     * @param dir 原始目录
     * @return 规范化后的目录
     */
    private static File normalizeDirectoryPath(File dir) {
        String path = dir.getAbsolutePath();
        if (AppUtils.isLocalOrDev()) {
            String osName = System.getProperty("os.name").toLowerCase();

            // 如果是Unix系统（Mac/Linux）且路径以/goj开头，则转换为用户目录下的相对路径
            if (!osName.contains("win") && path.startsWith("/goj")) {
                String userHome = System.getProperty("user.home");
                String relativePath = path.substring(1); // 去掉开头的/
                File normalizedDir = new File(userHome, relativePath);
                log.debug("路径规范化: {} -> {}", path, normalizedDir.getAbsolutePath());
                return normalizedDir;
            }
        }

        return dir;
    }

    /**
     * 验证目录创建权限
     *
     * @param dir 要验证的目录
     * @throws IOException 当权限不足时抛出
     */
    private static void validateDirectoryPermissions(File dir) throws IOException {
        // 检查父目录是否存在且可写
        File parent = dir.getParentFile();
        if (parent != null && parent.exists() && !parent.canWrite()) {
            throw new IOException("父目录没有写权限: " + parent.getAbsolutePath());
        }

        // 如果目录已存在，检查是否可写
        if (dir.exists() && !dir.canWrite()) {
            throw new IOException("目录已存在但没有写权限: " + dir.getAbsolutePath());
        }

        // 对于根目录路径，给出更明确的错误信息
        String path = dir.getAbsolutePath();
        if (path.startsWith("/goj") || path.startsWith("/judge")) {
            String osName = System.getProperty("os.name").toLowerCase();
            if (!osName.contains("win")) {
                throw new IOException("在Unix系统中无法在根目录创建路径，请检查路径配置: " + path);
            }
        }
    }
}
