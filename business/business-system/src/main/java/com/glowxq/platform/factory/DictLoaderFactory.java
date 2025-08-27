package com.glowxq.platform.factory;

import com.glowxq.core.common.entity.DictVO;
import com.glowxq.core.util.SpringApplicationContextUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.platform.enums.DynamicDictEnum;
import com.glowxq.platform.loader.DefaultStaticDictLoader;
import com.glowxq.platform.loader.DictLoader;
import com.glowxq.system.admin.pojo.vo.sysdict.DictTypeVO;
import com.glowxq.system.admin.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DictLoaderFactory {

    // 动态字典加载器
    private final List<DictLoader> dynamicLoaders = new ArrayList<>();

    // 静态字典加载器
    private final DictLoader defaultLoader;

    @Autowired
    public DictLoaderFactory(DefaultStaticDictLoader defaultStaticDictLoader, List<DictLoader> loaders) {
        this.defaultLoader = defaultStaticDictLoader;
        // 将动态加载器注册到工厂中
        for (DictLoader loader : loaders) {
            if (Utils.isNotNull(loader.getDynamicTypeCode())) {
                dynamicLoaders.add(loader);
            }
        }
    }

    /**
     * 获取所有字典 （静态 + 动态）
     *
     * @return Map
     */
    public Map<String, List<DictVO>> loadAllDict() {

        // 加载静态字典
        Map<String, List<DictVO>> result = new HashMap<>(defaultLoader.loadDict());

        // 加载所有动态字典
        for (DictLoader loader : dynamicLoaders) {
            result.putAll(loader.loadDict());
        }
        return result;
    }

    /**
     * 获取指定类型的字典
     *
     * @param typeCode
     *            类型
     * @return DictVO集合
     */
    public List<DictVO> getDictByType(String typeCode) {
        // 找不到typeCode
        if (!loadAllDict().containsKey(typeCode)) {
            // 动态字典，直接返回空集合
            if (typeCode.contains("dynamic_")) {
                return List.of();
            }
            // 静态字典，尝试获取
            return defaultLoader.getDict(typeCode);
        }
        return loadAllDict().get(typeCode);
    }

    /**
     * 查询所有字典类型（静态 + 动态）
     *
     * @return DictTypeVO 集合
     */
    public List<DictTypeVO> getAllDictType() {
        SysDictTypeService dictTypeService = SpringApplicationContextUtils.getInstance().getBean(SysDictTypeService.class);
        List<DictTypeVO> dictTypeVOS = dictTypeService.findDictType(); // 查询静态字典类型
        List<DictTypeVO> result = new ArrayList<>(dictTypeVOS);

        DictTypeVO dictTypeVO;
        // 获取动态字典类型
        for (DictLoader loader : dynamicLoaders) {
            DynamicDictEnum dictEnum = loader.getDynamicTypeCode();
            String typeCode = dictEnum.getTypeCode();
            String name = dictEnum.getName();
            dictTypeVO = DictTypeVO.builder().typeName(name).typeCode(typeCode).isDynamic(true).build();
            result.add(dictTypeVO);
        }
        return result;
    }

}
