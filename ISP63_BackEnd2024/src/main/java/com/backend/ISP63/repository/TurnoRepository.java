package com.backend.ISP63.repository;

import com.backend.ISP63.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    @Query
    List<Turno> findByTurno(String turno);

    //turnos mas recientes
    //List<Turno> findAllByOrderByTurnoDesc();


}