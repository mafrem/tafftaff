package fr.bl.client.grh.ca.avg.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import fr.bl.client.core.exception.SeditGwtException;
import fr.bl.client.grh.ca.avg.model.GrilleAncienneteGWT;
import fr.bl.client.grh.ca.avg.model.RatiosQuotasParTableauGWT;
import fr.bl.client.grh.ca.avg.model.dto.*;
import fr.bl.client.grh.ca.par.model.ParamRangClassementAGGWT;
import fr.bl.client.grh.ca.par.model.ValeurClassementAGGWT;
import fr.bl.client.grh.ca.par.model.dto.*;
import fr.bl.client.grh.coeur.ca.avg.model.AvisAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.PropositionAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.RangClassementAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.enums.EnumVerifierAGGWT;
import fr.bl.client.grh.coeur.ca.par.model.EcheanceCAPGWT;
import fr.bl.client.grh.coeur.ca.par.model.ParamCaOrgaGWT;
import fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypeAEAGGWT;
import fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypePresentationGWT;
import fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypeTableauGWT;
import fr.bl.client.grh.coeur.cs.model.AgentGWT;
import fr.bl.client.grh.coeur.cs.model.GradeGWT;
import fr.bl.client.grh.coeur.cs.model.dto.*;
import fr.bl.client.grh.coeur.cs.model.parametrage.CadreStatutaireGWT;
import fr.bl.client.grh.coeur.cs.model.parametrage.CollectiviteGWT;
import fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT;
import fr.bl.client.grh.coeur.nr.model.dto.RoleOrganisationnelDTOGWT;
import fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT;
import fr.bl.shared.grh.car.dto.CalculatriceCarriereDTO;
import fr.bl.shared.grh.car.dto.InjectionFicheCarriereRetourDTO;
import fr.bl.shared.grh.car.enums.EnumCalculatriceOperation;
import fr.bl.shared.grh.coeur.dto.CollectiviteNumerotationArreteParamDTO;
import fr.bl.shared.grh.coeur.dto.NumeroArreteDTO;

import java.io.Serializable;
import java.util.*;

public interface IGwtServiceCaAvg extends RemoteService {
	
	/**
	 * Utility class for simplifing access to the instance of async service.
	 */
	public static class Util {
		
		private static IGwtServiceCaAvgAsync instance;
		
		public static IGwtServiceCaAvgAsync getInstance(){ 
			if (instance == null) {
				instance = (IGwtServiceCaAvgAsync) GWT.create(IGwtServiceCaAvg.class);
				ServiceDefTarget target = (ServiceDefTarget) instance;
				String urlService = GWT.getModuleBaseURL() + "services/ServiceCaAvg.rpc";
				target.setServiceEntryPoint(urlService);
			}
			return instance;			
		}		
	}
	
	/**
     * Retourne la liste des tableaux d'avancement de grade en cours pour l'organisme passé en paramètre.
     * @param organisme Organisme
     * @param type Le type des tableau retourné (Grade, Promotion ou Reclassement))
     */
	List<EnCoursTableauAGDTOGWT> searchEnCoursTableauAG(final String idOrganisme, final String type) throws SeditGwtException;
	
	/**
	 * Retourne la liste des réussites à concours non validées pour l'organisme en cours.
	 * 
	 * @param organisme
	 * @return List 
	 * @throws SeditGwtException
	 */
	List<ReussiteConcoursDTOGWT> loadReussiteConcoursDTONonValidees(OrganismeGWT organisme) throws SeditGwtException;
	
	
	/**
	 * Retourne la liste des propositions AG pour réussite concours dont état < injecte.
	 * @param organisme
	 * @return List<ReussiteConcoursDTOGWT>
	 * @throws SeditGwtException
	 */
	List<ReussiteConcoursDTOGWT> loadPromotions(OrganismeGWT organisme) throws SeditGwtException ;
	
	/**
	 * Cree la propositionAG associer a cette reussite a concours
	 * @param reussiteConcoursDTOGWT
	 * @return PropositionAGGWT
	 * @throws SeditGwtException
	 */
	PropositionAGGWT createPropositionConcours(ReussiteConcoursDTOGWT reussiteConcoursDTOGWT) throws SeditGwtException ;
    
    /**
     * Recalcul le classement pour une proposition existante
     * @param idpropAGGWT
     * @return
     * @throws SeditGwtException
     */
    PropositionAGGWT recalculClassementPropositionAG(Integer idpropAGGWT) throws SeditGwtException;
    
    /**
     * Recalcul le classement pour les propositions existantes
     * @param idpropAGGWT
     * @return
     * @throws SeditGwtException
     */
    void recalculClassementPropositionAG(List<Integer> idpropAGGWT) throws SeditGwtException;
    
    /**
     * Recalcul le classement pour les propositions existantes
     * @param criteres
     * @throws SeditGwtException
     */
    void recalculClassementPropositionAG(Map<String, Object> criteres) throws SeditGwtException;
	
	/**
	 * Retourne la liste des Categories du cadre statutaire d'entree et la date
	 * @param cadreStatutaire
	 * @param date Date
	 * @return List<CategorieDTOGWT>
	 */
	List<CategorieDTOGWT> findCategorieDTOByCadreStatutaireAndDate(String idCadreStatutaire,Date date) throws SeditGwtException;
	
	/**
	 * Retourne la liste des statuts selon des critères
	 * 
	 * @param tri HashMap<String,Integer>
	 * @param criteres HashMap<String, Object>
	 * @param firstLine
	 * @param limitLine
	 * @param filter
	 * @return List<StatutDTOGWT>
	 */
	List<StatutDTOGWT> findStatutByCriteria(HashMap<String, Integer> tri, HashMap<String, Object> criteres, long firstLine, long limitLine, String filter)throws SeditGwtException;
	
	/**
	 * Recherche les filieres correspondant au cadre statutaire d'entre et de la date
	 * @param idCadreStatut String
	 * @param date Date
	 * @return List<FiliereDTOGWT>
	 */
	List<FiliereDTOGWT> findListFiliereDTOByCadreStatutaireAndDate(String idCadreStatut,Date date) throws SeditGwtException;
	

	/**
	 * Retourne la liste des FiliereDTO correspondant a un cadreStatutaire
	 * remplie de champ nbPropositionOfSpecifiedTableauAG du FiliereDTO
	 * la map de critere doit contenir : tableauAG, promouvable, enumTypePropositionAG
	 * @param idCadreStatut l'id du cadre statutaire
	 * @param date
	 * @return List<FiliereDTO>
	 */
	List<FiliereDTOGWT> findListFiliereDTOAndCountPropositionByCriteresAndCadreStatutaireAndDate(Map<String, Object> criteriaProposition,String idCadreStatut, Date date)throws SeditGwtException ;

	
	/**
	 * Recherche les groupe hierarchique en fonction du cadre statutaire en entree
	 * @param idCadreStatut String
	 * @return List<GroupeHierarchiqueDTOGWT>
	 */
	List<GroupeHierarchiqueDTOGWT> findListGroupeHierarchiqueDTOByCadreStatutaireAndDate(String idCadreStatut,Date date) throws SeditGwtException;
	
	/**
	 * Recherche les echeanciers en fonction de l'organisme en entree
	 * @param idOrganisme String
	 * @return List<EcheancierCAPDTOGWT>
	 */
	List<EcheancierCAPDTOGWT> findListEcheancierCAPDTOByOrganisme(String idOrganisme) throws SeditGwtException ;
	
