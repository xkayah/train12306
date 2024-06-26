package com.mnus.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mnus.business.domain.DailyTrainCarriage;
import com.mnus.business.domain.DailyTrainCarriageExample;
import com.mnus.business.domain.TrainCarriage;
import com.mnus.business.domain.TrainStation;
import com.mnus.business.mapper.DailyTrainCarriageMapper;
import com.mnus.business.mapper.TrainCarriageMapper;
import com.mnus.business.req.DailyTrainCarriageQueryReq;
import com.mnus.business.req.DailyTrainCarriageSaveReq;
import com.mnus.business.resp.DailyTrainCarriageQueryResp;
import com.mnus.common.req.EntityDeleteReq;
import com.mnus.common.resp.PageResp;
import com.mnus.common.utils.IdGenUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author: <a href="https://github.com/xkayah">xkayah</a>
 */
@Service
public class DailyTrainCarriageService {
    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);
    @Resource
    private DailyTrainCarriageMapper dailyTrainCarriageMapper;
    @Resource
    private TrainCarriageService trainCarriageService;

    public void save(DailyTrainCarriageSaveReq req) {
        DateTime now = DateTime.now();
        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
        // notnull,insert
        if (Objects.isNull(dailyTrainCarriage.getId())) {
            // todo 保存之前，先校验唯一键是否存在
            dailyTrainCarriage.setId(IdGenUtil.nextId());
            dailyTrainCarriage.setGmtCreate(now);
            dailyTrainCarriage.setGmtModified(now);
            dailyTrainCarriageMapper.insert(dailyTrainCarriage);
        } else {
            // null,update
            // if (!Objects.equals(ReqHolder.getUid(), req.getUserId())) {
            //    throw new BizException(BaseErrorCodeEnum.SYSTEM_USER_CANNOT_UPDATE_OTHER_USER);
            //}
            dailyTrainCarriage.setGmtModified(now);
            dailyTrainCarriageMapper.updateByPrimaryKeySelective(dailyTrainCarriage);
        }
    }

    public void delete(EntityDeleteReq req) {
        // todo 是否是该用户的数据
        dailyTrainCarriageMapper.deleteByPrimaryKey(req.getId());
    }

    public PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req) {
        DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
        DailyTrainCarriageExample.Criteria criteria = dailyTrainCarriageExample.createCriteria();
        String trainCode = req.getTrainCode();
        Date date = req.getDate();
        if (Objects.nonNull(trainCode)) {
            criteria
                    .andTrainCodeEqualTo(trainCode);
        }
        if (Objects.nonNull(date)) {
            criteria
                    .andDateEqualTo(date);
        }
        // 分页请求
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);
        // 获取分页
        PageInfo<DailyTrainCarriage> pageInfo = new PageInfo<>(dailyTrainCarriageList);
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        // 封装分页
        List<DailyTrainCarriageQueryResp> list = BeanUtil.copyToList(pageInfo.getList(), DailyTrainCarriageQueryResp.class);
        PageResp<DailyTrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(total);
        pageResp.setList(list);
        pageResp.setPages(pages);
        LOG.info("[query] pageNo:{},pageSize:{},total:{},pages:{}",
                req.getPageNo(), req.getPageSize(), total, pages);
        return pageResp;
    }

    /**
     * 生成某日期下所有的【日常车厢】信息
     *
     * @param date
     */
    public void genDaily(Date date, String trainCode) {
        // 删除该【日期】该【车次】下的所有【日常车厢】信息
        DailyTrainCarriageExample example = new DailyTrainCarriageExample();
        example.createCriteria()
                .andTrainCodeEqualTo(trainCode)
                .andDateEqualTo(date);
        dailyTrainCarriageMapper.deleteByExample(example);
        // 查某【车次】的所有【车厢】信息
        List<TrainCarriage> trainCarriageList = trainCarriageService.selectByTrainCode(trainCode);
        LOG.info("[Carriage]list size:{}", trainCarriageList.size());
        if (CollUtil.isEmpty(trainCarriageList)) {
            return;
        }
        for (TrainCarriage trainCarriage : trainCarriageList) {
            // 生成该【车次】该【日期】下的【日常车厢】信息
            DateTime now = DateTime.now();
            DailyTrainCarriage record = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriage.class);
            record.setId(IdGenUtil.nextId());
            record.setDate(date);
            record.setGmtCreate(now);
            record.setGmtModified(now);
            dailyTrainCarriageMapper.insert(record);
            LOG.info("[carriage]seat type:{}, idx:{}", trainCarriage.getSeatType(), trainCarriage.getIndex());
        }
    }

    public List<DailyTrainCarriage> select(Date date,String trainCode,String seatType){
        DailyTrainCarriageExample example = new DailyTrainCarriageExample();
        example.setOrderByClause("`date` ASC, `train_code` ASC, `index` ASC");
        example.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andSeatTypeEqualTo(seatType);
        return dailyTrainCarriageMapper.selectByExample(example);
    }
}