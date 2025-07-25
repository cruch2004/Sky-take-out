package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    @Override
    @Transactional // 开启事务保持事务的一致性
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        // 向套餐表插入数据
        setmealMapper.insert(setmeal);
        // 获取生成的套餐id
        Long setmealId = setmeal.getId();
        // 新增套餐中的菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        // 保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        int pageNum = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();
        // 开始分页
        PageHelper.startPage(pageNum, pageSize);
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        PageResult result = new PageResult(page.getTotal(), page.getResult());
        return result;
    }

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @Override
    @Transactional // 保持事务的一致性
    public void deleteBatch(List<Long> ids) {
        if (!ids.isEmpty()){
            // 判断删除的套餐中是否存在起售状态的套餐
            ids.forEach(setmealId -> {
                Setmeal setmeal = setmealMapper.getById(setmealId);
                if (setmeal.getStatus() == StatusConstant.ENABLE){
                    throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
                }
//            // 删除套餐表中的数据
//            setmealMapper.deleteById(setmealId);
//            // 删除套餐菜品表中的数据
//            setmealDishMapper.deleteBySetmealId(setmealId);
            });

            // 批量删除套餐表中的数据
            setmealMapper.deleteBatchByIds(ids);

            // 批量删除套餐菜品表中的数据
            setmealDishMapper.deleteBatchBySetmealIds(ids);
        }
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        // 根据套餐id查询套餐信息
        Setmeal setmeal = setmealMapper.getById(id);
        // 根据套餐id查询套餐关联的菜品信息
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO = SetmealVO.builder()
                .setmealDishes(setmealDishes)
                .build();
        BeanUtils.copyProperties(setmeal,setmealVO);
        log.info("setmealVO: {}",setmealVO);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 修改套餐表中的数据
        setmealMapper.update(setmeal);

        // 删除原套餐菜品表关联的菜品数据
        Long setmealId = setmeal.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        // 添加新更改的套餐菜品数据
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 套餐起售、停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 根据套餐的id设置套餐的售卖状态
        setmealMapper.startOrStop(status,id);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
