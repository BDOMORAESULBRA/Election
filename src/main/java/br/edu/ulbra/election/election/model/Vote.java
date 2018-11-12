package br.edu.ulbra.election.election.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.repository.VoteRepository;

@Entity
public class Vote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long election_Id;

	@Column(nullable = false)
	private Long voter_Id;

	@Column(nullable = false)
	private Long candidate_Id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getElectionId() {
		return election_Id;
	}

	public void setElectionId(Long electionId) {
		this.election_Id = electionId;
	}

	public Long getVoterId() {
		return voter_Id;
	}

	public void setVoterId(Long voterId) {
		this.voter_Id = voterId;
	}

	public Long getCandidateId() {
		return candidate_Id;
	}

	public void setCandidateId(Long candidateId) {
		this.candidate_Id = candidateId;
	}

	public static boolean verificaVoto(VoteInput voteInput, VoteRepository voteRepository) {

		Long idTeste = voteInput.getVoterId();
		Long eTeste = voteInput.getElectionId();

		Iterable<Vote> list = voteRepository.findAll();

		for (Vote v : list) {
			if (v.getVoterId().equals(idTeste) && eTeste.equals(v.getElectionId())) {
				return true;
			}
		}
		return false;

	}

}
