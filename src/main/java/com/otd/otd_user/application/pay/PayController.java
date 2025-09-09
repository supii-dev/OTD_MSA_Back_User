package com.otd.otd_user.application.pay;

import com.otd.otd_user.application.pay.model.KakaoPayApproveReq;
import com.otd.otd_user.application.pay.model.KakaoPayApproveRes;
import com.otd.otd_user.application.pay.model.KakaoPayReadyRes;
import com.otd.configuration.model.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("pay")
public class PayController {
    private final PayService payService;

    @PostMapping("ready") //카카오페이 준비 처리
    public ResultResponse<?> postReady() {
        KakaoPayReadyRes result = payService.postReady();
        return new ResultResponse<>("카카오페이 준비 완료", result);
    }

    @GetMapping("approve") //카카오페이 결제승인 처리
    public ResultResponse<KakaoPayApproveRes> getApprove(@ModelAttribute KakaoPayApproveReq req)
            throws IOException {
        log.info("getApprove - req: {}", req);
        KakaoPayApproveRes result = payService.getApprove(req);
        log.info("result: {}", result);
        return new ResultResponse<>("카카오페이 결제 완료", result);
    }
}