	/**
	 * Recherche les echeanciers en fonction de l'organisme en entree et du type de l'echeancier 
	 * @param organisme OrganismeGWT
	 * @param type EnumTypeAEAGGWT
	 * @return List<EcheancierCAPDTOGWT>
	 */
	List<EcheancierCAPDTOGWT> loadListEcheancierCAPDTOByOrganismeAndTypeAvancement(OrganismeGWT organisme, EnumTypeAEAGGWT type) throws SeditGwtException;
	
	/**
	 * Recherche les echeances d'un echeancier par son id
	 * @param echeancierCAPId
	 * @return List<EcheanceCAPGWT>
	 * @throws SeditGwtException
	 */	
	List<EcheanceCAPGWT> findEcheanceCAPByEcheancierId(Integer echeancierCAPId) throws SeditGwtException;

	/**
	 * Sauvegarde le tableauAG et le retourne une fois sauvé
	 * Demarre le Wf echeancier si le boolean d'entree startEcheancier= true
	 * @param utilisateurGWT
	 * @param tableauAGGWT
	 * @param startEcheancier
	 * @return TableauAGGWT
	 * @throws SeditGwtException
	 */
	TableauAGGWT saveAndReturnTableauAG(UtilisateurSMRHGWT utilisateurGWT, TableauAGGWT tableauAGGWT, boolean startEcheancier) throws SeditGwtException;

	/**
	 * Sauvegarde le tableauAG	
	 * @param tableauAGGWT
	 * @return TableauAGGWT
	 * @throws SeditGwtException
	 */
	void saveTableauAG(TableauAGGWT tableauAGGWT) throws SeditGwtException;
		
	/**
	 * Renvoie les FonctionAGDTO ayant pour cadre statutaire l'id passé en parametre
	 * @param idOrganisme
	 * @return  List<FonctionAGDTOGWT>
	 * @throws SeditGwtException
	 */
	List<FonctionAGDTOGWT> findListFonctionAGDTOByCadreStatutaire(String idOrganisme) throws SeditGwtException;
	
	/**
	 * Retourne la liste des paramPresentationCAP en focntion des parametres passes en entree 
	 * @param tri Map<String, Integer>
	 * @param criteria Map<String, Object
	 * @param startIndex long
	 * @param maxResult long
	 * @param filter String
	 * @return List <ParamPresentationCAPDTOGWT>
	 * @throws SeditGwtException
	 */
	List<ParamPresentationCAPDTOGWT> findListParamPresentationCAPDTO(Map<String, Integer> tri, Map<String, Object> criteria, long startIndex, long maxResult,String filter)throws SeditGwtException ;

	/**
	 * Renvoie le ParamCaOrga de l'organisme passé en parametre avec tous ces liens
	 * @param organisme
	 * @return <ParamCaOrgaGWT>
	 * @throws SeditGwtException
	 */
	ParamCaOrgaGWT findParamCaOrgaComplete(String idOrganisme) throws SeditGwtException;
	
	/**
	 * Renvoie le tableauAGGWT avec toutes ces objets sauf la liste de ces propositions
	 * @param tableauAGId
	 * @return TableauAG
	 */
	TableauAGGWT loadCompleteTableauAGWithoutProposition(Integer tableauAGId)  throws SeditGwtException;
	
	/**
	 * Retourne un tableau AG par son identifiant
	 * @param tableauId
	 * @return TableauAGGWT
	 * @throws SeditGwtException 
	 */
	TableauAGGWT findTableauAGById(Integer tableauId) throws SeditGwtException;	

	/**
	 * Suppression d'une liste de tableau d'avancement
	 * 
	 * @param listIdIKey
	 * @throws SeditGwtException
	 * @throws SeditGwtException
	 * @return boolean
	 */
	boolean deleteListTableauAG(List<Integer> listeIdTableaux) throws SeditGwtException;
	
	/**
	 * Suppression d'un tableau d'avancement
	 * 
	 * @param tableauAG
	 * @throws SeditGwtException 
	 * @throws SeditGwtException
	 */
	void deleteTableauAG(TableauAGGWT tableauAG) throws SeditGwtException;
	
	/**
	 * Recherche un tableau AG par son id
	 * @param tableauAGId
	 * @return TableauAGGWT
	 * @throws SeditGwtException 
	 */
	TableauAGGWT loadCompleteTableauAG(Integer tableauAGId) throws SeditGwtException;
	
	/**
	 * Méthode pour le CRUD
	 * 
	 * @param tri 
	 * @param criteria
	 * @param startIndex
	 * @param endIndex
	 * @param filter
	 * @return List
	 * @throws SeditGwtException 
	 */
	List<List<Serializable>> findListTableauAG(Map<String, Integer> tri, Map<String, Object> criteria, long startIndex, long endIndex, String filter, boolean showMoreColumns) throws SeditGwtException;

	/** 
	 * Méthode de comptage pour le CRUD
	 * @param criteria
	 * @param filter
	 * @return Long
	 * @throws SeditGwtException 
	 */
	Long countTableauAG(Map<String, Object> criteria, String filter) throws SeditGwtException;

	/**
	 * Supprime les promus avec propositions, avis et conditions ou non des tableaux passés en parametre.<br>
	 * 
	 * @param listeTableaux<br>
	 * @param supprPropositions<br>
	 * <li>true  : supprime les propositions.</li><br>
	 * <li>false : ne supprime pas les propositions.</li><br>
	 * @param supprAvis<br>
	 * <li>true : supprime les avis.</li><br>
	 * <li>false : ne supprime pas les avis</li><br>
	 * @param supprConditions<br>
	 * <li>true : supprime les consitions.</li><br>
	 * <li>false : ne supprime pas les consitions</li><br>
	 * @throws SeditGwtException 
	 */
	void purgerPromusDesTableaux(List<TableauAGGWT> listeTableaux, boolean supprPropositions, boolean supprAvis, boolean supprConditions) throws SeditGwtException;	
	
	/**
	 * Supprime les non promu avec propositions, avis et conditions ou non des tableaux passés en parametre.<br>
	 * 
	 * @param listeTableaux<br>
	 * @param supprPropositions<br>
	 * <li>true  : supprime les propositions.</li><br>
	 * <li>false : ne supprime pas les propositions.</li><br>
	 * @param supprAvis<br>
	 * <li>true : supprime les avis.</li><br>
	 * <li>false : ne supprime pas les avis</li><br>
	 * @param supprConditions<br>
	 * <li>true : supprime les consitions.</li><br>
	 * <li>false : ne supprime pas les consitions</li><br>
	 * @throws SeditGwtException 
	 */
	void purgerNonPromusDesTableaux(List<TableauAGGWT> listeTableaux, boolean supprPropositions, boolean supprAvis, boolean supprConditions) throws SeditGwtException;	
	
	
	/**
	 * Supprime les non promouvable avec propositions, avis et conditions ou non des tableaux passés en parametre.<br>	 * 
	 * @param listeTableaux<br>
	 * @param supprPropositions<br>
	 * <li>true  : supprime les propositions.</li><br>
	 * <li>false : ne supprime pas les propositions.</li><br>
	 * @param supprAvis<br>
	 * <li>true : supprime les avis.</li><br>
	 * <li>false : ne supprime pas les avis</li><br>
	 * @param supprConditions<br>
	 * <li>true : supprime les consitions.</li><br>
	 * <li>false : ne supprime pas les consitions</li><br>
	 * @throws SeditGwtException 
	 */
	void purgerNonPromouvablesDesTableaux(List<TableauAGGWT> listeTableaux, boolean supprPropositions, boolean supprAvis, boolean supprConditions) throws SeditGwtException;	
	
