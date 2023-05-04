package com.example.amm.domain.vo;

import java.util.List;

import com.example.amm.domain.entity.AccountDO;

import lombok.Data;

@Data
public class AccountVO {

    private AccountDO currentAccount;

    private List<AccountDO> accountDOList;

    private int money;

    private Long nextId;

    private AccountDO fromAccount;

    private AccountDO toAccount;

}
