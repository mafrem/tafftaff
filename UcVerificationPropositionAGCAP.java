package fr.sedit.grh.ca.avg.usecases.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sedit.core.common.usecases.AbstractSecuredUc;
import fr.sedit.core.security.DataSecured;
import fr.sedit.core.tools.Duration;
import fr.sedit.core.tools.UtilsDate;
import fr.sedit.grh.ca.avg.model.AncienneteFonction;
import fr.sedit.grh.ca.avg.model.GrilleAnciennete;
import fr.sedit.grh.ca.avg.model.dto.AvisAGDTO;
import fr.sedit.grh.ca.avg.model.dto.DetailAvancementAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGAExporterDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionNonPromouvableAGDTO;
import fr.sedit.grh.ca.avg.services.IServiceGrilleAnciennete;
import fr.sedit.grh.ca.avg.services.rules.AbstractRuleAvg;
import fr.sedit.grh.ca.avg.usecases.IUcVerificationPropositionAGCAP;
import fr.sedit.grh.ca.par.model.ParamRangClassementAG;
import fr.sedit.grh.ca.par.model.ValeurClassementAG;
import fr.sedit.grh.ca.par.model.enums.EnumModeClassement;
import fr.sedit.grh.ca.par.services.IServiceParamRangClassementAG;
import fr.sedit.grh.ca.par.services.IServiceValeurClassementAG;
import fr.sedit.grh.coeur.ca.ave.model.enums.EnumVerifier;
import fr.sedit.grh.coeur.ca.avg.model.ConditionDeProposition;
import fr.sedit.grh.coeur.ca.avg.model.DetailCondition;
import fr.sedit.grh.coeur.ca.avg.model.PropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.RangClassementAG;
import fr.sedit.grh.coeur.ca.avg.model.TableauAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumVerifierAG;
import fr.sedit.grh.coeur.ca.avg.services.IServiceConditionDeProposition;
import fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG;
import fr.sedit.grh.coeur.ca.avg.services.IServiceRangClassementAG;
import fr.sedit.grh.coeur.ca.avg.services.IServiceTableauAG;
import fr.sedit.grh.coeur.cs.model.Agent;
import fr.sedit.grh.coeur.cs.model.IndiceBrutMajore;
import fr.sedit.grh.coeur.cs.model.dto.CadreEmploiDTO;
import fr.sedit.grh.coeur.cs.model.dto.FiliereDTO;
import fr.sedit.grh.coeur.cs.model.dto.GradeDTO;
import fr.sedit.grh.coeur.cs.model.parametrage.Organisme;
import fr.sedit.grh.coeur.cs.services.IServiceAgent;
import fr.sedit.grh.coeur.cs.services.IServiceCadreEmploi;
import fr.sedit.grh.coeur.cs.services.IServiceFiliere;
import fr.sedit.grh.coeur.cs.services.IServiceGrade;
import fr.sedit.grh.coeur.cs.services.IServiceIndiceBrutMajore;
import fr.sedit.grh.coeur.cs.services.IServiceOrganisme;
import fr.sedit.grh.common.ExcelDocumentHelper;
import fr.sedit.grh.common.UtilsString;


/**
 *	@author magali.duretti
 *	Date création : 14 févr. 08
 */
@Transactional
public class UcVerificationPropositionAGCAP extends AbstractSecuredUc implements IUcVerificationPropositionAGCAP{

	//	Injection Spring**********************************************************
    private IServicePropositionAG servicePropositionAG;
    public void setServicePropositionAG(IServicePropositionAG servicePropositionAG) {
        this.servicePropositionAG = servicePropositionAG;
    }
    private IServiceGrade serviceGrade;
    public void setServiceGrade(IServiceGrade serviceGrade) {
        this.serviceGrade = serviceGrade;
    }
    private IServiceCadreEmploi serviceCadreEmploi;
    public void setServiceCadreEmploi(IServiceCadreEmploi serviceCadreEmploi) {
        this.serviceCadreEmploi = serviceCadreEmploi;
    }
    private IServiceFiliere serviceFiliere = null;	
	public void setServiceFiliere(IServiceFiliere filiere) {
		this.serviceFiliere = filiere;
	}
    private IServiceParamRangClassementAG serviceParamRangClassementAG;
    public void setServiceParamRangClassementAG(IServiceParamRangClassementAG serviceParamRangClassementAG) {
        this.serviceParamRangClassementAG = serviceParamRangClassementAG;
    }
    private IServiceValeurClassementAG serviceValeurClassementAG;
    public void setServiceValeurClassementAG(IServiceValeurClassementAG serviceValeurClassementAG) {
        this.serviceValeurClassementAG = serviceValeurClassementAG;
    }
    private IServiceRangClassementAG serviceRangClassementAG;
    public void setServiceRangClassementAG(IServiceRangClassementAG serviceRangClassementAG) {
        this.serviceRangClassementAG = serviceRangClassementAG;
    }
    private IServiceAgent serviceAgent;
    public void setServiceAgent(IServiceAgent serviceAgent) {
        this.serviceAgent = serviceAgent;
    }
    private IServiceConditionDeProposition serviceConditionDeProposition;
    public void setServiceConditionDeProposition(IServiceConditionDeProposition serviceConditionDeProposition) {
        this.serviceConditionDeProposition = serviceConditionDeProposition;
    }
    private IServiceGrilleAnciennete serviceGrilleAnciennete;
    public void setServiceGrilleAnciennete(IServiceGrilleAnciennete serviceGrilleAnciennete) {
        this.serviceGrilleAnciennete = serviceGrilleAnciennete;
    }
    private IServiceIndiceBrutMajore serviceIndiceBrutMajore;
    public void setServiceIndiceBrutMajore(IServiceIndiceBrutMajore serviceIndiceBrutMajore) {
        this.serviceIndiceBrutMajore = serviceIndiceBrutMajore;
    }
    
    @Autowired
    private IServiceOrganisme serviceOrganisme;
    
    @Autowired
    private IServiceTableauAG serviceTableauAG;
        
	//Fin injection Spring**********************************************************
	
    public UcVerificationPropositionAGCAP() {
        super(ModuleCode.AV);
    }
    
    
	private static final Log log = LogFactory.getLog(UcVerificationPropositionAGCAP.class);

