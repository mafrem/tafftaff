package fr.sedit.grh.coeur.ca.avg.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.bl.shared.grh.coeur.dto.CollectiviteNumerotationArreteParamDTO;
import fr.sedit.grh.ca.avg.model.dto.DetailAvancementAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGAExporterDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionNonPromouvableAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionsConcoursAgentDTO;
import fr.sedit.grh.ca.avg.services.rules.IRuleAvg;
import fr.sedit.grh.coeur.ca.avg.model.PropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.TableauAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypeAvancementAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumVerifierAG;
import fr.sedit.grh.coeur.ca.par.model.ParamCaOrga;
import fr.sedit.grh.coeur.cs.model.Agent;
import fr.sedit.grh.coeur.cs.model.CadreEmploi;
import fr.sedit.grh.coeur.cs.model.Carriere;
import fr.sedit.grh.coeur.cs.model.Categorie;
import fr.sedit.grh.coeur.cs.model.Filiere;
import fr.sedit.grh.coeur.cs.model.Grade;
import fr.sedit.grh.coeur.cs.model.Imprime;
import fr.sedit.grh.coeur.cs.model.dto.AgentDTO;
import fr.sedit.grh.coeur.cs.model.dto.CarrierePropositionAGDTO;
import fr.sedit.grh.coeur.cs.model.parametrage.Collectivite;
import fr.sedit.grh.coeur.cs.model.parametrage.Organisme;
import fr.sedit.grh.coeur.cs.model.parametrage.Service;
import fr.sedit.sedit.common.model.Notice;


/**
 * Generated Interface
 */
public interface IServicePropositionAG {

	/**
	 * Récupère une proposition via son identifiant
	 * @param propositionId	L'identifiant de la proposition
	 * @return PropositionAG
	 */
	PropositionAG findById(Integer propositionId);	

	/**
	 * Chargement des règles pour le moteur d'avancement de grade<br>
	 * sous la forme: Set(RuleAvgOr(RuleAvgLineRule(...),...,RuleAvgLineRule(...)))
	 * 
	 * @param organisme
	 * @param dateDebut
	 * @param DateFin
	 * @param listTypeAvancement Indique le type de règles à charger (Soit : Promotion interne et/ou avancement de grade soit reclassement)
	 * @return liste des règles (une par grade cible et type d'avancement) ou null si aucune regle trouvé
	 */
	Set<IRuleAvg> loadAndBuildRuleList(Organisme organisme, Date dateDebut, Date DateFin, final List<EnumTypeAvancementAG> listTypeAvancement);

	/**
	 * Chargement de la règle pour le moteur d'avancement de grade pour un grade cible et un type d'avancement<br>
	 * sous la forme: RuleAvgOr(RuleAvgLineRule(...),...,RuleAvgLineRule(...))
	 * 
	 * @param organisme
	 * @param dateDebut
	 * @param DateFin
	 * @param gradeCible
	 * @param typeAvancement
	 * @return règle pour le grade cible
	 */
	IRuleAvg loadAndBuildRuleList(Organisme organisme, Date dateDebut, Date DateFin, Grade gradeCible, EnumTypeAvancementAG typeAvancement);

	/**
	 * Construction d'une proposition à partir d'un calcul d'AVG
	 * 
	 * @param simulation flag si calcul de simulation
	 * @param paramCaOrga
	 * @param dateCalcul
	 * @param carriereDTO
	 * @param rule
	 * @return
	 */
	PropositionAG buildPropositionFromCalculs(boolean simulation, ParamCaOrga paramCaOrga, Date dateCalcul,CarrierePropositionAGDTO carriereDTO, IRuleAvg rule, TableauAG tableau, Date datePromotionTableau, Date dateDebut, Date dateFin);

	/**
	 * @param carriere
	 * @param codeRegroupement
	 * @return proposition la plus récente de l'année - 1
	 */
//	PropositionAG findPropositonAGAnneePrecedente(Carriere carriere, Long codeRegroupement);
	
