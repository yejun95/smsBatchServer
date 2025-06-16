package org.batch.smsbatchserver.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.nurigo.sdk.message.model.MessageType;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SmsLog {

    @Id
    private String groupId;
    private String from;
    private String to;
    private MessageType type;
    private String statusMessage;
    private String country;
    private String messageId;
    private String statusCode;
    private String regDate;
}
