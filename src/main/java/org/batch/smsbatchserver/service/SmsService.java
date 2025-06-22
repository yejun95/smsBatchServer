package org.batch.smsbatchserver.service;

import lombok.extern.slf4j.Slf4j;
import org.batch.smsbatchserver.util.SmsSendUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmsService {

    private final SmsSendUtil smsSendUtil;

    private final int threadCount = 3;

    public SmsService(SmsSendUtil smsSendUtil) {
        this.smsSendUtil = smsSendUtil;
    }

    // 서버 가동 후 의존성 주입받고 실행시키기 위해 @EventListener 선언
    @EventListener(ApplicationReadyEvent.class)
    public void startSendSMS() {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            final int num = i;
            executorService.submit(() -> smsSendUtil.sendOne(num));
        }

        // 리소스 정리
        executorService.shutdown();

        try {
            // 10초 동안 쓰레드가 정상 종료되길 기다림
            //  - 만약 그 안에 다 끝나면 true 반환 → if 조건문의 executorService.shutdownNow(); 실행 안됨
            //  - 하지만 10초가 지나도 작업이 안 끝나면 false → executorService.shutdownNow(); 실행 됨

            // awaitTermination : shutdown() 한 뒤에 진짜로 쓰레드들이 다 종료될 때까지 기다림
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("작업 완료. 애플리케이션 종료.");
        System.exit(0);  // 시스템 종료
    }
}
