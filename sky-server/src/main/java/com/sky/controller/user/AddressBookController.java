package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端地址簿接口
 */
@RestController
@RequestMapping("/user/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /** 查询当前登录用户的全部地址 */
    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        return Result.success(addressBookService.list(new AddressBook()));
    }

    /** 新增地址 */
    @PostMapping
    public Result<String> save(@RequestBody AddressBook addressBook) {
        log.info("新增地址：{}", addressBook);
        addressBookService.save(addressBook);
        return Result.success();
    }

    /** 根据 id 查询地址 */
    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable("id") Long id) {
        return Result.success(addressBookService.getById(id));
    }

    /** 根据 id 修改地址 */
    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook) {
        log.info("修改地址：{}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /** 根据 id 删除地址 */
    @DeleteMapping
    public Result<String> deleteById(@RequestParam("id") Long id) {
        log.info("删除地址：id={}", id);
        addressBookService.deleteById(id);
        return Result.success();
    }

    /** 设置默认地址 */
    @PutMapping("/default")
    public Result<String> setDefault(@RequestBody AddressBook addressBook) {
        log.info("设置默认地址：id={}", addressBook.getId());
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /** 查询当前登录用户的默认地址 */
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        AddressBook condition = AddressBook.builder()
                .isDefault(1)
                .build();
        List<AddressBook> list = addressBookService.list(condition);
        if (list != null && !list.isEmpty()) {
            return Result.success(list.get(0));
        }
        return Result.error("没有查询到默认地址");
    }
}
