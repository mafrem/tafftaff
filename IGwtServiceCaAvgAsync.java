package fr.bl.client.grh.ca.avg.remote;

import com.google.gwt.user.client.rpc.AsyncCallback;
import fr.bl.client.grh.ca.avg.model.GrilleAncienneteGWT;
import fr.bl.client.grh.ca.avg.model.RatiosQuotasParTableauGWT;
import fr.bl.client.grh.ca.par.model.ParamRangClassementAGGWT;
import fr.bl.client.grh.ca.par.model.dto.InfoCourrielDTOGWT;
import fr.bl.client.grh.ca.par.model.dto.ParamPresentationCAPDTOGWT;
import fr.bl.client.grh.ca.par.model.dto.TacheWfEcheancierDTOGWT;
import fr.bl.client.grh.coeur.ca.avg.model.AvisAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.PropositionAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT;
import fr.bl.client.grh.coeur.ca.par.model.ParamCaOrgaGWT;
import fr.bl.client.grh.coeur.cs.model.AgentGWT;
import fr.bl.client.grh.coeur.cs.model.GradeGWT;
import fr.bl.client.grh.coeur.cs.model.dto.CadreEmploiDTOGWT;
import fr.bl.client.grh.coeur.cs.model.dto.ContexteExecSignetPersoGWT;
import fr.bl.client.grh.coeur.cs.model.dto.ImprimeDTOGWT;
import fr.bl.client.grh.coeur.cs.model.dto.RetourEtListeErreurDTOGWT;
import fr.bl.client.grh.coeur.cs.model.parametrage.CollectiviteGWT;
import fr.bl.shared.grh.car.dto.CalculatriceCarriereDTO;
import fr.bl.shared.grh.car.dto.InjectionFicheCarriereRetourDTO;
import fr.bl.shared.grh.car.enums.EnumCalculatriceOperation;
import fr.bl.shared.grh.coeur.dto.CollectiviteNumerotationArreteParamDTO;
import fr.bl.shared.grh.coeur.dto.NumeroArreteDTO;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IGwtServiceCaAvgAsync {
	void calculListDetailAvancementVerificationPropositionAg(
			java.lang.Integer tableauId,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<java.util.List<java.io.Serializable>>> arg2);

	void calculPropositionsAGForTableauAG(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAGGWT,
			fr.bl.client.grh.coeur.cs.model.GradeGWT gradeCibleGWT,
			AsyncCallback<Void> callback);

	void calculRatiosEtQuotas(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableau,
			fr.bl.client.grh.coeur.cs.model.GradeGWT gradeProposition,
			java.lang.Boolean recalcul,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.RatiosQuotasParTableauGWT>> arg4);

	void changeDateOfPropositionAG(java.lang.Integer propositionId,
			java.lang.String nomChamps, java.util.Date date,
			AsyncCallback<Void> callback);

	void changePromuPropositionAG(java.lang.Integer propositionId,
			boolean promu,
			AsyncCallback<Void> callback);

	void changeRangClassementOfPropositionAG(java.lang.Integer propositionId,
			java.lang.String saisieLibre, java.lang.String valeur,
			AsyncCallback<Void> callback);

	void changeVerifieeListPropositionAG(
			java.util.List<java.lang.String[]> listTablePropositionIdAndBooleanVerifiee,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<java.lang.String[]>> arg2);

	void changeVerifieePropositionAG(
			java.lang.Integer propositionId,
			fr.bl.client.grh.coeur.ca.avg.model.enums.EnumVerifierAGGWT enumVerifierGWT,
			AsyncCallback<Void> callback);

	void checkAndSaveAndClotureTask(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAG,
			fr.bl.client.grh.ca.par.model.dto.TacheWfEcheancierDTOGWT task,
			java.lang.String userId,
			AsyncCallback<Void> callback);

	void cloturerTacheWF(
			fr.bl.client.grh.ca.par.model.dto.TacheWfEcheancierDTOGWT task,
			java.lang.String userId,
			AsyncCallback<Void> callback);

	void computeAndSaveGrilleAnciennete(
			java.lang.String carriereId,
			java.lang.Long codeRegroupement,
			AsyncCallback<GrilleAncienneteGWT> callback);

    void controleListPropositionsAGAvantInjection(java.util.List<fr.bl.client.grh.coeur.cs.model.parametrage.CollectiviteGWT> collectivites,
            AsyncCallback<Void> callback);

	void countAgentOfTableauAG(
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			java.lang.String filter,
			AsyncCallback<Long> callback);

	void countAllAvisAG(
			java.util.HashMap<java.lang.String, java.lang.Object> criteres,
			java.lang.String filtre,
			AsyncCallback<Long> callback);

	void countPromotionsConcoursAgent(
			java.util.HashMap<java.lang.String, java.lang.Object> criteria,
			java.lang.String filter,
			AsyncCallback<Long> callback);

	void countPropositionAGInjection(
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			java.lang.String filter,
			AsyncCallback<Long> callback);

	void countPropositionWithCriteria(
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			java.lang.String filter,
			AsyncCallback<Long> callback);

	void countRatiosQuotasParTableauAG(
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			java.lang.String filter,
			AsyncCallback<Long> callback);

	void countTableauAG(
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			java.lang.String filter,
			AsyncCallback<Long> callback);

	void createCourrierBatch(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableau,
			java.lang.String modeleTypeId, java.util.Date dateDecision,
			java.lang.String numeroArretePrefix,
			java.lang.Long numeroArreteIndex, NumeroArreteDTO numeroArreteDTO,
			ContexteExecSignetPersoGWT contexteExecSignetPersoGWT,
			AsyncCallback<Void> callback);
	
	void getListCollectiviteArreteCourrierBatch(TableauAGGWT tableau, AsyncCallback<List<CollectiviteNumerotationArreteParamDTO>> callback);

	void createPropositionConcours(
			fr.bl.client.grh.ca.avg.model.dto.ReussiteConcoursDTOGWT reussiteConcoursDTOGWT,
			AsyncCallback<PropositionAGGWT> callback);

    void deleteProposition(PropositionAGGWT propositionAG, com.google.gwt.user.client.rpc.AsyncCallback<java.lang.Void> callback);
	
	void deleteAllPropositionsByTableauAG(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAGGWT,
			AsyncCallback<Void> callback);

	void deleteAvisAG(fr.bl.client.grh.coeur.ca.avg.model.AvisAGGWT avis,
			AsyncCallback<Void> callback);

	void deleteAvisAGById(java.lang.Integer avisId,
			AsyncCallback<Void> callback);

	void deletePropositionsByTableauAGAndGradeCible(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAGGWT,
			fr.bl.client.grh.coeur.cs.model.GradeGWT gradeCibleGWT,
			AsyncCallback<Void> callback);
	
	void deleteAllPropositionsSimulation(AsyncCallback<Void> callback);

	void deleteTableauAG(fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAG,
			AsyncCallback<Void> callback);
	
	void deleteListTableauAG(List<Integer> listeIdTableaux, AsyncCallback<Boolean> callback);
	
	void demarrerDemandesAvisAG(
			fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT utilisateurGWT,
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableau,
			AsyncCallback<Void> callback);

	void endSaisieAG(
			fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT utilisateurGWT,
			java.lang.Long taskId,
			AsyncCallback<Void> callback);

	void excluePropositionManuellement(java.lang.Integer propositionId,
			java.lang.String motif,
			AsyncCallback<Void> callback);

	void exclurePropositionAG(java.lang.Integer propositionAGId,
			AsyncCallback<Void> callback);
	
	void exclureListPropositionAG(List<Integer> listPropositionsId,
            AsyncCallback<Void> callback);

	void exclureReintegrerPropositionAG(java.lang.Integer propositionId,
			boolean promouvable,
			AsyncCallback<Void> callback);

	void exportPropositionAGForExcel(
			java.util.Map<java.lang.String, java.lang.Integer> tri,
			java.util.Map<java.lang.String, java.lang.Object> criteres,
			java.lang.String filter,
			com.google.gwt.user.client.rpc.AsyncCallback<String> arg4);

	void exportRatiosQuotasParTableauForExcel(
			java.util.Map<java.lang.String, java.lang.Integer> tri,
			java.util.Map<java.lang.String, java.lang.Object> criteres,
			java.lang.String filter,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<java.util.List<java.lang.String>>> arg4);

	void findAvisAGById(
			java.lang.Integer avisId,
			AsyncCallback<AvisAGGWT> callback);

	void findCategorieDTOByCadreStatutaireAndDate(
			java.lang.String idCadreStatutaire,
			java.util.Date date,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.CategorieDTOGWT>> arg3);

	void findCollectivitesInTableauAG(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableau,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.Set<fr.bl.client.grh.coeur.cs.model.parametrage.CollectiviteGWT>> arg2);

	void findConditionEtDetailByConditionId(
			java.lang.Integer conditionId,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.ConditionDePropositionEtDetailDTOGWT>> arg2);

	void findEcheanceCAPByEcheancierId(
			java.lang.Integer echeancierCAPId,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.ca.par.model.EcheanceCAPGWT>> arg2);

	void findGradeById(
			java.lang.String id,
			AsyncCallback<GradeGWT> callback);

	void findListAgentAyantPropositionDansTableauAG(
			java.util.Map<java.lang.String, java.lang.Integer> tri,
			java.util.Map<java.lang.String, java.lang.Object> criteres,
			long firstLine,
			long limitLine,
			java.lang.String filter,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<java.util.List<java.io.Serializable>>> arg6);

	void findListAgentDTOByNomOrMatricule(
			java.lang.String nomOuMatricule,
			fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT organisme,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.AgentDTOGWT>> arg3);

	void findListAgentDansTableauAGByNameOrMatricule(
			java.util.Map<java.lang.String, java.lang.Object> criteres,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.AgentDTOGWT>> arg2);

	void findListAnciennete(
			java.lang.String carriereId,
			java.lang.Long codeRegroupement,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.GrilleAncienneteDTOGWT>> arg3);

	void findListAppreciation(
			java.lang.String organismeId,
			fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypeAEAGGWT typeGWT,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.par.model.dto.ParamAppreciationAvisDTOGWT>> arg3);

	void findListCadreEmploiDTOAndCountPropositionByCriteresAndFiliere(
			java.util.Map<java.lang.String, java.lang.Object> criteriaProposition,
			java.lang.String idFiliere,
			java.util.Date date,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.CadreEmploiDTOGWT>> arg4);

	void findListCadreEmploiDTOByFiliere(
			java.lang.String idFiliere,
			java.util.Date date,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.CadreEmploiDTOGWT>> arg3);

	void findListConditionAG(
			java.lang.Integer propositionId,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.ConditionDePropositionDTOGWT>> arg2);

	void findListEcheancierCAPDTOByOrganisme(
			java.lang.String idOrganisme,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.par.model.dto.EcheancierCAPDTOGWT>> arg2);

	void findListEchelonAndIndiceOfGradeByCode(
			java.lang.String codeGrade,
			fr.bl.client.grh.coeur.cs.model.parametrage.CadreStatutaireGWT cadreStatutaire,
			java.util.Date dateDebut,
			java.util.Date dateFin,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.EchelonEtIndicesDTOGWT>> arg5);

	void findListFiliereDTOAndCountPropositionByCriteresAndCadreStatutaireAndDate(
			java.util.Map<java.lang.String, java.lang.Object> criteriaProposition,
			java.lang.String idCadreStatut,
			java.util.Date date,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.FiliereDTOGWT>> arg4);

	void findListFiliereDTOByCadreStatutaireAndDate(
			java.lang.String idCadreStatut,
			java.util.Date date,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.FiliereDTOGWT>> arg3);

	void findListFonctionAGDTOByCadreStatutaire(
			java.lang.String idCadreStatutaire,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.par.model.dto.FonctionAGDTOGWT>> arg2);

	void findListGroupeHierarchiqueDTOByCadreStatutaireAndDate(
			java.lang.String idCadreStatut,
			java.util.Date date,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.GroupeHierarchiqueDTOGWT>> arg3);

	void findListGroupsValidateurs(
			java.util.Map<java.lang.String, java.lang.Integer> tri,
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAG,
			fr.bl.client.grh.coeur.cs.model.parametrage.CollectiviteGWT collectivite,
			boolean remplir,
			long firstLine,
			long limitLine,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.par.model.dto.GroupeTachesDTOGWT>> arg7);

	void findListJustificatifsRetardAG(
			java.lang.Integer detailId,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.JustificatifRetardAGDTOGWT>> arg2);

	void findListParamPresentationCAPDTO(
			java.util.Map<java.lang.String, java.lang.Integer> tri,
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			long startIndex,
			long maxResult,
			java.lang.String filter,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.par.model.dto.ParamPresentationCAPDTOGWT>> arg6);

	void findListPromotionsConcoursAgent(
			Map<java.lang.String, java.lang.Integer> tri,
			java.util.HashMap<java.lang.String, java.lang.Object> criteria,
			long firstLine,
			long limitLine,
			java.lang.String filter,
			boolean showMoreColumns,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.PropositionsConcoursAgentDTOGWT>> arg7);

	void findListPropositionAGDTO(
			java.util.Map<java.lang.String, java.lang.Integer> tri,
			java.util.Map<java.lang.String, java.lang.Object> criteres,
			long firstLine,
			long limitLine,
			java.lang.String filter,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.PropositionAGDTOGWT>> arg6);

	void findListPropositionIdSimule(
			java.lang.String id,
			java.lang.Long codeRegroupement,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<java.lang.Integer>> arg3);

	void findListPropositionNonPromouvableAGDTO(
			java.util.Map<java.lang.String, java.lang.Integer> tri,
			java.util.Map<java.lang.String, java.lang.Object> criteres,
			long firstLine,
			long limitLine,
			java.lang.String filter,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.PropositionNonPromouvableAGDTOGWT>> arg6);

	void findListRatiosQuotasParTableauAGDTO(
			java.util.Map<java.lang.String, java.lang.Integer> tri,
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			long startIndex,
			long endIndex,
			java.lang.String filter,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.RatiosQuotasParTableauAGDTOGWT>> arg6);

	void findListRoleOrganisationnelByAgentId(
			java.lang.String agentId,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.nr.model.dto.RoleOrganisationnelDTOGWT>> arg2);

	void findListServiceDTOByCollectivite(
			java.lang.String collectiviteId,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.ServiceDTOGWT>> arg2);

	void findListServicesInPropositionAG(
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.ServiceDTOGWT>> arg2);

	void findListTableauAG(
			java.util.Map<java.lang.String, java.lang.Integer> tri,
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			long startIndex,
			long endIndex,
			java.lang.String filter,
			boolean showMoreColumns,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<java.util.List<java.io.Serializable>>> arg7);

	void findListValeurClassementAGByParamRangClassementAG(
			fr.bl.client.grh.ca.par.model.ParamRangClassementAGGWT param,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.par.model.ValeurClassementAGGWT>> arg2);

	void findListeGradeDTOAndCountPropositionByCriteresAndCadreEmploi(
			java.util.Map<java.lang.String, java.lang.Object> criteriaProposition,
			java.lang.String idCadreEmploi,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.GradeDTOGWT>> arg3);

	void findListeGradeDTOByCadreEmploi(
			java.lang.String idCadreEmploi,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.GradeDTOGWT>> arg2);

	void findParamCaOrgaComplete(
			String idOrganisme,
			AsyncCallback<ParamCaOrgaGWT> callback);

	void findParamPresentationByTableau(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableau,
			fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypePresentationGWT typePresentationGWT,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.par.model.dto.ParamPresentationCAPDTOGWT>> arg3);

	void findParamPresentationByTypePresentation(
			fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT orga,
			fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypePresentationGWT typePresentation,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.par.model.dto.ParamPresentationCAPDTOGWT>> arg3);

	void findParamPresentationDefautOrganisme(
			String organismeId,
			fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypePresentationGWT typePresentationGWT,
			fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypeTableauGWT typeTableauGWT,
			AsyncCallback<ParamPresentationCAPDTOGWT> callback);

	void findParamPresentationDefautOrganismeByTableau(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableau,
			fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypePresentationGWT typePresentationGWT,
			AsyncCallback<ParamPresentationCAPDTOGWT> callback);

	void findParamRangClassementAGByOrganisme(
			fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT organismeGWT,
			AsyncCallback<ParamRangClassementAGGWT> callback);

	void findPropositionAGInjection(
			java.util.Map<java.lang.String, java.lang.Integer> tri,
			java.util.Map<java.lang.String, java.lang.Object> criteres,
			long firstLine,
			long limitLine,
			java.lang.String filter,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.PropositionAGDTOGWT>> arg6);

	void findPropositonAGById(
			java.lang.Integer propositionId,
			AsyncCallback<PropositionAGGWT> callback);

	void findStatutByCriteria(
			java.util.HashMap<java.lang.String, java.lang.Integer> tri,
			java.util.HashMap<java.lang.String, java.lang.Object> criteres,
			long firstLine,
			long limitLine,
			java.lang.String filter,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.coeur.cs.model.dto.StatutDTOGWT>> arg6);

	void findTableauAGById(
			java.lang.Integer tableauId,
			AsyncCallback<TableauAGGWT> callback);

	void getCurrentTaskTableau(
			java.lang.String tableauId,
			java.lang.String userId,
			AsyncCallback<TacheWfEcheancierDTOGWT> callback);

	void getDetailTache(
			java.lang.String actorId,
			java.lang.String taskName,
			AsyncCallback<TacheWfEcheancierDTOGWT> callback);

	void getDetailTache(
			long taskId,
			AsyncCallback<TacheWfEcheancierDTOGWT> callback);

	void getImprimeDTOByPropositionId(Integer propositionId, AsyncCallback<ImprimeDTOGWT> arg2);
	void getImprimeDTOForSPVByPropositionId(Integer propositionId, AsyncCallback<ImprimeDTOGWT> arg2);
	
	void loadContexteExecSignetPersoByPropositionAG(List<Integer> listIdProposition, final String modeleTypeId, AsyncCallback<ContexteExecSignetPersoGWT> arg2);
	void loadContexteExecSignetPersoForSPVByPropositionAG(List<Integer> listIdProposition, final String modeleTypeId, AsyncCallback<ContexteExecSignetPersoGWT> arg2);
    void loadContexteExecSignetPersoByTableauAG(TableauAGGWT tableau, final String modeleTypeId, AsyncCallback<ContexteExecSignetPersoGWT> callback);
    void loadContexteExecSignetPerso4InjectAG(CollectiviteGWT collectivite, final String modeleTypeId, AsyncCallback<ContexteExecSignetPersoGWT> callback);

	void getMailInfo(
			java.lang.Long taskId,
			AsyncCallback<InfoCourrielDTOGWT> callback);

	void getSaisieInfoAGByTaskId(
			fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT utilisateurGWT,
			java.lang.Long taskId,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.Map<java.lang.String, java.lang.Object>> arg3);

	void getSaisieInfoAGByTaskName(
			fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT utilisateurGWT,
			java.lang.String taskName,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.Map<java.lang.String, java.lang.Object>> arg3);

	void imprimerPropositionsBatch(java.lang.String user,
			java.lang.String rep,
			java.util.List<java.lang.Integer> propositionIds,
			AsyncCallback<RetourEtListeErreurDTOGWT> callback);

	void imprimerPropositionsBatch(String username, String userId, String rep, Map<String, Object> criteria, AsyncCallback<Void> callback);
	
	void initPropositionAndReturnPropositionAG(
			java.lang.Integer tableauAGId,
			java.lang.String carriereId,
			java.lang.Long codeRegroupement,
			AsyncCallback<PropositionAGGWT> callback);

	void injectPropositionsAGById(
			java.util.List<java.lang.Integer> propositionsId,
			fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT currentUserGWT,
			AsyncCallback<InjectionFicheCarriereRetourDTO> callback);

	void isDemandesAvisAGDemarre(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableau,
			AsyncCallback<Boolean> callback);

	void isPropositionAgHasConcoursAgent(java.lang.String idConcoursAgent,
			AsyncCallback<Boolean> callback);

	void loadCompletePropositonAGById(
			java.lang.Integer propositionId,
			AsyncCallback<PropositionAGGWT> callback);

	void loadCompleteRatiosQuotasParTableauById(
			java.lang.Integer ratiosQuotasParTableauId,
			AsyncCallback<RatiosQuotasParTableauGWT> callback);

	void loadCompleteRatiosQuotasParTableauByTableauAGAndGrade(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAGGWT,
			fr.bl.client.grh.coeur.cs.model.GradeGWT gradeGWT,
			AsyncCallback<RatiosQuotasParTableauGWT> callback);

	void loadCompleteRatiosQuotasParTableauWithGradeById(
			java.lang.Integer ratiosQuotasParTableauId,
			AsyncCallback<RatiosQuotasParTableauGWT> callback);

	void loadCompleteTableauAG(
			java.lang.Integer tableauAGId,
			AsyncCallback<TableauAGGWT> callback);

	void loadCompleteTableauAGWithoutProposition(
			java.lang.Integer tableauAGId,
			AsyncCallback<TableauAGGWT> callback);

	void loadListEcheancierCAPDTOByOrganismeAndTypeAvancement(
			fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT organisme,
			fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypeAEAGGWT type,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.par.model.dto.EcheancierCAPDTOGWT>> arg3);

	void loadPromotions(
			fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT organisme,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.ReussiteConcoursDTOGWT>> arg2);

	void loadReussiteConcoursDTONonValidees(
			fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT organisme,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.ReussiteConcoursDTOGWT>> arg2);

	void notifierParCourrielAG(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableau,
			AsyncCallback<Void> callback);

	void officialisePromotion(java.lang.Integer propositionId,
			java.lang.Boolean promu,
			AsyncCallback<Void> callback);

	void printCAPByTableau(Map<String, Serializable> params,
			AsyncCallback<String> callback);

	void printPromotions(
			fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypePresentationGWT typePresentation,
			java.lang.String strDateDeb, java.lang.String strDateFin,
			java.lang.Integer paramPresCAPId,
			AsyncCallback<String> callback);

	void purgerNonPromouvablesDesTableaux(
			java.util.List<fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT> listeTableaux,
			boolean supprPropositions, boolean supprAvis,
			boolean supprConditions,
			AsyncCallback<Void> callback);

	void purgerNonPromusDesTableaux(
			java.util.List<fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT> listeTableaux,
			boolean supprPropositions, boolean supprAvis,
			boolean supprConditions,
			AsyncCallback<Void> callback);

	void purgerPromusDesTableaux(
			java.util.List<fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT> listeTableaux,
			boolean supprPropositions, boolean supprAvis,
			boolean supprConditions,
			AsyncCallback<Void> callback);

	void reCalculPropositions(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAGGWT,
			java.util.Set<fr.bl.client.grh.coeur.ca.avg.model.PropositionAGGWT> listPropositionsAGARecalculer,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.Set<fr.bl.client.grh.coeur.ca.avg.model.PropositionAGGWT>> arg3);

	void reCalculPropositionsById(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAGGWT,
			java.util.List<java.lang.Integer> listIdPropositionsAGARecalculer,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.Set<fr.bl.client.grh.coeur.ca.avg.model.PropositionAGGWT>> arg3);

	void reCalculTableauAG(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAGGWT,
			AsyncCallback<Void> callback);

	void reCalculTableauAG(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAGGWT,
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			AsyncCallback<Void> callback);

	void recalculClassementPropositionAG(
			java.lang.Integer idpropAGGWT,
			AsyncCallback<PropositionAGGWT> callback);

	void recalculClassementPropositionAG(
			java.util.List<java.lang.Integer> idpropAGGWT,
			AsyncCallback<Void> callback);

	void recalculClassementPropositionAG(
			java.util.Map<java.lang.String, java.lang.Object> criteres,
			AsyncCallback<Void> callback);

	void refuserPropositionAG(java.lang.Integer propositionAGId,
			java.util.Date dateRefus, java.lang.String motifRefus,
			AsyncCallback<Void> callback);

	void reintegrerPropositionAGNonPromouvable(
			java.lang.Integer propositionAGId,
			AsyncCallback<Void> callback);

	void saveAndReturnTableauAG(
			fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT utilisateurGWT,
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAGGWT,
			boolean startEcheancier,
			AsyncCallback<TableauAGGWT> callback);

    void saveArreteAG(java.lang.Integer propositionId, java.lang.String modeleTypeId, java.util.Date dateGeneration, java.lang.Boolean officiel,
            java.lang.String numeroArrete, boolean launchEdition, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT,
            AsyncCallback<ImprimeDTOGWT> callback);

    void saveArreteSPVAG(Integer propositionId, String modeleTypeId, Date dateGeneration, Boolean officiel,
            String numeroArrete, boolean launchEdition, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT,
            AsyncCallback<ImprimeDTOGWT> arg6);
    
    void saveArreteAGTableau(fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableau, String modeleTypeId, java.util.Date dateDecision,
            java.lang.Boolean officiel, java.lang.String numeroArretePrefix, java.lang.Long numeroArreteIndex, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, 
            Boolean editerDejaExistant, NumeroArreteDTO startNumber,
            AsyncCallback<RetourEtListeErreurDTOGWT> callback);

    void saveArreteAGBatch(List<Integer> propositionIds, String modeleTypeId, java.util.Date dateDecision,
            Boolean officiel, String numeroArretePrefix, Long numeroArreteIndex, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, 
            Boolean editerDejaExistant, NumeroArreteDTO startNumber,
            AsyncCallback<RetourEtListeErreurDTOGWT> arg6);
    
    void saveArreteAGBatch(Map<String, Object> criteria, String userId, String modeleTypeId, java.util.Date dateDecision,
            Boolean officiel, String numeroArretePrefix, Long numeroArreteIndex, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, 
            Boolean editerDejaExistant, NumeroArreteDTO startNumber,
            AsyncCallback<Void> arg6);
    
    void saveArreteSPVAGBatch(List<Integer> propositionIds, String modeleTypeId, java.util.Date dateDecision,
            Boolean officiel, String numeroArretePrefix, Long numeroArreteIndex, NumeroArreteDTO numeroArreteDTO, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, Boolean editerDejaExistant,
           AsyncCallback<RetourEtListeErreurDTOGWT> arg6);

    void saveArreteAGBatch4Inject(fr.bl.client.grh.coeur.cs.model.parametrage.CollectiviteGWT collectivite, String modeleTypeId,
            java.util.Date dateDecision, java.lang.Boolean officiel, java.lang.String numeroArretePrefix, java.lang.Long numeroArreteIndex, 
            ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, Boolean editerDejaExistant, NumeroArreteDTO startNumber,
            AsyncCallback<RetourEtListeErreurDTOGWT> callback);
	void savePropositionAG(
			fr.bl.client.grh.coeur.ca.avg.model.PropositionAGGWT propositionAGGWT,
			java.util.HashMap<java.lang.String, java.lang.Object> map,
			AsyncCallback<Void> callback);

	void saveRangClassementAG(
			fr.bl.client.grh.coeur.ca.avg.model.RangClassementAGGWT rangClassementAGGWT,
			AsyncCallback<Void> callback);

	void saveRatiosQuotasParTableau(
			fr.bl.client.grh.ca.avg.model.RatiosQuotasParTableauGWT ratiosQuotasParTableau,
			AsyncCallback<RatiosQuotasParTableauGWT> callback);

	void saveTableauAG(fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAGGWT,
			AsyncCallback<Void> callback);

	void saveTableauAndClotureTache(
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAG,
			fr.bl.client.grh.ca.par.model.dto.TacheWfEcheancierDTOGWT task,
			java.lang.String userId,
			AsyncCallback<TableauAGGWT> callback);

	void searchAvisAG(
			java.util.HashMap<java.lang.String, java.lang.Integer> tris,
			java.util.HashMap<java.lang.String, java.lang.Object> criteres,
			long numLigne,
			long nbLignes,
			java.lang.String filtre,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.AvisAGDTOGWT>> arg6);

	void searchEnCoursTableauAG(
	        final String idOrganisme, final String type,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.EnCoursTableauAGDTOGWT>> arg2);

	void simulPropositionAndReturnPropositionAG(
			java.lang.String carriereId,
			java.lang.Long codeRegroupement,
			AsyncCallback<PropositionAGGWT> callback);

	void simulePropositionsSurConditions(
			java.util.HashMap<java.lang.String, java.lang.Object> mapCriteres,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.PropositionSimuleDTOGWT>> arg2);

	void startEcheancierCAP(
			java.util.List<fr.bl.client.grh.coeur.ca.par.model.EcheanceCAPGWT> listeEcheances,
			fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT tableauAG,
			java.lang.String userId,
			AsyncCallback<Void> callback);

	void updateAllAvisAG4CurrentAgentAndDate(
			fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT utilisateurGWT,
			java.util.List<fr.bl.client.grh.ca.avg.model.dto.AvisAGDTOGWT> listAvisDto,
			AsyncCallback<Void> callback);

	void updateAvisAG(
			fr.bl.client.grh.coeur.ca.avg.model.AvisAGGWT avis,
			java.lang.Integer propositionId,
			AsyncCallback<AvisAGGWT> callback);

	void updateAvisAG(
			fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT utilisateurGWT,
			java.lang.Integer avisId,
			java.lang.Integer propositionId,
			java.lang.Integer appreciationId,
			java.lang.String qualite,
			java.lang.String commentaire,
			AsyncCallback<AvisAGGWT> callback);

	void verifieIntegriteProposition(
			java.lang.Integer tableauAGId,
			java.lang.String carriereId,
			java.lang.Long codeRegroupement,
			java.lang.String gradeCibleId,
			AsyncCallback<PropositionAGGWT> callback);

	void findListTableauAGDTOByCodeOrLibelle(
			java.lang.String codeOrLibelle,
			fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT organisme,
			java.util.Map<java.lang.String, java.lang.Object> criteria,
			com.google.gwt.user.client.rpc.AsyncCallback<java.util.List<fr.bl.client.grh.ca.avg.model.dto.TableauAGDTOGWT>> arg);

	void countPropositionsPromouvableByTableauAG(
			String code,
			AsyncCallback<Long> callback);
	
	void deletePropositionAGById(Integer id, com.google.gwt.user.client.rpc.AsyncCallback<Void> result);
	 
	void findDatesForControleInjection(final List<String> codesCollectivite, com.google.gwt.user.client.rpc.AsyncCallback<Map<String,Date>> result);
	
	void changePromuSPVPropositionAG(java.lang.Integer propositionId,
			boolean promu,
			AsyncCallback<Void> callback);

    void loadContexteExecSignetPersoByCriteriaAG(Map<String, Object> criteria, String modeleTypeId, AsyncCallback<ContexteExecSignetPersoGWT> callback);
    
    void findListCadreEmploiDTO(String idCadreStatutaire, Date date, AsyncCallback<List<CadreEmploiDTOGWT>> callback);
    
    void findCollectiviteByPropositionIds(List<Integer> propositionIds, AsyncCallback<List<CollectiviteNumerotationArreteParamDTO>> callback);
    void findCollectiviteByPropositionIds(TableauAGGWT tableau, AsyncCallback<List<CollectiviteNumerotationArreteParamDTO>> callback);
    void findCollectiviteByPropositionIds(Map<String, Object> criteria, AsyncCallback<List<CollectiviteNumerotationArreteParamDTO>> callback);
    
    void calculDureeEntreDate(Date dateDebut, Date dateFin, AsyncCallback<CalculatriceCarriereDTO> callback);
    
    void calculDureeAjouterRetirer(Date dateToCalcul, HashMap<String, Integer> paramCalcul, AsyncCallback<CalculatriceCarriereDTO> callback);
    
    void cumulerDuree(CalculatriceCarriereDTO duree1, CalculatriceCarriereDTO duree2, EnumCalculatriceOperation operation, AsyncCallback<CalculatriceCarriereDTO> callback);
    
	void findListTableauAGByIdOrganisme(String organismeId,AsyncCallback<List<TableauAGGWT>> callback);

	void loadAgentWithCarriereAndFicheGradeEmploi(String idAgent, AsyncCallback<AgentGWT> callback);

	void importExcelPropositionAG(java.util.Map<java.lang.String, java.lang.Object> criteres, AsyncCallback<Boolean> callback);

}
