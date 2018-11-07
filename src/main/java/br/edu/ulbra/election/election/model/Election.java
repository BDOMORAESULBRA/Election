package br.edu.ulbra.election.election.model;

import javax.persistence.*;

import br.edu.ulbra.election.election.output.v1.ElectionOutput;

@Entity
public class Election {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer year;

	@Column(nullable = false)
	private String state_code;

	@Column(nullable = false)
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getStateCode() {
		return state_code;
	}

	public void setStateCode(String stateCode) {
		this.state_code = stateCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static ElectionOutput verifica(Election e, Integer year) {
		Integer y = e.getYear();
		
		if(y.equals(year)) {
			ElectionOutput electionOutput = new ElectionOutput();
			
			electionOutput.setId(e.getId());
			electionOutput.setStateCode(e.getStateCode());
			electionOutput.setDescription(e.getDescription());
			electionOutput.setYear(e.getYear());
			
			return electionOutput;
		}else {
			return null;
		}
	}

}
