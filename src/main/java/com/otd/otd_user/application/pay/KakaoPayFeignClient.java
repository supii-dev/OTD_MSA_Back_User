package com.otd.otd_user.application.pay;



import com.otd.otd_user.application.pay.model.KakaoPayApproveFeignReq;
import com.otd.otd_user.application.pay.model.KakaoPayApproveRes;
import com.otd.otd_user.application.pay.model.KakaoPayReadyFeignReq;
import com.otd.otd_user.application.pay.model.KakaoPayReadyRes;
import com.otd.configuration.feignclient.KakaoPayClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "kakaoPayApi"
           , url = "${constants.pay.kakao.base-url}"
           , configuration = { KakaoPayClientConfiguration.class })
public interface KakaoPayFeignClient {

    @PostMapping(value = "/ready")
    KakaoPayReadyRes postReady(@RequestBody KakaoPayReadyFeignReq req);

    @PostMapping(value = "/approve")
    KakaoPayApproveRes postApprove(KakaoPayApproveFeignReq req);
}
