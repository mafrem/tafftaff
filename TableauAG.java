package fr.sedit.grh.coeur.ca.avg.model;
import java.util.Date;
import java.util.Set;

import fr.sedit.core.common.model.AbstractDatedPersistentObjectNO;
import fr.sedit.grh.ca.avg.model.RatiosQuotasParTableau;
import fr.sedit.grh.ca.avg.model.TacheAvancementAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumEtatCalculTableauAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumPhaseTableauAG;
import fr.sedit.grh.coeur.ca.par.model.EcheancierCAP;
import fr.sedit.grh.coeur.ca.par.model.FonctionAG;
import fr.sedit.grh.coeur.ca.par.model.ParamPresentationCAP;
import fr.sedit.grh.coeur.cs.model.CadreEmploi;
import fr.sedit.grh.coeur.cs.model.Categorie;
import fr.sedit.grh.coeur.cs.model.Filiere;
import fr.sedit.grh.coeur.cs.model.GroupeHierarchique;
import fr.sedit.grh.coeur.cs.model.Statut;
import fr.sedit.grh.coeur.cs.model.habilitation.UniteGestion;
import fr.sedit.grh.coeur.cs.model.parametrage.Collectivite;
import fr.sedit.grh.coeur.cs.model.parametrage.Organisme;
import fr.sedit.grh.coeur.gc.model.eva.CampagneEvaluation;

/**
 * Index unique sur Organisme et code
 * @author ROSTAIN Guilhem
 * @version 1.0
 * @created 14-janv.-2008 11:34:19
 */
public class TableauAG extends AbstractDatedPersistentObjectNO {

	private static final long serialVersionUID = 7412456198687326266L;
	
	private String code;
	private String libelle;
	private Date datePromotion;
	private Date dateSession;
	private Date dateEtatTableau;
	private Boolean typePI = false;
	private Boolean typeAG = false;
	private Boolean typeRE = false;
	private Boolean officialise = false;
	private EnumPhaseTableauAG etatTableau = EnumPhaseTableauAG.INITIALISATION;
	private EnumEtatCalculTableauAG etatCalcul = EnumEtatCalculTableauAG.NON_CALCULE;

	private Organisme organisme;
	private EcheancierCAP echeancierCAP;
	private ParamPresentationCAP paramPresentationCapPI;
	private ParamPresentationCAP paramPresentationCapAG;
	private ParamPresentationCAP paramPresentationCapPIOfficialise;
	private ParamPresentationCAP paramPresentationCapAGOfficialise;
	private UniteGestion uniteGestion;
	
	private Set<GroupeHierarchique> listGroupeHierarchique;
	private Set<PropositionAG> listPropositionAG;
	private Set<Categorie> listCategorie;
	private Set<Filiere> listFiliere;
	private Set<Collectivite> listCollectivite;
	private Set<Statut> listStatut;
	private Set<TacheAvancementAG> listTacheAvancement;
	private Set<FonctionAG> listFonctionAG;
	private Set<RatiosQuotasParTableau> listRatiosQuotasParTableau;
	private Set<CampagneEvaluation> listCampagneEvaluation;
	private Set<CadreEmploi> listCadreEmploi;


