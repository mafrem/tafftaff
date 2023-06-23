package fr.sedit.grh.coeur.ca.avg.model;

import java.util.Date;
import java.util.Set;

import fr.sedit.core.tools.UtilsDuree;
import fr.sedit.grh.ca.par.model.enums.EnumModeCreation;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypeAvancementAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypeInjectionAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypePropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumVerifierAG;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumDecisionCAP;
import fr.sedit.grh.coeur.cs.model.Carriere;
import fr.sedit.grh.coeur.cs.model.ConcoursAgent;
import fr.sedit.grh.coeur.cs.model.Echelon;
import fr.sedit.grh.coeur.cs.model.FichePositionAdmin;
import fr.sedit.grh.coeur.cs.model.Grade;
import fr.sedit.grh.coeur.cs.model.Imprime;
import fr.sedit.grh.coeur.cs.model.Statut;
import fr.sedit.grh.coeur.cs.model.parametrage.MotifAvancement;
import fr.sedit.sedit.common.model.AbstractCompatibleCSObjectNO;

/**
 * @author ROSTAIN Guilhem
 * @version 1.0
 * @created 25-janv.-2008 11:45:11
 */
public class PropositionAG extends AbstractCompatibleCSObjectNO {

	private static final long serialVersionUID = 4776619938023979617L;
	
	// ** attributs
	private Long codeRegroupement;
	private Date dateCalcul;
	private Date dateEffet;
	private Date dateFinContrat;
	private Date dateInjection;
	private Date dateNomination;
	private Date dateRefus;
	private Date dateRetenue;
	private Integer dureeRetardAnnee;
	private Integer dureeRetardJour;
	private Boolean exclueManuellement = false;
	private String motifExclusion;
	private String motifRefus;
	private String msgControle;
	private Boolean promouvable = false;			// Détermine si la proposition est promouvable ou pas
	private Boolean promu = false;					// Détermine si l'avancement est validé ou non
	private Boolean refuse = false;
	private Boolean reclassementMedical = false;
	private Integer ancienneteAA;
	private Integer ancienneteMM;	
	private Integer ancienneteJJ;	
	private Date dateEntreeEchelon;	
	private Date dateEntreeGrade;
	private Date dateEntreeGradeRetenue;	
	private Date dateMiniAvancement;	
	private String reliquat;	
	private String indiceBrut;
	private Float montantBrut;
	private Date dateSPP;
	private Date dateSPV;
	private Date dateEntreeSPVActuel;
	private Boolean promuSPV = false;
	
	// ** enums
	private EnumTypeAvancementAG typeAvancement = EnumTypeAvancementAG.GRADE;
	private EnumTypePropositionAG typeProposition = EnumTypePropositionAG.PROMOUVABLE;
	private EnumTypeInjectionAG injection = EnumTypeInjectionAG.NON_INJECTE;
	private EnumModeCreation modeCreation = EnumModeCreation.MANUELLE;
	private EnumDecisionCAP decision = EnumDecisionCAP.NON_PROMU;
	private EnumVerifierAG verifie = EnumVerifierAG.NON;

	// ** liens
	private Carriere carriere;
	private Grade gradeCible;
	private TableauAG tableauAG;
	private Echelon chevronCible;
	private MotifAvancement motifAvancement;
	private Statut statutAvancement;
	private FichePositionAdmin fichePosition;
	private PropositionAG propositionAGAnneePrecedente;
	private RangClassementAG rangClassementAG;
	private ConcoursAgent reussiteConcours;
	private ConditionDeProposition conditionDePropositionSelectionnee;
	private Statut statutActuel;
	private Grade gradeAncien;
	private Echelon chevronAncien;
	private Imprime imprime;
	private Grade gradeSPVActuel;
	private Grade gradeSPVFutur;
	private Imprime imprimeSPV;
	
	// ** listes
	private Set<AvisAG> listAvisAG;
	private Set<ConditionDeProposition> listConditionDeProposition;
	
	
	
	/**
	 * Constructeur
	 */
	public PropositionAG(){super();}
	
