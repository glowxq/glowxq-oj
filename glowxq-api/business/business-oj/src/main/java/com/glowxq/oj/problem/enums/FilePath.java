package com.glowxq.oj.problem.enums;

import com.glowxq.core.common.constant.Constant;
import com.glowxq.core.common.enums.EnvType;
import com.glowxq.core.common.enums.base.BaseType;
import com.glowxq.core.util.AppUtils;
import com.glowxq.core.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/1/20
 */
@AllArgsConstructor
@Getter
public enum FilePath implements BaseType {
    USER_AVATAR_FOLDER("/goj/file/avatar", "user_image_", "用户头像"),

    GROUP_AVATAR_FOLDER("/goj/file/avatar/group", "group_image", "团队头像"),

    HOME_CAROUSEL_FOLDER("/goj/file/carousel", "carousel_", "轮播图"),

    MARKDOWN_FILE_FOLDER("/goj/file/md", "md_", "Markdown文件"),

    PROBLEM_FILE_FOLDER("/goj/file/problem", "problem_", "题目文件"),

    CONTEST_TEXT_PRINT_FOLDER("/goj/file/contest_print", "print_", "比赛打印文件"),

    IMG_API("/api/public/img/", "image_", "图片API"),

    FILE_API("/api/public/file/", "file_", "文件API"),

    TESTCASE_TMP_FOLDER("/goj/file/zip", "", "测试样例临时文件夹"),

    TESTCASE_BASE_FOLDER("/goj/testcase", "problem_", "题目测试样例文件夹"),

    FILE_DOWNLOAD_TMP_FOLDER("/goj/file/zip/download", "download_", "下载文件临时文件夹"),

    CONTEST_AC_SUBMISSION_TMP_FOLDER("/goj/file/zip/contest_ac", "contest_ac_", "比赛AC提交临时文件夹"),

    PROBLEM_UPLOAD_TMP_FOLDER("/goj/file/problem/upload/tmp", "tmp_", "题目上传临时文件存储文件夹");

    /**
     * code
     */
    private final String path;

    /**
     * 文件前缀
     */
    private final String prefix;

    /**
     * 最近类型
     */
    private final String name;

    /**
     * 根据code获取枚举
     *
     * @param code
     * @return
     */
    public static FilePath matchCode(String code) {
        for (FilePath pushStatus : FilePath.values()) {
            if (pushStatus.getCode().equals(code)) {
                return pushStatus;
            }
        }
        return null;
    }

    @Override
    public Object getCode() {
        return name();
    }

    /**
     * 构建文件路径
     *
     * @param id
     * @return
     */
    public String buildPath(Object id) {
        FilePath filePath = this;
        return filePath.getAutoPath() + File.separator + filePath.prefix + id;
    }

    /**
     * 构建文件URL路径
     *
     * @param id
     * @return
     */
    public String buildUrlPath(Object id) {
        FilePath filePath = this;
        return filePath.getPath().replaceFirst(Constant.URL_SEPARATOR, "") + Constant.URL_SEPARATOR + filePath.prefix + id;
    }

    /**
     * 获取自动路径
     *
     * @return
     */
    public String getAutoPath() {
        return getAutoPath(true);
    }

    /**
     * 获取自动路径，支持跨平台兼容性
     *
     * @param addWindowsDir 是否添加Windows目录 盘符
     * @return 规范化后的路径
     */
    public String getAutoPath(Boolean addWindowsDir) {
        String path = StringUtils.replace(this.getPath(), Constant.URL_SEPARATOR, File.separator);
        if (AppUtils.isLocalOrDev()) {
            String osName = System.getProperty("os.name").toLowerCase();

            // Windows系统处理
            if (osName.contains("win")) {
                if (!File.separator.equals(Constant.URL_SEPARATOR) && addWindowsDir) {
                    return Constant.WINDOWS_DIR + path;
                }
                return path;
            }

            // Unix系统（Mac/Linux）处理
            // 如果路径以系统分隔符开头（绝对路径），转换为用户主目录下的相对路径
            if (path.startsWith(File.separator)) {
                String userHome = System.getProperty("user.home");
                String relativePath = path.substring(1); // 去掉开头的分隔符
                return userHome + File.separator + relativePath;
            }
        }

        return path;
    }
}
