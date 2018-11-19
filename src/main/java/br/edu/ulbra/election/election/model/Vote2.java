package br.edu.ulbra.election.election.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.repository.VoteRepository;

@Entity
public class Vote2 {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long voter_Id;

	@Column(nullable = false)
	private Long candidate_Id;

	@Column(nullable = false)
	private Boolean blankVote;

	@Column(nullable = false)
	private Boolean nullVote;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Election election;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVoterId() {
		return voter_Id;
	}

	public void setVoterId(Long voter_Id) {
		this.voter_Id = voter_Id;
	}

	public Long getCandidateId() {
		return candidate_Id;
	}

	public void setCandidateId(Long candidate_Id) {
		this.candidate_Id = candidate_Id;
	}

	public Boolean getBlankVote() {
		return blankVote;
	}

	public void setBlankVote(Boolean blankVote) {
		this.blankVote = blankVote;
	}

	public Boolean getNullVote() {
		return nullVote;
	}

	public void setNullVote(Boolean nullVote) {
		this.nullVote = nullVote;
	}

	public Election getElection() {
		return election;
	}

	public void setElection(Election election) {
		this.election = election;
	}
	
	public static boolean verificaVoto(VoteInput voteInput, VoteRepository voteRepository) {

		Long idTeste = voteInput.getVoterId();
		Long eTeste = voteInput.getElectionId();

		Iterable<Vote2> list = voteRepository.findAll();

		for (Vote2 v : list) {
			if (v.getVoterId().equals(idTeste) && eTeste.equals(v.getElection().getId())) {
				return true;
			}
		}
		return false;

	}

}
