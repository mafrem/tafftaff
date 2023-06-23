package fr.sedit.grh.coeur.ca.avg.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.collect.Lists;

import fr.bl.shared.grh.coeur.dto.CollectiviteNumerotationArreteParamDTO;
import fr.bl.shared.grh.coeur.enums.EnumTypeNumerotationArrete;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGAExporterDTO;
import fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG;
import fr.sedit.grh.coeur.ca.avg.dao.IDaoTableauAG;
import fr.sedit.grh.coeur.ca.avg.model.PropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.TableauAG;
import fr.sedit.grh.coeur.cs.dao.IDaoOrganisme;
import fr.sedit.grh.coeur.cs.dao.impl.DaoAgentTest.AgentBuilder;
import fr.sedit.grh.coeur.cs.dao.impl.DaoCarriereTest.CarriereBuilder;
import fr.sedit.grh.coeur.cs.dao.impl.DaoCollectiviteITest.CollectiviteBuilder;
import fr.sedit.grh.coeur.cs.dao.impl.DaoOrganismeTest.OrganismeBuilder;
import fr.sedit.grh.coeur.cs.model.Agent;
import fr.sedit.grh.coeur.cs.model.Carriere;
import fr.sedit.grh.coeur.cs.model.parametrage.Collectivite;
import fr.sedit.grh.coeur.cs.model.parametrage.Organisme;
import fr.sedit.notest.AbstractTestSpring;

/**
 * 
 * @author julien.metais
 *
 */
public class DaoPropositionAGTest extends AbstractTestSpring {

	@Autowired
	@Qualifier("daoPropositionAG")
	private IDaoPropositionAG daoPropositionAG;

    @Autowired 
    @Qualifier("daoOrganisme")
    private IDaoOrganisme daoOrganisme;
	
    @Autowired 
    @Qualifier("daoTableauAG")
    private IDaoTableauAG daoTableauAG;
    
	@Test(timeout = AbstractTestSpring.TIME_OUT_DEFAULT)
	public void testFindCollectiviteByPropositionIds_UnknownIds_ReturnEmptyList() {
		
		final List<CollectiviteNumerotationArreteParamDTO> collectivites 
			= this.daoPropositionAG.findCollectiviteByPropositionIds(Lists.newArrayList(1, 2, 3));
		
		assertThat(collectivites).isEmpty();
	}
	
	@Test(timeout = AbstractTestSpring.TIME_OUT_DEFAULT)
	public void testFindCollectiviteByPropositionIds_PropositionsWithSameCollect_ReturnListWithOneElmt() {
		
		// Création d'un organisme
		
		final Organisme organisme = new OrganismeBuilder().with($_ -> {$_.code = "56";}).createOrganisme();
		super.saveFlushAndClearTestData(organisme);
		
		// Création d'une collectivité sur l'organisme
		
		final Collectivite collectivite = new CollectiviteBuilder().with($ -> {$.code = "C060";  $.organisme = organisme;
			$.employeur = true;}).createCollectivite();
		super.saveFlushAndClearTestData(collectivite);
		
		// Création d'une première carrière sur la collectivité
		
		final Agent agent1 = new AgentBuilder().with($ -> {$.nom = "MARTINEZ"; $.prenom = "Jean"; $.matricule = "JMAR";}).createAgent();
		super.saveFlushAndClearTestData(agent1);
		
		final Carriere carriere1 = new CarriereBuilder().with($ -> {$.agent = agent1; $.collectivite = collectivite;}).createCarriere();
		super.saveFlushAndClearTestData(carriere1);
		
		// Création d'une deuxième carrière sur la collectivité
		
		final Agent agent2 = new AgentBuilder().with($ -> {$.nom = "DOE"; $.prenom = "John"; $.matricule = "JDOE";}).createAgent();
		super.saveFlushAndClearTestData(agent2);
		
		final Carriere carriere2 = new CarriereBuilder().with($ -> {$.agent = agent2; $.collectivite = collectivite;}).createCarriere();
		super.saveFlushAndClearTestData(carriere2);
		
		// Création d'une proposition d'avancement de grade sur chaque des deux carrières créees
		
		final PropositionAG proposition1 = new PropositionAGBuilder().with($ -> {$.carriere = carriere1;}).createPropositionAG();
		super.saveFlushAndClearTestData(proposition1);
		
		final PropositionAG proposition2 = new PropositionAGBuilder().with($ -> {$.carriere = carriere2;}).createPropositionAG();
		super.saveFlushAndClearTestData(proposition2);
		
		// Invocation de la méthode testée
		
		final List<CollectiviteNumerotationArreteParamDTO> collectivites 
			= this.daoPropositionAG.findCollectiviteByPropositionIds(Lists.newArrayList(proposition1.getId(), proposition2.getId()));
		
		// Vérification des résultats
		
		assertThat(collectivites).hasSize(1);
		assertThat(collectivites).extracting("idCollectivite", "avecNumerotationArrete", "typeNumerotationArrete")
			.containsExactly(tuple(collectivite.getId(), false, null));
	}
	
