package fr.sedit.grh.coeur.ca.avg.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.bl.shared.grh.coeur.dto.CollectiviteNumerotationArreteParamDTO;
import fr.sedit.core.hibernate.IGenericDAO;
import fr.sedit.grh.ca.avg.model.dto.AvisAGDTO;
import fr.sedit.grh.ca.avg.model.dto.ConditionRemplieByPropositionDTO;
import fr.sedit.grh.ca.avg.model.dto.DetailAvancementAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGAExporterDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionDetailsAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionNonPromouvableAGDTO;
import fr.sedit.grh.coeur.ca.avg.model.ConditionDeProposition;
import fr.sedit.grh.coeur.ca.avg.model.PropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.TableauAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypeAvancementAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumVerifierAG;
import fr.sedit.grh.coeur.ca.par.model.LigneConditionAG;
import fr.sedit.grh.coeur.cs.model.Agent;
import fr.sedit.grh.coeur.cs.model.Carriere;
import fr.sedit.grh.coeur.cs.model.Grade;
import fr.sedit.grh.coeur.cs.model.Imprime;
import fr.sedit.grh.coeur.cs.model.dto.AgentDTO;

/**
 * Generated Interface
 */
public interface IDaoPropositionAG extends IGenericDAO<PropositionAG,Integer>{

	/**
	 * Charge la proposition pour affichage
	 * @param propositionId
	 * @return PropositionAG
	 */
	PropositionAG loadCompletePropositonAGById(Integer propositionId, final boolean lock);

	/**
	 * Compte le nombre de propositions avg validées et pas injectées pour une carrière donnée
	 * @param proposition
	 * @return nombre de propositions avg trouvées
	 */
	Long countExtraValidPropositionsAG(PropositionAG propositionAG);


	/**
	 * Retourne la liste des propositions promouvables d'un tableauAG passé en entree
	 * @param tri Map<String, Integer>
	 * @param criteres Map<String, Object>
	 * @param firstLine long
	 * @param limitLine long
	 * @param filter String
	 * @return List<PropositionAGDTO>
	 */
	List<PropositionAGDTO> findListPropositionAGDTO(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter);

	/**
	 * Compte les propositions verifiant les criteres en entree
	 * criteres pris en compte: tableauAG(TableauAG), gradeId(String),promouvable(Boolean) ,
	 * 							enumTypePropositionAG(EnumTypePropositionAG), enumVerifier(EnumVerifierAG)
	 * @param criteria Map<String, Object>
	 * @param filter String
	 * @return Long
	 */
	Long countPropositionWithCriteria(Map<String, Object> criteria, String filter) ;
	
	/**
	 * Change la valeur du champ verifiee de la propositionAG
	 * dont l'id est passe en entree et lui attribut la valeur passe en entree
	 * @param propositionId Integer
	 * @param enumVerifier EnumVerifierAG
	 */
	void changeVerifieePropositionAG(Integer propositionId, EnumVerifierAG enumVerifier);
	
			
	/**
	 * ReInitialise le boolean Promouvable avec celui passe en entree sur la proposition 
	 * dont l'id est passe en entree
	 * si boolean promouvable = true alors la proposition est promouvable
	 * sinon elle est non promouvable
	 * @param propositionId  Integer
	 * @param promouvable boolean
	 */
	void exclureReintegrerPropositionAG(Integer propositionId, boolean promouvable);
	
	/**
	 * Retourne la liste des propositions NonPromouvable en fonction des parametres en entree
	  * @param tri Map<String, Integer>
	 * @param criteres Map<String, Object>
	 * @param firstLine long
	 * @param limitLine long
	 * @param filter String
	 * @return List<PropositionNonPromouvableAGDTO>
	 */
	List<PropositionNonPromouvableAGDTO> findListPropositionNonPromouvableAGDTO(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter);

	/**
	 * Retourne la liste des PropositionAGAExporterDTO pour l'export excel
	 * @param criteres
	 * @return List<PropositionAGAExporterDTO>
	 */
	List<PropositionAGAExporterDTO> exportPropositionAGForExcel(Map<String, Object> criteres,Boolean isGpecInstalled);
	