	/**
	 * Renvoie la liste des cadres emplois de la filiere passée en parametre
	 * @param idFiliere
	 * @param date
	 * @throws SeditGwtException 
	 */
	List<CadreEmploiDTOGWT> findListCadreEmploiDTOByFiliere(String idFiliere, Date date)  throws SeditGwtException;
	
	/**
	 * Retourne la liste des CadreEmploiDTO correspondant a une filiere
	 * remplie de champ nbPropositionOfSpecifiedTableauAG du CadreEmploiDTO
	 * la map de critere doit contenir : tableauAG, promouvable, enumTypePropositionAG
	 * @param idFiliere l'id de la filiere
	 * @return List<CadreEmploiDTO>
	 */
	List<CadreEmploiDTOGWT> findListCadreEmploiDTOAndCountPropositionByCriteresAndFiliere(Map<String, Object> criteriaProposition,String idFiliere, Date date)  throws SeditGwtException;

	
	/**
	 * Retourne la liste des ratiosPArTableauAGDTO en fonction des criteres d'entree
	 * @param tri Map<String, Integer>
	 * @param criteria Map<String, Object>
	 * @param startIndex long 
	 * @param endIndex long
	 * @param filter String
	 * @return List<RatiosQuotasParTableauAGDTOGWT>
	 */
	List<RatiosQuotasParTableauAGDTOGWT> findListRatiosQuotasParTableauAGDTO(Map<String, Integer> tri, Map<String, Object> criteria, long startIndex, long endIndex, String filter)  throws SeditGwtException;
		
	/**
	 * Retourne le nombre des ratiosPArTableauAGDTO en fonction des criteres d'entree
	 * @param criteria Map<String, Object>
	 * @param filter String
	 * @return Long
	 */
	Long countRatiosQuotasParTableauAG(Map<String, Object> criteria, String filter) throws SeditGwtException;
	

	/**
	 * Retourne le ratiosQuotasParTableauGWT ayant l'id passé en parametre (avec l'objet ParamCaGrade)
	 * @param ratiosQuotasParTableauId
	 * @return RatiosQuotasParTableauGWT
	 * @throws SeditGwtException
	 */
	RatiosQuotasParTableauGWT loadCompleteRatiosQuotasParTableauById(Integer ratiosQuotasParTableauId)throws SeditGwtException ;
	
	/**
	 * Sauvegarde le RatiosQuotasParTableauGWT
	 * @param ratiosQuotasParTableau
     * @return RatiosQuotasParTableauGWT
	 */
	RatiosQuotasParTableauGWT saveRatiosQuotasParTableau(RatiosQuotasParTableauGWT ratiosQuotasParTableau)throws SeditGwtException;

	/**
	 * Retourne la liste des dto pour l'export excel des ratiosQuotasParTableau pour le
	 * tableau AG passe en entree
	 * @param tri Map
	 * @param criteres Map
	 * @param filter String
	 * 
	 * @return List<List<Serializable>>
	 */ 
	List<List<String>> exportRatiosQuotasParTableauForExcel(Map<String, Integer> tri, Map<String, Object> criteres, String filter)throws SeditGwtException;

	/**
	 * Retourne la liste des GradeDTOGWT correspondant a un cadre emploi
	 * 
	 * @param idCadreEmploi l'id du cadre emploi
	 * @return List<GradeDTOGWT>
	 */
	List<GradeDTOGWT> findListeGradeDTOByCadreEmploi(String idCadreEmploi) throws SeditGwtException;
	
	/**
	 * Retourne la liste des GradeDTO correspondant a un cadre emploi
	 * remplie de champ nbPropositionOfSpecifiedTableauAG du gradeDTO
	 * la map de critere doit contenir : tableauAG, promouvable, enumTypePropositionAG
	 * @param idCadreEmploi l'id du cadre emploi
	 * @return List<GradeDTOGWT>
	 */	
	List<GradeDTOGWT> findListeGradeDTOAndCountPropositionByCriteresAndCadreEmploi(Map<String, Object> criteriaProposition, String idCadreEmploi) throws SeditGwtException ;

	
	/**
	 * Retourne le RatiosQuotasParTableau correspondant au tableauAG et au grade passé en entree
	 * @param tableauAG TableauAGGWT
	 * @param grade GradeGWT
	 * @return RatiosQuotasParTableauGWT
	 */
	RatiosQuotasParTableauGWT loadCompleteRatiosQuotasParTableauByTableauAGAndGrade(TableauAGGWT tableauAGGWT,GradeGWT gradeGWT)throws SeditGwtException;

	/**
	 * Retourn le grade ayant l'id passe en entree
	 * @param id
	 * @return GradeGWT
	 */
	GradeGWT findGradeById(String id)throws SeditGwtException;
	
	/**
	 * Retourne la liste des propositions promouvables d'un tableauAG passé en entree
	 * @param tri Map<String, Integer>
	 * @param criteres Map<String, Object>
	 * @param firstLine long
	 * @param limitLine long
	 * @param filter String
	 * @return List<PropositionAGDTO>
	 */
	List<PropositionAGDTOGWT> findListPropositionAGDTO(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter)throws SeditGwtException ;

	/**
	 * Compte les propositions verifiant les criteres en entree
	 * criteres pris en compte: tableauAG(TableauAG), gradeId(String),promouvable(Boolean) ,
	 * 							enumTypePropositionAG(EnumTypePropositionAG), enumVerifier(EnumVerifierAG)
	 * @param criteria Map<String, Object>
	 * @param filter String
	 * @return Long
	 */
	Long countPropositionWithCriteria(Map<String, Object> criteria, String filter)throws SeditGwtException  ;
	
	/**
	 * Recherche et renvoie le ParamRangClassementAG de l'organisme passé en entree
	 * @param organisme
	 * @return ParamRangClassementAGGWT
	 */
	ParamRangClassementAGGWT findParamRangClassementAGByOrganisme(OrganismeGWT organismeGWT) throws SeditGwtException ;
		
	/**
	 * Retourne la liste des ValeurClassementAg correspondant au ParamRangClassementAg passe en entree
	 * @param param
	 * @return List<ValeurClassementAGGWT>
	 */

	List<ValeurClassementAGGWT> findListValeurClassementAGByParamRangClassementAG(ParamRangClassementAGGWT param) throws SeditGwtException ;

	
	/**
	 * Change la valeur du champ verifiee de la propositionAG
	 * dont l'id est passe en entree et lui attribut la valeur passe en entree
	 * @param propositionId Integer
	 * @param enumVerifier EnumVerifierAGGWT
	 */
	void changeVerifieePropositionAG(Integer propositionId, EnumVerifierAGGWT enumVerifierGWT) throws SeditGwtException;

	/**
	 * Gere une list de String[] qui contient l'id de la proposition et le code de l'enumVerifier verifiee
	 * modifier la valeur de l'attribut verifiee de la proposition dont l'id est passé en parametre
	 * et lui attribut la valeur de verifiee passé en parametre
	 * @param listTablePropositionIdAndBooleanVerifiee List of String[2]
	 */
	List<String[]> changeVerifieeListPropositionAG(List<String[]> listTablePropositionIdAndBooleanVerifiee) throws SeditGwtException ;
	
	/**
	 * Change le champ valeur ou saisie libre au choix de l'objet RangClassement vers lequel pointe la proposition
	 * dont l'id est passe en entree
	 * @param propositionId Integer
	 * @param saisieLibre String
	 * @param valeur String
	 */
	void changeRangClassementOfPropositionAG(Integer propositionId,String saisieLibre, String valeur) throws SeditGwtException ;

	/**
	 * Exclue les propositions (passe promouvable à false)
	 * @param listPropositionsId
	 * @throws SeditGwtException
	 */
	void exclureListPropositionAG(List<Integer> listPropositionsId) throws SeditGwtException;
	
