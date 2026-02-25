package com.glowxq.generator.core.builder.vue;

import com.glowxq.generator.core.AbstractCodeGenerationTemplate;
import com.glowxq.generator.core.GeneratorConstants;
import com.glowxq.generator.pojo.vo.GeneratorDetailVO;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.util.Map;

/**
 * @author glowxq
 * @since 2024/1/16 8:02
 */
public class IndexCodeBuilder extends AbstractCodeGenerationTemplate {

    public IndexCodeBuilder(FreeMarkerConfigurer configurer, String rootPath, GeneratorDetailVO detailVO, Map<String, Object> model) {
        super(configurer, rootPath, detailVO, model);
    }

    @Override
    protected String getTemplateFileName() {
        return File.separator + "vue" + File.separator + "index.vue.ftl";
    }

    @Override
    protected String getOutputFileName(Map<String, Object> model) {
        return model.get("indexClassName").toString();
    }

    @Override
    protected String getOutputPackage(Map<String, Object> model) {
        return model.get("indexPkg").toString();
    }

    @Override
    protected String getProjectPrefix() {
        return GeneratorConstants.PROJECT_WEB_PREFIX;
    }

    @Override
    protected String getExtension() {
        return ".vue";
    }

    @Override
    protected String getZipParentPackage() {
        return "vue";
    }

    @Override
    protected String alias() {
        return "index.vue";
    }

    @Override
    protected String language() {
        return "html";
    }
}
