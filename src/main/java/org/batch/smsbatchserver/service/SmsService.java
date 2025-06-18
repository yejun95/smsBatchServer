package org.batch.smsbatchserver.service;

import lombok.extern.slf4j.Slf4j;
import org.batch.smsbatchserver.util.SmsSendUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        // 쓰레드 풀에서 3개 생성
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // 람다식 버전
        for (int i = 1; i <= 1; i++) {
            // 익명클래스가 수행을 마쳤음에도 지역변수를 참조할 수 있기 때문에 final 선언 -> JVM constant pool에서 생명주기가 관리됨
            final int num = i;
            executorService.submit(() -> {
                smsSendUtil.sendOne(num);
            });
        }

        /** 람다식 안쓴 버전
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                smsSendUtil.sendOne(num);
            }
        }); **/

        // 리소스 정리
        executorService.shutdown();
    }
}
