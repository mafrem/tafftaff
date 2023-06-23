package fr.sedit.grh.coeur.ca.avg.model;
import java.util.Date;
import java.util.Set;

import fr.sedit.core.common.model.AbstractPersistentObjectNO;
import fr.sedit.grh.ca.avg.model.JustificatifRetardAG;
import fr.sedit.grh.coeur.ca.par.model.ValeurFonctionCondition;

/**
 * @author ROSTAIN Guilhem
 * @version 1.0
 * @created 16-janv.-2008 11:15:52
 */
public class DetailCondition extends AbstractPersistentObjectNO {

	private static final long serialVersionUID = -8666808375701621389L;
	
	/** ancienneté calculée pour la condition, peut être nulle*/
	private Integer ancienneteAnnee;
	private Integer ancienneteJour;
	/** Date à laquelle la condition est remplie */
	private Date dateDetail;

	private ConditionDeProposition conditionDeProposition;
	private ValeurFonctionCondition valeurFonctionCondition;

	private Set<JustificatifRetardAG> listJustificatifRetard;

	// helper variable (not mapped to db)
	public boolean failedAnciennete= false;
	
	/**
	 * Constructeur
	 */
	public DetailCondition(){super();}


	public Integer getAncienneteAnnee() {
		return ancienneteAnnee;
	}
	public void setAncienneteAnnee(Integer ancienneteAnnee) {
		this.ancienneteAnnee = ancienneteAnnee;
	}
	public Integer getAncienneteJour() {
		return ancienneteJour;
	}
	public void setAncienneteJour(Integer ancienneteJour) {
		this.ancienneteJour = ancienneteJour;
	}
	public ConditionDeProposition getConditionDeProposition() {
		return conditionDeProposition;
	}
	public void setConditionDeProposition(ConditionDeProposition conditionDeProposition) {
		this.conditionDeProposition = conditionDeProposition;
	}
	public Date getDateDetail() {
		return dateDetail;
	}
	public void setDateDetail(Date dateDetail) {
		this.dateDetail = dateDetail;
	}
	public Set<JustificatifRetardAG> getListJustificatifRetard() {
		return listJustificatifRetard;
	}
	public void setListJustificatifRetard(Set<JustificatifRetardAG> listJustificatifRetard) {
		this.listJustificatifRetard = listJustificatifRetard;
	}
	public ValeurFonctionCondition getValeurFonctionCondition() {
		return valeurFonctionCondition;
	}
	public void setValeurFonctionCondition(ValeurFonctionCondition valeurFonctionCondition) {
		this.valeurFonctionCondition = valeurFonctionCondition;
	}
}