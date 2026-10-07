package com.planio.app.repositories;

import com.planio.app.entity.Board;
import com.planio.app.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board,Long> {
    public List<Board> findByOwner(User owner);

    public List<Board> findByParticipants_Id(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT b
    FROM Board b
    WHERE b.id = :id
    """)
    Optional<Board> findByIdForUpdate(
            @Param("id") Long id
    );
}
