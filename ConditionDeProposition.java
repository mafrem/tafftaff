package fr.sedit.grh.coeur.ca.avg.model;
import java.util.Date;
import java.util.Set;

import fr.sedit.core.common.model.AbstractPersistentObjectNO;
import fr.sedit.grh.coeur.ca.par.model.LigneConditionAG;

/**
 * @author ROSTAIN Guilhem
 * @version 1.0
 * @created 16-janv.-2008 11:15:52
 */
public class ConditionDeProposition extends AbstractPersistentObjectNO {

	private static final long serialVersionUID = 8552975361391889202L;
	
	private Boolean conditionRemplie = false;
	private Date dateMini;

	private PropositionAG propositionAG;
	private LigneConditionAG conditionAG;
	
	private Set<DetailCondition> listDetailCondition;

	/**
	 * Constructeur
	 */
	public ConditionDeProposition(){
		super();
	}

	/**
	 * @return the conditionRemplie
	 */
	public Boolean getConditionRemplie() {
		return conditionRemplie;
	}

	/**
	 * @param conditionRemplie the conditionRemplie to set
	 */
	public void setConditionRemplie(Boolean conditionRemplie) {
		this.conditionRemplie = conditionRemplie;
	}

	/**
	 * @return the dateMini
	 */
	public Date getDateMini() {
		return dateMini;
	}

	/**
	 * @param dateMini the dateMini to set
	 */
	public void setDateMini(Date dateMini) {
		this.dateMini = dateMini;
	}

	/**
	 * @return the conditionAG
	 */
	public LigneConditionAG getConditionAG() {
		return conditionAG;
	}

	/**
	 * @param conditionAG the conditionAG to set
	 */
	public void setConditionAG(LigneConditionAG conditionAG) {
		this.conditionAG = conditionAG;
	}

	/**
	 * @return the listDetailCondition
	 */
	public Set<DetailCondition> getListDetailCondition() {
		return listDetailCondition;
	}

	/**
	 * @param listDetailCondition the listDetailCondition to set
	 */
	public void setListDetailCondition(Set<DetailCondition> listDetailCondition) {
		this.listDetailCondition = listDetailCondition;
	}

	public PropositionAG getPropositionAG() {
		return propositionAG;
	}

	public void setPropositionAG(PropositionAG propositionAG) {
		this.propositionAG = propositionAG;
	}
}