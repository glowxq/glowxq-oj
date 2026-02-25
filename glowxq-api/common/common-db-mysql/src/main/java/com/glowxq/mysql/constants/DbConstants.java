package com.glowxq.mysql.constants;

/**
 * 数据库常量接口 - 定义与数据库操作相关的常量
 * <p>
 * 此接口包含数据库表的标准字段名称，用于实体操作时的字段填充、查询等操作
 * </p>
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/6/6
 */
public interface DbConstants {

    /**
     * 创建者ID字段名
     * <p>
     * 用于记录创建实体的用户ID
     * </p>
     */
    String FIELD_CREATE_ID = "createId";

    /**
     * 创建时间字段名
     * <p>
     * 用于记录实体创建的时间
     * </p>
     */
    String FIELD_CREATE_TIME = "createTime";

    /**
     * 更新者ID字段名
     * <p>
     * 用于记录最后更新实体的用户ID
     * </p>
     */
    String FIELD_UPDATE_ID = "updateId";

    /**
     * 更新时间字段名
     * <p>
     * 用于记录实体最后更新的时间
     * </p>
     */
    String FIELD_UPDATE_TIME = "updateTime";

    /**
     * 部门范围字段名
     * <p>
     * 用于记录实体关联的部门ID列表
     * </p>
     */
    String FIELD_DEPT_SCOPE = "deptScope";

    /**
     * 部门ID字段名
     * <p>
     * 用于记录实体关联的部门ID
     * </p>
     */
    String FIELD_DEPT_ID = "deptId";
}
