package edu.uclm.esi.esimedia.be_esimedia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import edu.uclm.esi.esimedia.be_esimedia.exceptions.AudioUploadException;
import edu.uclm.esi.esimedia.be_esimedia.model.Contenido;
import edu.uclm.esi.esimedia.be_esimedia.repository.ContenidoRepository;

@Service
public class ContenidoSchedulerService {

    private final Logger logger = LoggerFactory.getLogger(ContenidoSchedulerService.class);

    private final ContenidoRepository contenidoRepository;

    @Autowired
    public ContenidoSchedulerService(ContenidoRepository contenidoRepository) {
        this.contenidoRepository = contenidoRepository;
    }

    // Se ejecuta cada 24 horas
    @Scheduled(fixedRate = 86400000)
    public void checkVisibilityDeadlines() {
        Instant now = Instant.now();
        
        // Encuentra contenidos visibles cuyo deadline ha pasado
        List<Contenido> contenidos = contenidoRepository
                        .findByVisibleTrueAndVisibilityDeadlineNotNullAndVisibilityDeadlineBefore(now);
        
        if (contenidos.isEmpty()) {
            return;
        }

        contenidos.forEach(contenido -> {
            contenido.setVisible(false);
            contenido.setVisibilityChangeDate(now);
        });
        
        try {
            contenidoRepository.saveAll(contenidos);
            logger.info("Contenido guardado exitosamente con IDs: {}", contenidos.stream().map(Contenido::getId).toList());
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al guardar el contenido en la base de datos: {}", e.getMessage(), e);
            throw new AudioUploadException();
        }
    }
}