	/**
	 * Constuit les dto DetailAvancementAGDTO en fonction du tableauAgId passé en parametre
	 * @param tableauId
	 * @return List<DetailAvancementAGDTO>
	 */
	List<DetailAvancementAGDTO> calculListDetailAvancementVerificationPropositionAg(Integer tableauId);
	
    /**
     * envoie la liste des ConditionDeProposition rempli= true de la proposition dont l'id est en entree
     * @param propositionId
     * @return
     */
    List<ConditionDeProposition> findListConditionDePropositionRemplieByPropositionId(Integer propositionId);
   
	/**
	 * Renvoie la liste des conditionRemplieAGDTO de la proposition dont l'id est en entree
	 * @param propositionId
	 * @return List<ConditionRemplieByPropositionDTO>
	 */
	List<ConditionRemplieByPropositionDTO> findListConditionRemplieByPropositionDTO(Integer propositionId);
		
	/**
	 * Renvoie la liste des AvisAGDTO de la proposition dont l'id est en entree
	 * @param propositionId
	 * @return List<AvisAGDTO>
	 */
	List<AvisAGDTO> findListAvisAGDTOByPropositionId(Integer propositionId);
	
	/**
	 * Update le champs promu de la proposition dans l'id est en entree
	 * @param propositionId Integer
	 * @param promu boolean
	 */
	void changePromuPropositionAG(Integer propositionId, boolean promu);
	
    /**
     * Officialise les propositions d'un tableau de raclassement en passant toutes les promouvable en promu.
     * 
     * @param idTableau
     */
    void updatePromuForPromouvableAndTableauReclassement(final Integer idTableau);
	
	/**
	 * Update un champs date de la proposition dont l'id est en entree
	 * comme par ex le champs dateNomination passer le String du champ et sa valeur
	 * @param propositionId Integer
	 * @param nomChamps String
	 * @param date Date
	 */
	void changeDateOfPropositionAG(Integer propositionId, String nomChamps, Date date);
	
	/**
	 * Recherche les propositions correspondantes aux criteres d'entree
	 * @param tri Map<String, Integer>
	 * @param criteres Map<String, Object>
	 * @param firstLine Long
	 * @param limitLine Long
	 * @param filter String
	 * @return  List<PropositionAGDTO>
	 */
	List<PropositionAGDTO> findPropositionAGInjection(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter);
	
	/**
	 * compte les propositions correspondantes aux criteres d'entree
	 * @param criteria Map<String, Object>
	 * @param filter String
	 * @return Long
	 */
	Long countPropositionAGInjection(Map<String, Object> criteria, String filter);
		
	/**
	 * Refuse la promotion
	 * @param propositionId
	 * @param dateRefus
	 * @param motifRefus
	 */
	void refuserPromotion(Integer propositionId, Date dateRefus, String motifRefus);

	/**
	 * Exclue une proposition manuellement
	 * @param propositionId
	 * @param motif
	 */
	void excluePropositionManuellement(Integer propositionId, String motif);
			
	/**
	 * @param date de sélection
	 * @param carriere
	 * @param codeRegroupement
	 * @return proposition la plus récente antérieure à dateLimite
	 */
	PropositionAG findPropositonAGAnneePrecedente(Date dateLimite, Carriere carriere, Long codeRegroupement);
	
	/**
	 * Charge une proposition avec le tableau et l'agent correspondants
	 * 
	 * @param tableauAG
	 * @param agent
	 * @return List<PropositionAG> 
	 */
	List<PropositionAG> loadByTableauAndAgent(TableauAG tableauAG, Agent agent);
	
	/**
	 * Charge une proposition avec la liste des tableaux et la carrière correspondante
	 * 
	 * @param Liste des tableaux
	 * @param carriere
	 * @return List<PropositionAG> 
	 */
	List<PropositionAG> loadByListTableauAndCarriere(List<Integer> listTableauAG, Carriere carriere);
	
	/**
	 * Retourne la liste des identifiant des propositions simulés pour 
	 * une carriere et un code regroupement donné
	 * @param carriereId
	 * @param codeRegroupement
	 * @return List<Integer>
	 */
	List<Integer> findListSimulePropositionId(String carriereId, Long codeRegroupement);
	
