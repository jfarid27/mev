package edu.dfci.cccb.mev.panda.rest.controllers;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.dfci.cccb.mev.panda.domain.GeneNetwork;
import edu.dfci.cccb.mev.panda.domain.GeneInteraction;

@RestController
@RequestMapping("/panda")
public class PandaAPIController {
	
	private @Inject GeneNetwork dao;
	
	@RequestMapping (value = "/rnaseq", method= GET)
	public Iterable<GeneInteraction> find (@RequestParam ("threshold") double strength, @RequestParam("type") String type) {
		return dao.findPairsWithStrengthGreaterThan(strength);
	}
	
	@RequestMapping (value = "/microarray", method= GET)
	public Iterable<GeneInteraction> find (@RequestParam ("threshold") double strength, @RequestParam("type") String type) {
		return dao.findPairsWithStrengthGreaterThan(strength);
	}
}