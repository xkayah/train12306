package com.mnus.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mnus.common.context.ReqHolder;
import com.mnus.common.enums.BaseErrorCodeEnum;
import com.mnus.common.exception.BizException;
import com.mnus.common.req.EntityDeleteReq;
import com.mnus.common.resp.PageResp;
import com.mnus.common.utils.IdGenUtil;
import com.mnus.business.domain.TrainCarriage;
import com.mnus.business.domain.TrainCarriageExample;
import com.mnus.business.mapper.TrainCarriageMapper;
import com.mnus.business.req.TrainCarriageQueryReq;
import com.mnus.business.req.TrainCarriageSaveReq;
import com.mnus.business.resp.TrainCarriageQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author: <a href="https://github.com/xkayah">xkayah</a>
 */
@Service
public class TrainCarriageService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainCarriageService.class);
    @Resource
    private TrainCarriageMapper trainCarriageMapper;

    public void save(TrainCarriageSaveReq req) {
        DateTime now = DateTime.now();
        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        // notnull,insert
        if (Objects.isNull(trainCarriage.getId())) {
            trainCarriage.setId(IdGenUtil.nextId());
            trainCarriage.setGmtCreate(now);
            trainCarriage.setGmtModified(now);
            trainCarriageMapper.insert(trainCarriage);
        } else {
            // null,update
            trainCarriage.setGmtModified(now);
            trainCarriageMapper.updateByPrimaryKeySelective(trainCarriage);
        }
    }

    public void delete(EntityDeleteReq req) {
        // todo 是否是该用户的数据
        trainCarriageMapper.deleteByPrimaryKey(req.getId());
    }

    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {
        // Long uid = req.getUserId();
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        // if (Objects.nonNull(uid)) {
        //     trainCarriageExample.createCriteria().andUserIdEqualTo(uid);
        // }
        // 分页请求
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        List<TrainCarriage> trainCarriageList = trainCarriageMapper.selectByExample(trainCarriageExample);
        // 获取分页
        PageInfo<TrainCarriage> pageInfo = new PageInfo<>(trainCarriageList);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        // 封装分页
        List<TrainCarriageQueryResp> list = BeanUtil.copyToList(pageInfo.getList(), TrainCarriageQueryResp.class);
        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(total);
        pageResp.setList(list);
        pageResp.setPages(pages);
        LOG.info("[query] pageNo:{},pageSize:{},total:{},pages:{}",
                req.getPageNo(), req.getPageSize(), total, pages);
        return pageResp;
    }

}