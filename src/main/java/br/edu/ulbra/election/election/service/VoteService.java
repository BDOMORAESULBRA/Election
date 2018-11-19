package br.edu.ulbra.election.election.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.client.VoterClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote2;
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
			VoterClientService voterClientService, ModelMapper modelMapper,
			CandidateClientService candidateClientService) {
		this.voteRepository = voteRepository;
		this.electionRepository = electionRepository;
		this.voterClientService = voterClientService;
		this.candidateClientService = candidateClientService;
	}

	public GenericOutput electionVote(VoteInput voteInput) {

		Vote2 vote = new Vote2();
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

	private void validateInput(Vote2 vote, VoteInput voteInput) {

		if (Vote2.verificaVoto(voteInput, voteRepository)) {
			throw new GenericOutputException("Vote used");
		}

		Election election = electionRepository.findById(voteInput.getElectionId()).orElse(null);
		if (election == null) {
			throw new GenericOutputException("Invalid Election");
		} else {
			vote.setElection(election);
		}

		try {
			if (voterClientService.getById(voteInput.getVoterId()) != null) {
				vote.setVoterId(voteInput.getVoterId());
			}

		} catch (FeignException e) {
			if (e.status() == 500) {
				throw new GenericOutputException("Invalid Voter");
			}
		}

		try {

			if (candidateClientService.verificaNumero(vote.getCandidateId()) != null) {
				vote.setNullVote(false);
			} else {
				vote.setNullVote(true);
			}

		} catch (FeignException e) {
			if (e.status() == 500) {
				vote.setNullVote(true);
			}
		}

	}

	public Boolean verificaVoter(Long voterId) {

		Iterable<Vote2> list = voteRepository.findAll();

		for (Vote2 v : list) {
			if (v.getVoterId().equals(voterId)) {
				return true;
			}
		}
		return false;
	}
}