	/**
	 * ReInitialise le boolean Promouvable avec celui passe en entree sur la proposition 
	 * dont l'id est passe en entree
	 * si boolean promouvable = true alors la proposition est promouvable
	 * sinon elle est non promouvable
	 * @param propositionId  Integer
	 * @param promouvable boolean
	 */
	void exclureReintegrerPropositionAG(Integer propositionId, boolean promouvable) throws SeditGwtException;
	
	/**
	 * Retourne la liste des propositions NonPromouvable en fonction des parametres en entree
	  * @param tri Map<String, Integer>
	 * @param criteres Map<String, Object>
	 * @param firstLine long
	 * @param limitLine long
	 * @param filter String
	 * @return List<PropositionNonPromouvableAGDTOGWT>
	 */
	List<PropositionNonPromouvableAGDTOGWT> findListPropositionNonPromouvableAGDTO(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter)throws SeditGwtException;

	
	/**
	 * Retourne la liste des agents ayant une propositionAG dans un tableauAG en particuliers
	 * @param tri Map<String, Integer>
	 * @param criteres Map<String, Object>
	 * @param firstLine long
	 * @param limitLine long
	 * @param filter String
	 * @return List<AgentDTOGWT>
	 */
	 public List<List<Serializable>> findListAgentAyantPropositionDansTableauAG(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine,long limitLine, String filter) throws SeditGwtException  ;
		
	 /**
	  * Conmpte le nombre d'agent ayant une propositionAG dans un tableauAG en particuliers
	  * @param criteria Map<String, Object>
	  * @param filter String
	  * @return Long
	  */
	 public Long countAgentOfTableauAG(Map<String, Object> criteria, String filter) throws SeditGwtException  ;
	 
	 /**
		 * Retourne la liste des agents dont le nom ou le matricule commence par la chaine passée en paramétre.
		 * dans un tableauAG en particuliers
		 * @param criteres Map
		 * @return List<AgentDTOGWT>
		 */
	 public List<AgentDTOGWT> findListAgentDansTableauAGByNameOrMatricule(Map<String, Object> criteres) throws SeditGwtException ;
	 
	 /**
	 * Retourne la liste des agents dont le nom ou le matricule commence par la chaine passée en paramétre.
	  * @param nomOuMatricule
	  * @param organisme
	  * @return List<AgentDTOGWT>
	  * @throws SeditGwtException
	  */
	List<AgentDTOGWT> findListAgentDTOByNomOrMatricule(String nomOuMatricule, OrganismeGWT organisme) throws SeditGwtException ;

	 /**
	 * Retourne la liste des PropositionAGAExporterDTOGWT pour l'export excel
	 * @param criteres
	 * @return List<PropositionAGAExporterDTO>
	 */
	String exportPropositionAGForExcel(Map<String, Integer> tri, Map<String, Object> criteres, String filter)throws SeditGwtException;
	
	/**
	 * Constuit les dto DetailAvancementAGDTO en fonction du tableauAgId passé en parametre
	 * @param tableauId
	 * @return List<DetailAvancementAGDTO>
	 */
	 public List<List<Serializable>> calculListDetailAvancementVerificationPropositionAg(Integer tableauId)  throws SeditGwtException;

	/**
	 * Recherche une proposition
	 * @param	propositionId	L'identifiant de la proposition
	 * @return
	 * @throws SeditGwtException
	 */
	PropositionAGGWT findPropositonAGById(Integer propositionId) throws SeditGwtException;

	/**
	 * Charge une proposition pour l'affichage
	 * @param propositionId
	 * @return PropositionAGGWT
	 * @throws SeditGwtException
	 */
	PropositionAGGWT loadCompletePropositonAGById(Integer propositionId) throws SeditGwtException;
	

	/**
	 * Retourne la liste des conditions de propositions pour l'affichage des propositions
	 * @param propositionId
	 * @return
	 * @throws SeditGwtException
	 */
	List<ConditionDePropositionDTOGWT> findListConditionAG(Integer propositionId) throws SeditGwtException;
	
	/**
	 * @param tris
	 * @param criteres
	 * @param numLigne
	 * @param nbLignes
	 * @param filtre
	 * @exception SeditGwtException
	 */
	List<AvisAGDTOGWT> searchAvisAG(final HashMap<String, Integer> tris, final HashMap<String, Object> criteres,
			final long numLigne, final long nbLignes, final String filtre)
			throws SeditGwtException;

	/**
	 * @param criteres
	 * @param filtre
	 * @exception SeditGwtException
	 */
	Long countAllAvisAG(final HashMap<String, Object> criteres, final String filtre)
			throws SeditGwtException;
	
	/**
	 * 
	 * @param detailId
	 * @return
	 * @throws SeditGwtException
	 */
	List<JustificatifRetardAGDTOGWT> findListJustificatifsRetardAG(Integer detailId) throws SeditGwtException;
	
	/**
	 * 
	 * @param codeGrade
	 * @param cadreStatutaire
	 * @param dateDebut
	 * @param dateFin
	 * @return
	 * @throws SeditGwtException
	 */
	List<EchelonEtIndicesDTOGWT> findListEchelonAndIndiceOfGradeByCode(String codeGrade, CadreStatutaireGWT cadreStatutaire, Date dateDebut, Date dateFin) throws SeditGwtException;
	
	/**
	 * Sauvegarde la proposition
	 * @param propositionAGGWT
	 * @param map 
	 * @throws SeditGwtException
	 */
	void savePropositionAG(PropositionAGGWT propositionAGGWT, HashMap<String, Object> map) throws SeditGwtException;

	/**
	 * sauvegarde le rang classement de la proposition
	 * @param rangClassementAGGWT
	 * @throws SeditGwtException
	 */
	void saveRangClassementAG(RangClassementAGGWT rangClassementAGGWT)  throws SeditGwtException;
	
	/**
	 * Initialise la proposition à partir de la carriere et du code regroupement pour aller chercher la bonne fiche grade emploi
	 * @param propositionAGGWT
	 * @param carriereId
	 * @param codeRegroupement
	 * @throws SeditGwtException
	 */
	PropositionAGGWT initPropositionAndReturnPropositionAG(Integer tableauAGId, String carriereId, Long codeRegroupement) throws SeditGwtException;	

	 /**
	  * Update le champs promu de la proposition dans l'id est en entree
	  * @param propositionId Integer
	  * @param promu boolean
	  */
	void changePromuPropositionAG(Integer propositionId, boolean promu)throws SeditGwtException;
		
	/**
	 * Update un champs date de la proposition dont l'id est en entree
	 * comme par ex le champs dateNomination passer le String du champ et sa valeur
	 * @param propositionId Integer
	 * @param nomChamps String
	 * @param date Date
	 */
	void changeDateOfPropositionAG(Integer propositionId, String nomChamps, Date date)throws SeditGwtException;

	/**
	 * Recherche les propositions non injectee ou controlee (ko ou ok) dont la dateNomination>mois de paie
	 * @param tri Map<String, Integer>
	 * @param criteres Map<String, Object>
	 * @param firstLine Long
	 * @param limitLine Long
	 * @param filter String
	 * @return  List<PropositionAGDTOGWT>
	 */
	List<PropositionAGDTOGWT> findPropositionAGInjection(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter) throws SeditGwtException ;
	
	/**
	 * compte les propositions non injectee ou controlee (ko ou ok) dont la dateNomination>mois de paie
	 * @param criteria Map<String, Object>
	 * @param filter String
	 * @return Long
	 */
	Long countPropositionAGInjection(Map<String, Object> criteria, String filter) throws SeditGwtException ;

