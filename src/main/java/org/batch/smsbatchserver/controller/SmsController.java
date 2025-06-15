package org.batch.smsbatchserver.controller;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

@Slf4j
@RestController
public class SmsController {

    private SecureRandom secureRandom;

    final DefaultMessageService messageService;

    public SmsController(
            @Value("${coolSms.api.key}") String key,
            @Value("${coolSms.api.secretKey}") String secretKey,
            @Value("${coolSms.api.url}") String url
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(key, secretKey, url);
    }

    public String createNumberKey() {
        log.warn("createNumberKey...");

        secureRandom = new SecureRandom();

        int randomNumber = 100000 + secureRandom.nextInt(900000);
        log.warn("randomNumber : " + randomNumber);

        return String.valueOf(randomNumber);
    }

    @PostMapping("/send-one")
    public SingleMessageSentResponse sendOne() {
        String numberKey = createNumberKey();

        Message message = new Message();

        message.setFrom("01042139191");
        message.setTo("01042139191");
        message.setText("테스트 sms api 랜덤값: " + numberKey);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);

        return response;
    }
}
