package com.planio.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.planio.app.entity.Label;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    List<Label> findByBoard_IdOrderByNameAsc(Long boardId);

    boolean existsByBoard_IdAndNameIgnoreCase(
            Long boardId,
            String name
    );
}