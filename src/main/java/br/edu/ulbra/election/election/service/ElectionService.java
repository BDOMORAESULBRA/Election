package br.edu.ulbra.election.election.service;

import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.model.Election;
import br.edu.ulbra.election.election.model.Vote2;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.input.v1.ElectionInput;
import br.edu.ulbra.election.election.output.v1.ElectionOutput;
import br.edu.ulbra.election.election.output.v1.GenericOutput;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElectionService {

	private final ElectionRepository electionRepository;

	private final ModelMapper modelMapper;

	private final VoteRepository voteRepository;

	private final CandidateClientService candidateClientService;

	private static final String MESSAGE_INVALID_ID = "Invalid id";
	private static final String MESSAGE_INVALID_YEAR = "Invalid year";
	private static final String MESSAGE_ELECTION_NOT_FOUND = "Election not found";

	@Autowired
	public ElectionService(ElectionRepository electionRepository, ModelMapper modelMapper,
			VoteRepository voteRepository, CandidateClientService candidateClientService) {
		this.electionRepository = electionRepository;
		this.modelMapper = modelMapper;
		this.voteRepository = voteRepository;
		this.candidateClientService = candidateClientService;
	}

	public List<ElectionOutput> getAll() {
		Type electionOutputListType = new TypeToken<List<ElectionOutput>>() {
		}.getType();
		return modelMapper.map(electionRepository.findAll(), electionOutputListType);
	}

	public List<ElectionOutput> getByYear(Integer year) {

		if (year == null) {
			throw new GenericOutputException(MESSAGE_INVALID_YEAR);
		}

		Type electionOutputListType = new TypeToken<List<ElectionOutput>>() {
		}.getType();

		Iterable<Election> list = electionRepository.findAll();

		ArrayList<ElectionOutput> lista = new ArrayList<>();

		ElectionOutput x = new ElectionOutput();

		for (Election e : list) {
			x = Election.verifica(e, year);
			if (x != null) {
				lista.add(x);
			}
		}

		return modelMapper.map(lista, electionOutputListType);
	}

	public ElectionOutput create(ElectionInput electionInput) {
		validateInput(electionInput);
		Election election = modelMapper.map(electionInput, Election.class);
		election = electionRepository.save(election);
		return modelMapper.map(election, ElectionOutput.class);
	}

	public ElectionOutput getById(Long electionId) {
		if (electionId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		Election election = electionRepository.findById(electionId).orElse(null);
		if (election == null) {
			throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
		}

		return modelMapper.map(election, ElectionOutput.class);
	}

	public ElectionOutput update(Long electionId, ElectionInput electionInput) {
		if (electionId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}
		validateInput(electionInput);

		verificaVote(electionId);
		verificaCandidate(electionId);

		Election election = electionRepository.findById(electionId).orElse(null);
		if (election == null) {
			throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
		}

		election.setYear(electionInput.getYear());
		election.setDescription(electionInput.getDescription());
		election.setStateCode(electionInput.getStateCode());
		election = electionRepository.save(election);
		return modelMapper.map(election, ElectionOutput.class);
	}

	public GenericOutput delete(Long electionId) {
		if (electionId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		verificaVote(electionId);
		verificaCandidate(electionId);

		Election election = electionRepository.findById(electionId).orElse(null);
		if (election == null) {
			throw new GenericOutputException(MESSAGE_ELECTION_NOT_FOUND);
		}

		electionRepository.delete(election);

		return new GenericOutput("Election deleted");
	}

	private void validateInput(ElectionInput electionInput) {
		if (StringUtils.isBlank(electionInput.getStateCode())
				|| Election.verificaEstado(electionInput.getStateCode()) == false) {
			throw new GenericOutputException("Invalid state code");
		}
		if (StringUtils.isBlank(electionInput.getDescription())
				|| electionInput.getDescription().trim().replace(" ", "").length() < 5) {
			throw new GenericOutputException("Invalid description");
		}
		if (electionInput.getYear() == null || electionInput.getYear() < 2000 || electionInput.getYear() >= 2200) {
			throw new GenericOutputException("Invalid year");
		}

	}

	public void verificaVote(Long electionId) {

		Iterable<Vote2> list = voteRepository.findAll();

		for (Vote2 v : list) {
			if (v.getElection().getId().equals(electionId)) {
				throw new GenericOutputException("Exists votes!");
			}
		}
	}

	public Boolean verificaVoteForCandidate(Long electionId) {

		try {
			verificaVote(electionId);
			return true;
		} catch (GenericOutputException e) {
			return false;
		}
	}

	private void verificaCandidate(Long electionId) {

		if (candidateClientService.verificaElection(electionId) != null) {
			throw new GenericOutputException("Exists candidates!");
		}
	}

}
