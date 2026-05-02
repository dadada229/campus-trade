package com.example.graduation_project.controller;

import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.common.UserContext;
import com.example.graduation_project.entity.Address;
import com.example.graduation_project.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public Result<Void> addAddress(@RequestBody Address address) {
        Long userId = UserContext.getUserId();
        addressService.addAddress(userId, address);
        return ResultGenerator.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        Long userId = UserContext.getUserId();
        address.setId(id);
        addressService.updateAddress(userId, address);
        return ResultGenerator.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        addressService.deleteAddress(userId, id);
        return ResultGenerator.success();
    }

    @GetMapping
    public Result<List<Address>> getAddressList() {
        Long userId = UserContext.getUserId();
        List<Address> addressList = addressService.getAddressList(userId);
        return ResultGenerator.success(addressList);
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        addressService.setDefaultAddress(userId, id);
        return ResultGenerator.success();
    }

}