	/**
	 * Recherche un avis par son id
	 * @param avisId
	 * @return
	 * @throws SeditGwtException
	 */
	AvisAGGWT findAvisAGById(Integer avisId) throws SeditGwtException;
	
	/**
	 * Update l'avis AG
	 * @param avis
	 * @param propositionId
	 * @return
	 * @throws SeditGwtException
	 */
	AvisAGGWT updateAvisAG(AvisAGGWT avis, Integer propositionId) throws SeditGwtException;
	

	/**
	 * Sauvegarde les avis en leur assignant la date courante et l'agent connecté
	 * @param utilisateurGWT
	 * @param listAvisAGDTO
	 */
	void updateAllAvisAG4CurrentAgentAndDate(UtilisateurSMRHGWT utilisateurGWT, List<AvisAGDTOGWT> listAvisDto) throws SeditGwtException;
	
	/**
	 * Supprime l'objet avisAG
	 * @param avis
	 * @throws SeditGwtException
	 */
	void deleteAvisAG(AvisAGGWT avis) throws SeditGwtException;
	
	/**
	 * supprime l'avis AG par son id
	 * @param avisId
	 * @throws SeditGwtException
	 */
	void deleteAvisAGById(Integer avisId) throws SeditGwtException;
	
	/**
	 * Renvoie la liste des appréciations associées à un organisme.
	 * Ordonné selon la propriété Appreciation.ordre
	 * 
	 * @param organismeId	L'identifiant de l'organisme
	 * @param typeGWT
	 * @return
	 */
	List<ParamAppreciationAvisDTOGWT> findListAppreciation(String organismeId,  EnumTypeAEAGGWT typeGWT) throws SeditGwtException;
	
	/**
	 * Met à jour ou crée, puis enregistre un avis
	 * @param utilisateurGWT TODO
	 * @param avisId	Identifiant de l'avis s'il existe déjà
	 * @param propositionId	Proposition associée à l'avis
	 * @param appreciationId	Identifiant de l'appreciation
	 * @param qualite
	 * @param commentaire
	 * @return 
	 * @throws SeditGwtException
	 */
	AvisAGGWT updateAvisAG(UtilisateurSMRHGWT utilisateurGWT, Integer avisId, Integer propositionId, Integer appreciationId, String qualite, String commentaire) throws SeditGwtException;
	
	/**
	 * Renvoie la liste des rôles organisationnels pour le choix de la "Qualité"
	 * @param agentId
	 * @return
	 * @throws SeditGwtException
	 */
	List<RoleOrganisationnelDTOGWT> findListRoleOrganisationnelByAgentId(String agentId) throws SeditGwtException;
		
	/**
	 * Exclue la proposition
	 * @param propositionAGId
	 * @throws SeditGwtException
	 */
	void exclurePropositionAG(Integer propositionAGId) throws SeditGwtException;	
		
	/**
	 * Réintégrer une proposition non promouvable (promouvable false-> true)
	 * @param propositionId
	 * @throws SeditGwtException
	 */
	void reintegrerPropositionAGNonPromouvable(Integer propositionAGId) throws SeditGwtException;
	
	/**
	 * Refuser une promotion
	 * @param id
	 * @param dateMotif
	 * @param motif
	 * @throws SeditGwtException
	 */
	void refuserPropositionAG(Integer propositionAGId, Date dateRefus, String motifRefus) throws SeditGwtException;

	/**
	 * Retourne les détails d'une condition
	 * @param conditionId
	 * @throws SeditGwtException
	 * @return List<ConditionDePropositionEtDetailDTOGWT>
	 */
	List<ConditionDePropositionEtDetailDTOGWT> findConditionEtDetailByConditionId(Integer conditionId) throws SeditGwtException;
	
	/**
	 * Exclure une proposition manuellement
	 * @param motif
	 * @param propositionId
	 */
	void excluePropositionManuellement(Integer propositionId, String motif) throws SeditGwtException;
	
	/**
	 * Retourne la liste d'anciennete pour une carriere et un regroupement
	 * @param carriereId
	 * @param codeRegroupement
	 * @throws SeditGwtException
	 */
	List<GrilleAncienneteDTOGWT> findListAnciennete(String carriereId, Long codeRegroupement) throws SeditGwtException;	
	
	/**
	 * Calcul de la grille d'ancienneté pour une carrière et un code regroupement donné
	 * 
	 * @param carriereId
	 * @param codeRegroupement
	 * @return null ou grille d'anciennete créée/mise à jour
	 */
	GrilleAncienneteGWT computeAndSaveGrilleAnciennete(String carriereId, Long codeRegroupement)throws SeditGwtException;
	
	/**
	 * Injection des propositions dont les id sont presents dans la liste passée en entree
	 * @param propositionsId List<java.lang.Integer>
     * @param currentUserGWT UtilisateurSMRHGWT
	 */
	InjectionFicheCarriereRetourDTO injectPropositionsAGById(List<Integer> propositionsId, UtilisateurSMRHGWT currentUserGWT) throws SeditGwtException;
	
	/**
	 * Controle des propositions ayant leur collectivite dans la liste des collectivites passeee en entree
	 * @param collectivites List<CollectiviteGWT>
	 */
	void controleListPropositionsAGAvantInjection(List<CollectiviteGWT> collectivites) throws SeditGwtException;
    
	/**
	 * Lance le calcul de simulation pour une carriere et un regroupement d'un agent
	 * @param carriereId
	 * @param codeRegroupement
	 * @throws SeditGwtException
	 * return PropositionAGGWT
	 */
	PropositionAGGWT simulPropositionAndReturnPropositionAG(String carriereId, Long codeRegroupement) throws SeditGwtException ;
	
	/**
	 * Recherche la liste des propositions simulées pour la carriere et le code regroupement donné
	 * @param id
	 * @param codeRegroupement
	 * @throws SeditGwtException
	 * return List<Integer>
	 */
	List<Integer> findListPropositionIdSimule(String id, Long codeRegroupement) throws SeditGwtException ;
	
	/**
	 * Calcul les propositions du tableau passé en parametre
	 * @param tableauAGGWT
	 * @throws SeditGwtException
	 */
	void calculPropositionsAGForTableauAG(TableauAGGWT tableauAGGWT, GradeGWT gradeCibleGWT) throws SeditGwtException;
	
	/**
	 * 
	 * @param tableauAGGWT TableauAGGWT
	 * @param criteria Map
	 * @throws SeditGwtException
	 */
	void reCalculTableauAG(TableauAGGWT tableauAGGWT, Map<String, Object> criteria) throws SeditGwtException;
	
	/**
	 * Recalcul le tableau passé en entree
	 * @param tableauAGGWT
	 * @throws SeditGwtException
	 */
	void reCalculTableauAG(TableauAGGWT tableauAGGWT) throws SeditGwtException;
	
	/**
	 * Recalcul les propositionsAg passees dans la liste en entree, cette liste de propositions appartient au tableau passe en entree
	 * @param tableauAGGWT
	 * @param listPropositionsAGARecalculer
	 * @return Set
	 * @throws SeditGwtException
	 */
	Set<PropositionAGGWT> reCalculPropositions(TableauAGGWT tableauAGGWT, Set<PropositionAGGWT> listPropositionsAGARecalculer) throws SeditGwtException;
	
	/**
	 * Recalcul les propositionsAg dont les id passees dans la liste en entree, cette liste de propositions appartient au tableau passe en entree
	 * @param tableauAGGWT
	 * @param listPropositionsAGARecalculer
	 * @return Set
	 * @throws SeditGwtException
	 */
	Set<PropositionAGGWT> reCalculPropositionsById(TableauAGGWT tableauAGGWT, List<Integer> listIdPropositionsAGARecalculer) throws SeditGwtException;
	
