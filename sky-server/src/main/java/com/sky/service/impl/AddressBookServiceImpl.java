package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.exception.AddressBookBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址簿业务实现类
 */
@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        return addressBookMapper.list(addressBook);
    }

    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    @Override
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressBookMapper.getByIdAndUserId(
                id, BaseContext.getCurrentId());
        if (addressBook == null) {
            throw new AddressBookBusinessException("地址不存在");
        }
        return addressBook;
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        // 是否默认只能通过专用接口修改，编辑地址时保持原状态。
        addressBook.setIsDefault(null);
        if (addressBookMapper.update(addressBook) == 0) {
            throw new AddressBookBusinessException("地址不存在");
        }
    }

    @Override
    @Transactional
    public void setDefault(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();

        // 先确认目标地址属于当前用户，避免清空默认地址后才发现目标无效。
        if (addressBookMapper.getByIdAndUserId(addressBook.getId(), userId) == null) {
            throw new AddressBookBusinessException("地址不存在");
        }

        AddressBook resetCondition = AddressBook.builder()
                .userId(userId)
                .isDefault(0)
                .build();
        addressBookMapper.updateIsDefaultByUserId(resetCondition);

        AddressBook defaultAddress = AddressBook.builder()
                .id(addressBook.getId())
                .userId(userId)
                .isDefault(1)
                .build();
        addressBookMapper.update(defaultAddress);
    }

    @Override
    public void deleteById(Long id) {
        if (addressBookMapper.deleteByIdAndUserId(id, BaseContext.getCurrentId()) == 0) {
            throw new AddressBookBusinessException("地址不存在");
        }
    }
}