    // *****************************************************************************
    // Helpers methods
    // *****************************************************************************
    /**
     * @return anciennete en nombre de jours (trentièmes)
     */
    public Integer getAnciennete(){
        Integer anc= null;
        if (ancienneteAA!=null || ancienneteMM!=null || ancienneteJJ!=null){
            anc= UtilsDuree.getNbJours((ancienneteAA==null ? 0 : ancienneteAA),
                                       (ancienneteMM==null ? 0 : ancienneteMM),
                                       (ancienneteJJ==null ? 0 : ancienneteJJ)).intValue();
        }
        return anc;
    }
    /**
     * set l'anciennete (AA,MM,JJ) à partir d'un nombre de jours
     * @param nbJours
     */
    public void setAnciennete(final Integer nbJours){
        if (nbJours==null){
            ancienneteAA= null;
            ancienneteMM= null;
            ancienneteJJ= null;
        }else{
            ancienneteAA= nbJours / (30 * 12);
            ancienneteMM= (nbJours % (30 * 12)) / 30;
            ancienneteJJ= nbJours % 30;
        }
    }
    
    // *****************************************************************************
    
    /**
     * @see fr.sedit.core.common.model.AbstractCompatibleCSObjectNO#getPobjExtract()
     */
    @Override
    // FIXME 14979
    public String getPobjExtract() {
        try {
            return buildPobjExtract(gradeCible != null ? gradeCible.getLibelleMoyen() : "",
                    typeAvancement != null ? typeAvancement.getLibelle() : "");
        } catch (Exception e) {
            String idStr = (getId() != null ? " (id=" + getId() + ")" : "");
            log.warn("PropositionAG" + idStr + " : impossible de générer le pobj_extract", e);
            return "??????????";
        }
    }
    
	/**
	 * @return the codeRegroupement
	 */
	public Long getCodeRegroupement() {
		return codeRegroupement;
	}

	/**
	 * @param codeRegroupement the codeRegroupement to set
	 */
	public void setCodeRegroupement(Long codeRegroupement) {
		this.codeRegroupement = codeRegroupement;
	}

	/**
	 * @return the dateCalcul
	 */
	public Date getDateCalcul() {
		return dateCalcul;
	}

	/**
	 * @param dateCalcul the dateCalcul to set
	 */
	public void setDateCalcul(Date dateCalcul) {
		this.dateCalcul = dateCalcul;
	}

	/**
	 * @return the dateEffet
	 */
	public Date getDateEffet() {
		return dateEffet;
	}

	/**
	 * @param dateEffet the dateEffet to set
	 */
	public void setDateEffet(Date dateEffet) {
		this.dateEffet = dateEffet;
	}

	/**
	 * @return the dateFinContrat
	 */
	public Date getDateFinContrat() {
		return dateFinContrat;
	}

	/**
	 * @param dateFinContrat the dateFinContrat to set
	 */
	public void setDateFinContrat(Date dateFinContrat) {
		this.dateFinContrat = dateFinContrat;
	}

	/**
	 * @return the dateInjection
	 */
	public Date getDateInjection() {
		return dateInjection;
	}

	/**
	 * @param dateInjection the dateInjection to set
	 */
	public void setDateInjection(Date dateInjection) {
		this.dateInjection = dateInjection;
	}

	/**
	 * @return the dateNomination
	 */
	public Date getDateNomination() {
		return dateNomination;
	}

	/**
	 * @param dateNomination the dateNomination to set
	 */
	public void setDateNomination(Date dateNomination) {
		this.dateNomination = dateNomination;
	}

	/**
	 * @return the dateRefus
	 */
	public Date getDateRefus() {
		return dateRefus;
	}

	/**
	 * @param dateRefus the dateRefus to set
	 */
	public void setDateRefus(Date dateRefus) {
		this.dateRefus = dateRefus;
	}

	/**
	 * @return the dateRetenue
	 */
	public Date getDateRetenue() {
		return dateRetenue;
	}

	/**
	 * @param dateRetenue the dateRetenue to set
	 */
	public void setDateRetenue(Date dateRetenue) {
		this.dateRetenue = dateRetenue;
	}

	/**
	 * @return the decision
	 */
	public EnumDecisionCAP getDecision() {
		return decision;
	}

	/**
	 * @param decision the decision to set
	 */
	public void setDecision(EnumDecisionCAP decision) {
		this.decision = decision;
	}

	/**
	 * @return the dureeRetardAnnee
	 */
	public Integer getDureeRetardAnnee() {
		return dureeRetardAnnee;
	}

	/**
	 * @param dureeRetardAnnee the dureeRetardAnnee to set
	 */
	public void setDureeRetardAnnee(Integer dureeRetardAnnee) {
		this.dureeRetardAnnee = dureeRetardAnnee;
	}

	/**
	 * @return the dureeRetardJour
	 */
	public Integer getDureeRetardJour() {
		return dureeRetardJour;
	}

	/**
	 * @param dureeRetardJour the dureeRetardJour to set
	 */
	public void setDureeRetardJour(Integer dureeRetardJour) {
		this.dureeRetardJour = dureeRetardJour;
	}

