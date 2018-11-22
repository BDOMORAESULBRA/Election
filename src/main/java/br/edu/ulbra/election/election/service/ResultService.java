package br.edu.ulbra.election.election.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ulbra.election.election.client.CandidateClientService;
import br.edu.ulbra.election.election.client.VoterClientService;
import br.edu.ulbra.election.election.exception.GenericOutputException;
import br.edu.ulbra.election.election.repository.ElectionRepository;
import br.edu.ulbra.election.election.repository.VoteRepository;
import feign.FeignException;
import br.edu.ulbra.election.election.output.v1.ElectionCandidateResultOutput;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;

@Service
public class ResultService {

	private final VoteRepository voteRepository;
	private final ElectionRepository electionRepository;
	private final VoterClientService voterClientService;
	private final CandidateClientService candidateClientService;

	@Autowired
	public ResultService(VoteRepository voteRepository, ElectionRepository electionRepository,
			VoterClientService voterClientService, CandidateClientService candidateClientService) {
		this.voteRepository = voteRepository;
		this.electionRepository = electionRepository;
		this.voterClientService = voterClientService;
		this.candidateClientService = candidateClientService;
	}

	public ElectionCandidateResultOutput getResultByCandidate(Long candidateId) {

		ElectionCandidateResultOutput result = new ElectionCandidateResultOutput();

		try {
			CandidateOutput candidate = candidateClientService.getById(candidateId);
			result.setCandidate(candidate);
			Long numberElection = candidate.getNumberElection();
			Long totalVotes = voteRepository.countByNumberElection(numberElection);

			if (totalVotes != null) {
				result.setTotalVotes(totalVotes);
			}else {
				result.setTotalVotes((long) 0);
			}

		} catch (FeignException e) {
			if (e.status() == 500) {
				throw new GenericOutputException("Candidate not found!");
			}
		}

		return result;
	}

}