	@Test(timeout = AbstractTestSpring.TIME_OUT_DEFAULT)
	public void testFindCollectiviteByPropositionIds_PropositionsWithDistinctCollects_ReturnOneCollectPerProposition() {
		
		// Création d'un organisme
		
		final Organisme organisme = new OrganismeBuilder().with($_ -> {$_.code = "56";}).createOrganisme();
		super.saveFlushAndClearTestData(organisme);
		
		// Création d'une collectivité avec deux carrières et deux propositions d'avancement de grade
		
		final Collectivite collectivite1 = new CollectiviteBuilder().with($ -> {$.code = "C060";  $.organisme = organisme; 
			$.employeur = true;}).createCollectivite();
		super.saveFlushAndClearTestData(collectivite1);
		
		final Agent agent1 = new AgentBuilder().with($ -> {$.nom = "MARTINEZ"; $.prenom = "Jean"; $.matricule = "JMAR";}).createAgent();
		super.saveFlushAndClearTestData(agent1);
		
		final Carriere carriere1 = new CarriereBuilder().with($ -> {$.agent = agent1; $.collectivite = collectivite1;}).createCarriere();
		super.saveFlushAndClearTestData(carriere1);
		
		final Agent agent2 = new AgentBuilder().with($ -> {$.nom = "DOE"; $.prenom = "John"; $.matricule = "JDOE";}).createAgent();
		super.saveFlushAndClearTestData(agent2);
		
		final Carriere carriere2 = new CarriereBuilder().with($ -> {$.agent = agent2; $.collectivite = collectivite1;}).createCarriere();
		super.saveFlushAndClearTestData(carriere2);
		
		final PropositionAG proposition1 = new PropositionAGBuilder().with($ -> {$.carriere = carriere1;}).createPropositionAG();
		super.saveFlushAndClearTestData(proposition1);
		
		final PropositionAG proposition2 = new PropositionAGBuilder().with($ -> {$.carriere = carriere2;}).createPropositionAG();
		super.saveFlushAndClearTestData(proposition2);
		
		// Création d'une deuxième collectivité avec une carrière et une proposition d'avancement de grade
		
		final Collectivite collectivite2 = new CollectiviteBuilder().with($ -> {$.code = "C061"; $.organisme = organisme;
			$.avecNumerotationArrete = true; $.typeNumerotationArrete = EnumTypeNumerotationArrete.INVITATION; 
			$.employeur = true;}).createCollectivite();
		super.saveFlushAndClearTestData(collectivite2);
		
		final Carriere carriere3 = new CarriereBuilder().with($ -> {$.agent = agent1; $.collectivite = collectivite2;}).createCarriere();
		super.saveFlushAndClearTestData(carriere3);
		
		final PropositionAG proposition3 = new PropositionAGBuilder().with($ -> {$.carriere = carriere3;}).createPropositionAG();
		super.saveFlushAndClearTestData(proposition3);
		
		final List<CollectiviteNumerotationArreteParamDTO> collectivites 
			= this.daoPropositionAG.findCollectiviteByPropositionIds(Lists.newArrayList(
					proposition1.getId(), proposition2.getId(), // Collectivité C060
					proposition3.getId())); // Collectivité C061
		
		assertThat(collectivites).hasSize(2);
		assertThat(collectivites).extracting("idCollectivite", "avecNumerotationArrete", "typeNumerotationArrete")
			.containsOnly(tuple(collectivite2.getId(), true, EnumTypeNumerotationArrete.INVITATION),
				tuple(collectivite1.getId(), false, null));
	}

	/**
	 * 
	 * @author julien.metais
	 *
	 */
	public static class PropositionAGBuilder {
		
		// index unique PK_PROPOSITIONAG = IDENTIFIANT
		public int id;
		public int version;
		
		public Carriere carriere;

		public PropositionAGBuilder with(final Consumer<PropositionAGBuilder> builderFunction) {
			builderFunction.accept(this);
			return this;
		}

		public PropositionAG createPropositionAG() {
			final PropositionAG propositionAG = new PropositionAG();
			propositionAG.setId(this.id);
			propositionAG.setVersion(this.version);
			propositionAG.setCarriere(this.carriere);
			return propositionAG;
		}
	}

    @Test(timeout = AbstractTestSpring.TIME_OUT_DEFAULT)
    public void testExportPropositionAGForExcel(){
        // Organisme base JUnit (REC_RH_JUNIT@RECRH19)
        //Organisme o = daoOrganisme.findOrganismeAndCadreStatutaireById("98765432100000539023000        ");

        // TableauAG Id = 1400970, code = 2019GRD, libelle = Avancement de grade 2019
        TableauAG tableauAG = daoTableauAG.loadTableauAG(1400970);
        
        Map<String, Object> criteres = new HashMap<String, Object>();
        criteres.put("tableauAGId", tableauAG.getId());
        criteres.put("tableauAG", tableauAG);
        criteres.put("nonPromouvable", Boolean.TRUE);
        criteres.put("promuExport", Boolean.TRUE);
        criteres.put("promouvable", Boolean.TRUE);
        
        List<PropositionAGAExporterDTO> lstPropositionAGAExporterDTO = daoPropositionAG.exportPropositionAGForExcel(criteres, true);

        assertNotNull(lstPropositionAGAExporterDTO);
        assertTrue(lstPropositionAGAExporterDTO.size() > 0);
        PropositionAGAExporterDTO propAG = lstPropositionAGAExporterDTO.get(0);
        // ESRH-6729
        assertNotNull(propAG.getCarriereId());
        assertNull(propAG.getLibelleCampagneEvaluation());
        assertNull(propAG.getDateEntretien());
        assertNull(propAG.getValeurProfessionnelle());
        assertNull(propAG.getPoidsValeurProfessionnelle());
        assertNull(propAG.getAvisEvaluateur());
        assertNull(propAG.getCommentaireEvaluateur());
    }
    
    @Test(timeout = AbstractTestSpring.TIME_OUT_DEFAULT)
    public void testInsertAndGetRangClassementAg() {
        String testSaisieLibre = "Test Saisie Libre";
        Integer nouvelIdentifiant = daoPropositionAG.insertNouveauRangClassementAg(testSaisieLibre);
        assertNotNull(nouvelIdentifiant);
        List<Integer> ids = daoPropositionAG.getIdClassementFromValeurSaisieLibre(testSaisieLibre);
        assertTrue(ids.contains(nouvelIdentifiant));
    }
}