	/**
	 * @return the exclueManuellement
	 */
	public Boolean getExclueManuellement() {
		return exclueManuellement;
	}

	/**
	 * @param exclueManuellement the exclueManuellement to set
	 */
	public void setExclueManuellement(Boolean exclueManuellement) {
		this.exclueManuellement = exclueManuellement;
	}

	/**
	 * @return the modeCreation
	 */
	public EnumModeCreation getModeCreation() {
		return modeCreation;
	}

	/**
	 * @param modeCreation the modeCreation to set
	 */
	public void setModeCreation(EnumModeCreation modeCreation) {
		this.modeCreation = modeCreation;
	}

	/**
	 * @return the motifExclusion
	 */
	public String getMotifExclusion() {
		return motifExclusion;
	}

	/**
	 * @param motifExclusion the motifExclusion to set
	 */
	public void setMotifExclusion(String motifExclusion) {
		this.motifExclusion = motifExclusion;
	}

	/**
	 * @return the motifRefus
	 */
	public String getMotifRefus() {
		return motifRefus;
	}

	/**
	 * @param motifRefus the motifRefus to set
	 */
	public void setMotifRefus(String motifRefus) {
		this.motifRefus = motifRefus;
	}

	/**
	 * @return the msgControle
	 */
	public java.lang.String getMsgControle() {
		return msgControle;
	}

	/**
	 * @param msgControle the msgControle to set
	 */
	public void setMsgControle(java.lang.String msgControle) {
		this.msgControle = msgControle;
	}

	/**
	 * @return the promouvable
	 */
	public Boolean getPromouvable() {
		return promouvable;
	}

	/**
	 * @param promouvable the promouvable to set
	 */
	public void setPromouvable(Boolean promouvable) {
		this.promouvable = promouvable;
	}

	/**
	 * @return the promu
	 */
	public Boolean getPromu() {
		return promu;
	}

	/**
	 * @param promu the promu to set
	 */
	public void setPromu(Boolean promu) {
		this.promu = promu;
	}

	/**
	 * @return the reclassementMedical
	 */
	public Boolean getReclassementMedical() {
		return reclassementMedical;
	}

	/**
	 * @param reclassementMedical the reclassementMedical to set
	 */
	public void setReclassementMedical(Boolean reclassementMedical) {
		this.reclassementMedical = reclassementMedical;
	}

	/**
	 * @return the refuse
	 */
	public Boolean getRefuse() {
		return refuse;
	}

	/**
	 * @param refuse the refuse to set
	 */
	public void setRefuse(Boolean refuse) {
		this.refuse = refuse;
	}

	/**
	 * @return the typeAvancement
	 */
	public EnumTypeAvancementAG getTypeAvancement() {
		return typeAvancement;
	}

	/**
	 * @param typeAvancement the typeAvancement to set
	 */
	public void setTypeAvancement(EnumTypeAvancementAG typeAvancement) {
		this.typeAvancement = typeAvancement;
	}

	/**
	 * @return the typeProposition
	 */
	public EnumTypePropositionAG getTypeProposition() {
		return typeProposition;
	}

	/**
	 * @param typeProposition the typeProposition to set
	 */
	public void setTypeProposition(EnumTypePropositionAG typeProposition) {
		this.typeProposition = typeProposition;
	}

	/**
	 * @return the verifie
	 */
	public EnumVerifierAG getVerifie() {
		return verifie;
	}

	/**
	 * @param verifie the verifie to set
	 */
	public void setVerifie(EnumVerifierAG verifie) {
		this.verifie = verifie;
	}

	/**
	 * @return the carriere
	 */
	public Carriere getCarriere() {
		return carriere;
	}

	/**
	 * @param carriere the carriere to set
	 */
	public void setCarriere(Carriere carriere) {
		this.carriere = carriere;
	}

	/**
	 * @return the gradeCible
	 */
	public Grade getGradeCible() {
		return gradeCible;
	}

	/**
	 * @param gradeCible the gradeCible to set
	 */
	public void setGradeCible(Grade gradeCible) {
		this.gradeCible = gradeCible;
	}

	/**
	 * @return the tableauAG
	 */
	public TableauAG getTableauAG() {
		return tableauAG;
	}

	/**
	 * @param tableauAG the tableauAG to set
	 */
	public void setTableauAG(TableauAG tableauAG) {
		this.tableauAG = tableauAG;
	}

	/**
	 * @return the chevronCible
	 */
	public Echelon getChevronCible() {
		return chevronCible;
	}