	/**
	 * Charge une proposition pour l'affichage
	 * @param propositionId
	 * @return PropositionAG
	 */
	PropositionAG loadCompletePropositonAGById(Integer propositionId, final boolean lock);

	/**
	 * Compte le nombre de propositions Avg validées et pas injectées pour une carrière donnée
	 * @param propositionAG
	 * @return
	 */
	Long countExtraValidPropositionsAG(PropositionAG propositionAG);
	
	/**
	 * Retourne la liste des propositions d'un tableauAG passé en entree
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
	 * sauvegarde la propositionAG
	 * @param proposition PropositionAG
	 */
	void savePropositionAG(PropositionAG proposition) ;
	
	/**
	 * Retourne la liste des PropositionAGAExporterDTO pour l'export excel
	 * @param criteres
	 * @return List<PropositionAGAExporterDTO>
	 */
	List<PropositionAGAExporterDTO> exportPropositionAGForExcel(Map<String, Object> criteres);
	
	/**
	 * Constuit les dto DetailAvancementAGDTO en fonction du tableauAgId passé en parametre
	 * @param tableauId
	 * @return List<DetailAvancementAGDTO>
	 */
	List<DetailAvancementAGDTO> calculListDetailAvancementVerificationPropositionAg(Integer tableauId);
	
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
	 * Recherche les propositions  correspondantes aux criteres d'entree
	 * @param tri Map<String, Integer>
	 * @param criteres Map<String, Object>
	 * @param firstLine Long
	 * @param limitLine Long
	 * @param filter String
	 * @return  List<PropositionAGDTO>
	 */
	 List<PropositionAGDTO> findPropositionAGInjection(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter);
	
	/**
	 * compte les propositions  correspondantes aux criteres d'entree
	 * @param criteria Map<String, Object>
	 * @param filter String
	 * @return Long
	 */
	Long countPropositionAGInjection(Map<String, Object> criteria, String filter);

	/**
	 * exclue la proposition
	 * @param propositionAG
	 */
	void exclureProposition(Integer propositionAGId);

	/**
	 * Réintègre la propositionAG non promouvable
	 * @param propositionAGId
	 */
	void reintegrerPropositionAGNonPromouvable(Integer propositionAGId);

	/**
	 * refuse la promotion
	 * @param propositionAGId
	 * @param dateMotif
	 * @param motif
	 */
	void refuserPropositionAG(Integer propositionAGId, Date dateRefus,
			String motifRefus);

	/**
	 * Exclue une proposition manuellement
	 * @param propositionId
	 * @param motif
	 */
	void excluePropositionManuellement(Integer propositionId, String motif);

	/**
	 * Recherche et charge la proposition correspondant au couple agent / carriere
	 * @param tableauAG
	 * @param agent
	 * @return List<PropositionAG>
	 */
	List<PropositionAG> loadByTableauAndAgent(TableauAG tableauAG, Agent agent);
	
	/**
	 * Recherche et charge la proposition correspondant au tableau / carriere
	 * @param listTableauAG
	 * @param carriere
	 * @return List<PropositionAG>
	 */
	List<PropositionAG> loadByListTableauAndCarriere(List<Integer> tableauAG, Carriere carriere);
	
	/**
	 * Retourne la liste des identifiant des propositions simulés pour 
	 * une carriere et un code regroupement donné
	 * @param carriereId
	 * @param codeRegroupement
	 * @return List<Integer>
	 */
	List<Integer> findListSimulePropositionId(String carriereId, Long codeRegroupement);
	
	/**
     * Recherche les identifiants des propositionsAG par collectivités dans un but du control.
     * 
     * @param collectivites List<Collectivite>
     * @return List<PropositionAG>
     */
    List<Integer> findListPropositionAGIdsByCollectivitesForControl(List<String> idCollectivites);

    /**
     * Met a jour la carriere synthetique
     * @param proposition PropositionAG
     * @param isGpecInstalled
     */
    void updateCarriereSynthetique(PropositionAG proposition, boolean isGpecInstalled);
    
    /**
     * Met a jour les régimes absence
     * @param proposition PropositionAG
     */
    void updateRegimeAbsence(PropositionAG proposition);
    
