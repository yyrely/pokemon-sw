package com.chuncongcong.sw.biz.service;

import com.chuncongcong.framework.exception.ServiceException;

/** Shared verification for the item entry and its dedicated image upload endpoint. */
public final class ItemEntryVerifier {
    private static final String VERIFICATION_CODE = "a1314257";

    private ItemEntryVerifier() {
    }

    public static void verify(String verificationCode) {
        if (!VERIFICATION_CODE.equals(verificationCode)) {
            throw new ServiceException(403, "验证码不正确");
        }
    }
}
