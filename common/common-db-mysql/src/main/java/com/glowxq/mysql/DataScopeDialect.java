package com.glowxq.mysql;

import com.glowxq.core.common.entity.LoginUser;
import com.glowxq.core.common.enums.DataScopeType;
import com.glowxq.core.datascope.DataScopeThreadLocal;
import com.glowxq.security.core.util.LoginUtils;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义方言实现 - 数据权限控制
 * <p>
 * 该类扩展了 MyBatis Flex 的 CommonsDialectImpl，实现了根据用户数据权限类型自动添加查询条件的功能。
 * 支持多种数据权限类型：全部数据、仅本人数据、本部门数据、本部门及下级部门数据、加入的班级数据等。
 * 通过重写 prepareAuth 方法，在查询执行前自动添加相应的数据筛选条件。
 * </p>
 *
 * @author glowxq
 * @since 2024/5/11
 */
@Slf4j
public class DataScopeDialect extends CommonsDialectImpl {

    /**
     * 准备权限过滤条件
     * <p>
     * 根据当前登录用户的数据权限类型，向查询中添加相应的过滤条件。
     * 例如：本人数据权限时添加 create_id = 当前用户ID 的条件。
     * </p>
     *
     * @param queryWrapper 查询包装器
     * @param operateType  操作类型（如SELECT、INSERT等）
     */
    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        log.info("DataScopeDialect 准备开始数据权限过滤...");
        // 检查数据权限是否启用，如未启用则不进行数据权限过滤
        if (!DataScopeThreadLocal.isDataScopeEnabled()) {
            return;
        }

        // 检查表是否在忽略列表中
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (!queryTables.isEmpty() && DataScopeThreadLocal.isTableIgnored(queryTables.getFirst().getName())) {
            log.debug("表 [{}] 在忽略列表中，跳过数据权限过滤", queryTables.getFirst().getName());
            return;
        }

        // 预先检查是否需要应用数据权限过滤，如不需要则直接返回
        if (preDataScopeCheckRule(queryWrapper, operateType)) {
            return;
        }

        // 获取当前登录用户及其数据权限类型
        LoginUser loginUser = LoginUtils.getLoginUser();
        DataScopeType dataScopeType = loginUser.dataScope();


        // 检查表是否包含需要过滤的字段，如不包含则直接返回
        if (containsFiledCheck(queryWrapper, operateType, dataScopeType)) {
            return;
        }

        // 根据不同的数据权限类型添加相应的查询条件
        switch (dataScopeType) {
            case All:
                // 全部数据权限，不添加额外条件
                break;
            case UserCreate:
                // 本人数据权限，添加创建者ID等于当前用户ID的条件
                queryWrapper.eq(dataScopeType.getField(), loginUser.getUserId());
                break;
            case Dept:
                // 本部门数据权限，添加部门ID在用户所属部门列表中的条件
                queryWrapper.in(dataScopeType.getField(), loginUser.getDepts());
                break;
            case DeptAndChildren:
                // 本部门及下级部门数据权限，添加部门ID在用户部门及其子部门列表中的条件
                queryWrapper.in(dataScopeType.getField(), loginUser.getDeptAndChildren());
                break;
            case JoinGroup:
                // 加入的班级数据权限，添加班级ID在用户所属班级列表中的条件
                queryWrapper.in(dataScopeType.getField(), loginUser.scopeGroupIds());
                break;
        }
    }

    /**
     * 检查查询表是否包含需要进行数据权限过滤的字段
     * <p>
     * 如果表中不包含对应的字段，则无法应用数据权限过滤，会记录警告日志并发送内部消息通知。
     * </p>
     *
     * @param queryWrapper  查询包装器
     * @param operateType   操作类型
     * @param dataScopeType 数据权限类型
     * @return 如果不包含字段返回true，否则返回false
     */
    private boolean containsFiledCheck(QueryWrapper queryWrapper, OperateType operateType, DataScopeType dataScopeType) {
        // 获取查询的表列表
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        // 获取第一个查询表，用于后续的字段检查
        QueryTable queryTable = queryTables.getFirst();
        // 获取QueryTable类中声明的所有字段
        String tableName = queryTable.getName();
        TableInfo tableInfo = TableInfoFactory.ofTableName(tableName);
        List<String> columns = Arrays.stream(tableInfo.getAllColumns()).map(String::toUpperCase).toList();
        // 检查是否包含需要过滤的字段（不区分大小写）
        boolean containsColumn = columns.contains(dataScopeType.getFieldUp());

        // 如果不包含过滤字段，则不添加过滤条件，记录警告并发送通知
        if (!containsColumn) {
            log.warn("数据权限过滤 不包含字段:[{}] table:{}", dataScopeType.getField(), queryTable.getName());
            return true;
        }
        return false;
    }

    /**
     * 数据权限过滤的预检查规则
     * <p>
     * 检查是否需要应用数据权限过滤，包括操作类型检查、登录状态检查、表检查、连接表检查、
     * 超级管理员检查、用户信息检查以及数据权限配置检查等。
     * </p>
     *
     * @param queryWrapper 查询包装器
     * @param operateType  操作类型
     * @return 如果不需要应用数据权限过滤则返回true，否则返回false
     */
    private boolean preDataScopeCheckRule(QueryWrapper queryWrapper, OperateType operateType) {
        // 只对查询操作进行数据权限过滤，其他操作类型直接调用父类方法并返回true
        if (operateType != OperateType.SELECT) {
            return true;
        }

        // 用户未登录时，无法应用数据权限过滤
        if (!LoginUtils.isLogin()) {
            return true;
        }

        // 查询表为空时，不进行数据权限过滤
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (CollectionUtils.isEmpty(queryTables)) {
            return true;
        }

        // 连表查询时，暂不支持数据权限过滤
        List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
        if (CollectionUtils.isNotEmpty(joinTables)) {
            return true;
        }

        // 超级管理员处理 - 根据配置决定是否对超级管理员进行数据权限过滤
        // if (LoginUtils.isSuperAdmin() && !DataScopeThreadLocal.isSuperAdminFilter()) {
        //     return true;
        // }

        // 无法获取到用户信息时，不进行数据权限过滤
        LoginUser loginUser = LoginUtils.getLoginUser();
        if (loginUser == null) {
            return true;
        }

        // 用户未配置数据权限时，不进行数据权限过滤
        DataScopeType dataScopeType = loginUser.dataScope();
        if (dataScopeType == null) {
            return true;
        }

        // 通过所有检查，需要应用数据权限过滤
        return false;
    }
}