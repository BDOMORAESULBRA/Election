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
	private Long electionId;

	@Column(nullable = false)
	private Long voterId;

	@Column(nullable = false)
	private Long candidateId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getElectionId() {
		return electionId;
	}

	public void setElectionId(Long electionId) {
		this.electionId = electionId;
	}

	public Long getVoterId() {
		return voterId;
	}

	public void setVoterId(Long voterId) {
		this.voterId = voterId;
	}

	public Long getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(Long candidateId) {
		this.candidateId = candidateId;
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
