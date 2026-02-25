package com.glowxq.mysql;

import com.github.pagehelper.PageInterceptor;
import com.glowxq.logger.PrintSQL;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Flex配置类 - 提供MyBatis Flex的核心配置
 *
 * @author glowxq
 * @since 2024/5/11 14:58
 */
@Slf4j
@Configuration
public class MybatisFlexConfiguration implements MyBatisFlexCustomizer {

    @Resource
    private DataScopeProperties dataScopeProperties;

    /**
     * 构造函数 - 初始化MyBatis Flex基础配置
     * 设置查询列行为并启用SQL打印功能
     */
    public MybatisFlexConfiguration() {
        log.info("初始化MyBatis Flex配置");
        // 关闭全局null参数忽略设置，确保查询条件中的null值也会被处理
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_NONE);
        log.debug("已设置QueryColumnBehavior为IGNORE_NONE");

        // 启用SQL打印，方便开发调试
        PrintSQL.print();
        log.debug("已启用SQL打印功能");
    }

    /**
     * 分页拦截器Bean - 提供分页查询功能
     *
     * @return PageInterceptor实例
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        log.debug("创建PageInterceptor分页拦截器");
        return new PageInterceptor();
    }

    /**
     * 逻辑删除处理器Bean - 处理实体的逻辑删除
     *
     * @return 自定义的EntityLogicDeleteListener实例
     */
    @Bean
    public LogicDeleteProcessor logicDeleteProcessor() {
        log.debug("创建EntityLogicDeleteListener逻辑删除处理器");
        return new EntityLogicDeleteListener();
    }

    /**
     * 自定义MyBatis Flex配置
     * 如果启用了数据范围功能，注册数据范围方言实现
     *
     * @param flexGlobalConfig MyBatis Flex全局配置对象
     */
    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        if (dataScopeProperties.isEnabled()) {
            log.info("数据范围功能已启用，注册DataScopeDialect方言");
            // 注册查询权限监听方言，用于实现数据权限控制
            DialectFactory.registerDialect(DbType.MYSQL, new DataScopeDialect());
        } else {
            log.info("数据范围功能未启用，跳过方言注册");
        }
    }
}
