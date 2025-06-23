package org.batch.smsbatchserver.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.batch.smsbatchserver.domain.SmsLog;
import org.batch.smsbatchserver.repository.SmsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsSendUtil {

    private final SmsRepository smsRepository;

    private SecureRandom secureRandom;

    DefaultMessageService messageService;

    @Value("${coolSms.api.key}")
    private String key;

    @Value("${coolSms.api.secretKey}")
    private String secretKey;

    @Value("${coolSms.api.url}")
    private String url;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(key, secretKey, url);
    }

    public String createNumberKey() {
        log.warn("createNumberKey...");

        secureRandom = new SecureRandom();

        int randomNumber = 100000 + secureRandom.nextInt(900000);
        log.warn("randomNumber : " + randomNumber);

        return String.valueOf(randomNumber);
    }

    public ResponseEntity<?> sendOne(int num) {
        String numberKey = createNumberKey();

        Message message = new Message();

        message.setFrom("01042139191");
        message.setTo("01042139191");
        message.setText(num + "번째 테스트 sms api 랜덤값: " + numberKey);

        SingleMessageSentResponse r = null;
        try {
            r = this.messageService.sendOne(new SingleMessageSendingRequest(message));

            SmsLog smsLog = SmsLog.builder()
                    .groupId(r.getGroupId())
                    .fromSMS(r.getFrom())
                    .toSMS(r.getTo())
                    .type(r.getType())
                    .statusMessage(r.getStatusMessage())
                    .country(r.getCountry())
                    .messageId(r.getMessageId())
                    .statusCode(r.getStatusCode())
                    .regDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();

            smsRepository.save(smsLog);
            log.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "-> " + smsLog.getFromSMS() + "에서 " + smsLog.getToSMS() + "로 " + num + "번째 전송 완료");
            return ResponseEntity.ok(num + "번째 전송 완료!!");
        } catch(Exception e) {
            log.info(String.valueOf(e));
            UUID uuid = UUID.randomUUID();
            SmsLog smsLog = SmsLog.builder()
                    .groupId(String.valueOf(uuid))
                    .fromSMS(message.getFrom())
                    .toSMS(message.getTo())
                    .type(r != null ? r.getType() : null)
                    .statusMessage(r != null ? r.getStatusMessage() : e.getMessage())
                    .country(r != null ? r.getCountry() : null)
                    .messageId(r != null ? r.getMessageId() : null)
                    .statusCode(r != null ? r.getStatusCode() : "500")
                    .regDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();

            smsRepository.save(smsLog);
            return ResponseEntity.internalServerError().body(num + "번째 전송 실패");
        }
    }


}