    /**w
	 * @see IUcVerificationPropositionAGCAP#countPropositionWithCriteria(Map, String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	@DataSecured
	public Long countPropositionWithCriteria(Map<String, Object> criteria, String filter) {
		return servicePropositionAG.countPropositionWithCriteria(criteria, filter);
	}
	
	 /**
	 * @see IUcVerificationPropositionAGCAP#findListPropositionAGDTO(Map, Map, long, long, String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	@DataSecured
	public List<PropositionAGDTO> findListPropositionAGDTO(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter) {
		return servicePropositionAG.findListPropositionAGDTO(tri, criteres, firstLine, limitLine, filter);
	}
    
	/**
	 * @see IUcVerificationPropositionAGCAP#findListeGradeDTOByCadreEmploi(String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<GradeDTO> findListeGradeDTOByCadreEmploi(String idCadreEmploi) {
		return serviceGrade.findListeGradeDTOByCadreEmploi(idCadreEmploi);
	}
	/**
	 * @see IUcVerificationPropositionAGCAP#findListeGradeDTOAndCountPropositionByCriteresAndCadreEmploi(Map,String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<GradeDTO> findListeGradeDTOAndCountPropositionByCriteresAndCadreEmploi(Map<String, Object> criteriaProposition,String idCadreEmploi) {
		List<GradeDTO> list = serviceGrade.findListeGradeDTOByCadreEmploi(idCadreEmploi);
		if(list!=null && !list.isEmpty()){
			for(GradeDTO grade:list){
				criteriaProposition.put("gradeId", grade.getId());
				grade.setNbPropositionOfSpecifiedTableauAG(countPropositionWithCriteria(criteriaProposition, null));
			}
		}
		return list;
	}
	
	 /**
	 * @see IUcVerificationPropositionAGCAP#findListCadreEmploiDTOAndCountPropositionByCriteresAndFiliere(Map,String,Date)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<CadreEmploiDTO> findListCadreEmploiDTOAndCountPropositionByCriteresAndFiliere(Map<String, Object> criteriaProposition,String idFiliere, Date date) {
		List<CadreEmploiDTO> list= serviceCadreEmploi.findListCadreEmploiDTOByFiliere(idFiliere, date);
		if(list!=null && !list.isEmpty()){
			for(CadreEmploiDTO cadreEmpl:list){
				criteriaProposition.put("cadreEmploiId", cadreEmpl.getId());
				cadreEmpl.setNbPropositionOfSpecifiedTableauAG(countPropositionWithCriteria(criteriaProposition, null));
			}
		}
		
		return list;
	}
	
	/**
	 * IUcVerificationPropositionAGCAP#findListFiliereDTOAndCountPropositionByCriteresAndCadreStatutaireAndDate(Map,String,Date)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	@DataSecured
	public List<FiliereDTO> findListFiliereDTOAndCountPropositionByCriteresAndCadreStatutaireAndDate(Map<String, Object> criteriaProposition,String idCadreStatut, Date date) {
		List<FiliereDTO> list = serviceFiliere.findListFiliereDTOByCadreStatutaireAndDate(idCadreStatut,date);
		if(list!=null && !list.isEmpty()){
			for(FiliereDTO filiere:list){
				criteriaProposition.put("filiereId", filiere.getId());
				filiere.setNbPropositionOfSpecifiedTableauAG(countPropositionWithCriteria(criteriaProposition, null));
			}
		}
		
		return list;
	}

	
	 /**
	 * @see IUcVerificationPropositionAGCAP#findParamRangClassementAGByOrganisme(Organisme)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public ParamRangClassementAG findParamRangClassementAGByOrganisme(Organisme organisme){
		return serviceParamRangClassementAG.loadParamRangClassementAGByOrganisme(organisme);
	}
	
	 /**
	 * @see IUcVerificationPropositionAGCAP#findListValeurClassementAGByParamRangClassementAG(ParamRangClassementAG)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<ValeurClassementAG> findListValeurClassementAGByParamRangClassementAG(ParamRangClassementAG param){
		return serviceValeurClassementAG.findListValeurClassementAGByParamRangClassementAG(param);
	}
	
	/**
	 * @see IUcVerificationPropositionAGCAP#changeVerifieePropositionAG(Integer, EnumVerifierAG)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void changeVerifieePropositionAG(Integer propositionId, EnumVerifierAG enumVerifier) {
		servicePropositionAG.changeVerifieePropositionAG(propositionId, enumVerifier);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void changeVerifieeListPropositionAG(List<String[]> listTablePropositionIdAndBooleanVerifiee) {
		for(int i=0; i<listTablePropositionIdAndBooleanVerifiee.size(); i++){
			String[] idAndVerifiee = listTablePropositionIdAndBooleanVerifiee.get(i);
			Integer propId = Integer.valueOf(idAndVerifiee[0]);
			String propVerifiee = idAndVerifiee[1];
			if(propVerifiee.equals(EnumVerifier.NON.getCode())){
				servicePropositionAG.changeVerifieePropositionAG(propId, EnumVerifierAG.NON);
			}
			if(propVerifiee.equals(EnumVerifier.OUI.getCode())){
				servicePropositionAG.changeVerifieePropositionAG(propId, EnumVerifierAG.OUI);
			}
			if(propVerifiee.equals(EnumVerifier.AVERIFIER.getCode())){
				servicePropositionAG.changeVerifieePropositionAG(propId, EnumVerifierAG.AVERIFIER);
			}
		}		
	}
	
	
	/**
	 * @see IUcVerificationPropositionAGCAP#changeRangClassementOfPropositionAG(Integer ,String , String)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void changeRangClassementOfPropositionAG(Integer propositionId,String saisieLibre, String valeur){
		PropositionAG propositionLoaded = servicePropositionAG.findById(propositionId);
		RangClassementAG rangClassement =null;
		rangClassement = propositionLoaded.getRangClassementAG();
		if(rangClassement==null){
			rangClassement =new RangClassementAG();
		}
		if(saisieLibre!=null){
			rangClassement.setSaisieLibre(saisieLibre);
			rangClassement.setValeur(null);
		}
		else if(valeur!=null){
			rangClassement.setSaisieLibre(null);
			rangClassement.setValeur(valeur);
		}
		else if(valeur==null && saisieLibre==null){
			rangClassement=null;
		}
		if(rangClassement!=null)serviceRangClassementAG.saveRangClassementAG(rangClassement);
		propositionLoaded.setRangClassementAG(rangClassement);
		servicePropositionAG.savePropositionAG(propositionLoaded);
	}
	
	/**
	 * @see IUcVerificationPropositionAGCAP#exclureReintegrerPropositionAG(Integer, boolean)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void exclureReintegrerPropositionAG(Integer propositionId, boolean promouvable) {
		servicePropositionAG.exclureReintegrerPropositionAG(propositionId, promouvable);
	}
	
	/**
	 * @see IUcVerificationPropositionAGCAP#findListPropositionNonPromouvableAGDTO(Map, Map, long, long, String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	@DataSecured
	public List<PropositionNonPromouvableAGDTO> findListPropositionNonPromouvableAGDTO(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter) {
		return servicePropositionAG.findListPropositionNonPromouvableAGDTO(tri, criteres, firstLine, limitLine, filter);
	}
	
	/**
	 * @see IUcVerificationPropositionAGCAP#exportPropositionAGForExcel(Map)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	@DataSecured
	public String exportPropositionAGForExcel(Map<String, Object> criteres) throws IOException {
		List<PropositionAGAExporterDTO> propAExporter = servicePropositionAG.exportPropositionAGForExcel(criteres);
		
		boolean ongletLDGAfficher = true;
		if( criteres!= null && criteres.containsKey("withConcours") && criteres.containsKey("withConcoursAgent")) {
			if((boolean) criteres.get("withConcours") && (boolean) criteres.get("withConcoursAgent")) {
				ongletLDGAfficher = false;
			}
		}
		
		String nomsheetAll = "feuille1";
		if( criteres!= null && criteres.containsKey("tableauAGId")) {
			TableauAG tableau = serviceTableauAG.findById((Integer)  criteres.get("tableauAGId"));
			if(tableau != null && tableau.getLibelle() != null) {
				nomsheetAll = tableau.getLibelle();
				if(tableau.getLibelle().length() >= 30) {
					nomsheetAll = tableau.getLibelle().substring(0,30);
				}
			}
		}
		
		int nbAvisMax = 0;
		
		// Préparation du fichier excel avec 2 feuilles
		ExcelDocumentHelper editionExcel = new ExcelDocumentHelper();
		String sheetAll = nomsheetAll;
		String sheetLDG = "LDG";
					
		nbAvisMax = writePropositionAGAll(editionExcel, propAExporter,sheetAll);
		if(ongletLDGAfficher) writePropositionAGLDG(editionExcel, propAExporter,sheetLDG,criteres);
		
		editionExcel.writeHeaderToSheet(sheetAll,getHeaderPropositionAGAll(nbAvisMax).toArray(new String[getHeaderPropositionAGAll(nbAvisMax).size()]));		
		if(ongletLDGAfficher) editionExcel.writeHeaderToSheet(sheetLDG,getHeaderPropositionAGLDG().toArray(new String[getHeaderPropositionAGLDG().size()]));
        
		return editionExcel.writeWorkbookAndGetPath("Liste");
	}
	
	private List<String> getHeaderPropositionAGAll(int nbAvisMax) {
		List<String> rowTitle = new ArrayList<String>();
		
		rowTitle.add("CodeUniteGestion");
        rowTitle.add("LibelleUniteGestion");
		rowTitle.add("CodeCollectivite");
		rowTitle.add("LibelleCollectivite");
        rowTitle.add("NomAgent");
        rowTitle.add("NomNaissanceAgent");
        rowTitle.add("PrenomAgent");
        rowTitle.add("MatriculeAgent");
        rowTitle.add("SexeAgent");
        rowTitle.add("DateNaissance");
        // rowTitle.add("CodeTriFiliere");
        rowTitle.add("CodeFiliere");
        rowTitle.add("LibelleFiliere");       
		rowTitle.add("CodeCategorie");
		rowTitle.add("LibelleCategorie");
		rowTitle.add("CodeGroupeHierarchique");
		rowTitle.add("LibelleGroupeHierarchique");	
		//rowTitle.add("CodeTriCadreEmploi");
		rowTitle.add("CodeCadreEmploi");
		rowTitle.add("LibelleCadreEmploi");       
		rowTitle.add("OrdreGradeDansCE");
		rowTitle.add("CodeGrade");
		rowTitle.add("LibelleMoyenGrade");
		rowTitle.add("LibelleLongGrade");  
		rowTitle.add("CodeEchelon");
		rowTitle.add("IndiceBrut");
		rowTitle.add("IndiceMajore");
		rowTitle.add("Reliquat");
		rowTitle.add("CodeStatut");
		rowTitle.add("LibelleStatut");
		rowTitle.add("CodeService");
		rowTitle.add("LibelleService");   
		rowTitle.add("CodeFonction");
		rowTitle.add("LibelleFonction");
        rowTitle.add("Poste");           
		rowTitle.add("CarrierePrinciaple");
		rowTitle.add("LibelleCarriere");
        rowTitle.add("CodeRegroupement");
		rowTitle.add("CodePositionAdmin");
		rowTitle.add("LibellePositionAdmin");
		rowTitle.add("DateEntreeEchelon");
		rowTitle.add("DateEntreeGrade");
		rowTitle.add("DateMiniAvancement");
		rowTitle.add("Promouvable");
		rowTitle.add("TypeProposition");	
		rowTitle.add("DateCalcul");
		rowTitle.add("DateEffet");
		rowTitle.add("DateInjection");
        rowTitle.add("DateNomination");
		rowTitle.add("DateRefus");
		rowTitle.add("DateRetenue");       
		rowTitle.add("Decision");
		rowTitle.add("DureeRetardAnnee");
		rowTitle.add("DureeRetardJour");       
		rowTitle.add("ExclueManuellement");
		rowTitle.add("Injectee");
		rowTitle.add("ModeCreation");       
		rowTitle.add("MotifExclusion");
		rowTitle.add("MotifRefus");       
		rowTitle.add("ReclassementMedical");
		rowTitle.add("Refuse");
		rowTitle.add("TypeAvancement");       
		rowTitle.add("Verifie");
		rowTitle.add("CodeGradeCible");
		rowTitle.add("LibelleMoyenGradeCible");
		rowTitle.add("LibelleLongGradeCible");
		rowTitle.add("CodeEchelonCible");
		rowTitle.add("IndiceBrutCible");
		rowTitle.add("IndiceMajoreCible");
		rowTitle.add("ReliquatCible");
        rowTitle.add("AncienneteAA");
        rowTitle.add("AncienneteMM");
        rowTitle.add("AncienneteJJ");     
		rowTitle.add("CodeMotifAvancement");
		rowTitle.add("LibelleMotifAvancement");
		rowTitle.add("StatutAvancementCode");
		rowTitle.add("StatutAvancementLibelle");    
		rowTitle.add("RangClassementAG");		
        rowTitle.add("ConditionDePropositionSelectionnee");
        rowTitle.add("ConditionRemplie");
        rowTitle.add("DateMini");
        
        for(int j=0;j<10;j++){
            rowTitle.add("DetailCondition "+j);
            rowTitle.add("DateDetailCondition "+j);
            rowTitle.add("AncienneteAnneeCondition "+j);
            rowTitle.add("AncienneteJourCondition "+j);
        }
        for(int j=0;j<10;j++){
            rowTitle.add("GrilleAncienneté "+j);
            rowTitle.add("DateEntreeGrille "+j);
            rowTitle.add("AncienneteAnneeGrille "+j);
            rowTitle.add("AncienneteJourGrille "+j);
        }
      
        rowTitle.add("MsgControle");
        rowTitle.add("DateCAP");
        rowTitle.add("CodeTableauAvancement");
        rowTitle.add("LibelleTableauAvancement");
        rowTitle.add("PhaseTableauAvancement");
     
		// concours
		rowTitle.add("DateObtentionConcours");
		rowTitle.add("CommentaireConcours");
        // GIFT IN482 - STH - 07/04/2009 - Plantage export avancement grade
        // rowTitle.add("CommissionConcours");
        // rowTitle.add("DateCommissionConcours");
		rowTitle.add("JustificatifsFournisConcours");
		rowTitle.add("TypeConcours");
		rowTitle.add("LibelleConcours");
		rowTitle.add("LibelleGradeConcours");
		// fin concours
		
		// Dates entrées
		rowTitle.add("DatePremiereTitularisation");
		rowTitle.add("DateEntreeAgentFonctionPublique");	
		rowTitle.add("DateEntreeFonctionnaireFonctionPublique");
		rowTitle.add("DateEntreeAgentFonctionPubliqueTerritoriale");
		rowTitle.add("DateEntreeFonctionnaireFonctionPubliqueTerritoriale");
		// 0036103
		rowTitle.add("DateCalculGrilleAnciennete");
		rowTitle.add("DateEntreeAgentCollect");
		
		// Hiérarchie services
		for(int i=1;i<=10;i++){
    	    rowTitle.add("Code service N"+i);
    	    rowTitle.add("Libellé service N"+i);
    	}
		

		for (int k = 0; k < nbAvisMax; k++) {
			rowTitle.add("Avis " + (k + 1) + " qualité");
			rowTitle.add("Avis " + (k + 1) + " nomResponsable");
			rowTitle.add("Avis " + (k + 1) + " prénomResponsable");
			rowTitle.add("Avis " + (k + 1) + " matriculeResponsable");
			rowTitle.add("Avis " + (k + 1) + " dateAvis");
			rowTitle.add("Avis " + (k + 1) + " appréciation");
			rowTitle.add("Avis " + (k + 1) + " commentaire");
		}
		
		return rowTitle;
	}
	
	
	private List<String> getHeaderPropositionAGLDG() {
		List<String> rowTitle = new ArrayList<String>();
		
		// tableau des LDG
		rowTitle.add("Promu");
        rowTitle.add("Classement");
        rowTitle.add("CodeCollectivite");
		rowTitle.add("LibelleCollectivite");
        rowTitle.add("NomAgent");
        rowTitle.add("NomNaissanceAgent");
        rowTitle.add("PrenomAgent");
        rowTitle.add("MatriculeAgent");
        rowTitle.add("SexeAgent");
        rowTitle.add("DateNaissance");
        rowTitle.add("CarrierePrincipale");
        rowTitle.add("LibelleCarriere");
        rowTitle.add("LibelleFiliere");       
		rowTitle.add("LibelleCategorie");
		rowTitle.add("LibelleCadreEmploi");       
		rowTitle.add("CodeGrade");
		rowTitle.add("LibelleLongGrade"); 
		rowTitle.add("DateEntreeGrade");
		rowTitle.add("DateMiniAvancement");
		rowTitle.add("Promouvable");
		rowTitle.add("TypeProposition");	
		rowTitle.add("DateCalcul");
		rowTitle.add("DateEffet");
		rowTitle.add("DateInjection");
        rowTitle.add("DateNomination");
		rowTitle.add("DateRefus");
		rowTitle.add("DateRetenue");
		rowTitle.add("Grade sommital");
		rowTitle.add("Date d'obtention examen professionnel grade actuel");
		rowTitle.add("Date d'obtention concours grade actuel");
		rowTitle.add("CodeEchelon");
		rowTitle.add("CodeRegroupement");
		rowTitle.add("CodePositionAdmin");
		rowTitle.add("LibellePositionAdmin");
		rowTitle.add("DateEntreeEchelon");
		rowTitle.add("Echelon sommital");
		rowTitle.add("LibelleStatut");
		rowTitle.add("CodeService");
		rowTitle.add("LibelleService");   
		rowTitle.add("CodeFonction");
		rowTitle.add("LibelleFonction");
        rowTitle.add("Poste");
        rowTitle.add("Code grade de référence");
        rowTitle.add("Grade de référence");
        rowTitle.add("Poste budgétaire");
        rowTitle.add("Poste d'encadrement ");
        rowTitle.add("Code service GPEC");
        rowTitle.add("Fonctions GPEC");
        rowTitle.add("TypeAvancement");
        rowTitle.add("CodeGradeCible");
        rowTitle.add("LibelleLongGradeCible");
        rowTitle.add("Date obtention examen professionnel grade cible");
        rowTitle.add("Date d'obtention concours grade cible");
        rowTitle.add("AncienneteAA");
        rowTitle.add("AncienneteMM");
        rowTitle.add("AncienneteJJ");
        rowTitle.add("DateObtentionConcours");
        rowTitle.add("CommentaireConcours");
        rowTitle.add("LibelleGradeConcours");
        // Hiérarchie services
 		for(int i=1;i<=1;i++){
     	    rowTitle.add("Code service N"+i);
     	    rowTitle.add("Libellé service N"+i);
     	}
        rowTitle.add("Libellé campagne Evaluation");
        rowTitle.add(" Date entretien");
        rowTitle.add("Valeur professionnelle");
        rowTitle.add("Poids valeur professionnelle");
        rowTitle.add("Avis Evaluateur");
        rowTitle.add("Commentaire évaluateur");
        rowTitle.add("Dernière sanction en cours");
		
		return rowTitle;
	}
	
	private int writePropositionAGAll(ExcelDocumentHelper editionExcel,List<PropositionAGAExporterDTO> propAExporter, String sheetAll) {
		
		int nbAvisMax = 0;
						
		for (int i = 0; i < propAExporter.size(); i++) {
			PropositionAGAExporterDTO prop = propAExporter.get(i);
			ConditionDeProposition condition = null;
			int sizeCondition = 1;
			if (prop.getListConditionAGRemplie() != null && prop.getListConditionAGRemplie().size() > 0) {
				sizeCondition = prop.getListConditionAGRemplie().size();
			}
			for (int k = 0; k < sizeCondition; k++) {
				// PropositionAExporterDTOGWT prop = (PropositionAExporterDTOGWT)listPropAExporterGWT.get(i);
				if (prop.getListConditionAGRemplie() != null && prop.getListConditionAGRemplie().size() > 0) {
					condition = prop.getListConditionAGRemplie().get(k);
				}
				List<String> row = new ArrayList<String>();
				row.add(prop.getCodeUniteGestion() != null ? prop.getCodeUniteGestion() : "");
				row.add(prop.getLibelleUniteGestion() != null ? prop.getLibelleUniteGestion() : "");
				row.add(prop.getCodeCollectivite() != null ? prop.getCodeCollectivite() : "");
				row.add(prop.getLibelleCollectivite() != null ? prop.getLibelleCollectivite() : "");
				row.add(prop.getNomAgent() != null ? prop.getNomAgent() : "");
				row.add(prop.getNomJeuneFilleAgent() != null ? prop.getNomJeuneFilleAgent() : "");
				row.add(prop.getPrenomAgent() != null ? prop.getPrenomAgent() : "");
				row.add(prop.getMatriculeAgent() != null ? prop.getMatriculeAgent() : "");
				row.add(prop.getSexeAgent() != null ? prop.getSexeAgent().getLibelle() : "");
				row.add(prop.getDateNaissance() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateNaissance()) : "");
				// row.add(prop.getCodeTriFiliere() != null ? prop.getCodeTriFiliere() : "");
				row.add(prop.getCodeFiliere() != null ? prop.getCodeFiliere() : "");
				row.add(prop.getLibelleFiliere() != null ? prop.getLibelleFiliere() : "");               
				row.add(prop.getCodeCategorie() != null ? prop.getCodeCategorie() : "");
				row.add(prop.getLibelleCategorie() != null ? prop.getLibelleCategorie() : "");
				row.add(prop.getCodeGroupeHierarchique() != null ? prop.getCodeGroupeHierarchique() : "");
				row.add(prop.getLibelleGroupeHierarchique() != null ? prop.getLibelleGroupeHierarchique() : "");				
				//row.add(prop.getCodeTriCadreEmploi() != null ? prop.getCodeTriCadreEmploi() : "");
				row.add(prop.getCodeCadreEmploi() != null ? prop.getCodeCadreEmploi() : "");
				row.add(prop.getLibelleCadreEmploi() != null ? prop.getLibelleCadreEmploi() : "");              
				row.add(prop.getOrdreGradeDansCE() != null ? prop.getOrdreGradeDansCE().toString() : "");
				row.add(prop.getCodeGrade() != null ? prop.getCodeGrade() : "");
				row.add(prop.getLibelleMoyenGrade() != null ? prop.getLibelleMoyenGrade() : "");
				row.add(prop.getLibelleLongGrade() != null ? prop.getLibelleLongGrade() : "");
				row.add(prop.getCodeEchelon() != null ? prop.getCodeEchelon() : "");
				row.add(prop.getAncienIndiceBrut() != null ? prop.getAncienIndiceBrut() : "");
	            row.add(prop.getAncienIndiceMaj() != null ? prop.getAncienIndiceMaj() : "");
				row.add(prop.getAncienReliquat() != null ? prop.getAncienReliquat() : "");
				row.add(prop.getCodeStatut() != null ? prop.getCodeStatut() : "");
				row.add(prop.getLibelleStatut() != null ? prop.getLibelleStatut() : "");
				row.add(prop.getCodeService() != null ? prop.getCodeService() : "");
				row.add(prop.getLibelleService() != null ? prop.getLibelleService() : "");
				row.add(prop.getCodeFonction() != null ? prop.getCodeFonction() : "");
				row.add(prop.getLibFonction() != null ? prop.getLibFonction() : "");
				row.add(prop.getDenominationDescriptifPoste() != null ? prop.getDenominationDescriptifPoste() : "");
				row.add(prop.getCarrierePrincipale() != null && prop.getCarrierePrincipale().booleanValue() ? "Oui" : "Non");
				row.add(prop.getLibelleCarriere() != null ? prop.getLibelleCarriere() : "");
				row.add(prop.getCodeRegroupement() != null ? prop.getCodeRegroupement().toString() : "");				
				row.add(prop.getCodePositionAdmin() != null ? prop.getCodePositionAdmin() : "");
				row.add(prop.getLibellePositionAdmin() != null ? prop.getLibellePositionAdmin() : "");                
				row.add(prop.getDateEntreeEchelon() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEntreeEchelon()) : "");
				row.add(prop.getDateEntreeGrade() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEntreeGrade()) : "");
				row.add(prop.getDateMiniAvancement() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateMiniAvancement()) : "");				
				row.add(prop.getPromouvable() != null && prop.getPromouvable().booleanValue() ? "Oui" : "Non");
				row.add(prop.getTypeProposition() != null ? prop.getTypeProposition().getLibelle() : "");				
				row.add(prop.getDateCalcul() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateCalcul()) : "");
				row.add(prop.getDateEffet() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEffet()) : "");
				row.add(prop.getDateInjection() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateInjection()) : "");
				row.add(prop.getDateNomination() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateNomination()) : "");
				row.add(prop.getDateRefus() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateRefus()) : "");
				row.add(prop.getDateRetenue() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateRetenue()) : "");               
				row.add(prop.getDecision() != null ? prop.getDecision().getLibelle() : "");
				row.add(prop.getDureeRetardAnnee() != null ? prop.getDureeRetardAnnee().toString() : "");
				row.add(prop.getDureeRetardJour() != null ? prop.getDureeRetardJour().toString() : "");                
				row.add(prop.getExclueManuellement() != null && prop.getExclueManuellement().booleanValue() ? "Oui" : "Non");
				row.add(prop.getInjection() != null ? prop.getInjection().getLibelle() : "");
				row.add(prop.getModeCreation() != null ? prop.getModeCreation().getLibelle() : "");				
				row.add(prop.getMotifExclusion() != null ? prop.getMotifExclusion().toString() : "");
				row.add(prop.getMotifRefus() != null ? prop.getMotifRefus().toString() : "");               
				row.add(prop.getReclassementMedical() != null && prop.getReclassementMedical().booleanValue() ? "Oui" : "Non");
				row.add(prop.getRefuse() != null && prop.getRefuse().booleanValue() ? "Oui" : "Non");
				row.add(prop.getTypeAvancement() != null ? prop.getTypeAvancement().getLibelle() : "");				
				row.add(prop.getVerifie() != null ? prop.getVerifie().getLibelle() : "");
				row.add(prop.getCodeGradeCible() != null ? prop.getCodeGradeCible().toString() : "");
				row.add(prop.getLibelleMoyenGradeCible() != null ? prop.getLibelleMoyenGradeCible().toString() : "");
				row.add(prop.getLibelleLongGradeCible() != null ? prop.getLibelleLongGradeCible().toString() : "");
				row.add(prop.getCodeChevronCible() != null ? prop.getCodeChevronCible().toString() : "");
				// Indice brut et majoré
				String indiceBrutCible = prop.getIndiceBrutProposition() != null ? prop.getIndiceBrutProposition() : "";
				String indiceMajoreCible = "";
				String idCadreStatutaireCible = prop.getIdCadreStatutaire() != null ? prop.getIdCadreStatutaire() : "";
				row.add(indiceBrutCible);
				if (indiceBrutCible.length() > 0 && idCadreStatutaireCible.length() > 0 && prop.getDateRetenue() != null) {
					IndiceBrutMajore indiceBrutMajore = serviceIndiceBrutMajore.findIndiceBrutMajoreValidByIndiceBrutAndCadreStatutaire(indiceBrutCible,
		                    idCadreStatutaireCible,
		                    prop.getDateRetenue());
					if (indiceBrutMajore != null) {
						indiceMajoreCible = indiceBrutMajore.getIndiceMajore();
					}
				} 
				row.add(indiceMajoreCible);
				Duration d = new Duration(prop.getAncienneteAA(), prop.getAncienneteMM(), prop.getAncienneteJJ());
				row.add(d.toDataBase());
				row.add(prop.getAncienneteAA() != null ? prop.getAncienneteAA().toString() : "");
				row.add(prop.getAncienneteMM() != null ? prop.getAncienneteMM().toString() : "");
				row.add(prop.getAncienneteJJ() != null ? prop.getAncienneteJJ().toString() : "");               
				row.add(prop.getCodeMotifAvancement() != null ? prop.getCodeMotifAvancement().toString() : "");
				row.add(prop.getLibelleMotifAvancement() != null ? prop.getLibelleMotifAvancement().toString() : "");               
				row.add(prop.getStatutAvancementCode() != null ? prop.getStatutAvancementCode().toString() : "");
				row.add(prop.getStatutAvancementLibelle() != null ? prop.getStatutAvancementLibelle().toString() : "");               
				row.add(prop.getRangSaisieLibre() != null ? prop.getRangSaisieLibre()
						: (prop.getRangValeur() != null ? prop.getRangValeur() : ""));

				//colonne condition selectionee : oui / non
				if (condition != null) {
					if (condition.getId().equals(prop.getIdConditionSelectionne())) {
						row.add("oui");
					} 
					else {
						row.add("non");
					}
				} 
				else {
					row.add("non");
				}

				//colonne condition 
				if (condition != null) {
					row.add((condition.getConditionAG() != null &&  condition.getConditionAG().getTexteCondition()!=null) ? 
							condition.getConditionAG().getTexteCondition() : "");
					row.add(condition.getDateMini() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(condition.getDateMini()) : "");
				}
				else{
					row.add("");
					row.add("");
				}

				if(condition!=null) {
					if(!Hibernate.isInitialized(condition.getListDetailCondition())) {
						Hibernate.initialize(condition.getListDetailCondition());
					}

					Set<DetailCondition>  listDetailHasSet = condition.getListDetailCondition();

					// MANTIS 15498 DSAM 06/2012 : tri sur le listDetail (valeurFonctionCondition.numOrdre ASC)
					List<DetailCondition> listDetail = new ArrayList<DetailCondition>(listDetailHasSet);
					Collections.sort(listDetail, new ComparatorDetailConditionAsc());
					int nbDetail=0;
					if(listDetail!=null){
						for(DetailCondition detail : listDetail){
							if(nbDetail<10){
								if(detail!=null && detail.getValeurFonctionCondition()!=null
										&& detail.getValeurFonctionCondition().getTexteCondition()!=null){
									row.add(detail.getValeurFonctionCondition().getTexteCondition() );
								}
								else{
									row.add("");
								}
								row.add(detail.getDateDetail() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(detail.getDateDetail()) : "");
								row.add(detail.getAncienneteAnnee()!=null?detail.getAncienneteAnnee().toString():"");
								row.add(detail.getAncienneteJour()!=null?detail.getAncienneteJour().toString():"");
							}
							nbDetail++;
						}
						//si listeDetail.size()<10 finir le remplissage des colonnes
						while(nbDetail<10){
							row.add("");
							row.add("");
							row.add("");
							row.add("");
							nbDetail++;
						}
					}
				}
				//si condition null remplir les colonnes 
				else{
					for(int j=0;j<10;j++){
						row.add("");
						row.add("");
						row.add("");
						row.add("");
					}
				}

				PropositionAG completeProp = servicePropositionAG.findById(prop.getId());
				GrilleAnciennete grille = null;

				if(completeProp != null) {
					if(!Hibernate.isInitialized(completeProp.getCarriere())) {
						Hibernate.initialize(completeProp.getCarriere());
					}
					if(!Hibernate.isInitialized(completeProp.getCodeRegroupement())) {
						Hibernate.initialize(completeProp.getCodeRegroupement());
					}

					grille = serviceGrilleAnciennete.findByCarriereRegroupement(completeProp.getCarriere(), completeProp.getCodeRegroupement());
				}

				List<AncienneteFonction> listAncienneteFonction = grille!=null ? new ArrayList<AncienneteFonction>(grille.getListAncienneteFonction()) : null;
				if(listAncienneteFonction!=null) {
					int nbGrille=0;
					// MANTIS 13209 SROM 10/2009 : tri sur le cheminClasseJava
					Collections.sort(listAncienneteFonction, new AncienneteFonction.ComparatorCheminClasseJavaAsc());
					for(AncienneteFonction ancienFonction : listAncienneteFonction){
						if(nbGrille<10){
							if(ancienFonction!=null && ancienFonction.getFonctionAG()!=null && ancienFonction.getFonctionAG().getMetaFonction()!=null
									&& ancienFonction.getFonctionAG().getMetaFonction().getCheminClasseJava()!=null){
								row.add(ancienFonction.getFonctionAG().getMetaFonction().getCheminClasseJava() );
							}
							else{
								row.add("");
							}
							row.add(ancienFonction.getDateEntree() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(ancienFonction.getDateEntree()) : "");
							row.add(ancienFonction.getAncienneteAnnee()!=null?ancienFonction.getAncienneteAnnee().toString():"");
							row.add(ancienFonction.getAncienneteJour()!=null?ancienFonction.getAncienneteJour().toString():"");
						}
						nbGrille++;
					}
					//si listAncienneteFonction.size()<10 finir le remplissage des colonnes
					while(nbGrille<10){
						row.add("");
						row.add("");
						row.add("");
						row.add("");
						nbGrille++;
					}
				}
				//si listAncienneteFonction null alors remplir les colonnes
				else{
					for(int j=0;j<10;j++){
						row.add("");
						row.add("");
						row.add("");
						row.add("");
					}
				}

				row.add(prop.getMsgControle() != null ? prop.getMsgControle() : "");
				row.add(prop.getDateCAP() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateCAP()) : "");
				row.add(prop.getCodeTableauAvancement() != null ? prop.getCodeTableauAvancement() : "");
				row.add(prop.getLibelleTableauAvancement() != null ? prop.getLibelleTableauAvancement() : "");
				row.add(prop.getPhaseTableauAvancement() != null ? prop.getPhaseTableauAvancement().getLibelle() : "");

				// concours
				row.add(prop.getReussiteConcoursDateObtention()!= null ?
						UtilsDate.Formatage.DATE_FORMATTER.format(prop.getReussiteConcoursDateObtention())
						: "");

				row.add(prop.getReussiteConcoursCommentaire()!= null ? prop.getReussiteConcoursCommentaire() : "");
				// GIFT IN482 - STH - 07/04/2009 - Plantage export avancement grade
				// row.add(prop.getReussiteConcoursCommission()!= null ? prop.getReussiteConcoursCommission() : "");
				// row.add(prop.getReussiteConcoursDateCommission()!= null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getReussiteConcoursDateCommission()) : "");
				row.add(prop.getReussiteConcoursJustificatifsFournis()!= null && prop.getReussiteConcoursJustificatifsFournis().booleanValue() ? "Oui" : "Non");
				row.add(prop.getReussiteConcoursTypeConcours()!= null ? prop.getReussiteConcoursTypeConcours().getLibelle() : "");
				row.add(prop.getConcoursLibelle()!= null ? prop.getConcoursLibelle() : "");
				row.add(prop.getConcoursLibelleGrade()!= null ? prop.getConcoursLibelleGrade() : "");
				// fin concours

				// Dates entrées
				row.add(prop.getDatePremiereTitularisation() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDatePremiereTitularisation()) : "");
				row.add(prop.getDateEntreeAgentFonctionPublique() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEntreeAgentFonctionPublique()) : "");
				row.add(prop.getDateEntreeFonctionnaireFonctionPublique() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEntreeFonctionnaireFonctionPublique()) : "");
				row.add(prop.getDateEntreeAgentFonctionPubliqueTerritoriale() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEntreeAgentFonctionPubliqueTerritoriale()) : "");
				row.add(prop.getDateEntreeFonctionnaireFonctionPubliqueTerritoriale() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEntreeFonctionnaireFonctionPubliqueTerritoriale()) : "");
				
				// date grille anciennete + date entrée collect
				row.add(prop.getDateCalculGrilleAnciennete() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateCalculGrilleAnciennete()) : "");
				row.add(prop.getDateEntreeAgentCollect() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEntreeAgentCollect()) : "");
				
				// Hiérarchie des services
				for(int j=1;j<=10;j++){
		            if (prop.getVueHierarchieService()!=null){
		                row.add(UtilsString.isNul(prop.getVueHierarchieService().getCodeService(j)));
		                row.add(UtilsString.isNul(prop.getVueHierarchieService().getLibelleService(j)));
		            }else{
		                row.add("");
		                row.add("");
		            }
		        }
				
				// Le suivi des avis doit rester à la fin de l'export (sinon décalage des données suivantes)
				if (prop.getListAvisAGDTO() != null && prop.getListAvisAGDTO().size() > 0) {
					for (int j = 0; j < prop.getListAvisAGDTO().size(); j++) {

						if (prop.getListAvisAGDTO().size() > nbAvisMax)
							nbAvisMax = prop.getListAvisAGDTO().size();

						AvisAGDTO avis = prop.getListAvisAGDTO().get(j);
						row.add(avis.getQualite() != null ? avis.getQualite() : "");
						row.add(avis.getNomAgent() != null ? avis.getNomAgent() : "");
						row.add(avis.getPrenomAgent() != null ? avis.getPrenomAgent() : "");
						row.add(avis.getMatriculeAgent() != null ? avis.getMatriculeAgent() : "");
						row.add(avis.getDateAvis() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(avis.getDateAvis()) : "");
						row.add(avis.getAppreciationLibelle() != null ? avis.getAppreciationLibelle() : "");
						row.add(avis.getCommentaire() != null ? avis.getCommentaire().trim() : "");
					}
				}
				
				editionExcel.writeLineToSheet(sheetAll,row.toArray(new String[row.size()]));
			}
		}
		
		return nbAvisMax;
	}
	
	private void writePropositionAGLDG(ExcelDocumentHelper editionExcel,List<PropositionAGAExporterDTO> propAExporter, String sheetLDG,Map<String, Object> criteres) {
		int nbligne = 1;
		
		List<ValeurClassementAG> listValeurClass = null;
		if( criteres!= null && criteres.containsKey("organismeID") ) {
			listValeurClass = serviceValeurClassementAG.findListValeurClassementAGByOrganisme((String) criteres.get("organismeID"));
		}
		
		for (int i = 0; i < propAExporter.size(); i++) {
			PropositionAGAExporterDTO prop = propAExporter.get(i);
						
			List<String> row = new ArrayList<String>();
			if (prop.isPromu())  {
				row.add("Oui");
			}else { 
				row.add("Non");
			}
			if(listValeurClass != null && !listValeurClass.isEmpty() && prop.getRangValeur() != null) {
				String valeurClassement = null;
				for (ValeurClassementAG v : listValeurClass) {
					if(prop.getRangValeur().equals(v.getValeur())) {
						valeurClassement = v.getLibelle();
					}
				}
				row.add(valeurClassement != null ? valeurClassement : (prop.getRangValeur() != null ? prop.getRangValeur() : ""));
			}else {
				row.add(prop.getRangSaisieLibre() != null ? prop.getRangSaisieLibre() : (prop.getRangValeur() != null ? prop.getRangValeur() : ""));
			}
			row.add(prop.getCodeCollectivite() != null ? prop.getCodeCollectivite() : "");
			row.add(prop.getLibelleCollectivite() != null ? prop.getLibelleCollectivite() : "");
			row.add(prop.getNomAgent() != null ? prop.getNomAgent() : "");
			row.add(prop.getNomJeuneFilleAgent() != null ? prop.getNomJeuneFilleAgent() : "");
			row.add(prop.getPrenomAgent() != null ? prop.getPrenomAgent() : "");
			row.add(prop.getMatriculeAgent() != null ? prop.getMatriculeAgent() : "");
			row.add(prop.getSexeAgent() != null ? prop.getSexeAgent().getLibelle() : "");
			row.add(prop.getDateNaissance() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateNaissance()) : "");
			row.add(prop.getCarrierePrincipale() != null && prop.getCarrierePrincipale().booleanValue() ? "Oui" : "Non");
			row.add(prop.getLibelleCarriere() != null ? prop.getLibelleCarriere() : "");
			row.add(prop.getLibelleFiliere() != null ? prop.getLibelleFiliere() : "");
			row.add(prop.getLibelleCategorie() != null ? prop.getLibelleCategorie() : "");				
			row.add(prop.getLibelleCadreEmploi() != null ? prop.getLibelleCadreEmploi() : "");              
			row.add(prop.getCodeGrade() != null ? prop.getCodeGrade() : "");
			row.add(prop.getLibelleLongGrade() != null ? prop.getLibelleLongGrade() : "");
			row.add(prop.getDateEntreeGrade() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEntreeGrade()) : "");
			row.add(prop.getDateMiniAvancement() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateMiniAvancement()) : "");				
			row.add(prop.getPromouvable() != null && prop.getPromouvable().booleanValue() ? "Oui" : "Non");
			row.add(prop.getTypeProposition() != null ? prop.getTypeProposition().getLibelle() : "");				
			row.add(prop.getDateCalcul() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateCalcul()) : "");
			row.add(prop.getDateEffet() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEffet()) : "");
			row.add(prop.getDateInjection() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateInjection()) : "");
			row.add(prop.getDateNomination() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateNomination()) : "");
			row.add(prop.getDateRefus() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateRefus()) : "");
			row.add(prop.getDateRetenue() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateRetenue()) : "");               
			row.add("");  //Grade sommital        
			row.add(prop.getDateObtentionExamenProGradeActuel() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateObtentionExamenProGradeActuel()) : ""); // Date d'obtention examen professionnel grade actuel  
			row.add(prop.getDateObtentionConcoursGradeActuel() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateObtentionConcoursGradeActuel()) : ""); // Date d'obtention concours grade actuel
			row.add(prop.getCodeEchelon() != null ? prop.getCodeEchelon() : "");
			row.add(prop.getCodeRegroupement() != null ? prop.getCodeRegroupement().toString() : "");				
			row.add(prop.getCodePositionAdmin() != null ? prop.getCodePositionAdmin() : "");
			row.add(prop.getLibellePositionAdmin() != null ? prop.getLibellePositionAdmin() : "");                
			row.add(prop.getDateEntreeEchelon() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEntreeEchelon()) : "");
			row.add("");  //Echelon sommital 
			row.add(prop.getLibelleStatut() != null ? prop.getLibelleStatut() : "");
			row.add(prop.getCodeService() != null ? prop.getCodeService() : "");
			row.add(prop.getLibelleService() != null ? prop.getLibelleService() : "");
			row.add(prop.getCodeFonction() != null ? prop.getCodeFonction() : "");
			row.add(prop.getLibFonction() != null ? prop.getLibFonction() : "");
			row.add(prop.getDenominationDescriptifPoste() != null ? prop.getDenominationDescriptifPoste() : "");
			row.add("");  //Code grade de référence
			row.add("");  //Grade de référence
			row.add("");  //Poste budgétaire
			row.add("");  //Poste d'encadrement 
			row.add("");  //Code service GPEC
			row.add("");  //Fonctions GPEC
			row.add(prop.getTypeAvancement() != null ? prop.getTypeAvancement().getLibelle() : "");	
			row.add(prop.getCodeGradeCible() != null ? prop.getCodeGradeCible().toString() : "");
			row.add(prop.getLibelleLongGradeCible() != null ? prop.getLibelleLongGradeCible().toString() : "");
			row.add(prop.getDateObtentionExamenProGradeCible() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateObtentionExamenProGradeCible()) : ""); //Date obtention examen professionnel grade cible
			row.add(prop.getDateObtentionConcoursGradeCible() != null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateObtentionConcoursGradeCible()) : "" );//Date d'obtention concours grade cible 
			row.add(prop.getAncienneteAA() != null ? prop.getAncienneteAA().toString() : "");
			row.add(prop.getAncienneteMM() != null ? prop.getAncienneteMM().toString() : "");
			row.add(prop.getAncienneteJJ() != null ? prop.getAncienneteJJ().toString() : "");
			row.add(prop.getReussiteConcoursDateObtention()!= null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getReussiteConcoursDateObtention()) : "");
			row.add(prop.getReussiteConcoursCommentaire()!= null ? prop.getReussiteConcoursCommentaire() : "");
			row.add(prop.getConcoursLibelleGrade()!= null ? prop.getConcoursLibelleGrade() : "");
			// Hiérarchie des services
			for(int j=1;j<=1;j++){
	            if (prop.getVueHierarchieService()!=null){
	                row.add(UtilsString.isNul(prop.getVueHierarchieService().getCodeService(j)));
	                row.add(UtilsString.isNul(prop.getVueHierarchieService().getLibelleService(j)));
	            }else{
	                row.add("");
	                row.add("");
	            }
	        }
            // ESRH-6729 Nouveaux champs LDG
            row.add(UtilsString.isNul(prop.getLibelleCampagneEvaluation()));  //Libellé campagne Evaluation
            row.add(prop.getDateEntretien()!= null ? UtilsDate.Formatage.DATE_FORMATTER.format(prop.getDateEntretien()) : "");  //Date entretien
            row.add(UtilsString.isNul(prop.getValeurProfessionnelle()));  //Valeur professionnelle
            row.add(UtilsString.isNul(prop.getPoidsValeurProfessionnelle()));  //Poids valeur professionnelle
            row.add(UtilsString.isNul(prop.getAvisEvaluateur()));  //Avis Evaluateur
            row.add(UtilsString.isNul(prop.getCommentaireEvaluateur()));  //Commentaire évaluateur
            row.add(""); //Dernière sanction en cours
            
			editionExcel.writeLineToSheet(sheetLDG, row.toArray(new String[row.size()]));
			nbligne++;
		}
		editionExcel.setListChoiseOnCellule(sheetLDG,new String[]{"Oui", "Non"},1,nbligne,0,0);
	}
	
	/**
     * Comparateur pour créer la liste triée sur le numéro d'order de la Fonction Condition
     */
    public static class ComparatorDetailConditionAsc implements Comparator<DetailCondition> {
        @Override
		public int compare(DetailCondition o1, DetailCondition o2) {
            
        	if ( o1.getValeurFonctionCondition() != null && o2.getValeurFonctionCondition() != null
        			&& o1.getValeurFonctionCondition().getNumOrdre() != null && o2.getValeurFonctionCondition().getNumOrdre() != null) {
            	return o1.getValeurFonctionCondition().getNumOrdre().compareTo(o2.getValeurFonctionCondition().getNumOrdre());
        	}
        	return 0;
        }
    }