	/**
     * suppression de la proposition
     * @param tableauAG
     */
	void deleteProposition(PropositionAGGWT propositionAG) throws SeditGwtException;
	
	/**
	 * suppression de toutes les propositions (sauf simulation) d'un tableau
	 * @param tableauAG
	 */
	void deleteAllPropositionsByTableauAG(TableauAGGWT tableauAGGWT) throws SeditGwtException;
	
	/**
	 * suppression de toutes les propositions ayant pour grade cible le grade passe en parametre
	 * (sauf simulation) d'un tableau
	 * @param tableauAGGWT
	 * @param gradeCibleGWT
	 */
	void deletePropositionsByTableauAGAndGradeCible(TableauAGGWT tableauAGGWT,GradeGWT gradeCibleGWT)throws SeditGwtException ;
	
    /**
     * Suppression de toutes les propositions de simulation.
     * 
     * @throws SeditGwtException
     */
    public void deleteAllPropositionsSimulation() throws SeditGwtException;
	
	/**
	 * Retourne la liste des serviceDTO pour une collectivité
	 * @param collectiviteId
	 * @return List<ServiceDTOGWT>
	 * @throws SeditGwtException
	 */
	List<ServiceDTOGWT> findListServiceDTOByCollectivite(String collectiviteId) throws SeditGwtException;
	
	/**
	 * Retourne la proposition existante pour le trio tableau carriere code regroupement
	 * si null pas de correspondance trouvés
	 * @param tableauAGId
	 * @param carriereId
	 * @param codeRegroupement
     * @param gradeCibleId
	 * @return {@link PropositionAGGWT}
	 * @throws SeditGwtException
	 */
	PropositionAGGWT verifieIntegriteProposition(Integer tableauAGId, String carriereId, Long codeRegroupement,String gradeCibleId) throws SeditGwtException;

	/**
	 * Simule les proposition suivant les critères inclus dans la HashMap
	 * pour un grade source ou un grade cible , un type d'avancement éventuellement et
	 * soit sur critères de filtres collectivite, service, catégories, Filières et cadre d'emplois
	 * soit sur un groupe d'agents clé = 'agents'
	 * 
	 * @param mapCriteres
	 * @return List<PropositionSimuleDTOGWT>
	 * @throws SeditGwtException
	 */
	List<PropositionSimuleDTOGWT> simulePropositionsSurConditions(HashMap<String, Object> mapCriteres) throws SeditGwtException;
	
	/**
	 * Méthode pour le CRUD
	 * 
	 * @param tri 
	 * @param criteria
	 * @param startIndex
	 * @param endIndex
	 * @param filter
	 * @param showMoreColumns
	 * @return List<ConcoursAgentDTOGWT>
	 */
	List<PropositionsConcoursAgentDTOGWT> findListPromotionsConcoursAgent(Map<String, Integer> tri, HashMap<String, Object> criteria,
			long firstLine, long limitLine, String filter, boolean showMoreColumns) throws SeditGwtException;

	/** 
	 * Méthode de comptage pour le CRUD
	 * @param criteria
	 * @param filter
	 * @return Long
	 */
	Long countPromotionsConcoursAgent(HashMap<String, Object> criteria, String filter) throws SeditGwtException;
	
	/**
	 * Officialise la proposition pour la réussite à concours (en principe)
	 * @param id
	 * @param promu
	 */
	void officialisePromotion(Integer propositionId, Boolean promu)  throws SeditGwtException;

	/**
     * Renvoie les informations de l'imprimé
     * @param propositionId
     * @return
     * @throws SeditGwtException
     */
    public ImprimeDTOGWT getImprimeDTOByPropositionId(Integer propositionId) throws SeditGwtException;
    public ImprimeDTOGWT getImprimeDTOForSPVByPropositionId(Integer propositionId) throws SeditGwtException;

    /**
     * Charge le contexte d'exécution des questionnaires a poser a l'utilisateur pour prendre en compte les signets personnalisés
     * 
     * @param listIdProposition
     * @param modeleTypeId
     * @return
     * @throws SeditGwtException
     */
    ContexteExecSignetPersoGWT loadContexteExecSignetPersoByPropositionAG(List<Integer> listIdProposition, final String modeleTypeId) throws SeditGwtException;
    ContexteExecSignetPersoGWT loadContexteExecSignetPersoForSPVByPropositionAG(List<Integer> listIdProposition, final String modeleTypeId) throws SeditGwtException;
    
    /**
     * Charge le contexte d'exécution des questionnaires a poser a l'utilisateur pour prendre en compte les signets personnalisés
     * 
     * @param tableau
     * @param modeleTypeId
     * @return
     * @throws SeditGwtException
     */
    ContexteExecSignetPersoGWT loadContexteExecSignetPersoByTableauAG(TableauAGGWT tableau, final String modeleTypeId) throws SeditGwtException;
    
    /**
     * Charge le contexte d'exécution des questionnaires a poser a l'utilisateur pour prendre en compte les signets personnalisés
     * 
     * @param criteria
     * @param modeleTypeId
     * @return
     * @throws SeditGwtException
     */
    ContexteExecSignetPersoGWT loadContexteExecSignetPersoByCriteriaAG(Map<String, Object> criteria, String modeleTypeId) throws SeditGwtException;
    /**
     * Charge le contexte d'exécution des questionnaires a poser a l'utilisateur pour prendre en compte les signets personnalisés
     * 
     * @param collectivite
     * @param modeleTypeId
     * @return
     * @throws SeditGwtException
     */
    ContexteExecSignetPersoGWT loadContexteExecSignetPerso4InjectAG(CollectiviteGWT collectivite, final String modeleTypeId) throws SeditGwtException;
	
    /**
     * Sauvegarde de l'imprimé (arrêté) d'une proposition d'avancement de grade
     * 
     * @param propostion Identifiant de la proposition d'avancement de grade
     * @param modeleTypeId Identifiant du modèle à utiliser par défaut
     * @param dateDecision Date de la décision
     * @param officielise Vrai si officialisé
     * @param numeroArrete Numéro de l'arrêté
     * @param launchEdition Vrai si on souhaite aussi lancer l'édition de l'arrêté
     * @return Informations sur l'édition de l'imprimé (arrêté) mise à jour
     * @throws SeditGwtException
     */
    public ImprimeDTOGWT saveArreteAG(Integer propositionId, String modeleTypeId, Date dateGeneration, Boolean officiel, String numeroArrete,
            boolean launchEdition, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT) throws SeditGwtException;
  
 public ImprimeDTOGWT saveArreteSPVAG(Integer propositionId, String modeleTypeId, Date dateGeneration, Boolean officiel, String numeroArrete,
            boolean launchEdition, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT) throws SeditGwtException;
	
	/**
	 * Sauvegarde/génération de l'imprimé d'une liste de propositions
	 * @param propositionIds
	 * @param dateDecision
	 * @param officiel
	 * @param numeroArretePrefix
	 * @param numeroArreteIndex
	 * @param editerDejaExistant
	 */
    public RetourEtListeErreurDTOGWT saveArreteAGBatch(List<Integer> propositionIds, String modeleTypeId, Date dateDecision, Boolean officiel, String numeroArretePrefix,
            Long numeroArreteIndex, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, Boolean editerDejaExistant, NumeroArreteDTO startNumber) throws SeditGwtException;

    public void saveArreteAGBatch(Map<String, Object> criteria, String userId, String modeleTypeId, Date dateDecision, Boolean officiel, String numeroArretePrefix,
            Long numeroArreteIndex, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, Boolean editerDejaExistant, NumeroArreteDTO startNumber) throws SeditGwtException;
    
