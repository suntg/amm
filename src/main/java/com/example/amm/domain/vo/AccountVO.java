package com.example.amm.domain.vo;


import com.example.amm.domain.entity.AccountDO;
import com.example.amm.domain.entity.TaskDO;
import lombok.Data;

import java.util.List;

@Data
public class AccountVO {


    private AccountDO currentAccount;

    private List<AccountDO> accountDOList;

    private int money;

    private Long nextId;




    private AccountDO fromAccount;

    private AccountDO toAccount;

}
