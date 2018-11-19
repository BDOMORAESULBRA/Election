package br.edu.ulbra.election.election.repository;

import br.edu.ulbra.election.election.model.Vote2;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote2, Long> {
}