    public RetourEtListeErreurDTOGWT saveArreteSPVAGBatch(List<Integer> propositionIds, String modeleTypeId, Date dateDecision, Boolean officiel, String numeroArretePrefix,
            Long numeroArreteIndex, NumeroArreteDTO numeroArreteDTO, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, Boolean editerDejaExistant) throws SeditGwtException;
	
	/**
	 * Sauvegarde/génération de l'imprimé des propositions d'un tableau
	 * @param tableauId
	 * @param dateDecision
	 * @param officiel
	 * @param numeroArretePrefix
	 * @param numeroArreteIndex
	 * @param editerDejaExistant
	 */
    public RetourEtListeErreurDTOGWT saveArreteAGTableau(TableauAGGWT tableau, String modeleTypeId, Date dateDecision, Boolean officiel, String numeroArretePrefix,
            Long numeroArreteIndex, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, Boolean editerDejaExistant, NumeroArreteDTO startNumber) throws SeditGwtException;
	
	/**
	 * Sauvegarde/génération de l'imprimé des propositions injectables d'une collectivité
	 * @param collectivite
	 * @param dateDecision
	 * @param officiel
	 * @param numeroArretePrefix
	 * @param numeroArreteIndex
	 * @param editerDejaExistant
	 * @throws SeditGwtException
	 */
    public RetourEtListeErreurDTOGWT saveArreteAGBatch4Inject(CollectiviteGWT collectivite, String modeleTypeId, Date dateDecision, Boolean officiel,
            String numeroArretePrefix, Long numeroArreteIndex, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, Boolean editerDejaExistant, NumeroArreteDTO startNumber) throws SeditGwtException;
	
    /**
     * Fusionne les arrêtés des propositions dans un seul document afin de pouvoir les imprimer par lot ensuite
     * @param user   utilisateur connecté
     * @param rep   répertoire dans lequel doit se trouver les documents
     * @param propositionIds    La liste des id propositions à sélectionner 
     * @return le nom du document résultat
     * @throws SeditGwtException
     */
    public RetourEtListeErreurDTOGWT imprimerPropositionsBatch(String user, String rep, List<Integer> propositionIds) throws SeditGwtException;
    
    /**
     * Fusionne les arrêtés des propositions dans un seul document afin de pouvoir les imprimer par lot ensuite
     * @param user   utilisateur connecté
     * @param rep   répertoire dans lequel doit se trouver les documents
     * @param criteria  critere pour charger la liste des id propositions 
     * @throws SeditGwtException
     */
    void imprimerPropositionsBatch(String username, String userId, String rep, Map<String, Object> criteria) throws SeditGwtException;
    
    /**
	 * Service permettant la construction et le démarrage du workflow 
	 * de gestion des échéancier CAP dans les avancements d'échelon.
	 * @param listeEcheances
	 * @param tableauAG
	 * @param userId
	 */
	void startEcheancierCAP(List<EcheanceCAPGWT> listeEcheances, TableauAGGWT tableauAG, String userId) throws SeditGwtException;
	
	/**
	 * Récupérer le détail d'une tâche par rapport à l'acteur demandeur et 
	 * au nom unique de la tâche
	 * @param actorId
	 * @param taskName
	 * @return
	 */
	TacheWfEcheancierDTOGWT getDetailTache(String actorId, String taskName) throws SeditGwtException;
	
	/**
	 * Récupérer le détail d'une tâche par rapport à son identifiant
	 * @param taskId
	 * @return
	 */
	TacheWfEcheancierDTOGWT getDetailTache(long taskId )throws SeditGwtException;
	
	/**
	 * Retourne la tache courante
	 * @param tableauId
	 * @param userId
	 * @return
	 */
	TacheWfEcheancierDTOGWT getCurrentTaskTableau(String tableauId, String userId) throws SeditGwtException;
	

	/**
	 * Cloture une tache du WF
	 * @param task
	 * @param userId
	 */
	void cloturerTacheWF(TacheWfEcheancierDTOGWT task, String userId) throws SeditGwtException;
	
	/**
	 * Sauvegarde le tableauAg et cloture la tache passée en parametre
	 * @param tableauAGGWT
	 * @param task
	 * @param userId
	 * @return
	 */
	TableauAGGWT saveTableauAndClotureTache(TableauAGGWT tableauAG, TacheWfEcheancierDTOGWT task, String userId) throws SeditGwtException;

	
	/**
	 * officialisation du tableau envoie des mails aux responsables
	 * @param tableauAG
	 */
	void notifierParCourrielAG( TableauAGGWT tableau) throws SeditGwtException;
	
	/**
	 * Génération du courrier aux promus d'un tableauAG
	 * @param tableauAG
	 * @param modeleTypeId
	 * @param dateDecision
	 * @param numeroArretePrefix
	 * @param numeroArreteIndex
	 * @param numeroArreteDTO
	 * @throws SeditGwtException
	 */
    public void createCourrierBatch(TableauAGGWT tableau, String modeleTypeId, Date dateDecision, String numeroArretePrefix, Long numeroArreteIndex, NumeroArreteDTO numeroArreteDTO,
            ContexteExecSignetPersoGWT contexteExecSignetPersoGWT) throws SeditGwtException;

    /**
	 * Retourne la Liste des Paramétrages de Numerotation des Arrêtés pour les Collectivités correspondant à la sélection du Batch
	 * @param tableau
	 * @return
	 * @throws SeditGwtException
	 */
	List<CollectiviteNumerotationArreteParamDTO> getListCollectiviteArreteCourrierBatch(TableauAGGWT tableau) throws SeditGwtException;
	
	/**
	 * Editions KSL : en fonction de l'état du tableau sort la liste des avancements ou la liste des promus
	 * @author aurore.maigron
	 * @param params
	 * @return
	 * @throws SeditGwtException
	 */
	String printCAPByTableau(Map<String, Serializable> params) throws SeditGwtException ;
	
	/**
	 * Editions KSL: sort en fct du type Presentation la liste des promus concours ou des promouvables concours
	 * @param typePresentation
	 * @param strDateDeb
	 * @param strDateFin
	 * @param paramPresCAPId
	 * @return
	 * @throws SeditGwtException
	 */
	String printPromotions(EnumTypePresentationGWT typePresentation,String strDateDeb, String strDateFin, Integer paramPresCAPId) throws SeditGwtException;
	
	
	/**
	 * retourne la liste des paramétrages existants associés au tableau en paramètre
	 * @author aurore.maigron
	 * @param tableau
	 * @return
	 * @throws SeditGwtException
	 */
	List<ParamPresentationCAPDTOGWT> findParamPresentationByTableau(TableauAGGWT tableau,EnumTypePresentationGWT typePresentationGWT) throws SeditGwtException;
	
	/**
	 * 
	 * Retourne la liste de paramétrages existants pour l'organisme en fonction du type de présentation
	 * @param orga
	 * @param typePresentation
	 * @throws SeditGwtException
	 */
	List<ParamPresentationCAPDTOGWT> findParamPresentationByTypePresentation(OrganismeGWT orga, EnumTypePresentationGWT  typePresentation) throws SeditGwtException;
	
	
	/**
	 * Retourne le paramétrage de l'édition associé a l'organisme en fonction de l'état du tableau
	 * @param tableau
	 * @param organisme
	 * @return
	 */
	ParamPresentationCAPDTOGWT findParamPresentationDefautOrganismeByTableau(TableauAGGWT tableau, EnumTypePresentationGWT typePresentationGWT)throws SeditGwtException;
	