    /**
     * Recherche les ids des propositionsAG par collectivités dans un but d'un contrôle.
     * 
     * @param collectivites List<Collectivite>
     * @return List<PropositionAG>
     */
    List<Integer> findListPropositionAGIdsByCollectivitesForControl(List<String> idCollectivites);

	/**
	 * suppression de toutes les propositions (sauf simulation) d'un tableau
	 * @param tableauAG
	 */
	void deletePropositionsByTableau(TableauAG tableauAG);
	
	/**
	 * suppression de toutes les propositions ayant pour grade cible le grade passe en parametre
	 * (sauf simulation) d'un tableau
	 * @param tableauAG
	 * @param gradeCible
	 */
	void deletePropositionsByTableauAGAndGradeCible(TableauAG tableauAG,Grade gradeCible);
	
	/**
     * Suppression de toures les propositions de simulation
     */
    public void deleteAllPropositionsSimulation();
    
    /**
     * retourne toutes les propositions (sauf simulation) fait a aprtir de cette condition
     * @param LigneConditionAG
     */
    List<PropositionAG> findListPropositionsByLigneConditionAG( LigneConditionAG ligneConditionAG) ;
    
    /**
     * retourne toutes les propositions (sauf simulation) fait a aprtir de cette condition et chevauchant ou postérieur à cette date
     * @param LigneConditionAG
     */
    List<PropositionAG> findListPropositionsByLigneConditionAGAfterDate( LigneConditionAG ligneConditionAG, Date dateRef) ;
    
    /**
     * retourne toutes les propositions simulées fait a aprtir de cette condition
     * @param LigneConditionAG
     */
    List<PropositionAG> findListPropositionsSimuleByLigneConditionAG( LigneConditionAG ligneConditionAG) ;
	
	/**
	 * supprime les propositions simulés pour un grade source
	 * ou un gradeCible et/ou un type avancement
	 * @param agents
	 * @param gradeCible
	 * @param gradeSource
	 * @param typeAvancement
	 */
	int deleteAllPropositionSimule(String carriereId, Long codeRegroupement, Grade gradeCible, Grade gradeSource, final List<EnumTypeAvancementAG> listTypeAvancement);
	
	/**
	 * supprime les propositions simulées des agents passés en paramètres
	 * @return
	 */
	void deletePropositionSimuleByAgents(List<Agent> agents);

	/**
     * Supprime toutes les propositions pour les carrières données
     * @param listCarriere
     */
    public void deletePropositionAGByAgent(final List<Carriere> listCarriere);
	
	/**
	 * Compte les propostions suite à réussite des concours agent pour le CRUD
	 * @param criteria
	 * @param filter
	 * @return
	 */	
	Long countPromotionsConcoursAgent(Map<String, Object> criteria, String filter);

	/**
	 * Retourne la liste propositions suite à réussite des concours agent pour le CRUD
	 * @param tri
	 * @param criteria
	 * @param startIndex
	 * @param endIndex
	 * @param filter
	 * @return List<PropositionAG>
	 */
	List<PropositionAG> findListPromotionsConcoursAgent(Map<String, Integer> tri, Map<String, Object> criteria,
			long firstLine, long limitLine, String filter);

	/**
	 * Officialise la proposition pour la réussite à concours
	 * @param propositionId
	 * @param promu
	 */
	void officialisePromotion(Integer propositionId, Boolean promu);

	/**
	 * liste des propositions pour un tableau et éventuellement un grade cible
	 * 
	 * @param tableau
	 * @param grade
	 * @return
	 */
	List<PropositionAG> findListPropositionByTableauGrade(TableauAG tableau, Grade grade);

	/**
	 * Renvoie les informations détaillées de la propositionAG
	 * @param propositionAG
	 * @param date
	 * @param dateHisto
	 * @return PropositionDetailsAGDTO
	 */
	PropositionDetailsAGDTO getPropositionInfoForDate(PropositionAG proposition,String nameGradeAttribut, Date date, Date dateHisto, String carriereId);
	
	List<CollectiviteNumerotationArreteParamDTO> findCollectiviteByPropositionIds(List<Integer> propositionIds);
		
