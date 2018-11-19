package br.edu.ulbra.election.election.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import br.edu.ulbra.election.election.output.v1.CandidateOutput;

@Service
public class CandidateClientService {

	private final CandidateClient candidateClient;

	@Autowired
	public CandidateClientService(CandidateClient candidateClient) {
		this.candidateClient = candidateClient;
	}

	public CandidateOutput verificaElection(Long id) {
		return this.candidateClient.verificaElection(id);
	}
	
	public CandidateOutput verificaNumero(Long numero) {
		return this.candidateClient.verificaNumero(numero);
	}

	@FeignClient(value = "candidate-service", url = "${url.candidate-service}")
	private interface CandidateClient {

		@GetMapping("/v1/candidate/{electionId}")
		CandidateOutput verificaElection(@PathVariable(name = "electionId") Long electionId);
		
		@GetMapping("/v1/candidate/{numberElection}")
		CandidateOutput verificaNumero(@PathVariable(name = "numberElection") Long numberElection);
	}
}
