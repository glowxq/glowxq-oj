package com.glowxq.oj.user.service;

import com.glowxq.core.common.entity.ImportExcelDTO;
import com.glowxq.core.common.entity.PageResult;
import com.glowxq.core.common.entity.SelectIdsDTO;
import com.glowxq.oj.user.pojo.dto.*;
import com.glowxq.oj.user.pojo.po.UserInfo;
import com.glowxq.oj.user.pojo.vo.UserInfoVO;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 用户信息 Service
 * </p>
 *
 * @author glowxq
 * @since 2025-06-23
 */
public interface UserInfoService extends IService<UserInfo> {

    void create(UserInfoCreateDTO dto);

    void update(UserInfoUpdateDTO dto);

    PageResult<UserInfoVO> page(UserInfoListDTO dto);

    List<UserInfoVO> list(UserInfoListDTO dto);

    void remove(SelectIdsDTO dto);

    UserInfoVO detail(Long id);

    void importExcel(ImportExcelDTO dto);

    void exportExcel(UserInfoListDTO dto, HttpServletResponse response);

    List<UserInfo> acRankingList(RankingDTO rankingDTO);

    List<UserInfo> acRankingList();

    void register(UserInfoRegisterDTO dto);

    void bind(UserInfoBindDTO dto);
}