	/**
	 * Renvoie la proposition avec sa carriere et la collectivite de la carriere
	 * @param propositionId Integer
	 * @return PropositionAG
	 */
	PropositionAG loadPropositionWithContent(Integer propositionId);
	
	/**
     * Renvoie la liste des fichiers imprimés sélectionnés pour une liste de propositions ids
     * @param lstPropositionIds la liste des Id de propositions
     * @return List<String>
     */
    List<Imprime> findFichierImprimeByListPropositionsId(List<Integer> lstPropositionIds);
    
    /**
     * Retourne l'imprimé d'une proposition
     * @param idProposition
     * @return
     */
    Imprime findImprimeByPropositionId(Integer idProposition);
    
    /**
     * Renvoie la liste des fichiers imprimés sélectionnés pour un tableau
     * @param codeOrganisme code organisme du tableau
     * @param codeTableau code tableauAG
     * @return List<String>
     */
    List<Imprime> findFichierImprimebyTableauId(Integer tableauId);
    
    /**
     * 
     * @param tableauAG
     * @param propositionIds
     * @return List<Integer>
     */
    List<Integer> findListPropositionsOfficialisees4Arretes(TableauAG tableau, List<Integer> propositionIds) ;
    
    /**
	 * Liste des identifiants de proposition pour les imprimés depuis l'injection
	 * @param propositionCriteria
	 * @return
	 */
	List<Integer> findInjectionPropIdsWithCriteria(Map<String, Object> propositionCriteria);
	
	/**
	 * retourne une liste propositionaG selon les criteres d'entree
	 * @param tri Map<String, Integer>
	 * @param criteres Map<String, Object>
	 * @return	List<PropositionAG>
	 */
	List<PropositionAG> findListPropositionAG(Map<String, Integer> tri, Map<String, Object> criteres);
    
    /**
     * Renvoi la liste des proposition ag qui ont pour concours agent l'id passé en paramètre
     * @param idConcoursAgent
     * @return
     */
    List<PropositionAG> findListPorpositionAGByConcoursAgent(String idConcoursAgent);

    /**
     * Chargement des propositions pour contexte arretés signet personnalisé
     * @param lstid
     * @return
     */
    List<PropositionAG> loadListPropositionByListIdForContexteArrete(List<Integer> lstid);
    
    /**
     * Renvoi une liste d'objet par rapport à un id d'imprimé
     * @param idImprime
     * @return
     */
    List<PropositionAG> findPropositionAGByIdImprime(String idImprime);
    
    /**
     * Retourne nom prénom et matricule de l'agent selon l'id proposition
     * @param idProposition
     * @return
     */
    AgentDTO findInfosAgentByProposition(Integer idProposition);

    /**
     * Retourne le nombre de propositions promouvable pour un Tableau AG
     * @param code du tableau AG
     * @return count
     */
	Long countPropositionsPromouvableByTableauAG(String code);

	/**
     * Retourne la liste des identifiants de propositions par collectivité. 
     * @param propositionIds liste des identifiants non trié.
     * @return la liste des identifiants de propositions par collectivité.
     */
    List<Integer> orderPropositionAGIdByCollectivite(List<Integer> propositionIds);
    
    /**
	 * Update le champs promuSPV de la proposition dans l'id est en entree
	 * @param propositionId Integer
	 * @param promu boolean
	 */
	void changePromuSPVPropositionAG(Integer propositionId, boolean promu);
 
	Date getDateRefFromTableauAG(Integer idTableauAG);

	List<Integer> findListPropositionIdByMatricule(String matricule, String typeavancement, String typePropositon, int tableauId, String codeCadre, String codeCadreCible);

	List<Integer> findListPropositionIdByMatricules(List<String> matricules, String typeavancement);
	
	void updatePromuClassementDateNominationDateEffetFromIdProposition(Integer idProposition, Date datePromotion,Integer idClassement, boolean promu);

	Date getDatePromotionFromTableauAG(Integer idTableauAG);

	List<Integer> getIdClassementFromValeurSaisieLibre(String saisieLibre);

	Integer insertNouveauRangClassementAg(String saisieLibre);

	boolean isValeurExisting(String valeur);

	List<Integer>  getIdClassementFromValeur(String valeur);

	boolean isSaisieLibreExisting(String saisieLibre);



}