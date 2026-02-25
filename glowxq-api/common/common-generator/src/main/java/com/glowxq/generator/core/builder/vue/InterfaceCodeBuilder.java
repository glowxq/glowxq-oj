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
public class InterfaceCodeBuilder extends AbstractCodeGenerationTemplate {

    public InterfaceCodeBuilder(FreeMarkerConfigurer configurer, String rootPath, GeneratorDetailVO detailVO, Map<String, Object> model) {
        super(configurer, rootPath, detailVO, model);
    }

    @Override
    protected String getTemplateFileName() {
        return File.separator + "vue" + File.separator + "interface.ts.ftl";
    }

    @Override
    protected String getOutputFileName(Map<String, Object> model) {
        return model.get("interfaceClassName").toString();
    }

    @Override
    protected String getOutputPackage(Map<String, Object> model) {
        return model.get("interfacePkg").toString();
    }

    @Override
    protected String getProjectPrefix() {
        return GeneratorConstants.PROJECT_WEB_PREFIX;
    }

    @Override
    protected String getExtension() {
        return ".ts";
    }

    @Override
    protected String getZipParentPackage() {
        return "vue";
    }

    @Override
    protected String alias() {
        return "interface.ts";
    }

    @Override
    protected String language() {
        return "ts";
    }

}
