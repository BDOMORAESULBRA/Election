package br.edu.ulbra.election.election.service;

import java.lang.reflect.Type;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.VoteInput;
import br.edu.ulbra.election.election.model.Vote;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import br.edu.ulbra.election.election.output.v1.VoteOutput;
import br.edu.ulbra.election.election.repository.VoteRepository;

@Service
public class VoteService {

	private final VoteRepository voteRepository;

	private final ModelMapper modelMapper;

	@Autowired
	public VoteService(VoteRepository voteRepository, ModelMapper modelMapper) {
		this.voteRepository = voteRepository;
		this.modelMapper = modelMapper;
	}

	public List<VoteOutput> getAll() {
		Type voteOutputListType = new TypeToken<List<VoteOutput>>() {
		}.getType();
		return modelMapper.map(voteRepository.findAll(), voteOutputListType);
	}

	public GenericOutput create(VoteInput voteInput) {
		validateInput(voteInput);
		Vote vote = modelMapper.map(voteInput, Vote.class);
		vote = voteRepository.save(vote);
		return new GenericOutput("Vote created");
	}

	private void validateInput(VoteInput voteInput) {
		if (voteInput.getElectionId() == null) {
			throw new GenericOutputException("Invalid election id");
		}else {
			if(Vote.verificaVoto(voteInput, voteRepository)) {
				throw new GenericOutputException("Vote used");
			}
		}
		if (voteInput.getVoterId() == null) {
			throw new GenericOutputException("Invalid voter id");
		}
		if (voteInput.getCandidateId() < 0) {
			throw new GenericOutputException("Invalid candidate id");
		}

	}

}
