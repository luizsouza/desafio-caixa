package br.gov.caixa.painelinvestimentos.repository;

import br.gov.caixa.painelinvestimentos.model.entity.TelemetriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TelemetriaRepository extends JpaRepository<TelemetriaEntity, Long> {

    // Busca todas as entradas de telemetria entre dois timestamps.
    
    List<TelemetriaEntity> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fim);
}