    /**
	 * @see IUcVerificationPropositionAGCAP#findAgentWithListCarriereAndListFicheGradeEmploi(String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public Agent loadAgentWithCarriereAndFicheGradeEmploi(String idAgent) {
		return serviceAgent.loadAgentWithCarriereAndFicheGradeEmploi(idAgent);
	}

	/**
	 * @see IUcVerificationPropositionAGCAP#calculListDetailAvancementVerificationPropositionAg(Integer)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	@DataSecured
	public List<DetailAvancementAGDTO> calculListDetailAvancementVerificationPropositionAg(Integer tableauId) {
		return servicePropositionAG.calculListDetailAvancementVerificationPropositionAg(tableauId);
	}
	
	private File _fileExcel; 

	/**
	 * @see IUcVerificationPropositionAGCAP#importExcelPropositionAG()
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	@DataSecured
	public Boolean importExcelPropositionAG(Map<String, Object> criteres) {
		
		if (criteres == null || criteres.isEmpty()) {
			log.error("No criteria provided.");
			return false;
		}

		String filePathServer = (String) criteres.get("filePathServer");
		if (filePathServer == null) {
			log.error("No file path provided.");
			return false;
		}

		_fileExcel = new File(filePathServer);
		if (!_fileExcel.exists() || _fileExcel.isDirectory()) {
			log.error("File does not exist or is a directory.");
			return false;
		}
		
		try (InputStream inp = new FileInputStream(_fileExcel)) {
			if (log.isTraceEnabled()) {
				log.trace("Method importExcelPropositionAG");
				log.trace("File path: " + filePathServer);
			}

			Workbook wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheet("LDG");
			if (sheet == null) {
				log.error("Sheet LDG not found in the file.");
				return false;
			}

	        // vérification du nombre de pages
	        if (wb.getNumberOfSheets() != 2) {
	        	log.error("Le fichier Excel doit contenir exactement deux pages");
	            return false;
	        }
	        if (!wb.getSheetName(1).equals("LDG")) {
	        	log.error("La deuxième page du fichier Excel doit être nommée 'LDG'");
	            return false;
	        }

	        Row firstRow = sheet.getRow(0); 
	        if (!firstRow.getCell(0).getStringCellValue().equals("Promu")
	                || !firstRow.getCell(1).getStringCellValue().equals("Classement")
	                || !firstRow.getCell(7).getStringCellValue().equals("MatriculeAgent")) {
	        	log.error("La première ligne du fichier Excel n'est pas au bon format");
	            return false;
	        }
	        
			return processSheet(criteres, sheet);

		} catch (IOException | InvalidFormatException e) {
			log.error("Error reading excel file", e);
			return false;
		} finally {
	    
	        File file = new File(filePathServer);
	        if (file.delete()) {
				if (log.isTraceEnabled()) {
					log.trace("Fichier supprimé");
				}
	        } else {
	            log.error("Erreur lors de la suppression du fichier");
	        }
	    }
	}

	private boolean processSheet(Map<String, Object> criteres, Sheet sheet) {
		boolean premierPassage = true;
		boolean securiteColonneMatricule = false;
		int idPropositionQuery = 0;
		int rowNumber = 0;
		TableauAG tableau = null;
		Date datePromotion = new Date();
		EnumModeClassement modeSaisie = EnumModeClassement.LIBRE;

		tableau = getTableauFromCriteres(criteres);
		if (tableau == null)
			return false;

		datePromotion = servicePropositionAG.getDatePromotionFromTableauAG(tableau.getId());

		Organisme organisme = getOrganismeFromCriteres(criteres);
		if (organisme == null)
			return false;

		modeSaisie = findParamRangClassementAGByOrganisme(organisme).getModeSaisie();

		for (Row row : sheet) {
			if (rowNumber > sheet.getLastRowNum())
				break;
			if (log.isTraceEnabled())
				log.trace("Ligne : " + row.getRowNum());

			if (premierPassage) {
				securiteColonneMatricule = isFirstRowMatriculeAgent(row);
				premierPassage = false;
			} else {
				if (!securiteColonneMatricule)
					return false;

				if (!processRow(row, modeSaisie, datePromotion,idPropositionQuery, tableau.getId()))
					return false;
			}

			++rowNumber;
		}
		return true;
	}

	private TableauAG getTableauFromCriteres(Map<String, Object> criteres) {
		if (criteres.containsKey("tableauAG")) {
			return serviceTableauAG.findById((Integer) criteres.get("tableauAG"));
		}

		return null;
	}

	private Organisme getOrganismeFromCriteres(Map<String, Object> criteres) {
		if (criteres.containsKey("organisme")) {
			return serviceOrganisme.findById((String) criteres.get("organisme"), true);
		}

		return null;
	}

	private boolean isFirstRowMatriculeAgent(Row row) {
		Cell cell = row.getCell(7);
		if (cell == null)
			return false;

		return cell.toString().equals("MatriculeAgent");
	}

	private boolean processRow(Row row, EnumModeClassement modeSaisie, Date datePromotion, int idPropositionQuery, int tableauId) {
		try {
			boolean promu = row.getCell(0).toString().equals("Oui");
			String valeurClassement = row.getCell(1).toString();
			Integer idClassement = null;
			String matricule = row.getCell(7).toString();

			String typeAvancement = row.getCell(48).toString();
			String typeAvancementValue = getTypeAvancementValue(typeAvancement);
			String typeProposition  = getTypePropositionValue(row.getCell(20).toString());

			String codeGrade = row.getCell(15).toString();
			String codeGradeCible = row.getCell(49).toString();
			
			if (log.isTraceEnabled())log.trace("Excel : Classement " + valeurClassement + "  - Promu : " + promu + " - Matricule :"
						+ matricule + " - Type Avancement : " + typeAvancement + " - Type Proposition : " + typeProposition +
						"Grades : " + codeGrade + " - "+ codeGradeCible);
			
			List<Integer> propositions = servicePropositionAG.findListPropositionIdByMatricule(matricule,
					typeAvancementValue, typeProposition, tableauId,codeGrade,codeGradeCible);
			if (log.isTraceEnabled())log.trace("Propositions : " + propositions);

			if (propositions.isEmpty()) {
				if (log.isTraceEnabled())log.trace("Pas de proposition pour l'agent : " + matricule);
				return true;
			}
				
			idPropositionQuery = getLastProposition(propositions);

			if (valeurClassement.length() < 100) {
				idClassement = getIdClassement(valeurClassement, modeSaisie);
			}
			servicePropositionAG.updatePromuClassementDateNominationDateEffetFromIdProposition(idPropositionQuery,
					datePromotion, idClassement, promu);
			if (log.isTraceEnabled())log.trace("Updated :" + propositions);
			
		} catch (Exception e) {
			log.error("Error processing row", e);
			return true;
		}

		return true;
	}

	private String getTypePropositionValue(String typeProposition) {
		if (typeProposition.equals("Promouvable")) {
			return "PRO";
		} else if (typeProposition.equals("Future")) {
			return "FUT";
		}else if (typeProposition.equals("Bloquée")) {
			return "BLO";
		}
		return "";
	}
	

	private String getTypeAvancementValue(String typeProposition) {
		if (typeProposition.equals("Promotion interne")) {
			return "PR";
		} else if (typeProposition.equals("Avancement de grade")) {
			return "AG";
		}
		return "";
	}

	private int getLastProposition(List<Integer> propositions) {
		if (propositions.size() > 1) {
			return propositions.get(propositions.size() - 1);
		} else {
			return propositions.get(0);
		}
	}

	private Integer getIdClassement(String valeurClassement, EnumModeClassement modeSaisie) {
		Integer idClassement = null;
		if (modeSaisie.equals(EnumModeClassement.LISTE) || modeSaisie.equals(EnumModeClassement.BOUTON)) {
			List<Integer> liste = servicePropositionAG.getIdClassementFromValeur(valeurClassement);
			if (!liste.isEmpty()) {
				idClassement = liste.get(0);
			}
		} else if (modeSaisie.equals(EnumModeClassement.LIBRE)) {
			if (servicePropositionAG.isSaisieLibreExisting(valeurClassement)) {
				idClassement = servicePropositionAG.getIdClassementFromValeurSaisieLibre(valeurClassement).get(0);
			} else if (valeurClassement != null && !valeurClassement.isEmpty()) {
				idClassement = servicePropositionAG.insertNouveauRangClassementAg(valeurClassement);
			}
		}

		return idClassement;
	}

}


 	
