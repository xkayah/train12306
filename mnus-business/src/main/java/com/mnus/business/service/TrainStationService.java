package com.mnus.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mnus.business.domain.TrainStation;
import com.mnus.business.domain.TrainStationExample;
import com.mnus.business.mapper.TrainStationMapper;
import com.mnus.business.req.TrainStationQueryReq;
import com.mnus.business.req.TrainStationSaveReq;
import com.mnus.business.resp.TrainStationQueryResp;
import com.mnus.common.enums.BaseErrorCodeEnum;
import com.mnus.common.exception.BizException;
import com.mnus.common.req.EntityDeleteReq;
import com.mnus.common.resp.PageResp;
import com.mnus.common.utils.IdGenUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author: <a href="https://github.com/xkayah">xkayah</a>
 */
@Service
public class TrainStationService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainStationService.class);
    @Resource
    private TrainStationMapper trainStationMapper;

    public void save(TrainStationSaveReq req) {
        DateTime now = DateTime.now();
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        // notnull,insert
        if (Objects.isNull(trainStation.getId())) {
            // 保存之前，先校验唯一键是否存在
            long count = countUnique(req.getTrainCode(), req.getIndex());
            if (count > 0L) {
                throw new BizException(BaseErrorCodeEnum.BUSINESS_TRAIN_STATION_NAME_ALREADY_EXISTS);
            }
            count = countUnique(req.getTrainCode(), req.getName());
            if (count > 0L) {
                throw new BizException(BaseErrorCodeEnum.BUSINESS_TRAIN_STATION_INDEX_ALREADY_EXISTS);
            }
            trainStation.setId(IdGenUtil.nextId());
            trainStation.setGmtCreate(now);
            trainStation.setGmtModified(now);
            trainStationMapper.insert(trainStation);
        } else {
            // null,update
            trainStation.setGmtModified(now);
            trainStationMapper.updateByPrimaryKeySelective(trainStation);
        }

    }

    private long countUnique(String trainCode, Integer idx) {
        TrainStationExample example = new TrainStationExample();
        example.createCriteria()
                .andTrainCodeEqualTo(trainCode)
                .andIndexEqualTo(idx);
        return trainStationMapper.countByExample(example);
    }

    private long countUnique(String trainCode, String name) {
        TrainStationExample example = new TrainStationExample();
        example.createCriteria()
                .andTrainCodeEqualTo(trainCode)
                .andNameEqualTo(name);
        return trainStationMapper.countByExample(example);
    }

    public void delete(EntityDeleteReq req) {
        // todo 是否是该用户的数据
        trainStationMapper.deleteByPrimaryKey(req.getId());
    }

    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.setOrderByClause("train_code asc, `index` asc");
        String trainCode = req.getTrainCode();
        if (StringUtils.hasText(trainCode)) {
            trainStationExample.createCriteria().
                    andTrainCodeEqualTo(trainCode);
        }
        // 分页请求
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        List<TrainStation> trainStationList = trainStationMapper.selectByExample(trainStationExample);
        // 获取分页
        PageInfo<TrainStation> pageInfo = new PageInfo<>(trainStationList);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        // 封装分页
        List<TrainStationQueryResp> list = BeanUtil.copyToList(pageInfo.getList(), TrainStationQueryResp.class);
        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(total);
        pageResp.setList(list);
        pageResp.setPages(pages);
        LOG.info("[query] pageNo:{},pageSize:{},total:{},pages:{}",
                req.getPageNo(), req.getPageSize(), total, pages);
        return pageResp;
    }

    public List<TrainStation> selectByTrainCode(String trainCode) {
        TrainStationExample example = new TrainStationExample();
        // 按站序升序返回，第一个是起始站，最后一个是重点站
        example.setOrderByClause("`index` asc");
        example.createCriteria().andTrainCodeEqualTo(trainCode);
        return trainStationMapper.selectByExample(example);
    }

    public int countTrainStation(String trainCode) {
        TrainStationExample example = new TrainStationExample();
        example.createCriteria().andTrainCodeEqualTo(trainCode);
        return (int) trainStationMapper.countByExample(example);
    }
}