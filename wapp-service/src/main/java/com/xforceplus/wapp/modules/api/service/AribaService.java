package com.xforceplus.wapp.modules.api.service;

import com.xforceplus.wapp.modules.api.entity.AribaCheckEntity;
import com.xforceplus.wapp.modules.api.entity.AribaCheckReturn;

import java.text.ParseException;

/**
 * TODO
 *
 * @atuthor wyman
 * @date 2020-05-06 11:37
 **/
public interface AribaService {
    AribaCheckReturn check(AribaCheckEntity aribaCheckEntity) throws ParseException;

    void saveRequest(String s, String auth, String sessionAuth, String response);

    AribaCheckReturn signInMark(AribaCheckEntity aribaCheckEntity);

    AribaCheckReturn auth(AribaCheckEntity aribaCheckEntity);

    AribaCheckReturn upload(AribaCheckEntity aribaCheckEntity);

    void check();
}
