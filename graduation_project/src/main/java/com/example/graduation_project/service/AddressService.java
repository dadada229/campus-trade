package com.example.graduation_project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduation_project.entity.Address;

import java.util.List;

public interface AddressService extends IService<Address> {

    void addAddress(Long userId, Address address);

    void updateAddress(Long userId, Address address);

    void deleteAddress(Long userId, Long addressId);

    List<Address> getAddressList(Long userId);

    void setDefaultAddress(Long userId, Long addressId);

}