	/**
	 * Constructeur
	 */
	public TableauAG(){
		super();
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the libelle
	 */
	public String getLibelle() {
		return libelle;
	}

	/**
	 * @param libelle the libelle to set
	 */
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	/**
	 * @return the datePromotion
	 */
	public Date getDatePromotion() {
		return datePromotion;
	}

	/**
	 * @param datePromotion the datePromotion to set
	 */
	public void setDatePromotion(Date datePromotion) {
		this.datePromotion = datePromotion;
	}

	/**
	 * @return the dateSession
	 */
	public Date getDateSession() {
		return dateSession;
	}

	/**
	 * @param dateSession the dateSession to set
	 */
	public void setDateSession(Date dateSession) {
		this.dateSession = dateSession;
	}

	/**
	 * @return the typePI
	 */
	public Boolean getTypePI() {
		return typePI;
	}

	/**
	 * @param typePI the typePI to set
	 */
	public void setTypePI(Boolean typePI) {
		this.typePI = typePI;
	}

	/**
	 * @return the typeAG
	 */
	public Boolean getTypeAG() {
		return typeAG;
	}

	/**
	 * @param typeAG the typeAG to set
	 */
	public void setTypeAG(Boolean typeAG) {
		this.typeAG = typeAG;
	}

	public Boolean getTypeRE() {
        return typeRE;
    }

    public void setTypeRE(Boolean typeRE) {
        this.typeRE = typeRE;
    }

    /**
	 * @return the officialise
	 */
	public Boolean getOfficialise() {
		return officialise;
	}

	/**
	 * @param officialise the officialise to set
	 */
	public void setOfficialise(Boolean officialise) {
		this.officialise = officialise;
	}

	/**
	 * @return the etatTableau
	 */
	public EnumPhaseTableauAG getEtatTableau() {
		return etatTableau;
	}

	/**
	 * @param etatTableau the etatTableau to set
	 */
	public void setEtatTableau(EnumPhaseTableauAG etatTableau) {
		this.etatTableau = etatTableau;
	}

	/**
	 * @return the organisme
	 */
	public Organisme getOrganisme() {
		return organisme;
	}

	/**
	 * @param organisme the organisme to set
	 */
	public void setOrganisme(Organisme organisme) {
		this.organisme = organisme;
	}

	/**
	 * @return the listCategorie
	 */
	public Set<Categorie> getListCategorie() {
		return listCategorie;
	}

	/**
	 * @param listCategorie the listCategorie to set
	 */
	public void setListCategorie(Set<Categorie> listCategorie) {
		this.listCategorie = listCategorie;
	}

	/**
	 * @return the listFiliere
	 */
	public Set<Filiere> getListFiliere() {
		return listFiliere;
	}

	/**
	 * @param listFiliere the listFiliere to set
	 */
	public void setListFiliere(Set<Filiere> listFiliere) {
		this.listFiliere = listFiliere;
	}

	/**
	 * @return the listCollectivite
	 */
	public Set<Collectivite> getListCollectivite() {
		return listCollectivite;
	}

	/**
	 * @param listCollectivite the listCollectivite to set
	 */
	public void setListCollectivite(Set<Collectivite> listCollectivite) {
		this.listCollectivite = listCollectivite;
	}

	/**
	 * @return the listStatut
	 */
	public Set<Statut> getListStatut() {
		return listStatut;
	}

	/**
	 * @param listStatut the listStatut to set
	 */
	public void setListStatut(Set<Statut> listStatut) {
		this.listStatut = listStatut;
	}

	public Set<CampagneEvaluation> getListCampagneEvaluation() {
		return listCampagneEvaluation;
	}

	public void setListCampagneEvaluation(
			Set<CampagneEvaluation> listCampagneEvaluation) {
		this.listCampagneEvaluation = listCampagneEvaluation;
	}

	/**
	 * @return the echeancierCAP
	 */
	public EcheancierCAP getEcheancierCAP() {
		return echeancierCAP;
	}

	/**
	 * @param echeancierCAP the echeancierCAP to set
	 */
	public void setEcheancierCAP(EcheancierCAP echeancierCAP) {
		this.echeancierCAP = echeancierCAP;
	}

	/**
	 * @return the paramPresentationCapPI
	 */
	public ParamPresentationCAP getParamPresentationCapPI() {
		return paramPresentationCapPI;
	}

	/**
	 * @param paramPresentationCapPI the paramPresentationCapPI to set
	 */
	public void setParamPresentationCapPI(
			ParamPresentationCAP paramPresentationCapPI) {
		this.paramPresentationCapPI = paramPresentationCapPI;
	}

	/**
	 * @return the paramPresentationCapAG
	 */
	public ParamPresentationCAP getParamPresentationCapAG() {
		return paramPresentationCapAG;
	}

	/**
	 * @param paramPresentationCapAG the paramPresentationCapAG to set
	 */
	public void setParamPresentationCapAG(
			ParamPresentationCAP paramPresentationCapAG) {
		this.paramPresentationCapAG = paramPresentationCapAG;
	}

	/**
	 * @return the listGroupeHierarchique
	 */
	public Set<GroupeHierarchique> getListGroupeHierarchique() {
		return listGroupeHierarchique;
	}

	/**
	 * @param listGroupeHierarchique the listGroupeHierarchique to set
	 */
	public void setListGroupeHierarchique(
			Set<GroupeHierarchique> listGroupeHierarchique) {
		this.listGroupeHierarchique = listGroupeHierarchique;
	}

	/**
	 * @return the listPropositionAG
	 */
	public Set<PropositionAG> getListPropositionAG() {
		return listPropositionAG;
	}

	/**
	 * @param listPropositionAG the listPropositionAG to set
	 */
	public void setListPropositionAG(Set<PropositionAG> listPropositionAG) {
		this.listPropositionAG = listPropositionAG;
	}

	public ParamPresentationCAP getParamPresentationCapAGOfficialise() {
		return paramPresentationCapAGOfficialise;
	}

	public void setParamPresentationCapAGOfficialise(ParamPresentationCAP paramPresentationCapAGOfficialise) {
		this.paramPresentationCapAGOfficialise = paramPresentationCapAGOfficialise;
	}

	public ParamPresentationCAP getParamPresentationCapPIOfficialise() {
		return paramPresentationCapPIOfficialise;
	}

	public void setParamPresentationCapPIOfficialise(ParamPresentationCAP paramPresentationCapPIOfficialise) {
		this.paramPresentationCapPIOfficialise = paramPresentationCapPIOfficialise;
	}
	
	public UniteGestion getUniteGestion() {
		return uniteGestion;
	}

	public void setUniteGestion(UniteGestion uniteGestion) {
		this.uniteGestion = uniteGestion;
	}

	public EnumEtatCalculTableauAG getEtatCalcul() {
		return etatCalcul;
	}

	public void setEtatCalcul(EnumEtatCalculTableauAG etatCalcul) {
		this.etatCalcul = etatCalcul;
	}

	public Set<FonctionAG> getListFonctionAG() {
		return listFonctionAG;
	}

	public void setListFonctionAG(Set<FonctionAG> listFonctionAG) {
		this.listFonctionAG = listFonctionAG;
	}

	public Set<RatiosQuotasParTableau> getListRatiosQuotasParTableau() {
		return listRatiosQuotasParTableau;
	}

	public void setListRatiosQuotasParTableau(Set<RatiosQuotasParTableau> listRatiosQuotasParTableau) {
		this.listRatiosQuotasParTableau = listRatiosQuotasParTableau;
	}

	public Set<TacheAvancementAG> getListTacheAvancement() {
		return listTacheAvancement;
	}

	public void setListTacheAvancement(Set<TacheAvancementAG> listTacheAvancement) {
		this.listTacheAvancement = listTacheAvancement;
	}

	public Date getDateEtatTableau() {
		return dateEtatTableau;
	}

	public void setDateEtatTableau(Date dateEtatTableau) {
		this.dateEtatTableau = dateEtatTableau;
	}

	public Set<CadreEmploi> getListCadreEmploi() {
		return listCadreEmploi;
	}

	public void setListCadreEmploi(Set<CadreEmploi> listCadreEmploi) {
		this.listCadreEmploi = listCadreEmploi;
	}
}