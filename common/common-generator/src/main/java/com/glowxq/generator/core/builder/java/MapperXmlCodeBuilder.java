package com.glowxq.generator.core.builder.java;

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
public class MapperXmlCodeBuilder extends AbstractCodeGenerationTemplate {

    public MapperXmlCodeBuilder(FreeMarkerConfigurer configurer, String rootPath, GeneratorDetailVO detailVO, Map<String, Object> model) {
        super(configurer, rootPath, detailVO, model);
    }

    @Override
    protected String getTemplateFileName() {
        return File.separator + "api" + File.separator + "mapper.xml.ftl";
    }

    @Override
    protected String getOutputFileName(Map<String, Object> model) {
        return model.get("mapperClassName").toString();
    }

    @Override
    protected String getOutputPackage(Map<String, Object> model) {
        return model.get("mapperXmlPkg").toString();
    }

    @Override
    protected String getProjectPrefix() {
        return GeneratorConstants.PROJECT_XML_PREFIX;
    }

    @Override
    protected String getExtension() {
        return ".xml";
    }

    @Override
    protected String getZipParentPackage() {
        return "java";
    }

    @Override
    protected String alias() {
        return "mapperXml";
    }

    @Override
    protected String language() {
        return "java";
    }

}
