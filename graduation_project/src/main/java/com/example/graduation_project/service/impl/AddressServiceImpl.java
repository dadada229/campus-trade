package com.example.graduation_project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.graduation_project.entity.Address;
import com.example.graduation_project.mapper.AddressMapper;
import com.example.graduation_project.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public void addAddress(Long userId, Address address) {
        log.info("添加收货地址: userId={}, address={}", userId, address);
        address.setUserId(userId);
        
        // 处理默认地址标记
        if (address.getIsDefault() == null || address.getIsDefault() != 1) {
            address.setIsDefault(0);
        }
        
        // 如果设为默认，先清除其他地址的默认标记
        if (address.getIsDefault() == 1) {
            removeDefault(userId);
        }
        
        save(address);
        log.info("添加收货地址成功: addressId={}", address.getId());
    }

    @Override
    public void updateAddress(Long userId, Address address) {
        log.info("更新收货地址: userId={}, addressId={}", userId, address.getId());
        Address exist = getById(address.getId());
        if (exist == null || !exist.getUserId().equals(userId)) {
            log.warn("地址不存在或无权限: addressId={}", address.getId());
            return;
        }

        // 处理默认地址标记
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            removeDefault(userId);
        }
        
        address.setUserId(userId);
        updateById(address);
        log.info("更新收货地址成功");
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        log.info("删除收货地址: userId={}, addressId={}", userId, addressId);
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getId, addressId)
                .eq(Address::getUserId, userId);
        remove(wrapper);
        log.info("删除收货地址成功");
    }

    @Override
    public List<Address> getAddressList(Long userId) {
        log.info("获取收货地址列表: userId={}", userId);
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);
        wrapper.orderByDesc(Address::getIsDefault);
        wrapper.orderByDesc(Address::getCreatedAt);
        List<Address> addressList = list(wrapper);
        log.info("获取收货地址列表成功: count={}", addressList.size());
        return addressList;
    }

    @Override
    public void setDefaultAddress(Long userId, Long addressId) {
        log.info("设置默认收货地址: userId={}, addressId={}", userId, addressId);
        removeDefault(userId);
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getId, addressId)
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 1);
        update(wrapper);
        log.info("设置默认收货地址成功");
    }

    private void removeDefault(Long userId) {
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .set(Address::getIsDefault, 0);
        update(wrapper);
    }

}
