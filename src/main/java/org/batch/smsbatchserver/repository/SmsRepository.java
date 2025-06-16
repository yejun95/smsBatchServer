package org.batch.smsbatchserver.repository;

import org.batch.smsbatchserver.domain.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsRepository extends JpaRepository<SmsLog, String> {
}
