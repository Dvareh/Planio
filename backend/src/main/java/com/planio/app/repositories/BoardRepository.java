package com.planio.app.repositories;

import com.planio.app.entity.Board;
import com.planio.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board,Long> {
    public List<Board> findByOwner(User owner);

    public List<Board> findByParticipants_Id(Long id);
}
