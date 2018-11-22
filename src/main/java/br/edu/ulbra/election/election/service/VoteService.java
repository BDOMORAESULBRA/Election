package br.edu.ulbra.election.election.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.client.VoterClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;

@Service
public class VoteService {

	private final VoteRepository voteRepository;

	private final ElectionRepository electionRepository;

	private final VoterClientService voterClientService;

	private final CandidateClientService candidateClientService;

	@Autowired
	public VoteService(VoteRepository voteRepository, ElectionRepository electionRepository,
			VoterClientService voterClientService, CandidateClientService candidateClientService) {
		this.voteRepository = voteRepository;
		this.electionRepository = electionRepository;
		this.voterClientService = voterClientService;
		this.candidateClientService = candidateClientService;
	}

	public GenericOutput electionVote(VoteInput voteInput) {

		Vote vote = new Vote();
		validateInput(vote, voteInput);

		if (voteInput.getCandidateId() == null) {
			vote.setBlankVote(true);
		} else {
			vote.setBlankVote(false);
		}

		voteRepository.save(vote);

		return new GenericOutput("OK");
	}

	public GenericOutput multiple(List<VoteInput> voteInputList) {
		for (VoteInput voteInput : voteInputList) {
			this.electionVote(voteInput);
		}
		return new GenericOutput("OK");
	}

	private void validateInput(Vote vote, VoteInput voteInput) {

		if (Vote.verificaVoto(voteInput, voteRepository)) {
			throw new GenericOutputException("Vote used");
		}

		Election election = electionRepository.findById(voteInput.getElectionId()).orElse(null);
		if (election == null) {
			throw new GenericOutputException("Invalid Election");
		} else {
			vote.setElection(election);
		}

		try {
			voterClientService.getById(voteInput.getVoterId());
			vote.setVoterId(voteInput.getVoterId());
		} catch (FeignException e) {
			if (e.status() == 500) {
				throw new GenericOutputException("Invalid Voter");
			}
		}

		try {
			candidateClientService.verificaNumero(voteInput.getCandidateId(), voteInput.getElectionId());
			vote.setNullVote(false);
		} catch (FeignException e) {
			if (e.status() == 500) {
				vote.setNullVote(true);
			} else {
				throw new GenericOutputException("Error");
			}
		}

	}

	public Boolean verificaVoter(Long voterId) {

		Vote vote = voteRepository.findFirstByVoterId(voterId);

		if (vote != null) {
			return true;
		} else {
			return false;
		}
	}
}
