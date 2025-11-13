package edu.uclm.esi.esimedia.be_esimedia.services;

import java.time.Instant;

import org.springframework.stereotype.Component;

import edu.uclm.esi.esimedia.be_esimedia.model.Log;
import edu.uclm.esi.esimedia.be_esimedia.repository.LogRepository;

@Component
public class LogComponent {
    private final LogRepository logRepository;

    public LogComponent(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void log(String user, String ip, String endpoint, String description) {
        Log log = new Log(user, ip, endpoint, description);
        log.setTimestamp(Instant.now());
        logRepository.save(log);
    }
}
