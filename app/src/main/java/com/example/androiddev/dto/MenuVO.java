package com.example.androiddev.dto;

import com.example.androiddev.common.CommonEnum;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class MenuVO {
    private CommonEnum.MenuCode code;
    private String title;

    public MenuVO(CommonEnum.MenuCode code, String title) {
        this.code  = code;
        this.title = title;
    }
}