	/**
	 * Retourne le paramétrage par défaut associé a l'organisme en fct du type présentation
	 * @param organismeId
	 * @param typePresentationGWT
	 * @param typeTableauGWT
	 * @return
	 * @throws SeditGwtException
	 */
	ParamPresentationCAPDTOGWT findParamPresentationDefautOrganisme(String organismeId, EnumTypePresentationGWT typePresentationGWT, EnumTypeTableauGWT typeTableauGWT)throws SeditGwtException;


	/**
	 *  Calcul des ratios et quotas pour un tableau et éventuellement un grade cible
	 * @param tableau TableauAGGWT
	 * @param gradeProposition GradeGWT
	 * @param recalcul Boolean
	 * @return List<RatiosQuotasParTableauGWT>
	 */
	List<RatiosQuotasParTableauGWT> calculRatiosEtQuotas(TableauAGGWT tableau, GradeGWT gradeProposition, Boolean recalcul)throws SeditGwtException;
	
	/**
	 * Retourne le RatiosQuotasParTableau correspondant 
	 * @param tableauAG
	 * @param grade
	 * @return
	 */
	RatiosQuotasParTableauGWT loadCompleteRatiosQuotasParTableauWithGradeById(Integer ratiosQuotasParTableauId) throws SeditGwtException;
	
	/**
	 * Renvoie la liste des groupes d'agents et les validateurs associés
	 * 
	 * @param tri
	 * @param remplir
	 * @param firstLine
	 * @param limitLine
	 * @param criteria
	 * @param tableauAG Si renseigné les données de validation sont chargées
	 * @throws SeditGwtException
	 */
	List<GroupeTachesDTOGWT> findListGroupsValidateurs(Map<String, Integer> tri, TableauAGGWT tableauAG, CollectiviteGWT collectivite, boolean remplir, 
			long firstLine,long limitLine) throws SeditGwtException ;

	/**
	 * Récupération des informations nécessaires à la relance par courriel de la saisie d'avis
	 * 
	 * @param taskId
	 * @return
	 * @throws SeditGwtException
	 */
	InfoCourrielDTOGWT getMailInfo(Long taskId) throws SeditGwtException;
	/**
	 * Le workflow est-il démarré?
	 * 
	 * @param tableau
	 * @return
	 * @throws SeditGwtException
	 */
	Boolean isDemandesAvisAGDemarre(TableauAGGWT tableau) throws SeditGwtException;
	
	/**
	 * Démarrage du workflow
	 * @param utilisateurGWT TODO
	 * @param tableau
	 * 
	 * @throws SeditGwtException
	 */
	void demarrerDemandesAvisAG(UtilisateurSMRHGWT utilisateurGWT, TableauAGGWT tableau) throws SeditGwtException;
	
	/**
	 * Récupération de la liste des collectivités des propositions du tableauAG
	 * 
	 * @param tableau
	 */
	Set<CollectiviteGWT> findCollectivitesInTableauAG(TableauAGGWT tableau) throws SeditGwtException;
	
	/**
	 * Valider le workflow et sauvegarder le tableau
	 * 
	 * @param tableau
	 * @param task
	 * @param userId
	 * @throws SeditGwtException
	 */
	void checkAndSaveAndClotureTask(TableauAGGWT tableauAG, TacheWfEcheancierDTOGWT task, String userId) throws SeditGwtException;
	
	/**
	 * Clot la tâche de saisie
	 * @param utilisateurGWT TODO
	 * @param taskId
	 */
	void endSaisieAG(UtilisateurSMRHGWT utilisateurGWT, Long taskId) throws SeditGwtException ;
	
	/**
	 * Récupère les informations nécessaire à la saisie des avis : TableauAG, Collectivite, List<String> (id Agents) 
	 * @param utilisateurGWT
	 * @param taskId
	 * @param loggedAs
	 * @param serverAgent
	 * @return
	 */
	Map<String, Object> getSaisieInfoAGByTaskId(UtilisateurSMRHGWT utilisateurGWT, Long taskId) throws SeditGwtException ;

	/**
	 * Récupère les informations nécessaire à la saisie des avis : taskId, TableauAG, Collectivite, List<String> (id Agents) 
	 * @param utilisateurGWT
	 * @param taskName
	 * @param loggedAs
	 * @param serverAgent
	 * @return
	 */
	Map<String, Object> getSaisieInfoAGByTaskName(UtilisateurSMRHGWT utilisateurGWT, String taskName) throws SeditGwtException ;
	
	/**
	 *  Retourne la liste des serviceDTO des propositionAG d'un tableauaG
	 * @param criteria Map<String, Object>
	 * @return List<ServiceDTOGWT>
	 */
	List<ServiceDTOGWT> findListServicesInPropositionAG(Map<String, Object> criteria) throws SeditGwtException;

    /**
     * True s'il y a des proposition ag attachées au concours agent, false sinon
     * @param idConcoursAgent
     * @return
     */
    Boolean isPropositionAgHasConcoursAgent(String idConcoursAgent);

	/**
	 * Rechercher un tableau AG suivant son code ou son libellé et l'organisme
	 * @param codeOrLibelle
	 * @param organisme
	 * @param autres critères
	 * @return List
	 * @throws SeditGwtException
	 */
	List<TableauAGDTOGWT> findListTableauAGDTOByCodeOrLibelle(String codeOrLibelle, OrganismeGWT organisme, Map<String, Object> criteria)	throws SeditGwtException;

    /**
     * Retourne le nombre de propositions promouvable pour un Tableau AG
     * @param code du tableau AG
     * @return count
     */
	Long countPropositionsPromouvableByTableauAG(String code) throws SeditGwtException;
	
	
	void deletePropositionAGById(Integer id)throws SeditGwtException;
	
	Map<String,Date> findDatesForControleInjection(final List<String> codesCollectivite) throws SeditGwtException;
	
	/**
	  * Update le champs promuSPV de la proposition dans l'id est en entree
	  * @param propositionId Integer
	  * @param promu boolean
	  */
	void changePromuSPVPropositionAG(Integer propositionId, boolean promu)throws SeditGwtException;

	List<CadreEmploiDTOGWT> findListCadreEmploiDTO(String idCadreStatutaire, Date date) throws SeditGwtException;
	
    List<CollectiviteNumerotationArreteParamDTO> findCollectiviteByPropositionIds(List<Integer> propositionIds) throws SeditGwtException;
	
	List<CollectiviteNumerotationArreteParamDTO> findCollectiviteByPropositionIds(TableauAGGWT tableau) throws SeditGwtException;
	
	List<CollectiviteNumerotationArreteParamDTO> findCollectiviteByPropositionIds(Map<String, Object> criteria) throws SeditGwtException;
	
	CalculatriceCarriereDTO calculDureeEntreDate(Date dateDebut, Date dateFin) throws SeditGwtException;
	
	CalculatriceCarriereDTO calculDureeAjouterRetirer(Date dateToCalcul, HashMap<String, Integer> paramCalcul) throws SeditGwtException;
	
	CalculatriceCarriereDTO cumulerDuree(CalculatriceCarriereDTO duree1, CalculatriceCarriereDTO duree2, EnumCalculatriceOperation operation) throws SeditGwtException;

	List<TableauAGGWT> findListTableauAGByIdOrganisme(String organismeId);

	/**
	 * Retourne l'agent avec sa liste de carriere et sa liste de FicheGradeEmploi. Seul les fiche grade emploi ouverte sont retourné.
	 */
	AgentGWT loadAgentWithCarriereAndFicheGradeEmploi(String idAgent) throws SeditGwtException;

	Boolean importExcelPropositionAG(Map<String, Object> criteres) throws SeditGwtException;

}
