package com.otd.otd_user.application.pass;

import com.otd.otd_user.application.pass.model.PassAuthResult;
import com.otd.otd_user.application.pass.model.PassAuthVerifyReq;
import com.otd.configuration.model.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/OTD/pass")
@RequiredArgsConstructor
public class PassController {
    private final PassService passService;

    @PostMapping("/verify")
    public ResultResponse<?> verifyPassAuth(@RequestBody PassAuthVerifyReq req) {
        PassAuthResult result = passService.verifyPassAuth(req);
        return new ResultResponse<>("PASS 인증 확인 완료", result);
    }
}