	/**
	 * suppression d'une proposition
	 * @param propositionAG
	 */
	void deleteProposition(PropositionAG propositionAG);
	
	
    public void deletePropositionById(Integer id);
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
	 * supprime les propositions simulés pour 
	 * une carriere et un code regroupement donné
	 * @param carriereId
	 * @param codeRegroupement
	 */
	void deletePropositionSimule(String carriereId, Long codeRegroupement);
	
	/**
	 * supprime les propositions simulés pour un grade source
	 * ou un gradeCible et/ou un type avancement
	 * 
	 * @param gradeCible
	 * @param gradeSource
	 * @param typeAvancement
	 */
	void deleteAllPropositionSimule(Organisme organisme, Date dateDebut, Date dateFin, Set<Collectivite> listCollectivites, Set<Service> listServices, 
									Set<Categorie> listCategories, Set<CadreEmploi> listCadreEmplois, Set<Filiere> listFilieres, 
									Grade gradeCible, Grade gradeSource, final List<EnumTypeAvancementAG> listTypeAvancement);
	
	/**
	 * supprime les propositions simulées des agents passés en paramètres
	 * @return
	 */
	void deletePropositionSimuleByAgents(List<Agent> agents);

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
	 * @return List<PropositionsConcoursAgentDTO>
	 */
	List<PropositionsConcoursAgentDTO> findListPromotionsConcoursAgent(Map<String, Integer> tri, Map<String, Object> criteria,
			long firstLine, long limitLine, String filter);

	/**
	 * Officialise la proposition pour la réussite à concours
	 * @param propositionId
	 * @param promu
	 */
	void officialisePromotion(Integer propositionId, Boolean promu);
	
	/**
	 * liste des propositions pour un grade
	 * 
	 * @param tableau
	 * @return
	 */
	List<PropositionAG> findListPropositionByTableauGrade(TableauAG tableau, Grade grade);

	/**
	 * Renvoie la proposition avec sa carriere et la collectivite de la carriere
	 * @param propositionId Integer
	 * @return PropositionAG
	 */
	PropositionAG loadPropositionWithContent(Integer propositionId);
	
	List<CollectiviteNumerotationArreteParamDTO> findCollectiviteByPropositionIds(List<Integer> propositionIds);
	
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
     * @param codeOrganisme code organisme du tableauAG
     * @param codeTableau code tableau
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
     * Indique que le controle d'injection a été exécuté ce jour
     * 
     * @param codeCollectivite la collectivité concerné par le controle
     * @param etatTraitement D pour en cours, et F lorsque le traitement en terminé
     */
    void setControleInjectionExecuted(final String codeCollectivite, final String etatTraitement);

    /**
     * Récupère l'enregistrement indiquant si le controle d'injection a été effectué aujourd'hui
     * 
     * @param code la collectivité concerné par le controle
     * @return
     */
    Notice findNoticeForControleInjection(final String codeCollectivite);

    /**
     * Chargement des propositions pour contexte arretés signet personnalisé
     * @param lstid
     * @return
     */
    List<PropositionAG> loadListPropositionByListIdForContexteArrete(final List<Integer> listIdProposition);
    
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

    List<Integer> findPropositionAGEditAllImprimeAll(Map<String, Object> criteria);

	List<Integer> findListPropositionIdByMatricule(String matricule, String typeavancement, String typeProposition, int tableauId, String codeGrade, String codeGradeCible);
	
	List<Integer> findListPropositionIdByMatricules(List<String> matricules, String typeavancement);

	void updatePromuClassementDateNominationDateEffetFromIdProposition(Integer idProposition, Date datePromotion, Integer idClassement, boolean promu);
	
	Date getDatePromotionFromTableauAG(Integer idTableauAG);

	List<Integer> getIdClassementFromValeurSaisieLibre(String saisieLibre);

	Integer insertNouveauRangClassementAg(String saisieLibre);

	boolean isValeurExisting(String valeur);

	List<Integer> getIdClassementFromValeur(String valeur);

	boolean isSaisieLibreExisting(String saisieLibre);

}
