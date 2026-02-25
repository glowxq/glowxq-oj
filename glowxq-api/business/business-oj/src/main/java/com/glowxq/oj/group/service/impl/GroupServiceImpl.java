package com.glowxq.oj.group.service.impl;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.core.common.enums.CommonResponseEnum;
import com.glowxq.core.util.BeanCopyUtils;
import com.glowxq.core.util.FileUtils;
import com.glowxq.core.util.PageUtils;
import com.glowxq.core.util.Utils;
import com.glowxq.excel.core.ExcelResult;
import com.glowxq.excel.utils.ExcelUtils;
import com.glowxq.oj.common.DataScopeUtils;
import com.glowxq.oj.common.enums.OjTagBind;
import com.glowxq.oj.course.mapper.CourseMapper;
import com.glowxq.oj.course.pojo.po.Course;
import com.glowxq.oj.group.mapper.GroupBindMapper;
import com.glowxq.oj.group.mapper.GroupMapper;
import com.glowxq.oj.group.pojo.dto.GroupCreateDTO;
import com.glowxq.oj.group.pojo.dto.GroupImportDTO;
import com.glowxq.oj.group.pojo.dto.GroupListDTO;
import com.glowxq.oj.group.pojo.dto.GroupUpdateDTO;
import com.glowxq.oj.group.pojo.po.Group;
import com.glowxq.oj.group.pojo.po.GroupBind;
import com.glowxq.oj.group.pojo.vo.GroupVO;
import com.glowxq.oj.group.service.GroupService;
import com.glowxq.oj.problem.mapper.ProblemMapper;
import com.glowxq.oj.problem.pojo.po.Problem;
import com.glowxq.oj.topic.mapper.TopicMapper;
import com.glowxq.oj.topic.pojo.po.Topic;
import com.glowxq.oj.user.mapper.UserInfoMapper;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.security.core.util.LoginUtils;
import com.glowxq.system.meta.base.TagBindEnum;
import com.glowxq.system.meta.enums.SystemTagBind;
import com.glowxq.system.meta.pojo.po.MetaTag;
import com.glowxq.system.meta.service.MetaTagService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 班级表 服务实现类
 * </p>
 *
 * @author glowxq
 * @since 2025-03-27
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    private final TopicMapper topicMapper;

    private final CourseMapper courseMapper;

    private final ProblemMapper problemMapper;

    private final GroupBindMapper groupBindMapper;

    private final MetaTagService metaTagService;

    private final UserInfoMapper userInfoMapper;

    private static QueryWrapper buildQueryWrapper(GroupListDTO dto) {
        QueryWrapper wrapper = QueryWrapper.create().from(Group.class);
        wrapper.like(Group::getName, dto.getName(), Utils.isNotNull(dto.getName()));
        wrapper.eq(Group::getCode, dto.getCode(), Utils.isNotNull(dto.getCode()));
        wrapper.eq(Group::getPrincipalUserId, dto.getPrincipalUserId(), Utils.isNotNull(dto.getPrincipalUserId()));
        wrapper.like(Group::getPrincipalName, dto.getPrincipalName(), Utils.isNotNull(dto.getPrincipalName()));
        wrapper.eq(Group::getDescription, dto.getDescription(), Utils.isNotNull(dto.getDescription()));
        wrapper.eq(Group::getColor, dto.getColor(), Utils.isNotNull(dto.getColor()));
        wrapper.eq(Group::getTextColor, dto.getTextColor(), Utils.isNotNull(dto.getTextColor()));
        wrapper.eq(Group::getEnable, dto.getEnable(), Utils.isNotNull(dto.getEnable()));
        wrapper.in(Group::getId, DataScopeUtils.scopeGroupIds(), DataScopeUtils.scopeEnable());
        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(GroupCreateDTO dto) {
        Group group = BeanCopyUtils.copy(dto, Group.class);
        save(group);
        metaTagService.bindTags(group.getId(), dto.getTagIds(), OjTagBind.Group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(GroupUpdateDTO dto) {
        Group group = BeanCopyUtils.copy(dto, Group.class);
        QueryWrapper wrapper;
        // id有效性校验
        wrapper = QueryWrapper.create()
                              .eq(Group::getId, dto.getId());
        CommonResponseEnum.INVALID_ID.assertTrue(count(wrapper) <= 0);

        saveOrUpdate(group);
        metaTagService.unBindAll(group.getId(), OjTagBind.Group);
        metaTagService.bindTags(group.getId(), dto.getTagIds(), OjTagBind.Group);
    }

    @Override
    public PageResult<GroupVO> page(GroupListDTO dto) {
        QueryWrapper qw = buildQueryWrapper(dto);
        Set<Long> groupIds = DataScopeUtils.scopeGroupIds();
        qw.in(Group::getId, groupIds, CollectionUtils.isNotEmpty(groupIds) && DataScopeUtils.scopeEnable());
        Page<GroupVO> page = pageAs(PageUtils.getPage(dto), qw, GroupVO.class);
        return PageUtils.getPageResult(page);
    }

    @Override
    public List<GroupVO> list(GroupListDTO dto) {
        return listAs(buildQueryWrapper(dto), GroupVO.class);
    }

    @Override
    public void remove(SelectIdsDTO dto) {
        CommonResponseEnum.INVALID_ID.assertTrue(dto.getIds().isEmpty());
        removeByIds(dto.getIds());
    }

    @Override
    public GroupVO detail(Object id) {
        Group group = getById((Serializable) id);
        CommonResponseEnum.INVALID_ID.assertNull(group);
        GroupVO groupVO = BeanCopyUtils.copy(group, GroupVO.class);

        // 主题数据
        List<GroupBind> groupBindTopic = groupBindMapper.listByGroupId(group.getId(), OjTagBind.Topic);
        List<Long> topicIds = groupBindTopic.stream().map(GroupBind::getBusinessId).collect(Collectors.toList());
        List<Topic> topicList = topicMapper.listByIds(topicIds);
        groupVO.setTopicList(topicList);

        // 题目数据
        List<GroupBind> groupBindProblem = groupBindMapper.listByGroupId(group.getId(), OjTagBind.Problem);
        List<Long> problemIds = groupBindProblem.stream().map(GroupBind::getBusinessId).collect(Collectors.toList());
        List<Problem> problemsList = problemMapper.listByIds(problemIds);
        groupVO.setProblemsList(problemsList);

        // 课程数据
        List<GroupBind> groupBindCourse = groupBindMapper.listByGroupId(group.getId(), OjTagBind.Course);
        List<Long> courseIds = groupBindCourse.stream().map(GroupBind::getBusinessId).collect(Collectors.toList());
        List<Course> courseList = courseMapper.listByIds(courseIds);
        groupVO.setCourseList(courseList);

        // 标签数据
        List<MetaTag> metaTagList = metaTagService.listByBusinessId(group.getId(), OjTagBind.Group);
        groupVO.setTagList(metaTagList);

        // 用户数据
        List<Long> userIds = this.listBusinessIdByGroupId(group.getId(), SystemTagBind.User);
        List<UserInfo> userInfos = userInfoMapper.selectListByIds(userIds);
        groupVO.setUserList(userInfos);

        return groupVO;
    }

    @SneakyThrows
    @Override
    public void importExcel(ImportExcelDTO dto) {
        ExcelResult<GroupImportDTO> excelResult = ExcelUtils.importExcel(dto.getFile().getInputStream(), GroupImportDTO.class, true);
        List<GroupImportDTO> list = excelResult.getList();
        List<String> errorList = excelResult.getErrorList();
        String analysis = excelResult.getAnalysis();
        System.out.println(" analysis : " + analysis);
        System.out.println(" isCover : " + dto.getIsCover());
    }

    @SneakyThrows
    @Override
    public void exportExcel(GroupListDTO dto, HttpServletResponse response) {
        List<GroupVO> list = list(dto);
        String fileName = "班级表模板";
        OutputStream os = FileUtils.getOutputStream(response, fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "班级表", GroupVO.class, os);
    }

    @Override
    public void bindGroups(Long businessId, List<Long> groupIdList, TagBindEnum bindType) {
        groupBindMapper.bindGroups(businessId, groupIdList, bindType);
    }

    @Override
    public void bindsGroups(List<Long> businessIds, List<Long> groupIdList, TagBindEnum bindType) {
        groupBindMapper.bindsGroups(businessIds, groupIdList, bindType);
    }

    @Override
    public void unBindAll(Long businessId, TagBindEnum tagBind) {
        groupBindMapper.deleteByBusinessId(businessId, tagBind);
    }

    @Override
    public void unBindsAll(List<Long> businessIds, TagBindEnum tagBind) {
        groupBindMapper.deleteByBusinessIds(businessIds, tagBind);
    }

    @Override
    public List<Group> listByBusinessId(Long businessId, TagBindEnum tagBind) {
        List<GroupBind> groupBinds = groupBindMapper.listByBusinessId(businessId, tagBind);
        if (CollectionUtils.isEmpty(groupBinds)) {
            return List.of();
        }
        List<Long> groupIds = groupBinds.stream().map(GroupBind::getGroupId).toList();
        return mapper.selectListByIds(groupIds);
    }

    @Override
    public Map<Long, List<Group>> mapByBusinessIds(List<Long> businessIds, TagBindEnum tagBind) {
        // 空输入快速返回
        if (CollectionUtils.isEmpty(businessIds)) {
            return Map.of();
        }

        // 查询业务绑定关系数据
        List<GroupBind> allGroupBinds = groupBindMapper.listByBusinessIds(businessIds, tagBind);
        if (CollectionUtils.isEmpty(allGroupBinds)) {
            return Map.of();
        }

        // 提取所有关联的群组ID（注意：此处可能存在的逻辑问题，GroupBind::getBusinessId实际应获取关联的群组ID？
        // 根据后续代码推断此处应为获取groupId，当前代码可能存在参数获取错误）
        List<Long> groupIds = allGroupBinds.stream().map(GroupBind::getGroupId).toList();

        // 批量查询群组详细信息
        List<Group> allGroupList = mapper.selectListByIds(groupIds);
        if (CollectionUtils.isEmpty(allGroupList)) {
            return Map.of();
        }

        // 构建内存数据结构
        Map<Long, Group> allGroupMap = allGroupList.stream().collect(Collectors.toMap(Group::getId, group -> group));
        Map<Long, List<GroupBind>> businessGoupBindMap = allGroupBinds.stream().collect(Collectors.groupingBy(GroupBind::getBusinessId));

        // 组装最终结果映射
        Map<Long, List<Group>> businessBindGroupMap = new HashMap<>();
        businessGoupBindMap.forEach((businessId, groupBindList) -> {
            // 转换绑定关系到群组对象
            List<Long> bindGroupIds = groupBindList.stream().map(GroupBind::getGroupId).toList();
            List<Group> bindGroup = bindGroupIds.stream().map(allGroupMap::get).filter(Objects::nonNull).toList();
            businessBindGroupMap.put(businessId, bindGroup);
        });

        return businessBindGroupMap;
    }

    @Override
    public List<Long> listBusinessIdByGroupIds(List<Long> groupIds, TagBindEnum tagBind) {
        if (CollectionUtils.isEmpty(groupIds) && LoginUtils.getLoginUser().containsAdminRole()) {
            return List.of();
        }
        List<GroupBind> groupBinds = groupBindMapper.listByGroupIds(groupIds, tagBind);
        // 获取到的班级业务ID为空
        if (CollectionUtils.isEmpty(groupBinds)) {
            return List.of(Long.MIN_VALUE);
        }

        return groupBinds.stream().map(GroupBind::getBusinessId).toList();
    }

    @Override
    public List<Long> listBusinessIdByGroupId(Long groupId, TagBindEnum tagBind) {
        List<GroupBind> groupBinds = groupBindMapper.listByGroupId(groupId, tagBind);
        return groupBinds.stream().map(GroupBind::getBusinessId).toList();
    }

    @Override
    public void bindByCode(Long businessId, String groupCode, TagBindEnum tagBindEnum) {
        if (StringUtils.isBlank(groupCode)) {
            return;
        }
        Group group = mapper.getByCode(groupCode);
        bindGroups(businessId, List.of(group.getId()), tagBindEnum);
    }
}