	/**
	 * @param chevronCible the chevronCible to set
	 */
	public void setChevronCible(Echelon echelonCible) {
		this.chevronCible = echelonCible;
	}

	/**
	 * @return the motifAvancement
	 */
	public MotifAvancement getMotifAvancement() {
		return motifAvancement;
	}

	/**
	 * @param motifAvancement the motifAvancement to set
	 */
	public void setMotifAvancement(MotifAvancement motifAvancement) {
		this.motifAvancement = motifAvancement;
	}

	/**
	 * @return the statutAvancement
	 */
	public Statut getStatutAvancement() {
		return statutAvancement;
	}

	/**
	 * @param statutAvancement the statutAvancement to set
	 */
	public void setStatutAvancement(Statut statutAvancement) {
		this.statutAvancement = statutAvancement;
	}

	/**
	 * @return the fichePosition
	 */
	public FichePositionAdmin getFichePosition() {
		return fichePosition;
	}

	/**
	 * @param fichePosition the fichePosition to set
	 */
	public void setFichePosition(FichePositionAdmin fichePosition) {
		this.fichePosition = fichePosition;
	}

	/**
	 * @return the propositionAGAnneePrecedente
	 */
	public PropositionAG getPropositionAGAnneePrecedente() {
		return propositionAGAnneePrecedente;
	}

	/**
	 * @param propositionAGAnneePrecedente the propositionAGAnneePrecedente to set
	 */
	public void setPropositionAGAnneePrecedente(
			PropositionAG propositionAGAnneePrecedente) {
		this.propositionAGAnneePrecedente = propositionAGAnneePrecedente;
	}

	/**
	 * @return the listAvisAG
	 */
	public Set<AvisAG> getListAvisAG() {
		return listAvisAG;
	}

	/**
	 * @param listAvisAG the listAvisAG to set
	 */
	public void setListAvisAG(Set<AvisAG> listAvisAG) {
		this.listAvisAG = listAvisAG;
	}

	/**
	 * @return the rangClassementAG
	 */
	public RangClassementAG getRangClassementAG() {
		return rangClassementAG;
	}

	/**
	 * @param rangClassementAG the rangClassementAG to set
	 */
	public void setRangClassementAG(RangClassementAG rangClassementAG) {
		this.rangClassementAG = rangClassementAG;
	}

	/**
	 * @return the reussiteConcours
	 */
	public ConcoursAgent getReussiteConcours() {
		return reussiteConcours;
	}

	/**
	 * @param reussiteConcours the reussiteConcours to set
	 */
	public void setReussiteConcours(ConcoursAgent reussiteConcours) {
		this.reussiteConcours = reussiteConcours;
	}

	/**
	 * @return the conditionDePropositionSelectionnee
	 */
	public ConditionDeProposition getConditionDePropositionSelectionnee() {
		return conditionDePropositionSelectionnee;
	}

	/**
	 * @param conditionDePropositionSelectionnee the conditionDePropositionSelectionnee to set
	 */
	public void setConditionDePropositionSelectionnee(
			ConditionDeProposition conditionDePropositionSelectionnee) {
		this.conditionDePropositionSelectionnee = conditionDePropositionSelectionnee;
	}

	/**
	 * @return the statutActuel
	 */
	public Statut getStatutActuel() {
		return statutActuel;
	}

	/**
	 * @param statutActuel the statutActuel to set
	 */
	public void setStatutActuel(Statut statutActuel) {
		this.statutActuel = statutActuel;
	}

	/**
	 * @return the gradeAncien
	 */
	public Grade getGradeAncien() {
		return gradeAncien;
	}

	/**
	 * @param gradeAncien the gradeAncien to set
	 */
	public void setGradeAncien(Grade gradeAncien) {
		this.gradeAncien = gradeAncien;
	}

	/**
	 * @return the chevronAncien
	 */
	public Echelon getChevronAncien() {
		return chevronAncien;
	}

	/**
	 * @param chevronAncien the chevronAncien to set
	 */
	public void setChevronAncien(Echelon echelonAncien) {
		this.chevronAncien = echelonAncien;
	}

	public Set<ConditionDeProposition> getListConditionDeProposition() {
		return listConditionDeProposition;
	}

	public void setListConditionDeProposition(
			Set<ConditionDeProposition> listConditionDeProposition) {
		this.listConditionDeProposition = listConditionDeProposition;
	}

	/**
	 * @return the ancienneteAA
	 */
	public Integer getAncienneteAA() {
		return ancienneteAA;
	}

