package com.hmdp.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.Result;
import com.hmdp.entity.Voucher;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 定义优惠券相关业务服务接口。
 *
 * @author wdk
 */
public interface IVoucherService extends IService<Voucher> {

    /**
     * 查询指定商铺下的优惠券列表。
     */
    Result queryVoucherOfShop(Long shopId);

    /**
     * 新增秒杀优惠券及其库存信息。
     */
    void addSeckillVoucher(Voucher voucher);

    @Override
    boolean saveBatch(Collection<Voucher> entityList, int batchSize);

    @Override
    boolean saveOrUpdateBatch(Collection<Voucher> entityList, int batchSize);

    @Override
    boolean updateBatchById(Collection<Voucher> entityList, int batchSize);

    @Override
    boolean saveOrUpdate(Voucher entity);

    @Override
    Voucher getOne(Wrapper<Voucher> queryWrapper, boolean throwEx);

    @Override
    Map<String, Object> getMap(Wrapper<Voucher> queryWrapper);

    @Override
    <V> V getObj(Wrapper<Voucher> queryWrapper, Function<? super Object, V> mapper);

    @Override
    BaseMapper<Voucher> getBaseMapper();

    @Override
    Class<Voucher> getEntityClass();

    @Override
    default boolean save(Voucher entity) {
        return IService.super.save(entity);
    }

    @Override
    default boolean saveBatch(Collection<Voucher> entityList) {
        return IService.super.saveBatch(entityList);
    }

    @Override
    default boolean saveOrUpdateBatch(Collection<Voucher> entityList) {
        return IService.super.saveOrUpdateBatch(entityList);
    }

    @Override
    default boolean removeById(Serializable id) {
        return IService.super.removeById(id);
    }

    @Override
    default boolean removeByMap(Map<String, Object> columnMap) {
        return IService.super.removeByMap(columnMap);
    }

    @Override
    default boolean remove(Wrapper<Voucher> queryWrapper) {
        return IService.super.remove(queryWrapper);
    }

    @Override
    default boolean removeByIds(Collection<? extends Serializable> idList) {
        return IService.super.removeByIds(idList);
    }

    @Override
    default boolean updateById(Voucher entity) {
        return IService.super.updateById(entity);
    }

    @Override
    default boolean update(Wrapper<Voucher> updateWrapper) {
        return IService.super.update(updateWrapper);
    }

    @Override
    default boolean update(Voucher entity, Wrapper<Voucher> updateWrapper) {
        return IService.super.update(entity, updateWrapper);
    }

    @Override
    default boolean updateBatchById(Collection<Voucher> entityList) {
        return IService.super.updateBatchById(entityList);
    }

    @Override
    default Voucher getById(Serializable id) {
        return IService.super.getById(id);
    }

    @Override
    default List<Voucher> listByIds(Collection<? extends Serializable> idList) {
        return IService.super.listByIds(idList);
    }

    @Override
    default List<Voucher> listByMap(Map<String, Object> columnMap) {
        return IService.super.listByMap(columnMap);
    }

    @Override
    default Voucher getOne(Wrapper<Voucher> queryWrapper) {
        return IService.super.getOne(queryWrapper);
    }

    @Override
    default int count() {
        return IService.super.count();
    }

    @Override
    default int count(Wrapper<Voucher> queryWrapper) {
        return IService.super.count(queryWrapper);
    }

    @Override
    default List<Voucher> list(Wrapper<Voucher> queryWrapper) {
        return IService.super.list(queryWrapper);
    }

    @Override
    default List<Voucher> list() {
        return IService.super.list();
    }

    @Override
    default <E extends IPage<Voucher>> E page(E page, Wrapper<Voucher> queryWrapper) {
        return IService.super.page(page, queryWrapper);
    }

    @Override
    default <E extends IPage<Voucher>> E page(E page) {
        return IService.super.page(page);
    }

    @Override
    default List<Map<String, Object>> listMaps(Wrapper<Voucher> queryWrapper) {
        return IService.super.listMaps(queryWrapper);
    }

    @Override
    default List<Map<String, Object>> listMaps() {
        return IService.super.listMaps();
    }

    @Override
    default List<Object> listObjs() {
        return IService.super.listObjs();
    }

    @Override
    default <V> List<V> listObjs(Function<? super Object, V> mapper) {
        return IService.super.listObjs(mapper);
    }

    @Override
    default List<Object> listObjs(Wrapper<Voucher> queryWrapper) {
        return IService.super.listObjs(queryWrapper);
    }

    @Override
    default <V> List<V> listObjs(Wrapper<Voucher> queryWrapper, Function<? super Object, V> mapper) {
        return IService.super.listObjs(queryWrapper, mapper);
    }

    @Override
    default <E extends IPage<Map<String, Object>>> E pageMaps(E page, Wrapper<Voucher> queryWrapper) {
        return IService.super.pageMaps(page, queryWrapper);
    }

    @Override
    default <E extends IPage<Map<String, Object>>> E pageMaps(E page) {
        return IService.super.pageMaps(page);
    }

    @Override
    default QueryChainWrapper<Voucher> query() {
        return IService.super.query();
    }

    @Override
    default LambdaQueryChainWrapper<Voucher> lambdaQuery() {
        return IService.super.lambdaQuery();
    }

    @Override
    default KtQueryChainWrapper<Voucher> ktQuery() {
        return IService.super.ktQuery();
    }

    @Override
    default KtUpdateChainWrapper<Voucher> ktUpdate() {
        return IService.super.ktUpdate();
    }

    @Override
    default UpdateChainWrapper<Voucher> update() {
        return IService.super.update();
    }

    @Override
    default LambdaUpdateChainWrapper<Voucher> lambdaUpdate() {
        return IService.super.lambdaUpdate();
    }

    @Override
    default boolean saveOrUpdate(Voucher entity, Wrapper<Voucher> updateWrapper) {
        return IService.super.saveOrUpdate(entity, updateWrapper);
    }
}