	/**
	 * @param ancienneteAA the ancienneteAA to set
	 */
	public void setAncienneteAA(Integer ancienneteAA) {
		this.ancienneteAA = ancienneteAA;
	}

	/**
	 * @return the ancienneteMM
	 */
	public Integer getAncienneteMM() {
		return ancienneteMM;
	}

	/**
	 * @param ancienneteMM the ancienneteMM to set
	 */
	public void setAncienneteMM(Integer ancienneteMM) {
		this.ancienneteMM = ancienneteMM;
	}

	/**
	 * @return the ancienneteJJ
	 */
	public Integer getAncienneteJJ() {
		return ancienneteJJ;
	}

	/**
	 * @param ancienneteJJ the ancienneteJJ to set
	 */
	public void setAncienneteJJ(Integer ancienneteJJ) {
		this.ancienneteJJ = ancienneteJJ;
	}

	/**
	 * @return the dateEntreeEchelon
	 */
	public Date getDateEntreeEchelon() {
		return dateEntreeEchelon;
	}

	/**
	 * @param dateEntreeEchelon the dateEntreeEchelon to set
	 */
	public void setDateEntreeEchelon(Date dateEntreeEchelon) {
		this.dateEntreeEchelon = dateEntreeEchelon;
	}

	/**
	 * @return the dateEntreeGrade
	 */
	public Date getDateEntreeGrade() {
		return dateEntreeGrade;
	}

	/**
	 * @param dateEntreeGrade the dateEntreeGrade to set
	 */
	public void setDateEntreeGrade(Date dateEntreeGrade) {
		this.dateEntreeGrade = dateEntreeGrade;
	}
	

	public Date getDateEntreeGradeRetenue() {
		return dateEntreeGradeRetenue;
	}

	public void setDateEntreeGradeRetenue(Date dateEntreeGradeRetenue) {
		this.dateEntreeGradeRetenue = dateEntreeGradeRetenue;
	}

	/**
	 * @return the dateMiniAvancement
	 */
	public Date getDateMiniAvancement() {
		return dateMiniAvancement;
	}

	/**
	 * @param dateMiniAvancement the dateMiniAvancement to set
	 */
	public void setDateMiniAvancement(Date dateMiniAvancement) {
		this.dateMiniAvancement = dateMiniAvancement;
	}

	/**
	 * @return the reliquat
	 */
	public String getReliquat() {
		return reliquat;
	}

	/**
	 * @param reliquat the reliquat to set
	 */
	public void setReliquat(String reliquat) {
		this.reliquat = reliquat;
	}

	public EnumTypeInjectionAG getInjection() {
		return injection;
	}

	public void setInjection(EnumTypeInjectionAG injection) {
		this.injection = injection;
	}

	public Imprime getImprime() {
		return imprime;
	}

	public void setImprime(Imprime imprime) {
		this.imprime = imprime;
	}

	public String getIndiceBrut() {
		return indiceBrut;
	}

	public void setIndiceBrut(String indiceBrut) {
		this.indiceBrut = indiceBrut;
	}

	public Float getMontantBrut() {
		return montantBrut;
	}

	public void setMontantBrut(Float montantBrut) {
		this.montantBrut = montantBrut;
	}

	public Date getDateSPP() {
		return dateSPP;
	}

	public void setDateSPP(Date dateSPP) {
		this.dateSPP = dateSPP;
	}

	public Date getDateSPV() {
		return dateSPV;
	}

	public void setDateSPV(Date dateSPV) {
		this.dateSPV = dateSPV;
	}

	public Date getDateEntreeSPVActuel() {
		return dateEntreeSPVActuel;
	}

	public void setDateEntreeSPVActuel(Date dateEntreeSPVActuel) {
		this.dateEntreeSPVActuel = dateEntreeSPVActuel;
	}

	public Grade getGradeSPVActuel() {
		return gradeSPVActuel;
	}

	public void setGradeSPVActuel(Grade gradeSPVActuel) {
		this.gradeSPVActuel = gradeSPVActuel;
	}

	public Grade getGradeSPVFutur() {
		return gradeSPVFutur;
	}

	public void setGradeSPVFutur(Grade gradeSPVFutur) {
		this.gradeSPVFutur = gradeSPVFutur;
	}

	public Boolean getPromuSPV() {
		return promuSPV;
	}

	public void setPromuSPV(Boolean promuSPV) {
		this.promuSPV = promuSPV;
	}

	public Imprime getImprimeSPV() {
		return imprimeSPV;
	}

	public void setImprimeSPV(Imprime imprimeSPV) {
		this.imprimeSPV = imprimeSPV;
	}
}